package ru.rybinsk.silhouette.ui;

import net.miginfocom.swing.MigLayout;
import org.apache.commons.lang.ArrayUtils;
import ru.rybinsk.silhouette.services.SettingsService;
import ru.rybinsk.silhouette.services.impl.SettingsServiceImpl;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FilenameFilter;

/**
 * Интерфейс настройки резервного копирования.
 *
 * @author Alexey Smirnov (smirnov89@bk.ru)
 */
public class BackupSettingsView extends JPanel implements ActionListener {

    private static final String BROWSE_SAVE_PATH_COMMAND = "browseSavePath";
    private static final String BROWSE_MYSQL_HOME_COMMAND = "browseMysqlHome";
    private static final String SAVE_COMMAND = "save";

    private static final String[] MYSQL_DIR_FILE_NAMES = new String[]{"mysql.exe","mysqldump.exe"};

    private JTextField savePathField;
    private JTextField mysqlHomeField;
    private JSpinner backupCountsField;
    private JCheckBox doOnStartupField;

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
        JLabel backupCountsLabel = new JLabel("Количество сохраняемых бэкапов");
        backupCountsField = new JSpinner();
        JLabel doOnStartupLabel = new JLabel("Делать резервную копию при старте");
        doOnStartupField = new JCheckBox();

        JButton browseSavePathBtn = new JButton("Обзор");
        JButton browseMysqlHomeBtn = new JButton("Обзор");
        JButton saveButton = new JButton("Сохранить");

        browseSavePathBtn.setActionCommand(BROWSE_SAVE_PATH_COMMAND);
        browseSavePathBtn.addActionListener(this);
        browseMysqlHomeBtn.setActionCommand(BROWSE_MYSQL_HOME_COMMAND);
        browseMysqlHomeBtn.addActionListener(this);
        saveButton.addActionListener(this);
        saveButton.setActionCommand(SAVE_COMMAND);
        savePathField.setEditable(false);
        savePathField.setText(settingsService.getSetting(SettingsServiceImpl.SettingNames.SAVE_PATH));
        mysqlHomeField.setEditable(false);
        mysqlHomeField.setText(settingsService.getSetting(SettingsServiceImpl.SettingNames.MYSQL_HOME));
        backupCountsField.setValue(new Integer(settingsService.getSetting(SettingsServiceImpl.SettingNames.BACKUP_COUNTS)));
        doOnStartupField.setSelected(new Boolean(settingsService.getSetting(SettingsServiceImpl.SettingNames.DO_ON_STARTUP)));

        add(mysqlHomeLabel, "cell 0 1");
        add(mysqlHomeField, "span 2 1");
        add(browseMysqlHomeBtn);
        add(savePathLabel, "cell 0 2");
        add(savePathField, "span 2 1");
        add(browseSavePathBtn);
        add(backupCountsLabel, "cell 0 3");
        add(backupCountsField, "span 2 1");
        add(doOnStartupLabel, "cell 0 4");
        add(doOnStartupField, "span 2 1");
        add(saveButton, "cell 0 5");
    }

    private void saveChanges() {

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
        } else{
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
        }
    }

    private interface FileValidator {
        boolean validate(File file);
    }
}
