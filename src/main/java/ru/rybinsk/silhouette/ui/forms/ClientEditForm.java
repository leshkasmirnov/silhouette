/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.forms;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.dao.PaymentMapper;
import ru.rybinsk.silhouette.dao.PersonalDataMapper;
import ru.rybinsk.silhouette.internal.SubscriptionType;
import ru.rybinsk.silhouette.model.Payment;
import ru.rybinsk.silhouette.model.PaymentExample;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.settings.ApplicationHolder;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.ui.listeners.SaveFormListener;
import ru.rybinsk.silhouette.util.Formatter;

import com.toedter.calendar.JDateChooser;

/**
 * Форма редактирования клиента.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ClientEditForm extends JPanel implements ActionListener {

    private static final String CANCEL_BUTTON = "cancel";
    private static final String SAVE_BUTTON = "save";
    private static final String CHANGE_BUTTON = "change";

    private static final long serialVersionUID = 1L;

    private PersonalData editingClient;

    private JButton changeButton;
    private JButton saveButton;
    private JButton cancelButton;

    private boolean edit;
    private JTextField lastNameTextField;
    private JTextField firstNameTextField;
    private JTextField middleNameTextField;
    private JTextField contractNumberTextField;
    private JTextField phone1TextField;
    private JTextField phone2TextField;
    private JDateChooser birthday;
    private JTextArea noteTextField;

    private Set<SaveFormListener> saveListeners = new HashSet<SaveFormListener>();

    public ClientEditForm(PersonalData personalData) {
        editingClient = personalData;
        if (editingClient == null) {
            editingClient = new PersonalData();
        }
        setLayout(new MigLayout("gap 15px"));
        paint();
    }

    private void paint() {
        JLabel lastNameLabel = new JLabel("Фамилия:");
        JLabel firstNameLabel = new JLabel("Имя:");
        JLabel middleNameLabel = new JLabel("Отчество:");
        JLabel contractNumberLabel = new JLabel("№ Договора:");
        JLabel phone1Label = new JLabel("Телефон 1:");
        JLabel phone2Label = new JLabel("Телефон 2:");
        JLabel birthdayLabel = new JLabel("Дата рождения:");
        JLabel noteLabel = new JLabel("Примечание:");
        JPanel subscriptionTypePanel = new JPanel();

        lastNameTextField = new JTextField(47);
        firstNameTextField = new JTextField(47);
        middleNameTextField = new JTextField(47);
        contractNumberTextField = new JTextField(12);
        phone1TextField = new JTextField(12);
        phone2TextField = new JTextField(12);
        birthday = new JDateChooser();
        noteTextField = new JTextArea(7, 40);
        noteTextField.setFont(phone2TextField.getFont());
        noteTextField.setBorder(phone2TextField.getBorder());

        fillClientsFields();

        birthday.setDateFormatString(Formatter.SIMPLE_DATE_FORMAT);

        changeButton = new JButton("Изменить");
        changeButton.setActionCommand(CHANGE_BUTTON);
        saveButton = new JButton("Сохранить");
        saveButton.setActionCommand(SAVE_BUTTON);
        cancelButton = new JButton("Отменить");
        cancelButton.setActionCommand(CANCEL_BUTTON);

        changeButton.addActionListener(this);
        saveButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // тип абонемента: дневной или недневной
        if (editingClient.getSubscription() != null) {
            if (editingClient.getSubscription().getType() != null) {
                JLabel subscriptionType = new JLabel("Вид абонемента: "
                        + SubscriptionType.forValue(editingClient.getSubscription().getType()).note());
                subscriptionTypePanel.add(subscriptionType);
            }
        }
        add(subscriptionTypePanel, "span");
        add(lastNameLabel, "cell 0 1");
        add(lastNameTextField, "span 2 1");
        add(firstNameLabel, "cell 0 2");
        add(firstNameTextField, "span 2 1");
        add(middleNameLabel, "cell 0 3");
        add(middleNameTextField, "span 2 1");
        add(contractNumberLabel, "cell 0 4");
        add(contractNumberTextField);
        // напоминание о платеже
        if (editingClient.getSubscription() != null) {
            JPanel remindPanel = paintSubscriptionRemindPanel();
            if (remindPanel != null) {
                add(remindPanel, "cell 2 4, span 1 3");
            }
        }
        add(phone1Label, "cell 0 5");
        add(phone1TextField);
        add(phone2Label, "cell 0 6");
        add(phone2TextField);
        add(birthdayLabel, "cell 0 7");
        add(birthday);
        add(noteLabel, "cell 0 8");
        add(noteTextField, "span 2 1");
        add(changeButton, "cell 1 9, span, gaptop 50");
        add(saveButton, "cell 1 9, span");
        add(cancelButton, "cell 1 9, span");

        if (editingClient.getId() != null) {
            setEditing(false);
        } else {
            setEditing(true);
        }
    }

    private JPanel paintSubscriptionRemindPanel() {
        PaymentMapper pMapper = DbSessionManager.getMapper(PaymentMapper.class);
        PaymentExample pe = new PaymentExample();
        pe.createCriteria().andSubscriptionIdEqualTo(editingClient.getSubscription().getId()).andPaidEqualTo(false);
        List<Payment> paymentList = pMapper.selectByExample(pe);
        DbSessionManager.closeSession();
        JPanel remindPanel = new JPanel(new MigLayout());
        if (!paymentList.isEmpty()) {
            Iterator<Payment> itr = paymentList.iterator();
            Payment minPayment;
            Payment p = itr.next();
            Date minDate = p.getPaymentDate();
            minPayment = p;
            while (itr.hasNext()) {
                p = itr.next();
                if (p.getPaymentDate().before(minDate)) {
                    minPayment = p;
                }
            }
            TitledBorder titledBorder = BorderFactory.createTitledBorder("Напомнить");
            titledBorder.setTitleColor(Color.RED);
            Font newFont = new Font(Font.DIALOG, Font.BOLD, 15);
            titledBorder.setTitleFont(newFont);
            remindPanel.setBorder(titledBorder);

            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            JLabel paymentDateLabel = new JLabel("Дата очередного платежа: " + sdf.format(minPayment.getPaymentDate()));
            JLabel sumPayment = new JLabel("Сумма платежа: " + minPayment.getSum());

            Font labelFont = new Font(Font.DIALOG, Font.BOLD, 13);
            paymentDateLabel.setFont(labelFont);
            sumPayment.setFont(labelFont);
            paymentDateLabel.setForeground(Color.RED);
            sumPayment.setForeground(Color.RED);

            remindPanel.add(paymentDateLabel);
            remindPanel.add(sumPayment, "cell 0 1");
            return remindPanel;
        }
        return null;
    }

    private void fillClientsFields() {
        lastNameTextField.setText(editingClient.getLastName());
        firstNameTextField.setText(editingClient.getFirstName());
        middleNameTextField.setText(editingClient.getMiddleName());
        contractNumberTextField.setText(editingClient.getContractNumber());
        phone1TextField.setText(editingClient.getPhone1());
        phone2TextField.setText(editingClient.getPhone2());
        birthday.setDate(editingClient.getBirthday());
        noteTextField.setText(editingClient.getNote());

    }

    private void updateEditingClient() {
        editingClient.setFirstName(firstNameTextField.getText());
        editingClient.setMiddleName(middleNameTextField.getText());
        editingClient.setLastName(lastNameTextField.getText());
        editingClient.setContractNumber(contractNumberTextField.getText());
        editingClient.setBirthday(birthday.getDate());
        editingClient.setActive(true);
        editingClient.setPhone1(phone1TextField.getText());
        editingClient.setPhone2(phone2TextField.getText());
        editingClient.setNote(noteTextField.getText());
    }

    private void setEditing(boolean editing) {
        if (editing) {
            changeButton.setEnabled(false);
            saveButton.setEnabled(true);
            cancelButton.setEnabled(true);
            edit = true;

            lastNameTextField.setEditable(true);
            firstNameTextField.setEditable(true);
            middleNameTextField.setEditable(true);
            contractNumberTextField.setEditable(true);
            phone1TextField.setEditable(true);
            phone2TextField.setEditable(true);
            birthday.setEnabled(true);
            noteTextField.setEditable(true);
        } else {
            changeButton.setEnabled(true);
            saveButton.setEnabled(false);
            cancelButton.setEnabled(false);
            edit = false;

            lastNameTextField.setEditable(false);
            firstNameTextField.setEditable(false);
            middleNameTextField.setEditable(false);
            contractNumberTextField.setEditable(false);
            phone1TextField.setEditable(false);
            phone2TextField.setEditable(false);
            birthday.setEnabled(false);
            noteTextField.setEditable(false);
        }
    }

    private boolean saveEditingClient() {
        boolean newItem = false;
        PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
        if (editingClient.getId() != null) {
            pdMapper.updateByPrimaryKey(editingClient);
        } else {
            pdMapper.insert(editingClient);
            newItem = true;
        }
        DbSessionManager.commit();
        DbSessionManager.closeSession();
        return newItem;
    }

    public void actionPerformed(ActionEvent event) {
        if (SAVE_BUTTON.equals(event.getActionCommand())) {
            saveForm();
        } else if (CHANGE_BUTTON.equals(event.getActionCommand())) {
            setEditing(true);
        } else if (CANCEL_BUTTON.equals(event.getActionCommand())) {
            resetForm();
        }

    }

    public boolean isEdit() {
        return edit;
    }

    public void saveForm() {
        updateEditingClient();
        boolean newItem = saveEditingClient();
        setEditing(false);
        for (SaveFormListener listener : saveListeners) {
            listener.saveForm(editingClient, newItem);
        }
    }

    public void resetForm() {
        if (editingClient.getId() != null) {
            PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
            editingClient = pdMapper.selectByPrimaryKey(editingClient.getId());
        } else {
            editingClient = new PersonalData();
        }

        fillClientsFields();
        setEditing(false);
    }

    public void showDialog() {
        JDialog dialog = new JDialog(ApplicationHolder.getMainFrame());
        dialog.setModal(true);
        dialog.setVisible(true);
    }

    public void addSaveListener(SaveFormListener listener) {
        saveListeners.add(listener);
    }

}
