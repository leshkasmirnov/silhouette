package ru.rybinsk.silhouette.services;

import java.io.FileNotFoundException;

/**
 * Created by ASmirnov on 7/20/2016.
 */
public interface DbService {

    /**
     * Backup database.
     *
     * @param savePath - path to save
     */
    public void createBackup(String mysqlPath, String savePath, String additionalSavePath) throws FileNotFoundException;
}
