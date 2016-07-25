/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ru.rybinsk.silhouette.services.DbService;
import ru.rybinsk.silhouette.services.SettingsService;
import ru.rybinsk.silhouette.services.impl.DbServiceImpl;
import ru.rybinsk.silhouette.services.impl.SettingsServiceImpl;
import ru.rybinsk.silhouette.settings.ApplicationHolder;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.ui.ErrorDialog;
import ru.rybinsk.silhouette.ui.SilhouetteMainFrame;
import ru.rybinsk.silhouette.util.Logger;

/**
 * Точка входа в приложение.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class App {

    public static void main(String[] args) {
        // инициализация переменных
        ApplicationHolder.init();
        // инициализация менеджера сессий.
        DbSessionManager.init();
        Logger.log("Запуск приложения");
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        // Отображение UI
        SilhouetteMainFrame mainFrame = new SilhouetteMainFrame();
        mainFrame.setExtendedState(mainFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        // mainFrame.setResizable(false);
        ApplicationHolder.setMainFrame(mainFrame);
        mainFrame.setVisible(true);

        SettingsService settingsService = SettingsServiceImpl.getInstance();
        if (Boolean.parseBoolean(settingsService.getSetting(SettingsServiceImpl.SettingNames.DO_ON_STARTUP))){
            DbService dbService = DbServiceImpl.getInstance();
            try {
                dbService.createBackup(settingsService.getSetting(SettingsServiceImpl.SettingNames.MYSQL_HOME),
                        settingsService.getSetting(SettingsServiceImpl.SettingNames.SAVE_PATH));
            } catch (Exception e1) {
                e1.printStackTrace();
                ErrorDialog errorDialog = new ErrorDialog("Ошибка при создании бэкапа! " + e1.getMessage());
                errorDialog.setVisible(true);
            }
        }
    }
}
