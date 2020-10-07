package highdeger.highdownloader;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class DownloadTaskRunnable implements Runnable {

    public enum Status {init, ready, downloading, paused, error, completed}
    public enum OnFinished {nothing, close_window, sleep, shutdown, forced_shutdown, run}
    private static int TASK_ID_COUNTER = 0;

    protected String url;
    protected String savePath;
    protected String saveName;
    protected String tempPath;
    protected String tempName;
    protected int taskId;
    private final ConcurrentLinkedDeque<StreamDownloadCallable> parts = new ConcurrentLinkedDeque<>();
    private final ArrayList<Future<String>> futures = new ArrayList<>();
    private Status status;
    private OnFinished onFinished;
    private final long size;
    private final int threadCount;
    protected AtomicLong downloadedSize = new AtomicLong();
    protected long lastSize = 0;
    protected AtomicLong speed = new AtomicLong();
    private ExecutorService executorService;
    private ScheduledExecutorService executorMonitor;
    private long[] sections;
    private WindowProgress windowProgress;
    private double progressBarMultiplier = 1;
    private JLabel labelTransferRate;
    private JLabel labelTimeLeft;
    private JLabel labelResume;
    private JProgressBar progressBar;
    private JLabel labelStatus;
    private JLabel labelDownloadedSize;
    private ScheduledFuture<?> scheduledFutureDownloadedSize;
    private ScheduledFuture<?> scheduledFutureTransferRate;
    private String[] partPaths;

    public DownloadTaskRunnable(String url, String savePath, String saveName, String tempPath, String tempName, int sectionCount) throws RuntimeException {
        status = Status.init;
        onFinished = OnFinished.nothing;
        taskId = TASK_ID_COUNTER++;
        this.url = url;
        this.savePath = savePath;
        this.saveName = saveName;
        setFolder("temp", tempPath);
        this.tempName = tempName;
        threadCount = sectionCount;
        setSections(sectionCount, (size = fetchSize()));
        status = Status.ready;
    }

    @Override
    public void run() {
        status = Status.downloading;
        executorService = Executors.newFixedThreadPool(threadCount);
        executorMonitor = Executors.newScheduledThreadPool(2);

        // submit callables to download each part
        for (int i = 0; i < sections.length; i++) {
            long offset = 0;
            for (int j = i - 1; j >= 0; j--)
                offset += sections[j];
            StreamDownloadCallable callable;
            setFile("none", tempPath, tempName + String.format(".p%d", i));
            callable = new StreamDownloadCallable(url, tempPath, tempName + String.format(".p%d", i), offset, sections[i]);
            parts.add(callable);
            futures.add(executorService.submit(callable));
        }

        // fetch filename with extension
        String filename_ext = "";
        if (url.contains("?"))
            filename_ext = url.split("\\?")[0];
        String[] temp = filename_ext.split("/");
        filename_ext = temp[temp.length - 1];

        // create window
        windowProgress = HighUI.createProgressWindow(filename_ext, 600, 414);
        Main.addToShowingWindow(windowProgress);
        progressBar = windowProgress.getProgressBar();
        labelTransferRate = windowProgress.getLabelTransferRate();
        labelTimeLeft = windowProgress.getLabelTimeLeft();
        labelResume = windowProgress.getLabelResume();
        labelStatus = windowProgress.getLabelStatus();
        labelDownloadedSize = windowProgress.getLabelDownloadedSize();
        windowProgress.getLabelUrl().setText(url);
        windowProgress.getLabelTotalSize().setText(HighUtil.bytesBeautify(size));
        if (size > Integer.MAX_VALUE)
            progressBarMultiplier = ((double) Integer.MAX_VALUE / size);
        progressBar.setMaximum((int) (size * progressBarMultiplier));
        progressBar.setMinimum(0);
        progressBar.setValue(0);
        labelTransferRate.setText("0 Bytes");
        labelTimeLeft.setText("0s");
        labelResume.setText("Unknown");

        // monitor downloaded size and check if finished
        Runnable checkDownloadedSize = new Runnable() {
            @Override
            public void run() {
                long temp = 0;
                for (StreamDownloadCallable part : parts) {
                    temp += part.getDownloadedSize();
                }
                downloadedSize.set(temp);
                labelDownloadedSize.setText(HighUtil.bytesBeautify(temp));
                if (temp >= size)
                    afterDownloadFinished();
            }
        };
        scheduledFutureDownloadedSize = executorMonitor.scheduleAtFixedRate(checkDownloadedSize, 50, 200, TimeUnit.MILLISECONDS);

        // monitor transfer rate
        Runnable checkTransferRate = new Runnable() {
            @Override
            public void run() {
                long tSize = downloadedSize.get();
                long tSpeed = (tSize - lastSize) * 5;
                if (tSpeed != 0) {
                    speed.set(tSpeed);
                    labelTransferRate.setText(HighUtil.bytesBeautify(tSpeed));
                }
                lastSize = tSize;
            }
        };
        scheduledFutureTransferRate = executorMonitor.scheduleAtFixedRate(checkTransferRate, 75, 200, TimeUnit.MILLISECONDS);
    }

    public File getFile(String type) {
        File f = null;
        switch (type) {
            case "save":
                f = new File(savePath + File.separator + saveName);
                break;
            case "temp":
                f = new File(tempPath + File.separator + tempName);
                break;
        }
        return f;
    }

    private boolean setFolder(String type, String path) {
        // check and create the save/temp folder
        if (path.lastIndexOf(File.separator) == path.length() - 1)
            path = path.substring(0, path.length() - 1);
        File pathFile = new File(path);
        if (!pathFile.exists())
            if (!pathFile.mkdirs())
                throw new RuntimeException("Can't create directories to the path", new Exception("mkdirs() method has failed"));

        switch (type) {
            case "save":
                savePath = path;
                break;
            case "temp":
                tempPath = path;
                break;
        }
        
        return true;
    }

    private boolean setFile(String type, String path, String name) throws RuntimeException {
        // check and create the save/temp file
        File f = new File(path + File.separator + name);
        if (!f.exists())
            try {
                if (!f.createNewFile())
                    throw new RuntimeException("Can't create the file (Unknown)", new Exception("createNewFile() method has failed"));
            } catch (IOException e) {
                throw new RuntimeException("Can't create the file (Permission)", new Exception("createNewFile() method has failed"));
            }

        switch (type) {
            case "save":
                saveName = name;
                break;
            case "temp":
                tempName = name;
                break;
            case "none":
                break;
        }

        return true;
    }

    private long fetchSize() {
        // fetch and set size of the file
        try {
            URL urlObject = new URL(url);
            URLConnection connection = urlObject.openConnection();
            return connection.getContentLengthLong();
        } catch (MalformedURLException e) {
            throw new RuntimeException("The url isn't in correct form", e);
        } catch (IOException e) {
            throw new RuntimeException("The connection stream can't be open", e);
        }
    }

    public void setSections(int count, long size) {
        long section_size = (long) Math.ceil((double) size / count);
        sections = new long[count];
        for (int i = 0; i < count; i++) {
            if (i == count - 1) {
                sections[i] = size;
            } else {
                sections[i] = section_size;
                size -= section_size;
            }
        }
    }

    protected long getDownloadedSize() {
        return downloadedSize.get();
    }

    private void afterDownloadFinished() {
        boolean allCompleted = true;
        partPaths = new String[threadCount];
        for (int i = 0; i < partPaths.length; i++) {
            try {
                partPaths[i] = futures.get(i).get(10, TimeUnit.MILLISECONDS);
            } catch (Exception e) {
                if (e instanceof InterruptedException) {
                    // TODO: do something about interruption in a callable/future
                } else if (e instanceof ExecutionException) {
                    // TODO: do something about runtime exception in a callable/future
                } else if (e instanceof TimeoutException) {
                    // TODO: do something about timeout in a callable/future
                }
                e.printStackTrace();
                allCompleted = false;
            }
        }
        if (!allCompleted) {
            // TODO: do something about the problem which caused unfinished callables/future
        } else {
            // change status
            status = Status.completed;
            labelStatus.setText("Completed");
            // set save folder to be ready
            setFolder("save", savePath);
            setFile("save", savePath, saveName);
            // cancel monitoring threads
            scheduledFutureDownloadedSize.cancel(true);
            scheduledFutureTransferRate.cancel(true);

            Runnable rebuildSections = new Runnable() {
                @Override
                public void run() {
                    try {
                        RandomAccessFile randomAccessFile = new RandomAccessFile(savePath + File.separator + saveName, "rw");
                        byte[] buffer = new byte[1024];

                        // read from each section file and write to the output file
                        for (int i = 0; i < partPaths.length; i++) {
                            FileInputStream fileInputStream = new FileInputStream(partPaths[i]);
                            int count;
                            // read and write loop
                            while ((count = fileInputStream.read(buffer, 0, buffer.length)) != -1) {
                                if ((i != partPaths.length - 1) && (count < buffer.length))
                                    // except last part and also last loop of
                                    count = Math.max(0, count - 1);
                                randomAccessFile.write(buffer, 0, count);
                            }
                            fileInputStream.close();
                        }
                        randomAccessFile.close();

                        System.out.println("finished");

                        switch (onFinished) {
                            case close_window:
                                windowProgress.dispose();
                                break;
                            case sleep:
                                // TODO: make os to sleep
                                break;
                            case shutdown:
                                // TODO: make os to shutdown
                                break;
                            case forced_shutdown:
                                // TODO: make os to forced shutdown
                                break;
                            case run:
                                // TODO: run user's script on the system terminal
                                break;
                        }
                    } catch (FileNotFoundException e) {
                        throw new RuntimeException("Can't find output file for rebuilding", e);
                    } catch (IOException e) {
                        throw new RuntimeException("Can't read/write part/output files for rebuilding", e);
                    }
                }
            };
            executorMonitor.submit(rebuildSections);
        }
    }
}
