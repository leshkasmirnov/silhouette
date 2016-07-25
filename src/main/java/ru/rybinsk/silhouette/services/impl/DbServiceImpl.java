package ru.rybinsk.silhouette.services.impl;

import ru.rybinsk.silhouette.internal.SystemConstants;
import ru.rybinsk.silhouette.services.DbService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 *
 */
public class DbServiceImpl implements DbService {

    public static final String EXECUTE_CMD_TEMPLATE = "C:/dev/MySQL/MySQL Server 5.6/bin/mysqldump -u %s -p%s --database %s -r %s";

    private static DbService instance;

    public static DbService  getInstance(){
        if (instance == null){
            instance = new DbServiceImpl();
        }
        return instance;
    }

    @Override
    public void createBackup(String savePath) throws FileNotFoundException{
        File saveDir = new File(savePath);
        if (!saveDir.exists()){
            throw new FileNotFoundException("Каталог для сохранения резервной копии не существует!");
        }
        String executeCmd = String.format(EXECUTE_CMD_TEMPLATE, SystemConstants.DB_USER, SystemConstants.DB_PASSWORD, SystemConstants.DB_NAME, savePath + "1.sql");
        try {
            Process runtimeProcess = Runtime.getRuntime().exec(executeCmd);
            int processComplete = runtimeProcess.waitFor();

            if (processComplete == 0) {
                System.out.println("Backup Complete");
            } else {
                System.out.println("Backup Failure");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
