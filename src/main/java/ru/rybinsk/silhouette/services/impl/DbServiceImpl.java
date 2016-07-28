package ru.rybinsk.silhouette.services.impl;

import org.apache.commons.lang.ArrayUtils;
import ru.rybinsk.silhouette.internal.SystemConstants;
import ru.rybinsk.silhouette.services.DbService;
import ru.rybinsk.silhouette.services.SettingsService;
import ru.rybinsk.silhouette.throwable.SilhouetteRuntimeException;
import ru.rybinsk.silhouette.util.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 */
public class DbServiceImpl implements DbService {

    private static final String EXECUTE_CMD_TEMPLATE = "%s/mysqldump -u %s -p%s --database %s -r %s";
    private static final String BACKUP_NAME_PREFIX = "silhouette_backup_";
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy_HHmmss");

    private static DbService instance;

    public static DbService getInstance() {
        if (instance == null) {
            instance = new DbServiceImpl();
        }
        return instance;
    }

    private void checkPaths(String mysqlPath, String savePath, String additionalSavePath) throws FileNotFoundException {
        File saveDir = new File(savePath);
        if (!saveDir.exists()) {
            throw new FileNotFoundException("Каталог для сохранения резервной копии не существует!");
        }

        if (additionalSavePath != null) {
            File addSaveDir = new File(additionalSavePath);
            if (!addSaveDir.exists()) {
                throw new FileNotFoundException("Каталог для сохранения дополнительной резервной копии не существует!");
            }
        }

        File mysqlDir = new File(mysqlPath);
        if (!mysqlDir.exists()) {
            throw new FileNotFoundException("Каталог c MySQL не существует!");
        }
    }

    private void doBackup(String mysqlPath, String savePath) {
        String executeCmd = String.format(EXECUTE_CMD_TEMPLATE, mysqlPath, SystemConstants.DB_USER,
                SystemConstants.DB_PASSWORD, SystemConstants.DB_NAME,
                savePath + BACKUP_NAME_PREFIX + simpleDateFormat.format(new Date()) + ".sql");
        try {
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                Logger.log("Создание резервной копии. Успешно.");
            } else {
                Logger.log("Создание резервной копии. Провал.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void sortFiles(File[] existBackups) {
        Arrays.sort(existBackups, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                String o1DateStr = o1.getName().replaceAll(BACKUP_NAME_PREFIX, "").replaceAll(".sql", "");
                String o2DateStr = o2.getName().replaceAll(BACKUP_NAME_PREFIX, "").replaceAll(".sql", "");

                try {
                    Date o1Date = simpleDateFormat.parse(o1DateStr);
                    Date o2Date = simpleDateFormat.parse(o2DateStr);
                    return o1Date.after(o2Date) ? 1 : -1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
    }

    private void createOneBackup(String mysqlPath, String savePath, Integer backupCounts) throws FileNotFoundException{
        File saveDir = new File(savePath);
        File[] existBackups = saveDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(BACKUP_NAME_PREFIX);
            }
        });

        if (existBackups.length < backupCounts) {
            doBackup(mysqlPath, savePath);
        } else {
            sortFiles(existBackups);
            for (int i = 0; i < existBackups.length - backupCounts + 1; i++) {
                boolean deleteResult = existBackups[i].delete();
                if (!deleteResult) {
                    throw new SilhouetteRuntimeException("Ошибка удаления файла [" + existBackups[i].getName() + "]");
                }
            }
            doBackup(mysqlPath, savePath);
        }
    }

    @Override
    public void createBackup(String mysqlPath, String savePath, String additionalSavePath) throws FileNotFoundException {
        checkPaths(mysqlPath, savePath, additionalSavePath);
        SettingsService settingsService = SettingsServiceImpl.getInstance();
        Integer backupCounts = Integer.valueOf(settingsService.getSetting(SettingsServiceImpl.SettingNames.BACKUP_COUNTS));
        Logger.log("Создание резервной копии");
        createOneBackup(mysqlPath, savePath, backupCounts);
        if (additionalSavePath != null){
            createOneBackup(mysqlPath, additionalSavePath, backupCounts);
        }
    }
}
