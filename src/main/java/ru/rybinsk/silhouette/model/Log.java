package ru.rybinsk.silhouette.model;

import java.io.Serializable;
import java.util.Date;

public class Log extends BaseModel implements Serializable {

    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column log.Date
     * @mbggenerated
     */
    private Date date;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column log.Message
     * @mbggenerated
     */
    private String message;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database table log
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column log.Date
     * @return  the value of log.Date
     * @mbggenerated
     */
    public Date getDate() {
        return date;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column log.Date
     * @param date  the value for log.Date
     * @mbggenerated
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column log.Message
     * @return  the value of log.Message
     * @mbggenerated
     */
    public String getMessage() {
        return message;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column log.Message
     * @param message  the value for log.Message
     * @mbggenerated
     */
    public void setMessage(String message) {
        this.message = message;
    }
}