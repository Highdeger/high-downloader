package highdeger.highdownloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

class HighUI {
    
    static int currentLookIndex = -1;

    /**
     * fetch all UI libraries supported on the OS and select the first one.
     * @return ModelLooksItem[]: UI libraries found on OS
     */
    static ModelLooksItem[] initUi() {
        String[] looks, looks_name;
        ModelLooksItem[] modelLooksItems;

        UIManager.LookAndFeelInfo[] looks_info = UIManager.getInstalledLookAndFeels();
        looks = new String[looks_info.length];
        looks_name = new String[looks_info.length];
        modelLooksItems = new ModelLooksItem[looks_info.length];

        for (int i = 0; i < looks_info.length; i++) {
            String temp = looks_info[i].getClassName();
            looks[i] = temp;

            if (temp.contains(".")) {
                String[] t1 = temp.split("\\.");
                if (t1.length > 0)
                    temp = t1[t1.length - 1];
            }
            if (temp.endsWith("LookAndFeel"))
                temp = temp.substring(0, temp.length() - 11);
            looks_name[i] = temp;

            modelLooksItems[i] = new ModelLooksItem(looks_name[i], looks[i]);
        }

        currentLookIndex = 0;
        changeLook(looks[currentLookIndex]);

        return modelLooksItems;
    }

    /**
     * change UI library to string id of the look.
     * @param look String: id of the look
     */
    private static void changeLook(String look) {
        try {
            UIManager.setLookAndFeel(look);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    /**
     * attach the comboBox to the list of UI libraries.
     * @param comboBox JComboBox: the comboBox
     * @param winsToRefresh Component[]: all the windows which are showing
     * @param modelLooksItems ModelLooksItem[]: UI libraries found on OS
     */
    private static void init_looks_combo_box(final JComboBox<ModelLooksItem> comboBox, final Component[] winsToRefresh,
                                             final ModelLooksItem[] modelLooksItems) {
        for (ModelLooksItem modelLooksItem : modelLooksItems) comboBox.addItem(modelLooksItem);

        comboBox.setSelectedIndex(currentLookIndex);
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int itemIndex = comboBox.getSelectedIndex();
                if (itemIndex != -1)
                    currentLookIndex = itemIndex;
                    changeLook(modelLooksItems[currentLookIndex].getValue());
                for (Component w : winsToRefresh)
                    SwingUtilities.updateComponentTreeUI(w);
            }
        });
    }

    /**
     * create instance of WindowMain.
     * @param title String:
     * @param width int:
     * @param height int:
     * @return WindowMain: the instance created
     */
    static WindowMain createMainWindow(String title, int width, int height) {
//        prepare window itself
        WindowMain win = new WindowMain(width, height);
        win.setTitle(title);
        win.setContentPane(win.getMainPanel());
        win.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
                /*
                e.getNewState() values:
                6 fullscreen
                1 minimize
                0 window
                */
            }
        });
        win.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        win.pack();

//        initialize Tasks menu
        JMenu tasksMenu = new JMenu("Tasks");
        JMenuItem createNewDownloadMenuItem = new JMenuItem("Create New Download");
        JMenuItem createBatchUrlDownloadMenuItem = new JMenuItem("Create Batch Download From URL");
        JMenuItem createBatchClipboardDownloadMenuItem = new JMenuItem("Create Batch Download From Clipboard");
        JMenuItem websiteDownloaderMenuItem = new JMenuItem("Website Downloader");
        JMenu exportMenu = new JMenu("Export");
        JMenuItem exportEncryptedMenuItem = new JMenuItem("To High Downloader's Encrypted Format");
        JMenuItem exportTextMenuItem = new JMenuItem("To Text Format");
        JMenu importMenu = new JMenu("Import");
        JMenuItem importEncryptedMenuItem = new JMenuItem("From High Downloader's Encrypted Format");
        JMenuItem importTextMenuItem = new JMenuItem("From Text Format");
        JMenuItem terminateMenuItem = new JMenuItem("Terminate");
        tasksMenu.add(createNewDownloadMenuItem);
        tasksMenu.add(createBatchUrlDownloadMenuItem);
        tasksMenu.add(createBatchClipboardDownloadMenuItem);
        tasksMenu.add(websiteDownloaderMenuItem);
        exportMenu.add(exportEncryptedMenuItem);
        exportMenu.add(exportTextMenuItem);
        tasksMenu.add(exportMenu);
        importMenu.add(importEncryptedMenuItem);
        importMenu.add(importTextMenuItem);
        tasksMenu.add(importMenu);
        tasksMenu.add(terminateMenuItem);

