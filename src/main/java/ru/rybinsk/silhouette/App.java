/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import ru.rybinsk.silhouette.settings.ApplicationHolder;
import ru.rybinsk.silhouette.settings.DbSessionManager;
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

    }
}
