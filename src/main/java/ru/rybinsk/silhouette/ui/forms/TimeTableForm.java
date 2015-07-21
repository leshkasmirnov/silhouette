/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.forms;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.print.PrinterException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.dao.AmbryMapper;
import ru.rybinsk.silhouette.dao.PersonalDataMapper;
import ru.rybinsk.silhouette.dao.SubscriptionMapper;
import ru.rybinsk.silhouette.dao.TimeTableMapper;
import ru.rybinsk.silhouette.internal.DurationType;
import ru.rybinsk.silhouette.internal.SubscriptionType;
import ru.rybinsk.silhouette.model.Ambry;
import ru.rybinsk.silhouette.model.AmbryExample;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.model.PersonalDataExample;
import ru.rybinsk.silhouette.model.Simulator;
import ru.rybinsk.silhouette.model.Subscription;
import ru.rybinsk.silhouette.model.SubscriptionExample;
import ru.rybinsk.silhouette.model.TimeTable;
import ru.rybinsk.silhouette.model.TimeTableExample;
import ru.rybinsk.silhouette.pojo.SimulatorTT;
import ru.rybinsk.silhouette.pojo.StateItem;
import ru.rybinsk.silhouette.settings.ApplicationHolder;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.throwable.SilhouetteRuntimeException;
import ru.rybinsk.silhouette.ui.ClientTimeTableDialog;
import ru.rybinsk.silhouette.ui.components.ConfirmDialog;
import ru.rybinsk.silhouette.ui.components.NotifyDialog;
import ru.rybinsk.silhouette.ui.listeners.SaveFormListener;
import ru.rybinsk.silhouette.util.Formatter;
import ru.rybinsk.silhouette.util.Logger;
import ru.rybinsk.silhouette.util.Printer;

import com.toedter.calendar.JDateChooser;

