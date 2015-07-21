/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui;

import java.awt.Color;
import java.awt.Font;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import ru.rybinsk.silhouette.dao.TimeTableMapper;
import ru.rybinsk.silhouette.internal.SubscriptionType;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.model.TimeTable;
import ru.rybinsk.silhouette.model.TimeTableExample;
import ru.rybinsk.silhouette.settings.ApplicationHolder;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.ui.forms.MainTimeTable;

/**
 * Диалоговое окно "Расписание клиента"
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ClientTimeTableDialog extends JDialog {

    private static final long serialVersionUID = 7378748192102298678L;

    public ClientTimeTableDialog(JFrame frame, PersonalData personalData, Date date, boolean editing) {
        super(frame, true);
        setLayout(new MigLayout());

        JLabel fioLabel = new JLabel(personalData.getFio());

        add(fioLabel);
        if (personalData.getSubscription() != null) {
            JPanel subscriptionTypePanel = new JPanel(new MigLayout());
            JLabel subscriptionTypeLabel = new JLabel("Вид абонемента: ");
            JLabel subscriptionType = new JLabel(SubscriptionType.forValue(personalData.getSubscription().getType())
                    .note());
            Font font = new Font(Font.DIALOG, Font.BOLD, 14);
            subscriptionTypeLabel.setFont(font);
            subscriptionType.setFont(font);
            subscriptionType.setForeground(Color.RED);

            subscriptionTypePanel.add(subscriptionTypeLabel);
            subscriptionTypePanel.add(subscriptionType);
            // if (personalData.getSubscription().getType()) {
            add(subscriptionTypePanel, "gapleft 20");
            // }
        }
        MainTimeTable mainTimeTable;
        if (editing) {
            TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
            TimeTableExample ttExample = new TimeTableExample();
            ttExample.createCriteria().andPersonalDataIdEqualTo(personalData.getId()).andDateEqualTo(date);

            List<TimeTable> list = ttMapper.selectByExample(ttExample);
            DbSessionManager.closeSession();

            Set<Integer> simulatorIdsSet = new LinkedHashSet<Integer>();
            for (TimeTable timeTable : list) {
                simulatorIdsSet.add(timeTable.getSimulatorId());
            }
            mainTimeTable = new MainTimeTable(simulatorIdsSet);

        } else {
            mainTimeTable = new MainTimeTable(null);
        }

        mainTimeTable.makeClientTimeTable(personalData, date, editing);
        add(mainTimeTable, "span, cell 0 1");
        pack();
        setLocationRelativeTo(ApplicationHolder.getMainFrame());
        setTitle("Расписание клиента");
    }

}
