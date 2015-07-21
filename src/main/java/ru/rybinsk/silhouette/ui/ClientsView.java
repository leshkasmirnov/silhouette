/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.binding.ClientsTableModel;
import ru.rybinsk.silhouette.dao.PersonalDataMapper;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.model.PersonalDataExample;
import ru.rybinsk.silhouette.model.PersonalDataExample.Criteria;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.ui.components.FullConfirmDialog;
import ru.rybinsk.silhouette.ui.forms.ClientEditForm;
import ru.rybinsk.silhouette.ui.forms.SubscriptionEditForm;
import ru.rybinsk.silhouette.ui.forms.TimeTableForm;
import ru.rybinsk.silhouette.ui.listeners.SaveFormListener;

/**
 * Список клиентов. view.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ClientsView extends JPanel implements ActionListener, FocusListener, SaveFormListener, ItemListener,
        ChangeListener, ListSelectionListener {

    private static final long serialVersionUID = 1L;

    private static final String COUNT_CLIENTS_TEXT = "Количество клиентов: ";
    private static final String ABONEMENT_FILTER_FIELD_NAME = "abonementFilterField";
    private static final String FIO_FILTER_FIELD_NAME = "fioFilterField";
    private static final String ABONEMENT_FILTER_FIELD_DEFAULT_TEXT = "Абонемент №";
    private static final String FIO_FILTER_FIELD_DEFAULT_TEXT = "Фамилия или Имя или Отчество";
    private static final String FILTER_BUTTON = "filter";
    private static final String ADD_CLIENT_BUTTON = "addClient";
    private static final int DEFAULT_SELECTED_ROW = 0;

    private JTable clientsTable;
    private List<PersonalData> pdList;
    // private ClientsController controller;
    private JTabbedPane tabs;
    private JPanel clientEditPanel;

    private int selectedRow;
    private ClientsTableModel dm;
    private JPanel clientsListPanel;
    private Checkbox onlyActiveCheckBox;
    private JLabel countClientsLabel;
    private JTextField fioFilterField;
    private JTextField abonementFilterField;
    private JScrollPane pane;
    private JPanel subscriptionEditPanel;
    private JPanel timeTableEditPanel;
    private JButton addButton;
    private JPanel clientSpecificationPanel;

    private ClientEditForm clientForm;

    private SubscriptionEditForm subscriptionEditForm;
    private boolean saveDialogShoswed;
    private boolean addingClient;
    private int selectedTab;

    private ListSelectionModel listSelectionModel;

    public ClientsView() {
        init();
        setLayout(new MigLayout());

        clientsListPanel = new JPanel();
        clientSpecificationPanel = new JPanel(new GridLayout());
        JPanel clientsFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel underTablePanel = new JPanel(new MigLayout());
        clientEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subscriptionEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeTableEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        clientsTable = new JTable();
        pane = new JScrollPane(clientsTable);
        tabs = new JTabbedPane();
        fioFilterField = new JTextField(31);
        abonementFilterField = new JTextField(11);
        JButton filterButton = new JButton("Поиск");
        onlyActiveCheckBox = new Checkbox("Только активные");
        countClientsLabel = new JLabel();
        addButton = new JButton("Добавить");

        // clientsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // clientsTable.setAutoCreateRowSorter(true);
        clientsTable.setRowHeight(25);
        listSelectionModel = clientsTable.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(this);

        clientsTable.setAutoscrolls(true);
        pane.setPreferredSize(new Dimension(430, 480));
        pane.setAutoscrolls(true);
        fioFilterField.addFocusListener(this);
        fioFilterField.setForeground(Color.lightGray);
        fioFilterField.setName(FIO_FILTER_FIELD_NAME);
        abonementFilterField.addFocusListener(this);
        abonementFilterField.setForeground(Color.lightGray);
        abonementFilterField.setName(ABONEMENT_FILTER_FIELD_NAME);
        addButton.setActionCommand(ADD_CLIENT_BUTTON);
        addButton.addActionListener(this);
        filterButton.setActionCommand(FILTER_BUTTON);
        filterButton.addActionListener(this);
        onlyActiveCheckBox.addItemListener(this);

        fioFilterField.setText(FIO_FILTER_FIELD_DEFAULT_TEXT);
        abonementFilterField.setText(ABONEMENT_FILTER_FIELD_DEFAULT_TEXT);

        tabs.addChangeListener(this);
        tabs.add("Клиент", clientEditPanel);
        tabs.add("Абонемент", subscriptionEditPanel);
        tabs.add("Расписание", timeTableEditPanel);

        clientsListPanel.setLayout(new MigLayout(new LC().flowY()));
        clientsFilterPanel.add(fioFilterField);
        clientsFilterPanel.add(abonementFilterField);
        clientsFilterPanel.add(filterButton);

        underTablePanel.add(onlyActiveCheckBox);
        underTablePanel.add(addButton, "gapleft 220");
        underTablePanel.add(countClientsLabel, "cell 0 1");

        clientsListPanel.add(clientsFilterPanel);
        clientsListPanel.add(pane);

        clientSpecificationPanel.add(tabs);
        clientSpecificationPanel.setPreferredSize(new Dimension(520, 518));

        add(clientsListPanel);
        add(clientSpecificationPanel, "gapleft 30");
        add(underTablePanel, "cell 0 1");

        updateRecords();

    }

    private void init() {
        // controller = new ClientsController();
        selectedRow = DEFAULT_SELECTED_ROW;

        // инициализация списка клиентов
        PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
        PersonalDataExample pdExample = new PersonalDataExample();
        pdExample.setOrderByClause("last_name");
        pdList = pdMapper.selectByExampleFull(pdExample);
        DbSessionManager.closeSession();

    }

    private void updateRecords() {
        dm = new ClientsTableModel(pdList);
        clientsTable.setModel(dm);
        clientsTable.getColumnModel().getColumn(2).setMaxWidth(55);
        clientsTable.getColumnModel().getColumn(2).setMinWidth(55);
        clientsTable.getColumnModel().getColumn(2).setWidth(5);
        if (!pdList.isEmpty()) {
            clientsTable.setRowSelectionInterval(DEFAULT_SELECTED_ROW, DEFAULT_SELECTED_ROW);
        }
        updateClientsCounter();
    }

    private void updateClientsCounter() {
        countClientsLabel.setText(COUNT_CLIENTS_TEXT + pdList.size());
        countClientsLabel.revalidate();
    }

    private void filterTable() {

        String fioString = fioFilterField.getText().equals(FIO_FILTER_FIELD_DEFAULT_TEXT) ? "" : fioFilterField
                .getText();
        String subscriptionString = abonementFilterField.getText().equals(ABONEMENT_FILTER_FIELD_DEFAULT_TEXT) ? ""
                : abonementFilterField.getText();

        PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
        PersonalDataExample pdExample = new PersonalDataExample();

        Criteria firstNameCriteria = pdExample.createCriteria().andFirstNameLike("%" + fioString + "%");
        Criteria lastNameCriteria = pdExample.createCriteria().andLastNameLike("%" + fioString + "%");
        Criteria middleNameCriteria = pdExample.createCriteria().andMiddleNameLike("%" + fioString + "%");

        if (onlyActiveCheckBox.getState()) {
            firstNameCriteria.andSubscriptionStateEqual(1);
            lastNameCriteria.andSubscriptionStateEqual(1);
            middleNameCriteria.andSubscriptionStateEqual(1);
        }

        if (!subscriptionString.equals("")) {
            firstNameCriteria.andSubscriptionNumberEqual(subscriptionString);
            lastNameCriteria.andSubscriptionNumberEqual(subscriptionString);
            middleNameCriteria.andSubscriptionNumberEqual(subscriptionString);
        }

        pdExample.or(firstNameCriteria);
        pdExample.or(lastNameCriteria);
        pdExample.or(middleNameCriteria);

        pdExample.setOrderByClause("last_name");
        pdList = pdMapper.selectByExampleFull(pdExample);
        DbSessionManager.closeSession();
        dm.setRecords(pdList);
        updateClientsCounter();
        clientsTable.repaint();
        pane.revalidate();
    }

    private void fillClientTab() {
        clientEditPanel.removeAll();
        PersonalData selectedPersonalData = pdList.get(selectedRow);
        clientForm = new ClientEditForm(selectedPersonalData);
        clientForm.addSaveListener(this);
        clientEditPanel.add(clientForm);
        clientEditPanel.revalidate();
    }

    private void fillSubscriptionTab() {
        subscriptionEditPanel.removeAll();
        PersonalData selectedPersonalData = pdList.get(selectedRow);
        subscriptionEditForm = new SubscriptionEditForm(selectedPersonalData);
        subscriptionEditForm.addSaveListener(this);
        subscriptionEditPanel.add(subscriptionEditForm);
        subscriptionEditPanel.repaint();
    }

    private void fillTimeTableTab() {
        timeTableEditPanel.removeAll();
        PersonalData selectedPersonalData = pdList.get(selectedRow);
        TimeTableForm timeTableForm = new TimeTableForm(selectedPersonalData);
        timeTableForm.addSaveListener(this);
        timeTableEditPanel.add(timeTableForm);
        timeTableEditPanel.repaint();
    }

    private boolean showSaveDialog() {
        FullConfirmDialog dialog = new FullConfirmDialog("Сохранить изменения?");
        dialog.setVisible(true);
        switch (dialog.getAnswer()) {
        case 0:
            if (getClientEditForm().isEdit()) {
                getClientEditForm().resetForm();
            }
            if (getSubscriptionEditForm().isEdit()) {
                getSubscriptionEditForm().resetForm();
            }
            break;
        case 1:
            if (getClientEditForm().isEdit()) {
                getClientEditForm().saveForm();
            }
            if (getSubscriptionEditForm().isEdit()) {
                getSubscriptionEditForm().saveForm();
            }
            break;
        case 2:
            return false;
        default:
            break;
        }
        return true;
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(ADD_CLIENT_BUTTON)) {
            if (checkEditingForms()) {
                if (!showSaveDialog()) {
                    return;
                }
            }
            addingClient = true;
            clientsTable.getSelectionModel().clearSelection();
            clientSpecificationPanel.repaint();
            clientEditPanel.removeAll();
            ClientEditForm form = new ClientEditForm(null);
            form.addSaveListener(this);
            clientEditPanel.add(form);
            clientEditPanel.revalidate();
        } else if (event.getActionCommand().equals(FILTER_BUTTON)) {
            if (checkEditingForms()) {
                if (!showSaveDialog()) {
                    return;
                }
            }
            filterTable();
        }

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
            if (field.getName().equals(FIO_FILTER_FIELD_NAME)) {
                if ("".equals(field.getText())) {
                    field.setForeground(Color.lightGray);
                    field.setText(FIO_FILTER_FIELD_DEFAULT_TEXT);
                }
            } else if (field.getName().equals(ABONEMENT_FILTER_FIELD_NAME)) {
                if ("".equals(field.getText())) {
                    field.setForeground(Color.lightGray);
                    field.setText(ABONEMENT_FILTER_FIELD_DEFAULT_TEXT);
                }
            }
        }
    }

    public void saveForm(Object item, boolean newItem) {

        if (item instanceof PersonalData) {
            if (newItem) {
                dm.getRecords().add((PersonalData) item);
                updateClientsCounter();
                addingClient = false;
            }
        }
        filterTable();

        clientsTable.repaint();
        clientsTable.revalidate();
        if (newItem) {
            selectedRow = dm.getRecords().indexOf(item);
            clientsTable.setRowSelectionInterval(selectedRow, selectedRow);
        }
        clientsTable.scrollRectToVisible(new Rectangle(clientsTable.getCellRect(selectedRow, 0, true)));
    }

    public void itemStateChanged(ItemEvent event) {
        filterTable();
    }

    public void stateChanged(ChangeEvent event) {
        if (pdList.isEmpty()) {
            return;
        }

        if (checkEditingForms() && !saveDialogShoswed) {
            if (!showSaveDialog()) {
                saveDialogShoswed = true;
                tabs.setSelectedIndex(selectedTab);
                return;
            }
        }
        if (checkEditingForms()) {
            saveDialogShoswed = false;
            return;
        }
        selectedTab = tabs.getSelectedIndex();
        if (event.getSource() instanceof JTabbedPane) {
            switch (tabs.getSelectedIndex()) {
            case 0:
                addButton.setVisible(true);
                fillClientTab();
                break;
            case 1:
                addButton.setVisible(false);
                fillSubscriptionTab();
                break;
            case 2:
                addButton.setVisible(false);
                fillTimeTableTab();
                break;
            default:
                break;
            }
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        if (e.getSource() instanceof ListSelectionModel) {
            if (checkEditingForms() && !saveDialogShoswed) {
                if (!showSaveDialog()) {
                    saveDialogShoswed = true;
                    if (!addingClient) {
                        clientsTable.setRowSelectionInterval(selectedRow, selectedRow);
                    } else {
                        clientsTable.clearSelection();
                    }
                    return;
                }
            }
            if (checkEditingForms()) {
                saveDialogShoswed = false;
                return;
            }

            if (clientsTable.getSelectedRow() != -1) {
                selectedRow = clientsTable.getSelectedRow();
                clientSpecificationPanel.repaint();
                // наполнение таба "Клиент"
                fillClientTab();

                // Наполнение таба "Абонемент"
                fillSubscriptionTab();

                // Отрисовка таба "Раписание"
                fillTimeTableTab();
            } else {
                clientEditPanel.removeAll();
                subscriptionEditPanel.removeAll();
            }
        }

    }

    private boolean checkEditingForms() {

        ClientEditForm clientEditForm = getClientEditForm();
        if (clientEditForm != null && clientEditForm.isEdit()) {
            return true;
        }

        SubscriptionEditForm subscriptionEditForm = getSubscriptionEditForm();
        if (subscriptionEditForm != null && subscriptionEditForm.isEdit()) {
            return true;
        }

        return false;
    }

    private ClientEditForm getClientEditForm() {
        if (clientEditPanel.getComponentCount() > 0) {
            ClientEditForm clientEditForm = (ClientEditForm) clientEditPanel.getComponent(0);
            return clientEditForm;
        }
        return null;
    }

    private SubscriptionEditForm getSubscriptionEditForm() {
        if (subscriptionEditPanel.getComponentCount() > 0) {
            SubscriptionEditForm subscriptionEditForm = (SubscriptionEditForm) subscriptionEditPanel.getComponent(0);
            return subscriptionEditForm;
        }
        return null;
    }

}
