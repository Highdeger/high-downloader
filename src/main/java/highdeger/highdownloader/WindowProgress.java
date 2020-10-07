package highdeger.highdownloader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class WindowProgress extends JFrame{
    private JTabbedPane tabbedPane;
    private JProgressBar progressBar;
    private JButton hideDetailsButton;
    private JButton cancelButton;
    private JButton pauseButton;
    private JTable connectionsTable;
    private JPanel mainPanel;
    private JLabel labelUrl;
    private JLabel labelStatus;
    private JLabel labelTotalSize;
    private JLabel labelDownloadedSize;
    private JLabel labelTransferRate;
    private JLabel labelTimeLeft;
    private JLabel labelResume;
    private DefaultTableModel defaultTableModel;

    public WindowProgress(int w, int h) throws HeadlessException {
//        set minimum size of the window and place it at center
        this.setMinimumSize(new Dimension(600, 414));
        this.setSize(new Dimension(w, h));
        this.setPreferredSize(new Dimension(w, h));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

        this.mainPanel = new JPanel();

//        create table model and attach to the table
        defaultTableModel = new DefaultTableModel();
        this.connectionsTable = new JTable(defaultTableModel);

//        add columns to model
        defaultTableModel.addColumn("#");
        defaultTableModel.addColumn("Downloaded");
        defaultTableModel.addColumn("Status");

//        create and add default value of the first row to the model
        String[][] rowData = new String[1][3];
        rowData[0][0] = "-";
        rowData[0][1] = "-";
        rowData[0][2] = "End of Connections";
        this.addRowToConnectionsTable(0, rowData[0]);

//        edit columns
        this.connectionsTable.getColumnModel().getColumn(0).setMaxWidth(28);
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    JButton getCancelButton() {
        return cancelButton;
    }

    JButton getPauseButton() {
        return pauseButton;
    }

    JButton getHideDetailsButton() {
        return hideDetailsButton;
    }

    JProgressBar getProgressBar() {
        return progressBar;
    }

    public JTable getConnectionsTable() {
        return connectionsTable;
    }

    public JLabel getLabelUrl() {
        return labelUrl;
    }

    public JLabel getLabelStatus() {
        return labelStatus;
    }

    public JLabel getLabelTotalSize() {
        return labelTotalSize;
    }

    public JLabel getLabelDownloadedSize() {
        return labelDownloadedSize;
    }

    public JLabel getLabelTransferRate() {
        return labelTransferRate;
    }

    public JLabel getLabelTimeLeft() {
        return labelTimeLeft;
    }

    public JLabel getLabelResume() {
        return labelResume;
    }

//    add row to the model from outside
//    IllegalArgumentException <= if row.length doesn't match columns quantity
    void addRowToConnectionsTable(int index, String[] row) throws IllegalArgumentException {
        if (row.length == defaultTableModel.getColumnCount()) {
            defaultTableModel.insertRow(index, row);
        } else {
            throw new IllegalArgumentException("Not enough values for the cells in a row.");
        }
    }
    
    void clearRowsInConnectionTable() {
        for (int i = defaultTableModel.getRowCount() - 1; i >= 0; i--) {
            defaultTableModel.removeRow(i);
        }
    }

    void editCellAt(int r, int c, String fresh) {
        defaultTableModel.setValueAt(fresh, r, c);
    }
}
