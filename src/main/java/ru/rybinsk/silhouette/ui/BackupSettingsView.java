package ru.rybinsk.silhouette.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.ArrayUtils;
import ru.rybinsk.silhouette.services.DbService;
import ru.rybinsk.silhouette.services.SettingsService;
import ru.rybinsk.silhouette.services.impl.DbServiceImpl;
import ru.rybinsk.silhouette.services.impl.SettingsServiceImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.HashMap;
import java.util.Map;

/**
 * Интерфейс настройки резервного копирования.
 *
 * @author Alexey Smirnov (smirnov89@bk.ru)
 */
public class BackupSettingsView extends JPanel implements ActionListener {

    private static final String BROWSE_SAVE_PATH_COMMAND = "browseSavePath";
    private static final String BROWSE_ADD_SAVE_PATH_COMMAND = "browseAddSavePath";
    private static final String BROWSE_MYSQL_HOME_COMMAND = "browseMysqlHome";
    private static final String SAVE_COMMAND = "save";
    private static final String BACKUP_COMMAND = "backup";

    private static final String[] MYSQL_DIR_FILE_NAMES = new String[]{"mysql.exe", "mysqldump.exe"};

    private JTextField savePathField;
    private JTextField mysqlHomeField;
    private JSpinner backupCountsField;
    private JCheckBox doOnStartupField;
    private JCheckBox doOnExitField;
    private JTextField addSavePathField;

    public BackupSettingsView() {
        setLayout(new MigLayout());
        paint();
    }

    private void paint() {
        SettingsService settingsService = SettingsServiceImpl.getInstance();

        JLabel mysqlHomeLabel = new JLabel("Путь до MySQL");
        mysqlHomeField = new JTextField(30);
        JLabel savePathLabel = new JLabel("Путь для сохранения резервных копий");
        savePathField = new JTextField(30);
        JLabel addSavePathLabel = new JLabel("Путь для сохранения дополнительных резервных копий");
        addSavePathField = new JTextField(30);
        JLabel backupCountsLabel = new JLabel("Количество сохраняемых бэкапов");
        SpinnerModel sm = new SpinnerNumberModel(Integer.parseInt(settingsService.getSetting(SettingsServiceImpl.SettingNames.BACKUP_COUNTS)), 1, 50, 1);
        backupCountsField = new JSpinner(sm);
        JLabel doOnStartupLabel = new JLabel("Делать резервную копию при старте");
        doOnStartupField = new JCheckBox();
        JLabel doOnExitLabel = new JLabel("Делать резервную копию при закрытии");
        doOnExitField = new JCheckBox();

        JButton browseSavePathBtn = new JButton("Обзор");
        JButton browseAddSavePathBtn = new JButton("Обзор");
        JButton browseMysqlHomeBtn = new JButton("Обзор");
        JButton saveButton = new JButton("Сохранить");
        JButton backupButton = new JButton("Сделать резервную копию");

        browseSavePathBtn.setActionCommand(BROWSE_SAVE_PATH_COMMAND);
        browseSavePathBtn.addActionListener(this);
        browseAddSavePathBtn.setActionCommand(BROWSE_ADD_SAVE_PATH_COMMAND);
        browseAddSavePathBtn.addActionListener(this);
        browseMysqlHomeBtn.setActionCommand(BROWSE_MYSQL_HOME_COMMAND);
        browseMysqlHomeBtn.addActionListener(this);
        saveButton.addActionListener(this);
        saveButton.setActionCommand(SAVE_COMMAND);
        backupButton.addActionListener(this);
        backupButton.setActionCommand(BACKUP_COMMAND);
        savePathField.setEditable(false);
        savePathField.setText(settingsService.getSetting(SettingsServiceImpl.SettingNames.SAVE_PATH));
        addSavePathField.setEditable(false);
        addSavePathField.setText(settingsService.getSetting(SettingsServiceImpl.SettingNames.ADDITIONAL_SAVE_PATH));
        mysqlHomeField.setEditable(false);
        mysqlHomeField.setText(settingsService.getSetting(SettingsServiceImpl.SettingNames.MYSQL_HOME));
        doOnStartupField.setSelected(new Boolean(settingsService.getSetting(SettingsServiceImpl.SettingNames.DO_ON_STARTUP)));
        doOnExitField.setSelected(new Boolean(settingsService.getSetting(SettingsServiceImpl.SettingNames.DO_ON_EXIT)));

        add(mysqlHomeLabel, "cell 0 1");
        add(mysqlHomeField, "span 2 1");
        add(browseMysqlHomeBtn);
        add(savePathLabel, "cell 0 2");
        add(savePathField, "span 2 1");
        add(browseSavePathBtn);
        add(addSavePathLabel, "cell 0 3");
        add(addSavePathField, "span 2 1");
        add(browseAddSavePathBtn);
        add(backupCountsLabel, "cell 0 4");
        add(backupCountsField, "span 2 1");
        add(doOnStartupLabel, "cell 0 5");
        add(doOnStartupField, "span 2 1");
        add(doOnExitLabel, "cell 0 6");
        add(doOnExitField, "span 2 1");
        add(saveButton, "cell 0 7");
        add(backupButton);
    }

