/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.pojo;

import javax.swing.JCheckBox;
import javax.swing.JTextField;

import ru.rybinsk.silhouette.model.Payment;

import com.toedter.calendar.JDateChooser;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class PaymentAssiciation {

    private JDateChooser dateChooser;
    private JTextField textField;
    private JCheckBox checkBox;

    private Payment payment;

    public PaymentAssiciation(JDateChooser dateChooser, JTextField textField, JCheckBox checkBox) {
        this.dateChooser = dateChooser;
        this.textField = textField;
        this.checkBox = checkBox;
    }

    public JDateChooser getDateChooser() {
        return dateChooser;
    }

    public void setDateChooser(JDateChooser dateChooser) {
        this.dateChooser = dateChooser;
    }

    public JTextField getTextField() {
        return textField;
    }

    public void setTextField(JTextField textField) {
        this.textField = textField;
    }

    public JCheckBox getCheckBox() {
        return checkBox;
    }

    public void setCheckBox(JCheckBox checkBox) {
        this.checkBox = checkBox;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void fillPayment() {
        if (payment == null) {
            payment = new Payment();
        }
        payment.setPaymentDate(dateChooser.getDate());
        payment.setSum(textField.getText());
        payment.setPaid(checkBox.isSelected());
    }
}
