/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.binding;

import java.util.Date;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import ru.rybinsk.silhouette.dao.TimeTableMapper;
import ru.rybinsk.silhouette.internal.DurationType;
import ru.rybinsk.silhouette.model.Simulator;
import ru.rybinsk.silhouette.model.TimeTable;
import ru.rybinsk.silhouette.model.TimeTableExample;
import ru.rybinsk.silhouette.pojo.CellSpan;
import ru.rybinsk.silhouette.settings.DbSessionManager;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class SimulatorTableModel extends AbstractTableModel implements SpanModel {

    private static final long serialVersionUID = -5713735291002353987L;
    private static final String FIRST_HEADER = "Услуга/время";

    private String[] timeHeaders;
    private List<Simulator> simulators;
    private Date date;
    private int column;

    public SimulatorTableModel(String[] headers, List<Simulator> simulators, Date date) {
        this.timeHeaders = headers;
        this.simulators = simulators;
        this.date = date;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    public int getColumnCount() {
        return timeHeaders.length + 1;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    public int getRowCount() {
        return simulators.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    public Object getValueAt(int rowIndex, int columnIndex) {
        switch (columnIndex) {
        case 0:
            return simulators.get(rowIndex).getName();
        default:
            String header = timeHeaders[columnIndex - 1];
            TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
            TimeTableExample ttExample = new TimeTableExample();
            ttExample.createCriteria().andTimeEqualTo(header).andSimulatorIdEqualTo(simulators.get(rowIndex).getId())
                    .andDateEqualTo(date);
            List<TimeTable> list = ttMapper.selectByExample(ttExample);
            DbSessionManager.closeSession();
            String result = "";
            for (TimeTable timeTable : list) {
                result += timeTable.getPersonalData().getFio();
                if (timeTable.getPersonalData().getSubscription() != null) {
                    result += "/" + timeTable.getPersonalData().getSubscription().getSubscriptionNumber();
                }
                result += "; ";
            }
            if (result.contains("; ")) {
                result = result.substring(0, result.lastIndexOf("; "));
            }

            return result;
        }
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return FIRST_HEADER;
        }
        return timeHeaders[column - 1];
    }

    public CellSpan getCellSpanAt(int rowIndex, int columnIndex) {
        DurationType durationType = DurationType.forValue(simulators.get(rowIndex).getDurationType());
        switch (durationType) {
        case Half:
            if (columnIndex == 1 || (columnIndex % 2 != 0)) {
                column = columnIndex;
            }
            if (columnIndex >= column && columnIndex <= (column + 1)) {
                CellSpan cellSpan = new CellSpan(rowIndex, column, 1, 2);
                return cellSpan;
            }
            break;

        case Whole:
            if (columnIndex == 1 || ((columnIndex % 2 != 0) && (columnIndex % 4 == 1))) {
                column = columnIndex;
            }
            if (columnIndex >= column && columnIndex <= (column + 3)) {
                CellSpan cellSpan = new CellSpan(rowIndex, column, 1, 4);
                return cellSpan;
            }
            break;
        default:
            return null;
        }
        return null;
    }

    public boolean isCellSpanOn() {
        return true;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Simulator> getSimulators() {
        return simulators;
    }

    public void setSimulators(List<Simulator> simulators) {
        this.simulators = simulators;
    }

}