    private void saveChanges() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put(SettingsServiceImpl.SettingNames.MYSQL_HOME, mysqlHomeField.getText());
        settings.put(SettingsServiceImpl.SettingNames.SAVE_PATH, savePathField.getText());
        settings.put(SettingsServiceImpl.SettingNames.BACKUP_COUNTS, String.valueOf(backupCountsField.getValue()));
        settings.put(SettingsServiceImpl.SettingNames.DO_ON_STARTUP, String.valueOf(doOnStartupField.isSelected()));
        settings.put(SettingsServiceImpl.SettingNames.DO_ON_EXIT, String.valueOf(doOnExitField.isSelected()));
        settings.put(SettingsServiceImpl.SettingNames.ADDITIONAL_SAVE_PATH, String.valueOf(addSavePathField.getText()));

        SettingsService settingsService = SettingsServiceImpl.getInstance();
        settingsService.saveSettings(settings);
    }

    private void showFileChooser(JTextField jTextField, FileValidator fileValidator) {
        JFileChooser fc = new JFileChooser(new File(jTextField.getText()));
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        JFrame jFrame = new JFrame();
        fc.showOpenDialog(jFrame);
        File selFile = fc.getSelectedFile();
        boolean success = true;
        if (fileValidator != null) {
            success = fileValidator.validate(selFile);
        }
        if (success) {
            jTextField.setText(selFile.getAbsolutePath());
        } else {
            ErrorDialog errorDialog = new ErrorDialog("Не верный каталог [" + selFile.getAbsolutePath() + "] !");
            errorDialog.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (BROWSE_SAVE_PATH_COMMAND.equals(e.getActionCommand())) {
            showFileChooser(savePathField, null);
        } else if (BROWSE_MYSQL_HOME_COMMAND.equals(e.getActionCommand())) {
            showFileChooser(mysqlHomeField, new FileValidator() {
                @Override
                public boolean validate(File file) {
                    String[] result = file.list(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return ArrayUtils.contains(MYSQL_DIR_FILE_NAMES, name);
                        }
                    });
                    return result.length == 2;
                }
            });
        } else if (SAVE_COMMAND.equals(e.getActionCommand())) {
            saveChanges();
        } else if (BACKUP_COMMAND.equals(e.getActionCommand())) {
            SettingsService settingsService = SettingsServiceImpl.getInstance();
            DbService dbService = DbServiceImpl.getInstance();
            try {
                dbService.createBackup(settingsService.getSetting(SettingsServiceImpl.SettingNames.MYSQL_HOME),
                        settingsService.getSetting(SettingsServiceImpl.SettingNames.SAVE_PATH),
                        settingsService.getSetting(SettingsServiceImpl.SettingNames.ADDITIONAL_SAVE_PATH));
            } catch (Exception e1) {
                e1.printStackTrace();
                ErrorDialog errorDialog = new ErrorDialog("Ошибка при создании бэкапа! " + e1.getMessage());
                errorDialog.setVisible(true);
            }
        } else if (BROWSE_ADD_SAVE_PATH_COMMAND.equals(e.getActionCommand())) {
            showFileChooser(addSavePathField, null);
        }
    }

    private interface FileValidator {
        boolean validate(File file);
    }
}
