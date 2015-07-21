/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.components;

import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;

import ru.rybinsk.silhouette.binding.SimulatorTableModel;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class SimulatorRowNumberTable extends JTable implements ChangeListener, PropertyChangeListener {

    private static final long serialVersionUID = -1115590016238270293L;
    private JTable main;

    public SimulatorRowNumberTable(JTable table) {
        main = table;
        main.addPropertyChangeListener(this);

        setFocusable(false);
        setAutoCreateColumnsFromModel(false);
        setModel(main.getModel());
        setSelectionModel(main.getSelectionModel());

        TableColumn column = new TableColumn();
        column.setHeaderValue(" ");
        addColumn(column);
        column.setCellRenderer(new RowNumberRenderer());

        getColumnModel().getColumn(0).setPreferredWidth(120);
        setPreferredScrollableViewportSize(getPreferredSize());
        // setRowHeight(30);
    }

    @Override
    public void addNotify() {
        super.addNotify();

        Component c = getParent();

        // Keep scrolling of the row table in sync with the main table.

        if (c instanceof JViewport) {
            JViewport viewport = (JViewport) c;
            viewport.addChangeListener(this);
        }
    }

    /*
     * Delegate method to main table
     */
    @Override
    public int getRowCount() {
        return main.getRowCount();
    }

    @Override
    public int getRowHeight(int row) {
        return main.getRowHeight(row);
    }

    /*
     * This table does not use any data from the main TableModel, so just return a value based on the row parameter.
     */
    @Override
    public Object getValueAt(int row, int column) {
        SimulatorTableModel stm = (SimulatorTableModel) main.getModel();
        return stm.getSimulators().get(row).getName();
    }

    /*
     * Don't edit data in the main TableModel by mistake
     */
    @Override
    public boolean isCellEditable(int row, int column) {
        return false;
    }

    //
    // Implement the ChangeListener
    //
    public void stateChanged(ChangeEvent e) {
        // Keep the scrolling of the row table in sync with main table

        JViewport viewport = (JViewport) e.getSource();
        JScrollPane scrollPane = (JScrollPane) viewport.getParent();
        scrollPane.getVerticalScrollBar().setValue(viewport.getViewPosition().y);
    }

    //
    // Implement the PropertyChangeListener
    //
    public void propertyChange(PropertyChangeEvent e) {
        // Keep the row table in sync with the main table

        if ("selectionModel".equals(e.getPropertyName())) {
            setSelectionModel(main.getSelectionModel());
        }

        if ("model".equals(e.getPropertyName())) {
            setModel(main.getModel());
        }
    }

    /*
     * Borrow the renderer from JDK1.4.2 table header
     */
    private static class RowNumberRenderer extends DefaultTableCellRenderer {

        private static final long serialVersionUID = 4892023650676563684L;

        public RowNumberRenderer() {
            setHorizontalAlignment(JLabel.LEFT);
        }

        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            if (table != null) {
                JTableHeader header = table.getTableHeader();

                if (header != null) {
                    setForeground(header.getForeground());
                    setBackground(header.getBackground());
                    setFont(header.getFont());
                }
            }

            if (isSelected) {
                setFont(getFont().deriveFont(Font.BOLD));
            }

            setText((value == null) ? "" : value.toString());
            // setBorder(UIManager.getBorder("TableHeader.cellBorder"));

            return this;
        }
    }

}
