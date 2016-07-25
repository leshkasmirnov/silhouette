package ru.rybinsk.silhouette.services.impl;

import ru.rybinsk.silhouette.dao.SettingsMapper;
import ru.rybinsk.silhouette.model.Settings;
import ru.rybinsk.silhouette.model.SettingsExample;
import ru.rybinsk.silhouette.services.SettingsService;
import ru.rybinsk.silhouette.settings.DbSessionManager;

import java.util.List;
import java.util.Map;

/**
 * Created by ASmirnov on 7/24/2016.
 */
public class SettingsServiceImpl implements SettingsService {

    public class SettingNames{
        public static final String MYSQL_HOME = "mysql.home";
        public static final String SAVE_PATH = "save.path";
        public static final String BACKUP_COUNTS = "backup.counts";
        public static final String DO_ON_STARTUP = "do.on.startup";
    }

    private static SettingsService instance;

    public static SettingsService getInstance(){
        if (instance == null){
            instance = new SettingsServiceImpl();
        }
        return instance;
    }

    @Override
    public String getSetting(String name) {
        SettingsMapper settingsMapper = DbSessionManager.getMapper(SettingsMapper.class);
        SettingsExample settingsExample = new SettingsExample();
        settingsExample.createCriteria().andParamNameEqualTo(name);
        List<Settings> finded = settingsMapper.selectByExample(settingsExample);
        if (finded.size() == 0){
            throw new RuntimeException("Не найдена настройка: [" + name + "]");
        }
        return finded.get(0).getParamValue();
    }

    @Override
    public void saveSetting(String name, String value) {

    }

    @Override
    public void saveSettings(Map<String, String> settings) {

    }
}
