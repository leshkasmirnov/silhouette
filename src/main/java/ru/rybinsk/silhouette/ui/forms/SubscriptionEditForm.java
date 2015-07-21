/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.forms;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.dao.PaymentMapper;
import ru.rybinsk.silhouette.dao.SubscriptionMapper;
import ru.rybinsk.silhouette.internal.SubscriptionPeriod;
import ru.rybinsk.silhouette.internal.SubscriptionType;
import ru.rybinsk.silhouette.model.Payment;
import ru.rybinsk.silhouette.model.PaymentExample;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.model.Subscription;
import ru.rybinsk.silhouette.pojo.PaymentAssiciation;
import ru.rybinsk.silhouette.pojo.StateItem;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.throwable.SilhouetteRuntimeException;
import ru.rybinsk.silhouette.ui.listeners.SaveFormListener;
import ru.rybinsk.silhouette.util.Formatter;

import com.toedter.calendar.JDateChooser;

/**
 * Форма для абонемента.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class SubscriptionEditForm extends JPanel implements ActionListener {

    private static final String CANCEL_BUTTON = "cancel";
    private static final String SAVE_BUTTON = "save";
    private static final String EDIT_BUTTON = "edit";
    private static final String ADD_BUTTON = "add";
    private static final String CONTRACT_NUMBER_TEXT = "" + "№ договора: ";
    private static final int COUNT_SUBSCRIPTION_PARTS = 4;

    private static final long serialVersionUID = 1L;

    private PersonalData personalData;
    private Subscription subscription;

    private JTextField subscriptionNumber;
    private JComboBox<StateItem> state;
    private JComboBox<StateItem> type;
    private JComboBox<StateItem> period;
    private JDateChooser dateFrom;
    private JDateChooser dateTo;
    private JTextField participations;
    private JTextField participationsPresent;
    private JPanel paymentPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton saveButton;
    private JButton cancelButton;
    private Set<SaveFormListener> saveListeners = new HashSet<SaveFormListener>();
    private Map<Integer, PaymentAssiciation> paymentParts = new HashMap<Integer, PaymentAssiciation>();
    private boolean edit;

    public SubscriptionEditForm(PersonalData personalData) {
        this.personalData = personalData;
        if (personalData == null) {
            throw new SilhouetteRuntimeException("Personal data не может быть null");
        }
        subscription = personalData.getSubscription();
        if (subscription == null) {
            subscription = new Subscription();
            subscription.setPersonalDataId(personalData.getId());
        }

        setLayout(new MigLayout("gap 15px"));
        paint();
    }

    private void paint() {
        JLabel fioLabel = new JLabel(personalData.getFio());
        JLabel contractNumberLabel = new JLabel(CONTRACT_NUMBER_TEXT + personalData.getContractNumber());
        JLabel subscriptionNumberLabel = new JLabel("№: ");
        JLabel stateLabel = new JLabel("Состояние: ");
        JLabel typeLabel = new JLabel("Вид абонемента: ");
        JLabel periodLabel = new JLabel("Срок абонемента: ");
        JLabel validityLabel = new JLabel("Период действия: ");
        JLabel participationsLabel = new JLabel("Количество занятий: ");
        JLabel participationsPresentLabel = new JLabel("в подарок: ");
        JLabel delimiterLabel = new JLabel("--");

        subscriptionNumber = new JTextField(10);
        state = new JComboBox<StateItem>(new StateItem[] { new StateItem(0, "Заблокирован"),
                new StateItem(1, "Активен") });

        type = new JComboBox<StateItem>();
        for (SubscriptionType subscrType : SubscriptionType.values()) {
            StateItem item = new StateItem(subscrType.getValue(), subscrType.note());
            type.addItem(item);
        }

        period = new JComboBox<StateItem>();
        for (SubscriptionPeriod subscrPeriod : SubscriptionPeriod.values()) {
            StateItem item = new StateItem(subscrPeriod.getValue(), subscrPeriod.note());
            period.addItem(item);
        }
        dateFrom = new JDateChooser(new Date());
        dateTo = new JDateChooser();
        participations = new JTextField(5);
        participationsPresent = new JTextField(10);
        paymentPanel = new JPanel();
        JPanel buttonsPanel = new JPanel();

        addButton = new JButton("Добавить");
        editButton = new JButton("Изменить");
        saveButton = new JButton("Сохранить");
        cancelButton = new JButton("Отменить");
        addButton.setActionCommand(ADD_BUTTON);
        editButton.setActionCommand(EDIT_BUTTON);
        saveButton.setActionCommand(SAVE_BUTTON);
        cancelButton.setActionCommand(CANCEL_BUTTON);
        addButton.addActionListener(this);
        editButton.addActionListener(this);
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);
        addButton.setEnabled(false);
        editButton.setEnabled(false);

        paymentPanel.setBorder(BorderFactory.createTitledBorder("Оплата"));
        paymentPanel.setLayout(new MigLayout("gap 15px"));

        dateFrom.setDateFormatString(Formatter.SIMPLE_DATE_FORMAT);
        dateTo.setDateFormatString(Formatter.SIMPLE_DATE_FORMAT);

        add(fioLabel);
        add(contractNumberLabel, "gapleft 25");

        add(subscriptionNumberLabel, "cell 0 1");
        add(subscriptionNumber, "cell 0 1");
        add(stateLabel, "gapleft 25");
        add(state, "cell 1 1");
        add(periodLabel, "cell 0 2");
        add(period, "cell 0 2");
        add(typeLabel, "gapleft 25");
        add(type, "cell 1 2");
        add(validityLabel, "cell 0 3");
        add(dateFrom, "cell 0 3");
        add(delimiterLabel);
        add(dateTo, "cell 1 3, gapleft 17");
        add(participationsLabel, "cell 0 4");
        add(participations, "cell 0 4");
        add(participationsPresentLabel, "gapleft 25");
        add(participationsPresent, "cell 1 4");

        fillSubscriptionFields();

        add(paymentPanel, "cell 0 5, span, gaptop 30");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(cancelButton);
        add(buttonsPanel, "cell 0 6, span, gaptop 30");

        setEditing(false);

    }

    private void repaintContentPaymentPanel() {
        JLabel paymentDateHeaderLabel = new JLabel("Дата");
        JLabel paymentPriceHeaderLabel = new JLabel("Сумма");
        JLabel paymentPaidHeaderLabel = new JLabel("Оплачено");
        paymentPanel.removeAll();
        paymentParts.clear();
        paymentPanel.add(paymentDateHeaderLabel);
        paymentPanel.add(paymentPriceHeaderLabel);
        paymentPanel.add(paymentPaidHeaderLabel);
        for (int i = 0; i < COUNT_SUBSCRIPTION_PARTS; i++) {
            JDateChooser paymentDate = new JDateChooser();
            JTextField paymentPrice = new JTextField(10);
            JCheckBox paymentPaid = new JCheckBox();

            paymentDate.setDateFormatString(Formatter.SIMPLE_DATE_FORMAT);

            paymentPanel.add(paymentDate, "cell 0 " + i + 1);
            paymentPanel.add(paymentPrice);
            paymentPanel.add(paymentPaid);

            paymentParts.put(i + 1, new PaymentAssiciation(paymentDate, paymentPrice, paymentPaid));
        }
        paymentPanel.revalidate();
    }

    private void fillSubscriptionFields() {
        subscriptionNumber.setText(subscription.getSubscriptionNumber());
        state.setSelectedIndex(subscription.getState());
        type.setSelectedIndex(subscription.getType());
        period.setSelectedIndex(subscription.getPeriod());
        dateFrom.setDate(subscription.getDateFrom());
        dateTo.setDate(subscription.getDateTo());
        if (subscription.getParticipationNumber() != null) {
            participations.setText(subscription.getParticipationNumber().toString().replace(".0", ""));
        }
        if (subscription.getParticipationNumberPresent() != null) {
            participationsPresent.setText(subscription.getParticipationNumberPresent().toString().replace(".0", ""));
        }

        repaintContentPaymentPanel();
        if (subscription.getId() != null) {
            PaymentMapper pMapper = DbSessionManager.getMapper(PaymentMapper.class);
            PaymentExample pe = new PaymentExample();
            pe.createCriteria().andSubscriptionIdEqualTo(subscription.getId());
            List<Payment> paymentList = pMapper.selectByExample(pe);
            DbSessionManager.closeSession();
            int i = 1;
            for (Payment payment : paymentList) {
                paymentParts.get(i).getDateChooser().setDate(payment.getPaymentDate());
                paymentParts.get(i).getTextField().setText(payment.getSum());
                paymentParts.get(i).getCheckBox().setSelected(payment.getPaid());
                paymentParts.get(i).setPayment(payment);
                i++;
            }
        }

    }

    private void updateSubscription() {
        if (!"".equals(subscriptionNumber.getText())) {
            subscription.setSubscriptionNumber(subscriptionNumber.getText());
        } else {
            subscription.setSubscriptionNumber(null);
        }
        subscription.setState(state.getSelectedIndex());
        subscription.setType(type.getSelectedIndex());
        subscription.setPeriod(period.getSelectedIndex());
        subscription.setDateFrom(dateFrom.getDate());
        subscription.setDateTo(dateTo.getDate());
        if (!"".equals(participations.getText())) {
            subscription.setParticipationNumber(Float.parseFloat(participations.getText()));
        }
        if (!"".equals(participationsPresent.getText())) {
            subscription.setParticipationNumberPresent(Float.parseFloat(participationsPresent.getText()));
        }
    }

    private void setEditing(boolean b) {
        edit = b;
        if (b) {
            subscriptionNumber.setEditable(true);
            state.setEnabled(true);
            type.setEnabled(true);
            period.setEnabled(true);
            dateFrom.setEnabled(true);
            dateTo.setEnabled(true);
            participations.setEditable(true);
            participationsPresent.setEditable(true);
            for (PaymentAssiciation item : paymentParts.values()) {
                item.getDateChooser().setEnabled(true);
                item.getTextField().setEditable(true);
                item.getCheckBox().setEnabled(true);
            }

            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            editButton.setEnabled(false);
            addButton.setEnabled(false);
        } else {
            subscriptionNumber.setEditable(false);
            state.setEnabled(false);
            type.setEnabled(false);
            period.setEnabled(false);
            dateFrom.setEnabled(false);
            dateTo.setEnabled(false);
            participations.setEditable(false);
            participationsPresent.setEditable(false);
            for (PaymentAssiciation item : paymentParts.values()) {
                item.getDateChooser().setEnabled(false);
                item.getTextField().setEditable(false);
                item.getCheckBox().setEnabled(false);
            }

            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
            if (subscription.getId() != null) {
                editButton.setEnabled(true);
            } else {
                addButton.setEnabled(true);
            }
        }

    }

    private boolean saveSubscription() {
        boolean newItem = false;
        SubscriptionMapper subscrMapper = DbSessionManager.getMapper(SubscriptionMapper.class);
        PaymentMapper pMapper = DbSessionManager.getMapper(PaymentMapper.class);
        try {
            if (subscription.getId() != null) {
                // update
                subscrMapper.updateByPrimaryKey(subscription);
            } else {
                // insert
                subscrMapper.insert(subscription);
                newItem = true;
            }

            for (int i = 0; i < COUNT_SUBSCRIPTION_PARTS; i++) {
                PaymentAssiciation item = paymentParts.get(i + 1);
                item.fillPayment();
                if (item.getDateChooser().getDate() != null && !"".equals(item.getTextField().getText())) {
                    item.fillPayment();
                    if (item.getPayment().getId() != null) {
                        pMapper.updateByPrimaryKey(item.getPayment());
                    } else {
                        item.getPayment().setSubscriptionId(subscription.getId());
                        pMapper.insert(item.getPayment());
                    }
                } else if (item.getDateChooser().getDate() == null && "".equals(item.getTextField().getText())) {
                    if (item.getPayment().getId() != null) {
                        pMapper.deleteByPrimaryKey(item.getPayment().getId());
                    }
                }
            }
            DbSessionManager.commit();
        } catch (Exception e) {
            e.printStackTrace();
            DbSessionManager.rollBack();
        } finally {
            DbSessionManager.closeSession();
        }
        return newItem;

    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(ADD_BUTTON)) {
            setEditing(true);
        } else if (event.getActionCommand().equals(EDIT_BUTTON)) {
            setEditing(true);
        } else if (event.getActionCommand().equals(CANCEL_BUTTON)) {
            resetForm();
        } else if (event.getActionCommand().equals(SAVE_BUTTON)) {
            saveForm();
        }

    }

    public void saveForm() {
        updateSubscription();
        saveSubscription();
        setEditing(false);
        for (SaveFormListener listener : saveListeners) {
            personalData.setSubscription(subscription);
            listener.saveForm(personalData, false);
        }
    }

    public void resetForm() {
        if (subscription.getId() != null) {
            SubscriptionMapper subscrMapper = DbSessionManager.getMapper(SubscriptionMapper.class);
            subscription = subscrMapper.selectByPrimaryKey(subscription.getId());
        } else {
            subscription = new Subscription();
            subscription.setPersonalDataId(personalData.getId());
        }
        fillSubscriptionFields();
        setEditing(false);
    }

    public void addSaveListener(SaveFormListener listener) {
        saveListeners.add(listener);
    }

    public boolean isEdit() {
        return edit;
    }

}
