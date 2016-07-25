package ru.rybinsk.silhouette.services;

import java.util.Map;

/**
 * Created by ASmirnov on 7/24/2016.
 */
public interface SettingsService {

    /**
     * Получить настройку по имени.
     *
     * @param name
     * @return
     */
    public String getSetting(String name);

    /**
     * Сохранить настройку.
     *
     * @param name
     * @param value
     */
    public void saveSetting(String name, String value);

    /**
     * Сохранить настройки.
     *
     * @param settings
     */
    public void saveSettings(Map<String, String> settings);
}
