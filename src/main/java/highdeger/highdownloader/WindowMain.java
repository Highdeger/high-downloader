package highdeger.highdownloader;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class WindowMain extends JFrame {
    private JPanel mainPanel;
    private JTree mainTree;
    private JScrollPane treeScrollPane;
    private JTable mainTable;
    private JScrollPane tableScrollPane;
    private JButton stopButton;
    private JButton stopAllButton;
    private JButton addButton;
    private JButton deleteButton;
    private JButton queueManagerButton;
    private JComboBox<ModelLooksItem> looksComboBox;
    private JScrollPane scrollPaneButtons;
    private DefaultTableModel defaultTableModel;

    public WindowMain(int w, int h) throws HeadlessException {
//        set minimum size of the window and place it at center
        this.setMinimumSize(new Dimension(640, 480));
        this.setSize(new Dimension(w, h));
        this.setPreferredSize(new Dimension(w, h));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLocation((screenSize.width - this.getWidth()) / 2, (screenSize.height - this.getHeight()) / 2);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here

//        create table model for attaching to the table
        this.defaultTableModel = new DefaultTableModel();
//        create custom table
        this.mainTable = new JTable(this.defaultTableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component component = super.prepareRenderer(renderer, row, column);

//                get width of content inside the component of the cell
                int rendererWidth = component.getPreferredSize().width;

//                get the header component of the column
                TableColumn tableColumn = getColumnModel().getColumn(column);
                Object tableColumnValue = tableColumn.getHeaderValue();
                TableCellRenderer tableColumnRenderer = tableColumn.getHeaderRenderer();
                if (tableColumnRenderer == null)
                    tableColumnRenderer = mainTable.getTableHeader().getDefaultRenderer();
                Component tableColumnRendererComponent = tableColumnRenderer.getTableCellRendererComponent(mainTable, tableColumnValue, false, false, -1, column);

//                choose max between width of cell content and column header
                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumnRendererComponent.getPreferredSize().width + 20));
//                choose max between width of cell content and column preferred width
//                tableColumn.setPreferredWidth(Math.max(rendererWidth + getIntercellSpacing().width, tableColumn.getPreferredWidth()));

                return component;
            }
        };

//        add columns to model
        this.defaultTableModel.addColumn("#");
        this.defaultTableModel.addColumn("Filename");
        this.defaultTableModel.addColumn("Queue");
        this.defaultTableModel.addColumn("Size");
        this.defaultTableModel.addColumn("Status");
        this.defaultTableModel.addColumn("Time left");
        this.defaultTableModel.addColumn("Rate");
        this.defaultTableModel.addColumn("Add Date");
        this.defaultTableModel.addColumn("Last Try Date");
        this.defaultTableModel.addColumn("Description");
        this.defaultTableModel.addColumn("Save To");
        this.defaultTableModel.addColumn("Referer");

//        create and add default value of the first row to the model
        String[][] rowData = new String[1][12];
        for (int i = 0; i < 11; i++) { rowData[0][i] = " "; }
        this.addRowToMainTable(0, rowData[0]);

