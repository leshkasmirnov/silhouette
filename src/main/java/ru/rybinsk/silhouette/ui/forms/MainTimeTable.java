/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.forms;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.binding.MyCellRenderer;
import ru.rybinsk.silhouette.binding.SimulatorTableModel;
import ru.rybinsk.silhouette.dao.SimulatorMapper;
import ru.rybinsk.silhouette.dao.TimeTableMapper;
import ru.rybinsk.silhouette.internal.DurationType;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.model.Simulator;
import ru.rybinsk.silhouette.model.SimulatorExample;
import ru.rybinsk.silhouette.model.TimeTable;
import ru.rybinsk.silhouette.model.TimeTableExample;
import ru.rybinsk.silhouette.pojo.StateItem;
import ru.rybinsk.silhouette.settings.ApplicationHolder;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.ui.components.CellSpanTable;
import ru.rybinsk.silhouette.ui.components.ConfirmDialog;
import ru.rybinsk.silhouette.util.Formatter;
import ru.rybinsk.silhouette.util.Logger;
import ru.rybinsk.silhouette.util.TableColumnHider;

import com.toedter.calendar.JDateChooser;

/**
 * Главное расписание.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class MainTimeTable extends JPanel implements ChangeListener, PropertyChangeListener, MouseListener,
        ActionListener, ItemListener {

    private static final String CLIENT_INFO_COMMAND = "clientInfoCommand";

    private static final String RESET_FILTER = "resetFilter";

    private static final long serialVersionUID = -905694845666316988L;

    private static final String TIME_SPINNER_TO = "timeSpinnerTo";
    private static final String TIME_SPINNER_FROM = "timeSpinnerFrom";
    private static final int FILTERS_SIZE = 4;
    private static final String CANCEL_COMMAND = "cancel";
    private static final String MAKE_COMMAND = "make";

    private String[] timeSpinnerFromSteps;
    private String[] timeSpinnerToSteps;
    private String[] tableColumnNames;
    private Map<String, Integer> spinnerValues = new HashMap<String, Integer>();
    private Map<String, Integer> tableColumnsMap = new HashMap<String, Integer>();
    private String previousSpinnerFromValue;
    private String previousSpinnerToValue;
    private int timeSpinnerToStepsSize;

    private TableColumnHider tableColumnHider;
    private JSpinner timeSpinnerFrom;
    private JSpinner timeSpinnerTo;
    private CellSpanTable timeTable;
    private JDateChooser date;

    private SimulatorTableModel tableModel;
    private PersonalData pd;

    private int selectedRow;
    private int selectedColumn;
    private int spinnerFromOffset;

    private List<Simulator> simulatorList;
    private Map<Integer, Integer> filterMap = new HashMap<Integer, Integer>();

    private JPanel filterPanel;
    private Color hsbColor;

    private static final int MIN_WIDTH = 15;
    private static final int MAX_WIDTH = 21000;
    private static final int PREF_WIDTH = 75;

    private Set<Integer> simulatorIdsSet;
    private Map<Integer, JComboBox<?>> filtersComboboxMap = new HashMap<Integer, JComboBox<?>>();

    // соответствие между идентификаторами тренажеров и индексами элементов кобобокса
    private Map<Integer, Integer> comboboxIndexMapping = new HashMap<Integer, Integer>();

    private boolean clientTimeTable;

    private StateItem[] simulatorsMetadataCache;

    public MainTimeTable(Set<Integer> simulatorIdsSet) {
        this.simulatorIdsSet = simulatorIdsSet;
        setLayout(new MigLayout());
        initTimeSpinnersSteps();
        paint();
    }

    private void initFilterMap() {
        filterMap.put(0, 0);
        filterMap.put(1, 0);
        filterMap.put(2, 0);
        filterMap.put(3, 0);
    }

    private void initTimeSpinnersSteps() {
        int tableColumnNamesSize = 56;
        int timeSpinnerFromStepsSize = 14;
        timeSpinnerToStepsSize = 15;
        tableColumnNames = new String[tableColumnNamesSize];
        timeSpinnerFromSteps = new String[timeSpinnerFromStepsSize];
        timeSpinnerToSteps = new String[timeSpinnerToStepsSize];
        int j = 0;
        int k = 0;
        for (int i = 8; i < 22; i++) {
            String val = i + ":" + "00";
            tableColumnNames[j] = val;
            timeSpinnerFromSteps[k] = val;
            timeSpinnerToSteps[k] = val;
            spinnerValues.put(val, k);
            tableColumnsMap.put(val, j);
            j++;
            tableColumnNames[j] = i + ":" + "15";
            tableColumnsMap.put(i + ":" + "15", j);
            j++;
            tableColumnNames[j] = i + ":" + "30";
            tableColumnsMap.put(i + ":" + "30", j);
            j++;
            tableColumnNames[j] = i + ":" + "45";
            tableColumnsMap.put(i + ":" + "45", j);
            j++;
            k++;

        }
        timeSpinnerToSteps[k] = 22 + ":" + "00";
        spinnerValues.put(22 + ":" + "00", k);
    }

    private void paint() {
        JLabel dateLabel = new JLabel("Дата: ");
        JLabel timeLabel = new JLabel("Время: ");
        date = new JDateChooser(new Date());
        SpinnerModel spinnerModelFrom = new SpinnerListModel(timeSpinnerFromSteps);
        SpinnerModel spinnerModelto = new SpinnerListModel(timeSpinnerToSteps);
        timeSpinnerFrom = new JSpinner(spinnerModelFrom);
        timeSpinnerTo = new JSpinner(spinnerModelto);
        timeTable = new CellSpanTable();
        JScrollPane scrolPane = new JScrollPane(timeTable);
        // JTable rowTable = new SimulatorRowNumberTable(timeTable);
        // scrolPane.setRowHeaderView(rowTable);
        // scrolPane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

        date.setDateFormatString(Formatter.SIMPLE_DATE_FORMAT);
        timeTable.setColumnSelectionAllowed(false);
        timeTable.setRowSelectionAllowed(false);
        timeTable.setCellSelectionEnabled(true);
        timeTable.addMouseListener(this);
        Dimension dt = scrolPane.getSize();
        dt.width = 1095;
        dt.height = 800;
        scrolPane.setPreferredSize(dt);
        timeTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrolPane.setAutoscrolls(true);
        simulatorList = getAllSimulators();
        tableModel = new SimulatorTableModel(tableColumnNames, simulatorList, date.getDate());

        timeTable.setModel(tableModel);
        timeTable.setRowHeight(30);
        float[] hsbColors = new float[3];
        Color.RGBtoHSB(51, 153, 255, hsbColors);
        hsbColor = Color.getHSBColor(hsbColors[0], hsbColors[1], hsbColors[2]);
        timeTable.setDefaultRenderer(Object.class, new MyCellRenderer(hsbColor));

        timeTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        tableColumnHider = new TableColumnHider(timeTable);

        timeSpinnerFrom.addChangeListener(this);
        // ((DefaultEditor) timeSpinnerFrom.getEditor()).getTextField().setEditable(false);
        timeSpinnerTo.addChangeListener(this);
        // ((DefaultEditor) timeSpinnerTo.getEditor()).getTextField().setEditable(false);
        timeSpinnerFrom.setName(TIME_SPINNER_FROM);
        timeSpinnerTo.setName(TIME_SPINNER_TO);
        date.getDateEditor().addPropertyChangeListener(this);

        // set the preferred size
        Dimension d = timeSpinnerFrom.getPreferredSize();
        d.width = 50;
        timeSpinnerFrom.setPreferredSize(d);
        timeSpinnerTo.setPreferredSize(d);

        filterPanel = generateFilterPanel();

        add(dateLabel);
        add(date);
        add(timeLabel, "gapleft 30");
        add(timeSpinnerFrom, "cell 2 0");
        add(timeSpinnerTo, "cell 2 0, gapleft 10");
        add(filterPanel, "cell 0 1, span 2");
        add(scrolPane, "cell 2 1, gapleft 30, gaptop 20, span");

        previousSpinnerFromValue = (String) timeSpinnerFrom.getValue();
        previousSpinnerToValue = timeSpinnerToSteps[timeSpinnerToStepsSize - 1];
        timeSpinnerTo.setValue(timeSpinnerToSteps[timeSpinnerToStepsSize - 1]);

        if (simulatorIdsSet != null) {
            if (simulatorIdsSet.size() > 0 && simulatorIdsSet.size() <= 4) {
                int key = 0;
                for (Integer integer : simulatorIdsSet) {
                    if (comboboxIndexMapping.containsKey(integer)) {
                        filtersComboboxMap.get(key).setSelectedIndex(comboboxIndexMapping.get(integer));
                    }
                    key++;
                }
            }
        }
    }

    private List<Simulator> getAllSimulators() {
        SimulatorMapper sm = DbSessionManager.getMapper(SimulatorMapper.class);
        SimulatorExample se = new SimulatorExample();
        se.setOrderByClause("Ord");
        se.createCriteria().andActivityEqualTo(Boolean.TRUE);

        // фильтрация по тренажерам
        List<Simulator> resultList = null;
        Set<Simulator> resultSet = new LinkedHashSet<Simulator>();
        for (Integer simulatorId : filterMap.values()) {
            if (simulatorId != 0) {
                // resultList.add(sm.selectByPrimaryKey(simulatorId));
                resultSet.add(sm.selectByPrimaryKey(simulatorId));
            }
        }
        if (resultSet.isEmpty()) {
            resultList = sm.selectByExample(se);
        } else {
            resultList = new ArrayList<Simulator>();
            resultList.addAll(resultSet);
        }

        DbSessionManager.closeSession();
        return resultList;
    }

    private JPanel generateFilterPanel() {
        JPanel panel = new JPanel(new MigLayout());
        JButton resetFilterButton = new JButton("Отменить фильтр");

        panel.setBorder(BorderFactory.createTitledBorder("Услуги"));
        Dimension filterPanelDimension = new Dimension(150, 228);
        panel.setPreferredSize(filterPanelDimension);
        resetFilterButton.setActionCommand(RESET_FILTER);
        resetFilterButton.addActionListener(this);

        for (int i = 0; i < FILTERS_SIZE; i++) {
            JComboBox<StateItem> filter = new JComboBox<StateItem>();
            for (StateItem item : getSimulatorsMetaDataForComboBox()) {
                filter.addItem(item);
            }
            if (filterMap.containsKey(i)) {
                filter.setSelectedIndex(comboboxIndexMapping.get(filterMap.get(i)));
            }
            filter.addItemListener(this);
            filter.setName(Integer.toString(i));
            if (i == 0) {
                panel.add(filter, "center");
            } else {
                panel.add(filter, "cell 0 " + i + ", gaptop 15, center");
            }
            filtersComboboxMap.put(i, filter);
        }
        panel.add(resetFilterButton, "cell 0 " + FILTERS_SIZE + 1 + ", gaptop 15, center");
        return panel;
    }

    private StateItem[] getSimulatorsMetaDataForComboBox() {
        if (simulatorsMetadataCache == null) {
            simulatorsMetadataCache = new StateItem[getAllSimulators().size() + 1];
            simulatorsMetadataCache[0] = new StateItem(0, "");
            int i = 1;
            for (Simulator item : getAllSimulators()) {
                simulatorsMetadataCache[i] = new StateItem(item.getId(), item.getName());
                i++;
            }
            // если ещё не заполняли соответствие между индексами фильтров и объектами StateItem, заполняем
            if (comboboxIndexMapping.isEmpty()) {
                int index = 0;
                for (StateItem stateItem : simulatorsMetadataCache) {
                    comboboxIndexMapping.put(stateItem.getId(), index);
                    index++;
                }
            }
        }
        return simulatorsMetadataCache;
    }

    private void spinnerFromStateChanged() {
        int prevFrom = spinnerValues.get(previousSpinnerFromValue);
        int curFrom = spinnerValues.get(timeSpinnerFrom.getValue());
        int curState = prevFrom - curFrom;

        if (curState < 0) {
            curState = Math.abs(curState);
            // hide columns
            int tableColumnIndex = tableColumnsMap.get(previousSpinnerFromValue);
            for (int i = tableColumnIndex; i < tableColumnsMap.get(timeSpinnerFrom.getValue()); i++) {
                // tableColumnHider.hide(tableColumnNames[i]);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMaxWidth(0);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMinWidth(0);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setPreferredWidth(0);
            }
        } else if (curState > 0) {
            // show columns
            int tableColumnIndex = tableColumnsMap.get(previousSpinnerFromValue);
            for (int i = tableColumnIndex; i >= tableColumnsMap.get(timeSpinnerFrom.getValue()); i--) {
                // tableColumnHider.show(tableColumnNames[i]);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMaxWidth(MAX_WIDTH);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMinWidth(MIN_WIDTH);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setPreferredWidth(PREF_WIDTH);
            }

        }

    }

    private void spinnerToStateChanged() {
        int prevTo = spinnerValues.get(previousSpinnerToValue);
        int curTo = spinnerValues.get(timeSpinnerTo.getValue());
        int curState = prevTo - curTo;
        // System.out.println("-----------------");
        if (curState < 0) {
            // show columns
            // curState = Math.abs(curState);
            String firstKey = (String) (timeSpinnerTo.getValue().equals("22:00") ? "21:45" : timeSpinnerTo.getValue());
            String lastKey = previousSpinnerToValue;
            int firstIndex = tableColumnsMap.get(firstKey);
            int lastIndex = tableColumnsMap.get(lastKey);
            for (int i = firstIndex; i >= lastIndex; i--) {
                if (firstKey.contains(":00") && i == firstIndex) {
                    continue;
                }
                // System.out.println("Show column: " + tableColumnNames[i]);
                // tableColumnHider.show(tableColumnNames[i]);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMaxWidth(MAX_WIDTH);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMinWidth(MIN_WIDTH);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setPreferredWidth(PREF_WIDTH);
            }
        } else if (curState > 0) {
            // hide columns

            String firstKey = (String) timeSpinnerTo.getValue();
            String lastKey = previousSpinnerToValue.equals("22:00") ? "21:45" : previousSpinnerToValue;
            int firstIndex = tableColumnsMap.get(firstKey);
            int lastIndex = tableColumnsMap.get(lastKey);
            for (int i = firstIndex; i <= lastIndex; i++) {
                // if (i == lastIndex && lastKey.contains(":00")) {
                // continue;
                // }
                // System.out.println("Hide column: " + tableColumnNames[i]);

                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMaxWidth(0);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setMinWidth(0);
                timeTable.getColumnModel().getColumn(timeTable.getColumnModel().getColumnIndex(tableColumnNames[i]))
                        .setPreferredWidth(0);
                // tableColumnHider.hide(tableColumnNames[i]);
            }
        }
    }

    private void makeCommand() {
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        TimeTable timeTable = new TimeTable();
        timeTable.setDate(date.getDate());
        timeTable.setTime(tableColumnNames[getSelectedColumn() - 1]);
        timeTable.setPersonalDataId(pd.getId());
        timeTable.setSimulatorId(tableModel.getSimulators().get(selectedRow).getId());
        ttMapper.insert(timeTable);
        DbSessionManager.commit();
        DbSessionManager.closeSession();
        Logger.log("Клиент с ID [" + pd.getId() + "] записан на " + Formatter.formatDate(date.getDate()) + " "
                + tableColumnNames[getSelectedColumn() - 1] + " Тренажер ["
                + tableModel.getSimulators().get(selectedRow).getName() + "].");
        repaint();
        revalidate();
    }

    private void cancelCommand() {
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);

        TimeTableExample ttExample = new TimeTableExample();
        ttExample.createCriteria().andDateEqualTo(date.getDate())
                .andTimeEqualTo(tableColumnNames[getSelectedColumn() - 1]).andPersonalDataIdEqualTo(pd.getId())
                .andSimulatorIdEqualTo(tableModel.getSimulators().get(selectedRow).getId());
        List<TimeTable> list = ttMapper.selectByExample(ttExample);
        for (TimeTable timeTable : list) {
            ttMapper.deleteByPrimaryKey(timeTable.getId());
        }

        DbSessionManager.commit();
        DbSessionManager.closeSession();
        Logger.log("Занятие клиента с ID [" + pd.getId() + "] " + Formatter.formatDate(date.getDate()) + " "
                + tableColumnNames[getSelectedColumn() - 1] + " Тренажер ["
                + tableModel.getSimulators().get(selectedRow).getName() + "] отменено.");
        repaint();
        revalidate();
    }

    private void clientInfoCommand() {
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);

        TimeTableExample ttExample = new TimeTableExample();
        ttExample.createCriteria().andDateEqualTo(date.getDate())
                .andTimeEqualTo(tableColumnNames[getSelectedColumn() - 1])
                .andSimulatorIdEqualTo(tableModel.getSimulators().get(selectedRow).getId());
        List<TimeTable> list = ttMapper.selectByExample(ttExample);
        // предполагаем что на одно время может быть записан 1 клиент
        if (!list.isEmpty()) {
            PersonalData pd = list.get(0).getPersonalData();
            JDialog dialog = new JDialog(ApplicationHolder.getMainFrame());
            dialog.setLayout(new MigLayout());
            dialog.setModal(true);
            ClientSpecificationPanel specificationPanel = new ClientSpecificationPanel(pd);
            dialog.add(specificationPanel, "span, cell 0 1");
            dialog.setTitle("Информация о клиенте - " + pd.getFio());
            dialog.pack();
            dialog.setLocationRelativeTo(ApplicationHolder.getMainFrame());
            dialog.setVisible(true);
            dialog.addWindowListener(new WindowListener() {

                @Override
                public void windowOpened(WindowEvent arg0) {

                }

                @Override
                public void windowIconified(WindowEvent arg0) {

                }

                @Override
                public void windowDeiconified(WindowEvent arg0) {

                }

                @Override
                public void windowDeactivated(WindowEvent arg0) {

                }

                @Override
                public void windowClosing(WindowEvent arg0) {
                }

                @Override
                public void windowClosed(WindowEvent arg0) {
                    timeTable.repaint();
                    timeTable.revalidate();
                }

                @Override
                public void windowActivated(WindowEvent arg0) {
                }
            });
        }
    }

    @SuppressWarnings("unchecked")
    private void resetFilter() {
        for (Component component : filterPanel.getComponents()) {
            if (component instanceof JComboBox<?>) {
                JComboBox<StateItem> cmbx = (JComboBox<StateItem>) component;
                cmbx.setSelectedIndex(0);
            }
        }
        initFilterMap();
        tableModel.setSimulators(getAllSimulators());
        timeTable.repaint();
        timeTable.revalidate();
    }

    public void stateChanged(ChangeEvent event) {
        if (event.getSource() instanceof JSpinner) {
            if (spinnerValues.get(timeSpinnerTo.getValue()) <= spinnerValues.get(timeSpinnerFrom.getValue())) {
                timeSpinnerFrom.setValue(previousSpinnerFromValue);
                timeSpinnerTo.setValue(previousSpinnerToValue);
                return;
            }
            JSpinner spinner = (JSpinner) event.getSource();

            if (spinner.getName().equals(TIME_SPINNER_FROM)) {

                // spinnerFromOffset = tableColumnsMap.get(timeSpinnerFrom.getValue());
                spinnerFromStateChanged();
            } else if (spinner.getName().equals(TIME_SPINNER_TO)) {
                spinnerToStateChanged();
            }

            previousSpinnerFromValue = (String) timeSpinnerFrom.getValue();
            previousSpinnerToValue = (String) timeSpinnerTo.getValue();
        }

    }

    public void propertyChange(PropertyChangeEvent event) {
        tableModel.setDate(date.getDate());
        timeTable.repaint();
        timeTable.revalidate();
    }

    public void makeClientTimeTable(PersonalData pd, Date date, boolean editing) {
        this.pd = pd;

        if (date != null) {
            this.date.setDate(date);
        }
        timeTable.setDefaultRenderer(Object.class, new MyCellRenderer(hsbColor, pd));
        clientTimeTable = true;
    }

    public void mouseClicked(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseReleased(MouseEvent event) {
        Point point = event.getPoint();
        selectedRow = timeTable.rowAtPoint(point);
        selectedColumn = timeTable.columnAtPoint(point);
        if (selectedColumn < 1) {
            return;
        }
        if (selectedRow < 0) {
            return;
        }

        if (event.isPopupTrigger() && event.getComponent() instanceof JTable) {
            JPopupMenu popup = new JPopupMenu();
            if (clientTimeTable) {
                JMenuItem makeItem = new JMenuItem("Записать");
                JMenuItem cancelItem = new JMenuItem("Отменить");
                makeItem.addActionListener(this);
                makeItem.setActionCommand(MAKE_COMMAND);
                cancelItem.addActionListener(this);
                cancelItem.setActionCommand(CANCEL_COMMAND);
                popup.add(makeItem);
                popup.add(cancelItem);

                if (!tableModel.getValueAt(selectedRow, getSelectedColumn()).equals("")) {
                    makeItem.setEnabled(false);
                } else {
                    cancelItem.setEnabled(false);
                }
            } else {
                JMenuItem clientInfoItem = new JMenuItem("Информация о клиенте");
                clientInfoItem.addActionListener(this);
                clientInfoItem.setActionCommand(CLIENT_INFO_COMMAND);
                popup.add(clientInfoItem);
                if (tableModel.getValueAt(selectedRow, getSelectedColumn()).equals("")) {
                    clientInfoItem.setEnabled(false);
                }
            }

            popup.show(event.getComponent(), event.getX(), event.getY());
        }
    }

    private int getSelectedColumn() {
        int column = selectedColumn;
        DurationType durationType = DurationType
                .forValue(tableModel.getSimulators().get(selectedRow).getDurationType());
        switch (durationType) {
        case Half:
            while (column != 1 && (column % 2 == 0)) {
                column--;
            }
            break;

        case Whole:
            while (column != 1 && ((column % 2 == 0) || (column % 4 != 1))) {
                column--;
            }
            break;
        default:
            break;
        }
        return column;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(MAKE_COMMAND)) {
            makeCommand();
        } else if (event.getActionCommand().equals(CANCEL_COMMAND)) {
            ConfirmDialog confirmDialog = new ConfirmDialog("Отменить занятие ?");
            confirmDialog.setVisible(true);
            if (confirmDialog.getAnswer()) {
                cancelCommand();
            }
        } else if (event.getActionCommand().equals(CLIENT_INFO_COMMAND)) {
            clientInfoCommand();
        } else if (event.getActionCommand().equals(RESET_FILTER)) {
            resetFilter();
        }
    }

    @SuppressWarnings("unchecked")
    public void itemStateChanged(ItemEvent event) {
        if (event.getStateChange() == 1) {
            if (event.getSource() instanceof JComboBox<?>) {
                JComboBox<StateItem> combobox = (JComboBox<StateItem>) event.getSource();
                StateItem stateItem = (StateItem) event.getItem();
                filterMap.put(Integer.parseInt(combobox.getName()), stateItem.getId());
            }
            tableModel.setSimulators(getAllSimulators());
            timeTable.repaint();
            timeTable.revalidate();
        }
    }
}
