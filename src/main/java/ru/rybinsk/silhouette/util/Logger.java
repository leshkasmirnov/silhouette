/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.util;

import java.util.Date;

import ru.rybinsk.silhouette.dao.LogMapper;
import ru.rybinsk.silhouette.model.Log;
import ru.rybinsk.silhouette.settings.DbSessionManager;

/**
 * Логгер
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class Logger {
    public static void log(Date date, String message) {
        LogMapper logMapper = DbSessionManager.getMapper(LogMapper.class);

        Log logItem = new Log();
        logItem.setDate(date);
        logItem.setMessage(message);

        logMapper.insert(logItem);

        DbSessionManager.commit();
        DbSessionManager.closeSession();
    }

    public static void log(String message) {
        log(new Date(), message);
    }

}
