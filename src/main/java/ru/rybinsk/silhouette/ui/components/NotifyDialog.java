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
public class NotifyDialog extends JDialog implements ActionListener {

    private static final long serialVersionUID = 5825343486818087457L;

    private JPanel myPanel = null;
    private JButton yesButton = null;

    private static final String DEFAULT_TITLE = "Ошибка";

    public NotifyDialog(String myMessage) {
        this(DEFAULT_TITLE, myMessage);
    }

    public NotifyDialog(String title, String myMessage) {
        super(ApplicationHolder.getMainFrame(), true);
        myPanel = new JPanel(new MigLayout());
        yesButton = new JButton("OK");

        myPanel.add(new JLabel(myMessage), "span, center");
        myPanel.add(yesButton, "cell 0 1, span, center");

        yesButton.addActionListener(this);

        setTitle(title);
        getContentPane().add(myPanel);
        pack();
        setLocationRelativeTo(ApplicationHolder.getMainFrame());
    }

    public void actionPerformed(ActionEvent e) {
        if (yesButton == e.getSource()) {
            setVisible(false);
        }
    }

}
