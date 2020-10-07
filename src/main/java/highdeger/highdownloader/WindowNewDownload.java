package highdeger.highdownloader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

public class WindowNewDownload extends JFrame {
    private JTextArea newUrl;
    private JButton addButton;
    private JPanel mainPanel;

    public WindowNewDownload(int w, int h) throws HeadlessException {
//        set minimum size of the window and place it at center
        this.setMinimumSize(new Dimension(400, 200));
        this.setSize(new Dimension(w, h));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
        this.setUndecorated(true);
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.setAlwaysOnTop(true);

        this.mainPanel.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
//                mouseEvent.translatePoint(mouseEvent.getComponent().getLocation().x,
//                        mouseEvent.getComponent().getLocation().y);
                moveWindow(mouseEvent.getXOnScreen(), mouseEvent.getYOnScreen());
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        this.newUrl.setLineWrap(true);
        this.newUrl.setWrapStyleWord(true);
    }

    public JPanel getMainPanel() {
        return this.mainPanel;
    }

    public JButton getAddButton() {
        return this.addButton;
    }

    public JTextArea getNewUrl() {
        return this.newUrl;
    }

    private void moveWindow(int x, int y) {
//        int dx = x - this.getX();
//        int dy = y - this.getY();
//        this.setLocation(dx + x, dy + y);
        this.setLocation(x, y);
//        this.repaint();
    }
}
