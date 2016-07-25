/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.settings;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import ru.rybinsk.silhouette.dao.*;
import ru.rybinsk.silhouette.internal.SystemConstants;
import ru.rybinsk.silhouette.model.Settings;
import ru.rybinsk.silhouette.throwable.SilhouetteRuntimeException;

/**
 * Менеджер ibatis сессий.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class DbSessionManager {

    private static SqlSessionFactory ssf;
    private static SqlSession ss;

    public static void init() {
        TransactionFactory tf = new JdbcTransactionFactory();
        SqlSessionFactoryBuilder ssfb = new SqlSessionFactoryBuilder();
        BasicDataSource bds = new BasicDataSource();
        Class<?> driver = null;
        try {
            driver = Class.forName(SystemConstants.JDBC_DRIVER_CLASS_NAME);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        bds.setDriverClassName(driver.getClass().getName());
        bds.setUrl(SystemConstants.DB_CONNECTION_URL);
        bds.setUsername(SystemConstants.DB_USER);
        bds.setPassword(SystemConstants.DB_PASSWORD);

        Environment env = new Environment("main", tf, bds);
        Configuration config = new Configuration(env);

        // добавление мапперов
        config.addMapper(PersonalDataMapper.class);
        config.addMapper(SubscriptionMapper.class);
        config.addMapper(PaymentMapper.class);
        config.addMapper(SimulatorMapper.class);
        config.addMapper(TimeTableMapper.class);
        config.addMapper(LogMapper.class);
        config.addMapper(AmbryMapper.class);
        config.addMapper(SettingsMapper.class);

        ssf = ssfb.build(config);
    }

    private static void openSession() {
        if (ss == null) {
            ss = ssf.openSession();
        }
    }

    public static SqlSession openNewSession() {
        ss = ssf.openSession(true);
        return ss;
    }

    public static <T> T getMapper(Class<T> clazz) {
        openSession();
        return ss.getMapper(clazz);
    }

    public static void closeSession() {
        if (ss == null) {
            throw new SilhouetteRuntimeException("Сессия не открыта!");
        }
        ss.close();
        ss = null;
    }

    public static void commit() {
        if (ss == null) {
            throw new SilhouetteRuntimeException("Сессия не открыта!");
        }
        ss.commit();
    }

    public static void rollBack() {
        if (ss == null) {
            throw new SilhouetteRuntimeException("Сессия не открыта!");
        }
        ss.rollback();
    }

    public static boolean isSessionActive() {
        return !(ss == null);
    }
}
