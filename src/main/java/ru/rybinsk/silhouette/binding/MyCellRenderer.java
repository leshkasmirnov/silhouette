/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.binding;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

import ru.rybinsk.silhouette.model.PersonalData;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class MyCellRenderer extends DefaultTableCellRenderer {

    private static final long serialVersionUID = 3840265207269350120L;
    private Color selectedCellColor;
    private PersonalData pd;

    public MyCellRenderer(Color selectedCellColor) {
        this.selectedCellColor = selectedCellColor;
    }

    public MyCellRenderer(Color selectedCellColor, PersonalData pd) {
        this.selectedCellColor = selectedCellColor;
        this.pd = pd;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
            int row, int column) {
        MyCellRenderer component = (MyCellRenderer) super.getTableCellRendererComponent(table, value, isSelected,
                hasFocus, row, column);
        if (!value.equals("") && value != null && column != 0) {
            component.setToolTipText((String) value);
        } else {
            component.setToolTipText(null);
        }

        if (!value.equals("") && value != null && column != 0 && !isSelected) {
            component.setBackground(Color.LIGHT_GRAY);
            if (pd != null) {
                if (((String) value).contains(pd.getFio())) {
                    component.setBackground(Color.MAGENTA);
                }
            }
        } else {
            if (!isSelected) {
                component.setBackground(Color.WHITE);
            } else {
                component.setBackground(selectedCellColor);
            }
        }

        return component;
    }
}