//        create nodes for main tree
        DefaultMutableTreeNode nodeAll = new DefaultMutableTreeNode("All");
        DefaultMutableTreeNode nodeAllCompressed = new DefaultMutableTreeNode("Compressed");
        DefaultMutableTreeNode nodeAllDocument = new DefaultMutableTreeNode("Document");
        DefaultMutableTreeNode nodeAllAudio = new DefaultMutableTreeNode("Audio");
        DefaultMutableTreeNode nodeAllVideo = new DefaultMutableTreeNode("Video");
        DefaultMutableTreeNode nodeAllProgram = new DefaultMutableTreeNode("Program");
        DefaultMutableTreeNode nodeAllMisc = new DefaultMutableTreeNode("Misc");
        nodeAll.add(nodeAllCompressed);
        nodeAll.add(nodeAllDocument);
        nodeAll.add(nodeAllAudio);
        nodeAll.add(nodeAllVideo);
        nodeAll.add(nodeAllProgram);
        nodeAll.add(nodeAllMisc);
        DefaultMutableTreeNode nodeUnfinished = new DefaultMutableTreeNode("Unfinished");
        DefaultMutableTreeNode nodeUnfinishedCompressed = new DefaultMutableTreeNode("Compressed");
        DefaultMutableTreeNode nodeUnfinishedDocument = new DefaultMutableTreeNode("Document");
        DefaultMutableTreeNode nodeUnfinishedAudio = new DefaultMutableTreeNode("Audio");
        DefaultMutableTreeNode nodeUnfinishedVideo = new DefaultMutableTreeNode("Video");
        DefaultMutableTreeNode nodeUnfinishedProgram = new DefaultMutableTreeNode("Program");
        DefaultMutableTreeNode nodeUnfinishedMisc = new DefaultMutableTreeNode("Misc");
        nodeUnfinished.add(nodeUnfinishedCompressed);
        nodeUnfinished.add(nodeUnfinishedDocument);
        nodeUnfinished.add(nodeUnfinishedAudio);
        nodeUnfinished.add(nodeUnfinishedVideo);
        nodeUnfinished.add(nodeUnfinishedProgram);
        nodeUnfinished.add(nodeUnfinishedMisc);
        DefaultMutableTreeNode nodeFinished = new DefaultMutableTreeNode("Finished");
        DefaultMutableTreeNode nodeFinishedCompressed = new DefaultMutableTreeNode("Compressed");
        DefaultMutableTreeNode nodeFinishedDocument = new DefaultMutableTreeNode("Document");
        DefaultMutableTreeNode nodeFinishedAudio = new DefaultMutableTreeNode("Audio");
        DefaultMutableTreeNode nodeFinishedVideo = new DefaultMutableTreeNode("Video");
        DefaultMutableTreeNode nodeFinishedProgram = new DefaultMutableTreeNode("Program");
        DefaultMutableTreeNode nodeFinishedMisc = new DefaultMutableTreeNode("Misc");
        nodeFinished.add(nodeFinishedCompressed);
        nodeFinished.add(nodeFinishedDocument);
        nodeFinished.add(nodeFinishedAudio);
        nodeFinished.add(nodeFinishedVideo);
        nodeFinished.add(nodeFinishedProgram);
        nodeFinished.add(nodeFinishedMisc);
        DefaultMutableTreeNode nodeQueues = new DefaultMutableTreeNode("Queues");
        DefaultMutableTreeNode nodeQueuesMain = new DefaultMutableTreeNode("Main");
        nodeQueues.add(nodeQueuesMain);
        DefaultMutableTreeNode nodeCategories = new DefaultMutableTreeNode("Categories");
        nodeCategories.add(nodeAll);
        nodeCategories.add(nodeUnfinished);
        nodeCategories.add(nodeFinished);
        nodeCategories.add(nodeQueues);
//        create table with the root node
        this.mainTree = new JTree(nodeCategories);
    }

    JPanel getMainPanel() {
        return mainPanel;
    }

    JTable getMainTable() {
        return mainTable;
    }

    JTree getMainTree() {
        return mainTree;
    }

    JScrollPane getTableScrollPane() {
        return tableScrollPane;
    }

    JScrollPane getTreeScrollPane() {
        return treeScrollPane;
    }

    JComboBox<ModelLooksItem> getLooksComboBox() {
        return looksComboBox;
    }

    public JScrollPane getScrollPaneButtons() {
        return scrollPaneButtons;
    }

    public JButton getAddButton() {
        return addButton;
    }

    /**
     * add a row to the main table
     * @param index int: index of where to add the new row
     * @param row String[]: array of data to be inserted in the specified index
     */
    void addRowToMainTable(int index, String[] row) throws IllegalArgumentException {
        if (row.length == this.defaultTableModel.getColumnCount()) {
            this.defaultTableModel.insertRow(index, row);
        } else {
            throw new IllegalArgumentException("Not enough values for the cells in a row.");
        }
    }

    /**
     * update a cell in the main table
     * @param row_index int:
     * @param col_index int:
     * @param fresh String: the new content to be put in the cell
     */
    void updateCellInMainTable(int row_index, int col_index, String fresh) throws IllegalArgumentException {
        if ((col_index < this.defaultTableModel.getColumnCount()) && (row_index < this.defaultTableModel.getRowCount())) {
            this.defaultTableModel.setValueAt(fresh, row_index, col_index);
        } else {
            throw new IllegalArgumentException("One of row or column indexes is out of bound of the table.");
        }
    }

    /**
     * clear all rows in the main table
     */
    void clearMainTable() {
        for (int i = this.defaultTableModel.getRowCount() - 1; i >= 0; i--)
            this.defaultTableModel.removeRow(i);
    }
}
