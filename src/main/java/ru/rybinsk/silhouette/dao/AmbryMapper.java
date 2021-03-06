package ru.rybinsk.silhouette.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import ru.rybinsk.silhouette.model.Ambry;
import ru.rybinsk.silhouette.model.AmbryExample;

public interface AmbryMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int countByExample(AmbryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int deleteByExample(AmbryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int insert(Ambry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int insertSelective(Ambry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    List<Ambry> selectByExample(AmbryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    Ambry selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int updateByExampleSelective(@Param("record") Ambry record, @Param("example") AmbryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int updateByExample(@Param("record") Ambry record, @Param("example") AmbryExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int updateByPrimaryKeySelective(Ambry record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table ambry
     *
     * @mbggenerated
     */
    int updateByPrimaryKey(Ambry record);
}