package ru.rybinsk.silhouette.ui;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.settings.ApplicationHolder;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Created by ASmirnov on 7/24/2016.
 */
public class ErrorDialog extends JDialog {

    public ErrorDialog(String message) {
        setLayout(new MigLayout());
        setResizable(false);
        setTitle("Ошибка!");
        setLocationRelativeTo(ApplicationHolder.getMainFrame());

        JLabel messageLabel = new JLabel(message);
        JButton okButton = new JButton("OK");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ErrorDialog.this.setVisible(false);
            }
        });
        add(messageLabel, "cell 0 1");
        add(okButton, "cell 0 2, center");
        pack();
    }
}
