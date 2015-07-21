package ru.rybinsk.silhouette.model;

import java.io.Serializable;
import java.util.Date;

public class Payment extends BaseModel implements Serializable {

    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column payment.Subscription_Id
     * @mbggenerated
     */
    private Integer subscriptionId;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column payment.Paid
     * @mbggenerated
     */
    private Boolean paid;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column payment.Payment_Date
     * @mbggenerated
     */
    private Date paymentDate;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database column payment.Sum
     * @mbggenerated
     */
    private String sum;
    /**
     * This field was generated by MyBatis Generator. This field corresponds to the database table payment
     * @mbggenerated
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column payment.Subscription_Id
     * @return  the value of payment.Subscription_Id
     * @mbggenerated
     */
    public Integer getSubscriptionId() {
        return subscriptionId;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column payment.Subscription_Id
     * @param subscriptionId  the value for payment.Subscription_Id
     * @mbggenerated
     */
    public void setSubscriptionId(Integer subscriptionId) {
        this.subscriptionId = subscriptionId;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column payment.Paid
     * @return  the value of payment.Paid
     * @mbggenerated
     */
    public Boolean getPaid() {
        return paid;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column payment.Paid
     * @param paid  the value for payment.Paid
     * @mbggenerated
     */
    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column payment.Payment_Date
     * @return  the value of payment.Payment_Date
     * @mbggenerated
     */
    public Date getPaymentDate() {
        return paymentDate;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column payment.Payment_Date
     * @param paymentDate  the value for payment.Payment_Date
     * @mbggenerated
     */
    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    /**
     * This method was generated by MyBatis Generator. This method returns the value of the database column payment.Sum
     * @return  the value of payment.Sum
     * @mbggenerated
     */
    public String getSum() {
        return sum;
    }

    /**
     * This method was generated by MyBatis Generator. This method sets the value of the database column payment.Sum
     * @param sum  the value for payment.Sum
     * @mbggenerated
     */
    public void setSum(String sum) {
        this.sum = sum;
    }
}