/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.binding;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ru.rybinsk.silhouette.model.PersonalData;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ClientsTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] headerNames = { "ФИО", "№ Абонемента", "Активен" };
    private List<PersonalData> records;

    public ClientsTableModel(List<PersonalData> records) {
        this.records = records;
    }

    public int getColumnCount() {
        return headerNames.length;
    }

    public int getRowCount() {
        return records.size();
    }

    public Object getValueAt(int rowIndex, int columnIndex) {
        PersonalData pd = records.get(rowIndex);
        switch (columnIndex) {
        case 0:
            return pd.getFio();
        case 1:
            return pd.getSubscription() != null ? pd.getSubscription().getSubscriptionNumber() : "";
        case 2:
            if (pd.getSubscription() != null) {
                if (pd.getSubscription().getState() == 1) {
                    return true;
                }
            }
            return false;
        default:
            return "";
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        if (columnIndex == 2) {
            return Boolean.class;
        }

        return super.getColumnClass(columnIndex);
    }

    @Override
    public String getColumnName(int column) {
        return headerNames[column];
    }

    public List<PersonalData> getRecords() {
        return records;
    }

    public void setRecords(List<PersonalData> records) {
        this.records = new ArrayList<PersonalData>(records);
    }

}