//        initialize Actions menu
        JMenu actionsMenu = new JMenu("Actions");
        JMenuItem resumeMenuItem = new JMenuItem("Resume");
        JMenuItem stopMenuItem = new JMenuItem("Stop");
        JMenuItem stopAllMenuItem = new JMenuItem("Stop All");
        JMenuItem removeMenuItem = new JMenuItem("Delete");
        JMenuItem redownloadMenuItem = new JMenuItem("Re-Download");
        JMenuItem deleteAllCompletedMenuItem = new JMenuItem("Delete All Completed");
        actionsMenu.add(resumeMenuItem);
        actionsMenu.add(stopMenuItem);
        actionsMenu.add(stopAllMenuItem);
        actionsMenu.add(removeMenuItem);
        actionsMenu.add(redownloadMenuItem);
        actionsMenu.add(deleteAllCompletedMenuItem);

//        initialize Options menu
        JMenu optionsMenu = new JMenu("Options");
        JMenu speedLimiterMenu = new JMenu("Speed Limiter");
        JMenuItem limitOnMenuItem = new JMenuItem("On");
        JMenuItem limitOffMenuItem = new JMenuItem("Off");
        JMenuItem limitSettingsMenuItem = new JMenuItem("Settings");
        JMenuItem coreSettingsMenuItem = new JMenuItem("Core Settings");
        speedLimiterMenu.add(limitOnMenuItem);
        speedLimiterMenu.add(limitOffMenuItem);
        speedLimiterMenu.add(limitSettingsMenuItem);
        optionsMenu.add(speedLimiterMenu);
        optionsMenu.add(coreSettingsMenuItem);

//        initialize Start/Stop Queue menu
        JMenu startStopQueueMenu = new JMenu("Start/Stop Queue");

//        MenuBar init & set
//        initialize menu-bar
        JMenuBar mainMenuBar = new JMenuBar();
        mainMenuBar.add(tasksMenu);
        mainMenuBar.add(actionsMenu);
        mainMenuBar.add(optionsMenu);
        win.setJMenuBar(mainMenuBar);

//        make window visible and return it
        win.setVisible(true);
        return win;
    }

    /**
     * create instance of WindowProgress.
     * @param title String:
     * @param width int:
     * @param height int:
     * @return WindowProgress: the instance created
     */
    static WindowProgress createProgressWindow(String title, int width, int height) {
        WindowProgress win = new WindowProgress(width, height);
        win.setTitle(title);
        win.setContentPane(win.getMainPanel());
        win.addWindowStateListener(new WindowStateListener() {
            @Override
            public void windowStateChanged(WindowEvent e) {
//                e.getNewState() (down)
//                6 fullscreen
//                1 minimize
//                0 window
            }
        });
        win.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.pack();

//        set listeners of buttons
        win.getCancelButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("Cancel ckicked.");
            }
        });
        win.getPauseButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("Pause ckicked.");
            }
        });
        win.getHideDetailsButton().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                System.out.println("HideDetails ckicked.");
            }
        });

//        place of listener additions

        win.setVisible(true);
        return win;
    }

    /**
     * create instance of WindowSettings.
     * @param width int:
     * @param height int:
     * @param modelLooksItems ModelLooksItem[]:
     * @param winsToRefresh ArrayList<Component>:
     * @return WindowSettings: the instance created
     */
    static WindowSettings createSettingsWindow(int width, int height, ModelLooksItem[] modelLooksItems,
                                                     ArrayList<Component> winsToRefresh) {
        WindowSettings win = new WindowSettings(width, height);
        win.setTitle("Settings");
        win.setContentPane(win.getMainPanel());
        win.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.pack();

//        set combobox of looks
        JComboBox<ModelLooksItem> looksComboBox = win.getLooksComboBox();
        winsToRefresh.add(win);
        Component[] componentsShowing = winsToRefresh.toArray(new Component[0]);
        init_looks_combo_box(looksComboBox, componentsShowing, modelLooksItems);

        win.setVisible(true);
        return win;
    }

    /**
     * create instance of WindowNewDownload.
     * @param width int:
     * @param height int:
     * @return WindowNewDownload: the instance created
     */
    static WindowNewDownload createNewDownloadWindow(int width, int height) {
        WindowNewDownload win = new WindowNewDownload(width, height);
        win.setTitle("Create New Download");
        win.setContentPane(win.getMainPanel());
        win.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {

            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        win.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        win.pack();

        win.setVisible(true);
        return win;
    }
}
