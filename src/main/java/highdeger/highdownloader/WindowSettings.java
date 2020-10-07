package highdeger.highdownloader;

import javax.swing.*;
import java.awt.*;

public class WindowSettings extends JFrame {
    private JComboBox<ModelLooksItem> looksComboBox;
    private JPanel mainPanel;

    public WindowSettings(int w, int h) throws HeadlessException {
//        set minimum size of the window and place it at center
        this.setMinimumSize(new Dimension(400, 200));
        this.setSize(new Dimension(w, h));
        this.setPreferredSize(new Dimension(w, h));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
    }

    public JPanel getMainPanel() {
        return mainPanel;
    }

    public JComboBox<ModelLooksItem> getLooksComboBox() {
        return looksComboBox;
    }
}
