/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.settings.ApplicationHolder;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class FullConfirmDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 5825343486818087457L;

    private JPanel myPanel = null;
    private JButton yesButton = null;
    private JButton noButton = null;
    private JButton cancelButton = null;
    private int answer = 0;

    public FullConfirmDialog(String myMessage) {
        super(ApplicationHolder.getMainFrame(), true);
        myPanel = new JPanel(new MigLayout());
        yesButton = new JButton("Да");
        noButton = new JButton("Нет");
        cancelButton = new JButton("Отмена");

        myPanel.add(new JLabel(myMessage), "span, center");
        myPanel.add(yesButton, "cell 0 1, span, center");
        myPanel.add(noButton, "cell 0 1, span, center");
        myPanel.add(cancelButton, "cell 0 1, span, center");

        yesButton.addActionListener(this);
        noButton.addActionListener(this);
        cancelButton.addActionListener(this);

        setTitle("Подтверждение");
        setResizable(false);
        getContentPane().add(myPanel);
        pack();
        setLocationRelativeTo(ApplicationHolder.getMainFrame());
    }

    public int getAnswer() {
        return answer;
    }

    public void actionPerformed(ActionEvent e) {
        if (yesButton == e.getSource()) {
            answer = 1;
            setVisible(false);
        } else if (noButton == e.getSource()) {
            answer = 0;
            setVisible(false);
        } else if (cancelButton == e.getSource()) {
            answer = 2;
            setVisible(false);
        }
    }

}
