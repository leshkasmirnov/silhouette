/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.settings;

import ru.rybinsk.silhouette.ui.SilhouetteMainFrame;

/**
 * Класс для хранения переменных потока.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ApplicationHolder {

    private static SilhouetteMainFrame mainFrame;

    /**
     * Инициализация.
     */
    public static void init() {

    }

    public static SilhouetteMainFrame getMainFrame() {
        return mainFrame;
    }

    public static void setMainFrame(SilhouetteMainFrame mainFrame) {
        ApplicationHolder.mainFrame = mainFrame;
    }

}
