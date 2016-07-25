package ru.rybinsk.silhouette.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import ru.rybinsk.silhouette.model.Settings;
import ru.rybinsk.silhouette.model.SettingsExample;

public interface SettingsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int countByExample(SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int deleteByExample(SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int insert(Settings record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int insertSelective(Settings record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    List<Settings> selectByExample(SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    Settings selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Settings record, @Param("example") SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Settings record, @Param("example") SettingsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Settings record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table settings
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Settings record);
}