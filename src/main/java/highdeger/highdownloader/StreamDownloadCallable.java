package highdeger.highdownloader;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;

public class StreamDownloadCallable implements Callable<String> {

    private static final int BUFFER_SIZE = 1024;

    private final String url;
    private final String savePath;
    private final String saveName;
    private final long positionStart;
    private final long positionEnd;
    private long position;
    private boolean completed = false;
    private URL urlObj;
    private File target;

    public StreamDownloadCallable(String url, String savePath, String saveName, long offset, long length) {
        // check if url is ok
        try {
            urlObj = new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException("The url isn't in correct form", e);
        }
        this.url = url;

        // check and create the "save" file and folder of the section
        File saveDir = new File(savePath + File.separator);
        if (!saveDir.exists()) {
            if (!saveDir.mkdirs()) {
                throw new RuntimeException("Can't create directories to the path", new Exception("mkdirs() method has failed"));
            }
        }
        target = new File(savePath + File.separator + saveName);
        if (!target.exists()) {
            try {
                if (!target.createNewFile()) {
                    throw new RuntimeException("Can't create the target file (Unknown)", new Exception("createNewFile() method has failed"));
                }
            } catch (IOException e) {
                throw new RuntimeException("Can't create the target file (IO Error)", e);
            }
        }
        this.savePath = savePath;
        this.saveName = saveName;

        this.positionStart = offset;
        this.positionEnd = offset + length;
        this.position = this.positionStart;
    }

    public StreamDownloadCallable(String url, String savePath, String saveName, long offset, long length, long position) {
        this(url, savePath, saveName, offset, length);
        this.position = position;
    }


    @Override
    public String call() throws RuntimeException {
        // open streams
        BufferedInputStream bufferedInputStream;
        RandomAccessFile randomAccessFile;
        byte[] buffer = new byte[BUFFER_SIZE];
        try {
            URLConnection urlConnection = urlObj.openConnection();
            urlConnection.setRequestProperty("Range", String.format("bytes=%d-%d", position, positionEnd));
            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
//            long skipped = bufferedInputStream.skip(position);

            randomAccessFile = new RandomAccessFile(target, "rw");

            // read and write loop
            while (position < positionEnd) {
                if (Thread.currentThread().isInterrupted()) {
                    // TODO: do something about the current thread being interrupted
                    break;
                }
                int readCount = bufferedInputStream.read(buffer, 0, BUFFER_SIZE);
                if (readCount != -1) {
                    randomAccessFile.write(buffer, 0, readCount); // readCount - 1
                    position += readCount;
                } else
                    break;
            }

            // close streams
            bufferedInputStream.close();
            randomAccessFile.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("The target file can't be found", e);
        } catch (IOException e) {
            throw new RuntimeException("The connection stream can't be open", e);
        }

        completed = true;
        return getAbsoluteFileAddress();
    }

    public String getAbsoluteFileAddress() {
        return savePath + File.separator + saveName;
    }

    public long getDownloadedSize() {
        return position - positionStart;
    }
}
