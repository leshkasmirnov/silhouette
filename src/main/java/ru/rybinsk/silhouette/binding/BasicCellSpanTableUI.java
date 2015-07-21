/**
 * � Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.binding;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.TableCellRenderer;

import ru.rybinsk.silhouette.pojo.CellSpan;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class BasicCellSpanTableUI extends BasicTableUI {

    public static ComponentUI createUI(JComponent jcomponent) {
        return new BasicCellSpanTableUI();
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        Rectangle oldClipBounds = g.getClipBounds();
        Rectangle clipBounds = new Rectangle(oldClipBounds);
        int tableWidth = table.getColumnModel().getTotalColumnWidth();
        clipBounds.width = Math.min(clipBounds.width, tableWidth);
        g.setClip(clipBounds);

        int firstIndex = table.rowAtPoint(new Point(0, clipBounds.y));
        int lastIndex = table.getRowCount() - 1;

        Rectangle rowRect = new Rectangle(0, 0, tableWidth, table.getRowHeight() + table.getRowMargin());
        rowRect.y = firstIndex * rowRect.height;

        for (int index = firstIndex; index <= lastIndex; index++) {
            if (rowRect.intersects(clipBounds)) {
                paintRow(g, index);
            }
            rowRect.y += rowRect.height;
        }
        g.setClip(oldClipBounds);
    }

    private void paintRow(Graphics g, int row) {
        Rectangle rect = g.getClipBounds();

        SpanModel tableModel = (SpanModel) table.getModel();
        int numColumns = table.getColumnCount();

        for (int column = 0; column < numColumns; column++) {

            CellSpan cellSpan = tableModel.getCellSpanAt(row, column);

            Rectangle cellRect = table.getCellRect(row, column, true);
            int cellRow, cellColumn;
            if (cellSpan == null || (row == cellSpan.getRow() && column == cellSpan.getColumn())) {
                cellRow = row;
                cellColumn = column;
            } else {
                cellRow = cellSpan.getRow();
                cellColumn = cellSpan.getColumn();
            }
            if (cellRect.intersects(rect)) {
                paintCell(g, cellRect, cellRow, cellColumn);
            }
        }

    }

    private void paintCell(Graphics g, Rectangle cellRect, int row, int column) {
        int spacingHeight = table.getRowMargin();
        int spacingWidth = table.getColumnModel().getColumnMargin();

        Color c = g.getColor();
        g.setColor(table.getGridColor());
        g.drawRect(cellRect.x, cellRect.y, cellRect.width, cellRect.height);
        g.setColor(c);

        cellRect.setBounds(cellRect.x + spacingWidth / 2, cellRect.y + spacingHeight / 2,
                cellRect.width - spacingWidth, cellRect.height - spacingHeight);

        if (table.isEditing() && table.getEditingRow() == row && table.getEditingColumn() == column) {
            Component component = table.getEditorComponent();
            component.setBounds(cellRect);
            component.validate();
        } else {
            TableCellRenderer renderer = table.getCellRenderer(row, column);
            Component component = table.prepareRenderer(renderer, row, column);

            if (component.getParent() == null) {
                rendererPane.add(component);
            }
            rendererPane.paintComponent(g, component, table, cellRect.x, cellRect.y, cellRect.width, cellRect.height,
                    true);
        }
    }

}
