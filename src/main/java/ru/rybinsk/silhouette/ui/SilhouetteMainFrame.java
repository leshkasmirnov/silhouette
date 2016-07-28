/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui;

import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.internal.SystemConstants;
import ru.rybinsk.silhouette.services.DbService;
import ru.rybinsk.silhouette.services.SettingsService;
import ru.rybinsk.silhouette.services.impl.DbServiceImpl;
import ru.rybinsk.silhouette.services.impl.SettingsServiceImpl;
import ru.rybinsk.silhouette.ui.components.ConfirmDialog;
import ru.rybinsk.silhouette.ui.forms.MainTimeTable;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class SilhouetteMainFrame extends JFrame implements ActionListener, WindowListener {

    private static final String REPORTS_MUNU_ITEM = "reportsMunuItem";
    private static final String TIME_TABLE_MENU_ITEM = "timeTableMenuItem";
    private static final String CLIENTS_LIST_MENU_ITEM = "clientsList";
    private static final String BACKUP_SETTINGS_MENU_ITEM = "backupSettingsMenuItem";

    private static final long serialVersionUID = 1L;
    private static final String MAIN_TITLE = "Спортивно-оздоровительный клуб \"Силуэт\". Версия "
            + SystemConstants.VERSION;
    private JMenu clientsMenu;
    private JMenu timeTableMenu;
    private JMenu reportsMenu;
    private JMenu settingsMenu;

    public SilhouetteMainFrame() {
        setTitle(MAIN_TITLE);
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize().getWidth());
        int ySize = ((int) tk.getScreenSize().getHeight());
        // setSize(xSize, ySize);
        // setLocation(210, 30);

        // setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        // меню
        JMenuBar menuBar = new JMenuBar();
        clientsMenu = new JMenu("Клиенты");
        timeTableMenu = new JMenu("Расписание");
        reportsMenu = new JMenu("Отчеты");
        settingsMenu = new JMenu("Настройки");

        JMenuItem clientsMenuItem = new JMenuItem("Список клиентов");
        JMenuItem timeTableMenuItem = new JMenuItem("Тренажеры");
        JMenuItem reportsMenuItem = new JMenuItem("Отчеты");
        JMenuItem backupSettingsMenuItem = new JMenuItem("Настройки резервного копирования");

        timeTableMenuItem.setActionCommand(TIME_TABLE_MENU_ITEM);
        clientsMenuItem.setActionCommand(CLIENTS_LIST_MENU_ITEM);
        reportsMenuItem.setActionCommand(REPORTS_MUNU_ITEM);
        backupSettingsMenuItem.setActionCommand(BACKUP_SETTINGS_MENU_ITEM);

        clientsMenu.add(clientsMenuItem);
        timeTableMenu.add(timeTableMenuItem);
        settingsMenu.add(backupSettingsMenuItem);

        menuBar.add(clientsMenu);
        menuBar.add(timeTableMenu);
        menuBar.add(reportsMenu);
        menuBar.add(settingsMenu);
        reportsMenu.add(reportsMenuItem);

        timeTableMenuItem.addActionListener(this);
        clientsMenuItem.addActionListener(this);
        reportsMenuItem.addActionListener(this);
        backupSettingsMenuItem.addActionListener(this);

        getContentPane().setLayout(new MigLayout());
        setJMenuBar(menuBar);
        addWindowListener(this);
        goToClientsView();
    }

    private void goToClientsView() {
        ClientsView clientsView = new ClientsView();
        getContentPane().removeAll();
        add(clientsView);
        getContentPane().repaint();
        getContentPane().revalidate();
        clientsMenu.setSelected(true);
        reportsMenu.setSelected(false);
        timeTableMenu.setSelected(false);
        settingsMenu.setSelected(false);
        // pack();
    }

    public void actionPerformed(ActionEvent event) {
        if (event.getActionCommand().equals(CLIENTS_LIST_MENU_ITEM)) {
            // JProgressBar progressBar = new JProgressBar();
            // progressBar.setIndeterminate(true);
            goToClientsView();

        } else if (event.getActionCommand().equals(TIME_TABLE_MENU_ITEM)) {
            MainTimeTable mainTimeTable = new MainTimeTable(null);
            getContentPane().removeAll();
            add(mainTimeTable);
            getContentPane().repaint();
            getContentPane().revalidate();
            pack();
            this.setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
            clientsMenu.setSelected(false);
            reportsMenu.setSelected(false);
            timeTableMenu.setSelected(true);
            settingsMenu.setSelected(false);
        } else if (event.getActionCommand().equals(REPORTS_MUNU_ITEM)) {
            getContentPane().removeAll();
            ReportsView reportsView = new ReportsView();
            add(reportsView);
            getContentPane().repaint();
            getContentPane().revalidate();
            clientsMenu.setSelected(false);
            reportsMenu.setSelected(true);
            timeTableMenu.setSelected(false);
            settingsMenu.setSelected(false);
            // pack();
        }else if (BACKUP_SETTINGS_MENU_ITEM.equals(event.getActionCommand())){
            getContentPane().removeAll();
            BackupSettingsView backupSettingsView = new BackupSettingsView();
            add(backupSettingsView);
            getContentPane().repaint();
            getContentPane().revalidate();
            clientsMenu.setSelected(false);
            reportsMenu.setSelected(false);
            timeTableMenu.setSelected(false);
            settingsMenu.setSelected(true);
        }
    }

    @Override
    public void windowActivated(WindowEvent arg0) {

    }

    @Override
    public void windowClosed(WindowEvent arg0) {
    }

    @Override
    public void windowClosing(WindowEvent event) {
        ConfirmDialog dialog = new ConfirmDialog("Вы действительно хотите выйти из программы ?");
        dialog.setVisible(true);
        if (dialog.getAnswer()) {
            SettingsService settingsService = SettingsServiceImpl.getInstance();
            if (Boolean.parseBoolean(settingsService.getSetting(SettingsServiceImpl.SettingNames.DO_ON_EXIT))){
                DbService dbService = DbServiceImpl.getInstance();
                try {
                    dbService.createBackup(settingsService.getSetting(SettingsServiceImpl.SettingNames.MYSQL_HOME),
                            settingsService.getSetting(SettingsServiceImpl.SettingNames.SAVE_PATH),
                            settingsService.getSetting(SettingsServiceImpl.SettingNames.ADDITIONAL_SAVE_PATH));
                    System.exit(0);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    ErrorDialog errorDialog = new ErrorDialog("Ошибка при создании бэкапа! " + e1.getMessage());
                    errorDialog.setVisible(true);
                    errorDialog.addWindowListener(new WindowListener() {
                        @Override
                        public void windowOpened(WindowEvent e) {
                        }
                        @Override
                        public void windowClosing(WindowEvent e) {
                        }
                        @Override
                        public void windowClosed(WindowEvent e) {
                        }
                        @Override
                        public void windowIconified(WindowEvent e) {
                        }
                        @Override
                        public void windowDeiconified(WindowEvent e) {
                        }
                        @Override
                        public void windowActivated(WindowEvent e) {
                        }
                        @Override
                        public void windowDeactivated(WindowEvent e) {
                            System.exit(0);
                        }
                    });
                }
            }else{
                System.exit(0);
            }

        }
    }

    @Override
    public void windowDeactivated(WindowEvent arg0) {
    }

    @Override
    public void windowDeiconified(WindowEvent arg0) {
    }

    @Override
    public void windowIconified(WindowEvent arg0) {
    }

    @Override
    public void windowOpened(WindowEvent arg0) {
    }
}