/**
 * Вкладка "Расписание".
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class TimeTableForm extends JPanel implements FocusListener, ActionListener, PropertyChangeListener,
        ItemListener {

    private static final String AMBRY_COMBOBOX = "ambryCombobox";
    private static final long serialVersionUID = 9184078696946650077L;
    private static final String VIEW_ALL_BUTTON = "viewAll";
    private static final String EDIT_PARTICIPATION_BUTTON = "editParticipation";
    private static final String CANCEL_PARTICIPATION_BUTTON = "cancelParticipation";
    private static final String MAKE_PARTICIPATION_BUTTON = "makeParticipation";
    private static final String PRINT_ACT_BUTTON = "printAct";
    private static final String PRINT_TIME_TABLE_BUTTON = "printTimeTable";
    private static final String PRESENT_DEFAULT_TEXT = "№ абонемента дарителя";
    private static final String PRESENT_TEXT_FIELD = "presentTextField";

    private static final String ACT_PRINT_FORM = "Дата: %s\nКлиент: %s\nНомер абонемента: %s\nВид абонемента: %s\nПериод действия абонемента: %s\nЗанятие:\n%s\nСписание: %s\nКоличество занятий: %s\nНомер шкафчика: %s\nПодпись клиента (с информацией ознакомлена, согласна, претензий не имею): ______________.";
    private static final String ACT_PRINT_FORM_PRESENT = "Дата: %s\nКлиент: %s\nНомер абонемента: %s\nВид абонемента: %s\nПериод действия абонемента: %s\nЗанятие:\n%s\nСписание: %s\nЗанятия в подарок от: %s\nКоличество занятий: %s\nКоличество занятий в подарок: %s\nНомер шкафчика: %s\nПодпись клиента (с информацией ознакомлена, согласена, претензий не имею): ______________.";

    private static final String TIME_TABLE_PRINT_FORM_PRESENT = "Дата: %s\nКлиент: %s\nНомер абонемента: %s\nЗанятие:\n%s\nСтоимость услуг: %s\nЗанятия в подарок от: %s";
    private static final String TIME_TABLE_PRINT_FORM = "Дата: %s\nКлиент: %s\nНомер абонемента: %s\nЗанятие:\n%s\nСтоимость услуг: %s";

    private PersonalData personalData;
    private Subscription subscription;
    private JCheckBox present;
    private JTextField presentTextField;
    private JTextField countParticipationTextField;
    private JCheckBox half;

    private Set<SaveFormListener> saveListeners = new HashSet<SaveFormListener>();
    private JDateChooser date;
    private JTextArea timeTable;

    private float total = -1;
    private JButton printAct;
    private JButton cancelParticipation;
    private JComboBox<StateItem> ambry;
    private JButton editParticipation;
    private JButton printTimeTable;

    public TimeTableForm(PersonalData pd) {
        if (pd == null) {
            throw new SilhouetteRuntimeException("Personal Data mast not be null.");
        }
        personalData = pd;
        subscription = pd.getSubscription();
        setLayout(new MigLayout("gap 15px"));
        paint();
    }

    private void paint() {
        JLabel fioLabel = new JLabel(personalData.getFio());
        JLabel dateLabel = new JLabel("Дата: ");
        date = new JDateChooser();
        JButton viewAll = new JButton("Просмотр всех занятий");
        timeTable = new JTextArea(12, 57);
        JScrollPane timeTableScrollPane = new JScrollPane(timeTable);
        half = new JCheckBox("Минус 0,5 занятия");
        present = new JCheckBox("Подарок");
        presentTextField = new JTextField(PRESENT_DEFAULT_TEXT, 17);
        printTimeTable = new JButton("Печать расписания");
        printAct = new JButton("Печать акта");
        JButton makeParticipation = new JButton("Записать на занятие");
        cancelParticipation = new JButton("Отменить запись");
        editParticipation = new JButton("Редактировать запись");
        JLabel keyLabel = new JLabel("Номер шкафчика: ");
        ambry = new JComboBox<StateItem>();

        fillAmbryCombobox();
        ambry.setName(AMBRY_COMBOBOX);
        ambry.addItemListener(this);
        half.addItemListener(this);
        date.getDateEditor().addPropertyChangeListener(this);
        date.setDateFormatString(Formatter.SIMPLE_DATE_FORMAT);
        printTimeTable.addActionListener(this);
        printAct.addActionListener(this);
        makeParticipation.addActionListener(this);
        cancelParticipation.addActionListener(this);
        editParticipation.addActionListener(this);
        viewAll.addActionListener(this);
        printTimeTable.setActionCommand(PRINT_TIME_TABLE_BUTTON);
        printAct.setActionCommand(PRINT_ACT_BUTTON);
        makeParticipation.setActionCommand(MAKE_PARTICIPATION_BUTTON);
        cancelParticipation.setActionCommand(CANCEL_PARTICIPATION_BUTTON);
        editParticipation.setActionCommand(EDIT_PARTICIPATION_BUTTON);
        viewAll.setActionCommand(VIEW_ALL_BUTTON);

        Dimension d = new Dimension(100, 50);
        printAct.setPreferredSize(d);
        printAct.setForeground(Color.RED);
        makeParticipation.setPreferredSize(d);
        cancelParticipation.setPreferredSize(d);
        editParticipation.setPreferredSize(d);
        printTimeTable.setPreferredSize(d);

        presentTextField.addFocusListener(this);
        presentTextField.setForeground(Color.lightGray);
        presentTextField.setName(PRESENT_TEXT_FIELD);
        timeTableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        timeTableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        timeTable.setBorder(presentTextField.getBorder());
        timeTable.setFont(presentTextField.getFont());

        add(fioLabel);
        if (subscription != null) {
            JLabel countParticipationLabel = new JLabel("Занятий: ");

            countParticipationTextField = new JTextField(5);
            if (subscription.getParticipationNumber() != null) {
                countParticipationTextField.setText(subscription.getParticipationNumber().toString().replace(".0", ""));
            }
            // JCheckBox subscriptionType = new JCheckBox("Дневной", subscription.getType());
            JLabel subscriptionType = new JLabel(SubscriptionType.forValue(subscription.getType()).note());
            countParticipationTextField.setEditable(false);
            subscriptionType.setEnabled(false);

            add(countParticipationLabel, "gapleft 50");
            add(countParticipationTextField, "cell 1 0");
            add(subscriptionType, "cell 1 0");
        }
        add(dateLabel, "cell 0 1");
        add(date, "cell 0 1");
        add(viewAll);
        add(timeTableScrollPane, "cell 0 2, span");
        add(half, "cell 0 3, gaptop 5");
        add(present, "span, right");
        add(presentTextField, "cell 1 3");
        add(keyLabel, "cell 0 4");
        add(ambry, "cell 0 4");
        add(makeParticipation, "cell 0 5, span, gaptop 15");
        add(editParticipation, "cell 0 5, span,  gaptop 15, gapleft 38");
        add(cancelParticipation, "cell 0 5, span, right, gapleft 34");
        add(printTimeTable, "cell 0 6");
        add(printAct, "span, right");

        date.setDate(new Date());

    }

    private void fillAmbryCombobox() {
        AmbryMapper mapper = DbSessionManager.getMapper(AmbryMapper.class);
        AmbryExample example = new AmbryExample();
        List<Ambry> list = mapper.selectByExample(example);
        DbSessionManager.closeSession();
        ambry.addItem(new StateItem(0, ""));
        for (Ambry item : list) {
            StateItem stateItem = new StateItem(item.getId(), item.getName());
            ambry.addItem(stateItem);
        }
        if (personalData.getAmbryId() != null) {
            ambry.setSelectedIndex(personalData.getAmbryId());
        }
    }

    private void notifyListeners(Subscription subscription) {
        for (SaveFormListener listener : saveListeners) {
            listener.saveForm(subscription, false);
        }

    }

    private float minusPresentParticipations() {
        String subscriptionNumber = presentTextField.getText();
        if (subscriptionNumber.equals(PRESENT_DEFAULT_TEXT)) {
            subscriptionNumber = "";
        }
        if (!"".equals(subscriptionNumber)) {
            SubscriptionMapper subscriptionMapper = DbSessionManager.getMapper(SubscriptionMapper.class);
            SubscriptionExample se = new SubscriptionExample();
            se.createCriteria().andSubscriptionNumberEqualTo(subscriptionNumber);
            List<Subscription> subscriptions = subscriptionMapper.selectByExample(se);
            if (subscriptions.isEmpty()) {
                NotifyDialog dialog = new NotifyDialog("Не найден абонемент №" + subscriptionNumber);
                dialog.setVisible(true);
            } else {
                if (subscriptions.size() == 1) {
                    Subscription s = subscriptions.get(0);
                    Date currentDate = new Date();
                    if (currentDate.after(s.getDateFrom()) && currentDate.before(s.getDateTo())) {
                        if (s.getParticipationNumberPresent() != null && s.getParticipationNumberPresent() > 0) {
                            ConfirmDialog confirmDialog = new ConfirmDialog("С абонемента №"
                                    + s.getSubscriptionNumber() + " будут списаны занятия. Продолжить?");
                            confirmDialog.setVisible(true);
                            if (confirmDialog.getAnswer()) {
                                float participations = total;
                                s.setParticipationNumberPresent(s.getParticipationNumberPresent() - participations);
                                subscriptionMapper.updateByPrimaryKey(s);
                                DbSessionManager.commit();
                                notifyListeners(s);
                                Logger.log("С абонемента №" + s.getSubscriptionNumber() + " списано " + participations
                                        + " занятий в подарок для клиента с ID [" + personalData.getId() + "]");
                                return participations;
                            }
                        } else {
                            NotifyDialog dialog = new NotifyDialog("У дарителя нет занятий для дарения");
                            dialog.setVisible(true);
                        }
                    } else {
                        NotifyDialog dialog = new NotifyDialog("Абонемент дарителя в данный момент не активен");
                        dialog.setVisible(true);
                    }
                }
            }
            if (DbSessionManager.isSessionActive()) {
                DbSessionManager.closeSession();
            }
        } else {
            NotifyDialog dialog = new NotifyDialog("Не введен номер абонемента дарителя");
            dialog.setVisible(true);
        }
        return 0;
    }

    private float minusParticipations() {
        SubscriptionMapper subscriptionMapper = DbSessionManager.getMapper(SubscriptionMapper.class);
        float participations = total;
        subscription.setParticipationNumber(subscription.getParticipationNumber() - participations);
        subscriptionMapper.updateByPrimaryKey(subscription);
        countParticipationTextField.setText(subscription.getParticipationNumber().toString().replace(".0", ""));
        DbSessionManager.commit();
        DbSessionManager.closeSession();
        Logger.log("С абонемента №" + subscription.getSubscriptionNumber() + " списано " + participations + " занятий");
        if (participations > 0) {
            return participations;
        } else {
            return 0;
        }

    }

    private void fillTimeTableTextArea() {
        timeTable.setText(null);
        printAct.setEnabled(true);
        cancelParticipation.setEnabled(true);
        half.setEnabled(true);
        editParticipation.setEnabled(true);
        printTimeTable.setEnabled(true);
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        TimeTableExample ttExample = new TimeTableExample();
        if (date.getDate() == null) {
            ttExample.createCriteria().andPersonalDataIdEqualTo(personalData.getId())
                    .andDateGreaterThanOrEqualTo(new Date());
        } else {
            ttExample.createCriteria().andDateEqualTo(date.getDate()).andPersonalDataIdEqualTo(personalData.getId());
            ttExample.setOrderByClause("time");
        }

        List<TimeTable> list = ttMapper.selectByExample(ttExample);
        DbSessionManager.closeSession();
        if (date.getDate() == null) {
            Map<Date, List<TimeTable>> timeTableMap = new LinkedHashMap<Date, List<TimeTable>>();

            for (TimeTable timeTable : list) {

                if (timeTableMap.containsKey(timeTable.getDate())) {
                    List<TimeTable> ttList = timeTableMap.get(timeTable.getDate());
                    ttList.add(timeTable);
                    timeTableMap.put(timeTable.getDate(), ttList);
                } else {
                    List<TimeTable> ttList = new ArrayList<TimeTable>();
                    ttList.add(timeTable);
                    timeTableMap.put(timeTable.getDate(), ttList);
                }
            }
            if (!timeTableMap.isEmpty()) {
                String text = "Все занятия:\n\n";
                for (Date date : timeTableMap.keySet()) {
                    text += "Дата: " + Formatter.formatDate(date) + "\n";
                    List<TimeTable> ttList = timeTableMap.get(date);
                    SimulatorTT oneDyTimeTable = generateTimeTableForOneDay(ttList);
                    if (oneDyTimeTable != null) {
                        text += oneDyTimeTable.getTimeTable();
                        text += "--\n";
                        String totalString = Float.toString(oneDyTimeTable.getCost());
                        totalString = totalString.replaceAll(".0", "");
                        // TODO: Склонять слово "занятие" по падежам
                        text += "Итого: " + totalString + " занятий\n\n";
                    }
                }
                timeTable.setText(text);
            }
            printAct.setEnabled(false);
            cancelParticipation.setEnabled(false);
            half.setEnabled(false);
            editParticipation.setEnabled(false);
            printTimeTable.setEnabled(false);
            return;
        }
        String text = "Дата: " + Formatter.formatDate(date.getDate()) + "\n";
        SimulatorTT oneDyTimeTable = generateTimeTableForOneDay(list);
        if (oneDyTimeTable != null) {
            text += oneDyTimeTable.getTimeTable();
            text += "--\n";
            total = oneDyTimeTable.getCost();
            if (half.isSelected()) {
                total -= 0.5f;
            }
            String totalString = Float.toString(total);
            totalString = totalString.replace(".0", "");
            // TODO: Склонять слово "занятие" по падежам
            text += "Итого: " + totalString + " занятий";
            timeTable.setText(text);
        }
    }

    private SimulatorTT generateTimeTableForOneDay(List<TimeTable> list) {
        String result = "";
        float cost = 0;
        for (TimeTable item : list) {
            Simulator simulator = item.getSimulator();
            String timeTo = "";
            DurationType dt = DurationType.forValue(simulator.getDurationType());
            int minutes = Formatter.parseMinutes(item.getTime());
            int hours = Formatter.parseHours(item.getTime());
            switch (dt) {
            case Half:
                if (minutes == 0) {
                    timeTo = Integer.toString(hours) + ":" + "30";
                } else {
                    timeTo = Integer.toString(hours + 1) + ":" + "00";
                }
                break;
            case Whole:
                timeTo = Integer.toString(hours + 1) + ":" + "00";
                break;
            case Quarter:
                if (minutes == 0) {
                    timeTo = Integer.toString(hours) + ":" + "15";
                } else if (minutes == 15) {
                    timeTo = Integer.toString(hours) + ":" + "30";
                } else if (minutes == 30) {
                    timeTo = Integer.toString(hours) + ":" + "45";
                } else if (minutes == 45) {
                    timeTo = Integer.toString(hours + 1) + ":" + "00";
                }
                break;
            default:
                break;
            }

            String countParts = Formatter.formatFloat(simulator.getCost());
            result += simulator.getName() + " " + item.getTime() + " - " + timeTo + " (" + countParts + ")\n";
            cost += simulator.getCost();
        }

        if (!list.isEmpty()) {
            return new SimulatorTT(cost, result);
        } else {
            return null;
        }
    }

    public void addSaveListener(SaveFormListener listener) {
        saveListeners.add(listener);
    }

    public void focusGained(FocusEvent event) {
        if (event.getComponent() instanceof JTextField) {
            ((JTextField) event.getComponent()).setText("");
            ((JTextField) event.getComponent()).setForeground(Color.BLACK);
        }

    }

    public void focusLost(FocusEvent event) {
        if (event.getComponent() instanceof JTextField) {
            JTextField field = (JTextField) event.getComponent();
            if (field.getName().equals(PRESENT_TEXT_FIELD)) {
                if ("".equals(field.getText())) {
                    field.setForeground(Color.lightGray);
                    field.setText(PRESENT_DEFAULT_TEXT);
                }
            }
        }

    }

    public void actionPerformed(ActionEvent event) {
        if (PRINT_TIME_TABLE_BUTTON.equals(event.getActionCommand())) {
            if (total > 0) {
                String printOut = formingPrintTimeTable(present.isSelected());

                try {
                    Printer.getInstance().print(printOut);
                    if (date.getDate() != null) {
                        Logger.log("Вывод на печать расписания для клиента с ID [" + personalData.getId()
                                + "] за дату " + Formatter.formatDate(date.getDate()));
                    } else {
                        Logger.log("Вывод на печать расписания для клиента с ID [" + personalData.getId()
                                + "] за все время ");
                    }
                } catch (PrinterException e) {
                    e.printStackTrace();
                }
            } else {
                NotifyDialog notifyDialog = new NotifyDialog("Уведомление", "На выбранную дату нет занятий");
                notifyDialog.setVisible(true);
            }
        } else if (PRINT_ACT_BUTTON.equals(event.getActionCommand())) {

            if (present.isSelected()) {
                if (minusPresentParticipations() > 0) {
                    String act = formingPrintAct(true, ACT_PRINT_FORM_PRESENT);
                    try {
                        Printer.getInstance().print(act);
                    } catch (PrinterException e) {
                        e.printStackTrace();
                    }
                }
                ambry.setSelectedIndex(0);
                setActPrinted();
                return;
            }

            if (total > 0) {
                if (subscription != null) {
                    if (minusParticipations() > 0) {
                        String act = formingPrintAct(false, ACT_PRINT_FORM);
                        try {
                            Printer.getInstance().print(act);
                        } catch (PrinterException e) {
                            e.printStackTrace();
                        }
                        ambry.setSelectedIndex(0);
                        setActPrinted();
                    }
                } else {
                    NotifyDialog notifyDialog = new NotifyDialog("Уведомление", "Разовое посещение. Оплата наличными");
                    notifyDialog.setVisible(true);
                    String act = formingPrintAct(false, ACT_PRINT_FORM);
                    try {
                        Printer.getInstance().print(act);
                    } catch (PrinterException e) {
                        e.printStackTrace();
                    }
                    setActPrinted();
                    ambry.setSelectedIndex(0);
                }
            } else {
                NotifyDialog notifyDialog = new NotifyDialog("Уведомление", "На выбранную дату нет занятий");
                notifyDialog.setVisible(true);
            }
        } else if (MAKE_PARTICIPATION_BUTTON.equals(event.getActionCommand())) {
            ClientTimeTableDialog dialog = new ClientTimeTableDialog(ApplicationHolder.getMainFrame(), personalData,
                    date.getDate(), false);
            dialog.setVisible(true);
            fillTimeTableTextArea();
        } else if (EDIT_PARTICIPATION_BUTTON.equals(event.getActionCommand())) {
            ClientTimeTableDialog dialog = new ClientTimeTableDialog(ApplicationHolder.getMainFrame(), personalData,
                    date.getDate(), true);
            dialog.setVisible(true);
            fillTimeTableTextArea();
        } else if (CANCEL_PARTICIPATION_BUTTON.equals(event.getActionCommand())) {
            ConfirmDialog dialog = new ConfirmDialog("Отменить все занятия клиента на выбранную дату ?");
            dialog.setVisible(true);
            if (dialog.getAnswer()) {
                TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
                TimeTableExample example = new TimeTableExample();
                example.createCriteria().andDateEqualTo(date.getDate()).andPersonalDataIdEqualTo(personalData.getId());
                List<TimeTable> list = ttMapper.selectByExample(example);
                for (TimeTable timeTable : list) {
                    ttMapper.deleteByPrimaryKey(timeTable.getId());
                }
                DbSessionManager.commit();
                DbSessionManager.closeSession();
                fillTimeTableTextArea();
                total = 0;
            }
        } else if (VIEW_ALL_BUTTON.equals(event.getActionCommand())) {
            date.setDate(null);
        }

    }

    private void setActPrinted() {
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        TimeTableExample example = new TimeTableExample();
        example.createCriteria().andPersonalDataIdEqualTo(personalData.getId()).andDateEqualTo(date.getDate());
        List<TimeTable> list = ttMapper.selectByExample(example);
        for (TimeTable timeTable : list) {
            timeTable.setPrintedAct(true);
            ttMapper.updateByPrimaryKey(timeTable);
        }
        DbSessionManager.commit();
        DbSessionManager.closeSession();
    }

    private String formingPrintAct(boolean present, String template) {
        Date actDate = date.getDate();
        String client = personalData.getFio();
        String subscriptionNumber = "";
        String subscriptionType = "";
        String subscriptionPeriod = "";
        if (personalData.getSubscription() != null) {
            subscriptionNumber = personalData.getSubscription().getSubscriptionNumber() != null ? personalData
                    .getSubscription().getSubscriptionNumber() : "";
            subscriptionType = SubscriptionType.forValue(personalData.getSubscription().getType()).note();
            subscriptionPeriod = Formatter.formatDate(personalData.getSubscription().getDateFrom()) + " - "
                    + Formatter.formatDate(personalData.getSubscription().getDateTo());
        }
        String simulators = "";
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        TimeTableExample ttExample = new TimeTableExample();
        ttExample.createCriteria().andDateEqualTo(date.getDate()).andPersonalDataIdEqualTo(personalData.getId());
        List<TimeTable> list = ttMapper.selectByExample(ttExample);
        SimulatorTT oneDayTimeTable = generateTimeTableForOneDay(list);
        if (oneDayTimeTable != null) {
            String str = oneDayTimeTable.getTimeTable();
            str = str.substring(0, str.lastIndexOf("\n"));
            simulators += str;
        }
        String totalString = Float.toString(total);
        String writeoff = totalString.replace(".0", "");
        String presenter = "";
        if (present) {
            PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
            PersonalDataExample pdExample = new PersonalDataExample();
            pdExample.createCriteria().andSubscriptionNumberEqual(presentTextField.getText());
            List<PersonalData> pdList = pdMapper.selectByExampleFull(pdExample);
            DbSessionManager.closeSession();
            if (!pdList.isEmpty()) {
                presenter = pdList.get(0).getFio();
            }

        }
        String balance = "";
        if (personalData.getSubscription() != null) {
            balance = Float.toString(personalData.getSubscription().getParticipationNumber());
            if (balance.contains(".0")) {
                balance = balance.replace(".0", "");
            }
        }
        String presentParticipations = "";
        if (present) {
            float participations = total;
            if (half.isSelected()) {
                participations = participations - 0.5f;
            }
            presentParticipations = Float.toString(participations);
            if (presentParticipations.contains(".0")) {
                presentParticipations = presentParticipations.replace(".0", "");
            }
        }
        String act = "";
        if (present) {
            act = String.format(template, Formatter.formatDate(actDate), client, subscriptionNumber, subscriptionType,
                    subscriptionPeriod, simulators, writeoff, presenter, balance, presentParticipations,
                    ((StateItem) ambry.getSelectedItem()).getValue(), act);
        } else {
            act = String.format(template, Formatter.formatDate(actDate), client, subscriptionNumber, subscriptionType,
                    subscriptionPeriod, simulators, writeoff, balance,
                    ((StateItem) ambry.getSelectedItem()).getValue(), act);
        }

        return act;
    }

    private String formingPrintTimeTable(boolean present) {

        Date timeTableDate = date.getDate();
        String client = personalData.getFio();
        String subscrNumber = "";
        if (personalData.getSubscription() != null) {
            subscrNumber = personalData.getSubscription().getSubscriptionNumber();
        }
        String participations = "";
        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        TimeTableExample ttExample = new TimeTableExample();
        ttExample.createCriteria().andDateEqualTo(date.getDate()).andPersonalDataIdEqualTo(personalData.getId());
        List<TimeTable> list = ttMapper.selectByExample(ttExample);
        SimulatorTT oneDayTimeTable = generateTimeTableForOneDay(list);
        if (oneDayTimeTable != null) {
            String str = oneDayTimeTable.getTimeTable();
            str = str.substring(0, str.lastIndexOf("\n"));
            participations += str;
        }
        String totalString = Float.toString(total);
        String sumaryParticipations = totalString.replace(".0", "");
        String presenter = "";
        if (present) {
            PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
            PersonalDataExample pdExample = new PersonalDataExample();
            pdExample.createCriteria().andSubscriptionNumberEqual(presentTextField.getText());
            List<PersonalData> pdList = pdMapper.selectByExampleFull(pdExample);
            DbSessionManager.closeSession();
            if (!pdList.isEmpty()) {
                presenter = pdList.get(0).getFio();
            }

        }
        String printTimeTable = "";
        if (present) {
            printTimeTable = String.format(TIME_TABLE_PRINT_FORM_PRESENT, Formatter.formatDate(timeTableDate), client,
                    subscrNumber, participations, sumaryParticipations, presenter);
        } else {
            printTimeTable = String.format(TIME_TABLE_PRINT_FORM, Formatter.formatDate(timeTableDate), client,
                    subscrNumber, participations, sumaryParticipations);
        }

        // "Дата: %s\nКлиент: %s\nНомер абонемента: %s\nЗанятие:\n%s\nСтоимость услуг: %s\nЗанятия в подарок от: %s";
        // "Дата: %s\nКлиент: %s\nНомер абонемента: %s\nЗанятие:\n%s\nСтоимость услуг: %s";
        return printTimeTable;
    }

    public void propertyChange(PropertyChangeEvent event) {
        if ("date".equals(event.getPropertyName())) {
            fillTimeTableTextArea();
        }

    }

    @SuppressWarnings("unchecked")
    @Override
    public void itemStateChanged(ItemEvent event) {
        if (event.getSource() instanceof JCheckBox) {
            fillTimeTableTextArea();
        } else if (event.getSource() instanceof JComboBox<?>) {
            JComboBox<StateItem> combobox = (JComboBox<StateItem>) event.getSource();
            if (event.getStateChange() == 2) {
                PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
                if (combobox.getSelectedIndex() != 0) {
                    personalData.setAmbryId(combobox.getSelectedIndex());
                } else {
                    personalData.setAmbryId(null);
                }
                pdMapper.updateByPrimaryKey(personalData);
                DbSessionManager.commit();
                DbSessionManager.closeSession();
            }
        }

    }
}
