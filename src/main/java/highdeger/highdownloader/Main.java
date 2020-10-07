package highdeger.highdownloader;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static ModelLooksItem[] modelLooksItems;
    private static DatabaseHelper db = null;
    private static ArrayList<Component> showingWindows = new ArrayList<>();
    private static WindowMain windowMain;
    private static WindowSettings windowSettings;
    private static WindowNewDownload windowNewDownload;
    private static ArrayList<String> treePath = new ArrayList<>();
    private static ArrayList<DownloadTaskRunnable> downloadTaskRunnables = new ArrayList<>();
    private static ExecutorService executorService;
    private static ActionListener menuActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String cmd = actionEvent.getActionCommand();
            switch (cmd) {
                case "Core Settings":
//                initialize settings window
                    windowSettings = HighUI.createSettingsWindow(400, 200, modelLooksItems, showingWindows);
                    break;
                case "Create New Download":
//                initialize new download window
                    windowNewDownload = HighUI.createNewDownloadWindow(400, 200);
                    windowNewDownload.getAddButton().addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent actionEvent) {
                            String url_temp = windowNewDownload.getNewUrl().getText();
                            ArrayList<HighDownload> list = db.findAllDownloadsByUrl(url_temp);
                            if (list.size() > 0) {
                                // TODO: show duplicate window (add duplicate/resume original/discard)
                            } else {
                                // TODO: add a new download
                            }
                            DownloadTaskRunnable runnable = new DownloadTaskRunnable(url_temp, "/home/hassan/testdl/save", "testsave.rar", "/home/hassan/testdl/temp", "testtemp.rar", 4);
                            executorService.submit(runnable);
                            downloadTaskRunnables.add(runnable);
                            windowNewDownload.dispose();
                        }
                    });
                    break;
                case "Terminate":
                    windowMain.dispose();
                    break;
            }
        }
    };

    public static void main(String[] args) {
        executorService = Executors.newCachedThreadPool();
        modelLooksItems = HighUI.initUi();

//        initialize main window
        windowMain = HighUI.createMainWindow("High Downloader", 800, 600);
        addToShowingWindow(windowMain);
//        attach menu items to the listener
        JMenuItem menuItemCoreSetting = (JMenuItem) windowMain.getJMenuBar().getMenu(2).getMenuComponent(1);
        menuItemCoreSetting.addActionListener(menuActionListener);
        JMenuItem menuItemCreateNewDownload = (JMenuItem) windowMain.getJMenuBar().getMenu(0).getMenuComponent(0);
        menuItemCreateNewDownload.addActionListener(menuActionListener);
        JMenuItem menuItemTerminate = (JMenuItem) windowMain.getJMenuBar().getMenu(0).getMenuComponent(6);
        menuItemTerminate.addActionListener(menuActionListener);
//        attach main tree to a listener
        windowMain.getMainTree().addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
                treePath.clear();
                for (Object o : treeSelectionEvent.getPath().getPath()) {
                    if (!o.toString().equals("Categories"))
                        treePath.add(o.toString());
                }
                System.out.println(treePath);
            }
        });

////        test downloading
//        start_download("https://dl2.soft98.ir/soft/i/Internet.Download.Manager.6.37.Build.14.Retail.Repack.zip?1591365814", 2);
//        rebuilt_sections(new String[] {"file_section-0.high", "file_section-1.high"}, "file.rar");

        // deploy database
        try {
            db = new DatabaseHelper("high.db");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(6);
        }
    }

    static void addToShowingWindow(Component c) {
        showingWindows.add(c);
    }
}
