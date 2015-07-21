/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.forms;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.ui.listeners.SaveFormListener;

/**
 * Панель настроек для клиента. (табы клиент, абонемент, расписание).
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ClientSpecificationPanel extends JPanel implements ChangeListener {

    private static final long serialVersionUID = -5388128269220357154L;

    private PersonalData selectedPersonalData;
    private SaveFormListener saveFormListener;
    private ChangeListener changeListener;

    private JTabbedPane tabs;
    private JPanel clientEditPanel;
    private JPanel subscriptionEditPanel;
    private JPanel timeTableEditPanel;

    private ClientEditForm clientForm;

    private SubscriptionEditForm subscriptionEditForm;

    private TimeTableForm timeTableForm;

    /**
     * Конструктор.
     * 
     * @param selectedPersonalData
     *            - объект {@link PersonalData}
     * @param saveFormListener
     */
    public ClientSpecificationPanel(PersonalData selectedPersonalData) {
        this(selectedPersonalData, null, null);
    }

    /**
     * Конструктор.
     * 
     * @param selectedPersonalData
     *            - объект {@link PersonalData}
     * @param saveFormListener
     *            - лиснер для форм клиента.
     */
    public ClientSpecificationPanel(PersonalData selectedPersonalData, SaveFormListener saveFormListener) {
        this(selectedPersonalData, saveFormListener, null);
    }

    /**
     * Конструктор.
     * 
     * @param selectedPersonalData
     *            - объект {@link PersonalData}
     * @param saveFormListener
     *            - лиснер для форм клиента.
     * @param changeListener
     *            - лиснер для панели табов.
     */
    public ClientSpecificationPanel(PersonalData selectedPersonalData, SaveFormListener saveFormListener,
            ChangeListener changeListener) {
        super(new GridLayout());
        this.selectedPersonalData = selectedPersonalData;
        this.saveFormListener = saveFormListener;
        this.changeListener = changeListener;
        createContent();
    }

    private void createContent() {
        tabs = new JTabbedPane();
        if (changeListener != null) {
            tabs.addChangeListener(changeListener);
        }
        tabs.addChangeListener(this);

        clientEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        subscriptionEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeTableEditPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        tabs.add("Клиент", clientEditPanel);
        tabs.add("Абонемент", subscriptionEditPanel);
        tabs.add("Расписание", timeTableEditPanel);

        add(tabs);
    }

    @Override
    public void stateChanged(ChangeEvent event) {
        if (event.getSource() instanceof JTabbedPane) {
            fillTab();
        }
    }

    private void fillTab() {
        switch (tabs.getSelectedIndex()) {
        case 0:
            fillClientTab();
            break;
        case 1:
            fillSubscriptionTab();
            break;
        case 2:
            fillTimeTableTab();
            break;
        default:
            break;
        }
    }

    private void fillClientTab() {
        clientEditPanel.removeAll();
        clientForm = new ClientEditForm(selectedPersonalData);
        if (saveFormListener != null) {
            clientForm.addSaveListener(saveFormListener);
        }
        clientEditPanel.add(clientForm);
        clientEditPanel.revalidate();
    }

    private void fillSubscriptionTab() {
        subscriptionEditPanel.removeAll();
        subscriptionEditForm = new SubscriptionEditForm(selectedPersonalData);
        if (saveFormListener != null) {
            subscriptionEditForm.addSaveListener(saveFormListener);
        }
        subscriptionEditPanel.add(subscriptionEditForm);
        subscriptionEditPanel.repaint();
    }

    private void fillTimeTableTab() {
        timeTableEditPanel.removeAll();
        timeTableForm = new TimeTableForm(selectedPersonalData);
        if (saveFormListener != null) {
            timeTableForm.addSaveListener(saveFormListener);
        }
        timeTableEditPanel.add(timeTableForm);
        timeTableEditPanel.repaint();
    }

    public void setSelectedPersonalData(PersonalData selectedPersonalData) {
        this.selectedPersonalData = selectedPersonalData;
        fillTab();
    }

    public JPanel getClientEditPanel() {
        return clientEditPanel;
    }

    public JPanel getSubscriptionEditPanel() {
        return subscriptionEditPanel;
    }

    public JPanel getTimeTableEditPanel() {
        return timeTableEditPanel;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }

    public void setSaveFormListener(SaveFormListener saveFormListener) {
        this.saveFormListener = saveFormListener;
        if (saveFormListener != null) {
            clientForm.addSaveListener(saveFormListener);
            subscriptionEditForm.addSaveListener(saveFormListener);
            timeTableForm.addSaveListener(saveFormListener);
        }
    }

    public void setChangeListener(ChangeListener changeListener) {
        this.changeListener = changeListener;
        if (changeListener != null) {
            tabs.addChangeListener(changeListener);
        }
    }

}
