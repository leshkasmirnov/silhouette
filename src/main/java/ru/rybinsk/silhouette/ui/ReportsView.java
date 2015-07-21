/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;

import ru.rybinsk.silhouette.dao.PaymentMapper;
import ru.rybinsk.silhouette.dao.PersonalDataMapper;
import ru.rybinsk.silhouette.dao.SimulatorMapper;
import ru.rybinsk.silhouette.dao.SubscriptionMapper;
import ru.rybinsk.silhouette.dao.TimeTableMapper;
import ru.rybinsk.silhouette.internal.SubscriptionPeriod;
import ru.rybinsk.silhouette.internal.SubscriptionType;
import ru.rybinsk.silhouette.model.Payment;
import ru.rybinsk.silhouette.model.PaymentExample;
import ru.rybinsk.silhouette.model.PersonalData;
import ru.rybinsk.silhouette.model.PersonalDataExample;
import ru.rybinsk.silhouette.model.Simulator;
import ru.rybinsk.silhouette.model.SimulatorExample;
import ru.rybinsk.silhouette.model.Subscription;
import ru.rybinsk.silhouette.model.TimeTable;
import ru.rybinsk.silhouette.model.TimeTableExample;
import ru.rybinsk.silhouette.model.TimeTableExample.Criteria;
import ru.rybinsk.silhouette.settings.DbSessionManager;
import ru.rybinsk.silhouette.ui.components.NotifyDialog;
import ru.rybinsk.silhouette.util.Formatter;

import com.toedter.calendar.JDateChooser;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class ReportsView extends JPanel implements ActionListener {

    private static final String FORM_AND_PRINT = "formAndPrint";
    private static final String END_SHIFT_REPORT = "endShiftReport";
    private static final long serialVersionUID = -2503926743533523301L;
    private JCheckBox clientsReports;
    private JCheckBox subscriptionsReports;
    private JCheckBox equipmentsReports;
    private JFileChooser fileChooser;

    private static final String[] CLIENTS_REPORT_HEADERS = new String[] { "ФИО", "№ абонемента", "Телефон 1",
            "Телефон 2", "№ договора" };
    private static final String[] SUBSCRIPTIONS_REPORT_HEADERS = new String[] { "ФИО", "№ абонемента", "Состояние",
            "Вид", "Период действия", "Количество занятий", "Количество занятий в подарок" };
    private static final String[] EQUIPMENTS_HEADERS = new String[] { "Название тренажера", "Количество занятий" };
    private static final String[] END_SHIFT_HEADERS1 = new String[] { "ФИО", "№ абонемента" };
    private static final String[] END_SHIFT_HEADERS2 = new String[] { "ФИО", "№ абонемента", "Дата платежа", "Сумма" };
    private JDateChooser dateFromChooser;
    private JDateChooser dateToChooser;

    public ReportsView() {
        setLayout(new MigLayout("gap 15px 15px"));
        paint();
    }

    private void paint() {
        JLabel endShiftReportLabel = new JLabel("Отчет: закрытие смены");
        JButton endShiftReportButton = new JButton("Сформировать");
        JPanel reportsPanel = new JPanel(new MigLayout("gap 15px 15px"));
        clientsReports = new JCheckBox("Отчет 1: Информация о клиентах (все клиенты)");
        subscriptionsReports = new JCheckBox("Отчет 2: Информация об абонементах");
        equipmentsReports = new JCheckBox("Отчет 3: Загрузка оборудования");
        JLabel periodLabel = new JLabel("Период: ");
        dateFromChooser = new JDateChooser();
        dateToChooser = new JDateChooser();
        JButton formAndPrintButton = new JButton("Сформировать отчеты");
        fileChooser = new JFileChooser();
        FileNameExtensionFilter fileFilter = new FileNameExtensionFilter("MS Excel", "xls");
        fileChooser.setFileFilter(fileFilter);

        endShiftReportButton.setActionCommand(END_SHIFT_REPORT);
        endShiftReportButton.addActionListener(this);
        formAndPrintButton.setActionCommand(FORM_AND_PRINT);
        formAndPrintButton.addActionListener(this);
        reportsPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        reportsPanel.add(clientsReports);
        reportsPanel.add(subscriptionsReports, "cell 0 1");
        reportsPanel.add(equipmentsReports, "cell 0 2");
        reportsPanel.add(periodLabel, "cell 0 3");
        reportsPanel.add(dateFromChooser, "cell 0 3");
        reportsPanel.add(dateToChooser, "cell 0 3");
        reportsPanel.add(formAndPrintButton, "cell 0 4, right");

        add(endShiftReportLabel);
        add(endShiftReportButton, "cell 0 0");
        add(reportsPanel, "cell 0 1");
    }

    private void generateHeaderRow(HSSFWorkbook workBook, HSSFSheet sheet, String[] headers) {
        HSSFCellStyle handlerCellsStyle = workBook.createCellStyle();
        handlerCellsStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        handlerCellsStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
        handlerCellsStyle.setFillBackgroundColor(HSSFColor.GREY_25_PERCENT.index);
        handlerCellsStyle.setBorderTop((short) 1);
        handlerCellsStyle.setBorderBottom((short) 1);
        handlerCellsStyle.setBorderLeft((short) 1);
        handlerCellsStyle.setBorderRight((short) 1);

        HSSFRow headerRow = sheet.createRow(0);

        int i = 0;
        for (String heder : headers) {
            HSSFCell cell = headerRow.createCell(i);
            cell.setCellStyle(handlerCellsStyle);
            cell.setCellValue(heder);
            i++;
        }
    }

    private void formingEndShiftReport() {
        HSSFWorkbook workBook = new HSSFWorkbook();
        HSSFSheet sheet = workBook.createSheet("Список клиентов с нераспечатанным актом");

        generateHeaderRow(workBook, sheet, END_SHIFT_HEADERS1);

        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        TimeTableExample ttExample = new TimeTableExample();
        ttExample.createCriteria().andDateEqualTo(new Date()).andPrintedActEqualTo(false);
        List<TimeTable> list = ttMapper.selectByExample(ttExample);
        DbSessionManager.closeSession();
        Map<Integer, PersonalData> pdMap = new HashMap<Integer, PersonalData>();
        for (TimeTable timeTable : list) {
            PersonalData pd = timeTable.getPersonalData();
            pdMap.put(pd.getId(), pd);
        }
        int i = 1;
        for (PersonalData pd : pdMap.values()) {
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell1 = row.createCell(0);
            HSSFCell cell2 = row.createCell(1);
            cell1.setCellValue(pd.getFio());
            String subscrN = "";
            if (pd.getSubscription() != null) {
                subscrN = pd.getSubscription().getSubscriptionNumber();
            }
            cell2.setCellValue(subscrN);
            i++;
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);

        HSSFSheet remindsSheet = workBook.createSheet("Напоминания");
        generateHeaderRow(workBook, remindsSheet, END_SHIFT_HEADERS2);
        PaymentMapper pMapper = DbSessionManager.getMapper(PaymentMapper.class);
        PaymentExample pExample = new PaymentExample();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        pExample.createCriteria().andPaymentDateGreaterThanOrEqualTo(new Date())
                .andPaymentDateLessThanOrEqualTo(calendar.getTime()).andPaidEqualTo(false);
        List<Payment> pList = pMapper.selectByExample(pExample);
        SubscriptionMapper sMapper = DbSessionManager.getMapper(SubscriptionMapper.class);
        PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
        int j = 1;
        for (Payment payment : pList) {
            Subscription s = sMapper.selectByPrimaryKey(payment.getSubscriptionId());
            PersonalData pd = pdMapper.selectByPrimaryKey(s.getPersonalDataId());

            HSSFRow row = remindsSheet.createRow(j);
            HSSFCell cell1 = row.createCell(0);
            HSSFCell cell2 = row.createCell(1);
            HSSFCell cell3 = row.createCell(2);
            HSSFCell cell4 = row.createCell(3);

            cell1.setCellValue(pd.getFio());
            cell2.setCellValue(s.getSubscriptionNumber());
            cell3.setCellValue(Formatter.formatDate(payment.getPaymentDate()));
            cell4.setCellValue(payment.getSum());

            j++;
        }
        DbSessionManager.closeSession();
        remindsSheet.autoSizeColumn(0);
        remindsSheet.autoSizeColumn(1);
        remindsSheet.autoSizeColumn(2);
        remindsSheet.autoSizeColumn(3);

        FileOutputStream fos = null;
        try {
            File file = new File("Отчет_закрытие_смены_" + Formatter.formatDate(new Date()) + ".xls");
            fos = new FileOutputStream(file);
            workBook.write(fos);
            fileChooser.setSelectedFile(file);
            NotifyDialog dialog = new NotifyDialog("Уведомление", "Отчет сформирован");
            dialog.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void formingClientsReport(HSSFWorkbook workBook) {
        HSSFSheet sheet = workBook.createSheet("Информация о клиентах");

        generateHeaderRow(workBook, sheet, CLIENTS_REPORT_HEADERS);

        PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
        PersonalDataExample example = new PersonalDataExample();
        List<PersonalData> list = pdMapper.selectByExampleFull(example);
        DbSessionManager.closeSession();
        int i = 1;
        for (PersonalData pd : list) {
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell1 = row.createCell(0);
            HSSFCell cell2 = row.createCell(1);
            HSSFCell cell3 = row.createCell(2);
            HSSFCell cell4 = row.createCell(3);
            HSSFCell cell5 = row.createCell(4);

            cell1.setCellValue(pd.getFio());
            String subscrN = "";
            if (pd.getSubscription() != null) {
                subscrN = pd.getSubscription().getSubscriptionNumber();
            }
            cell2.setCellValue(subscrN);
            cell3.setCellValue(pd.getPhone1());
            cell4.setCellValue(pd.getPhone2());
            cell5.setCellValue(pd.getContractNumber());
            i++;
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
    }

    private void formingSubscriptionsreport(HSSFWorkbook workBook) {
        HSSFSheet sheet = workBook.createSheet("Информация об абонементах");

        generateHeaderRow(workBook, sheet, SUBSCRIPTIONS_REPORT_HEADERS);

        PersonalDataMapper pdMapper = DbSessionManager.getMapper(PersonalDataMapper.class);
        PersonalDataExample example = new PersonalDataExample();
        example.createCriteria().andSubscriptionIdIsNotNull();
        List<PersonalData> list = pdMapper.selectByExampleFull(example);
        DbSessionManager.closeSession();
        int i = 1;
        for (PersonalData pd : list) {
            HSSFRow row = sheet.createRow(i);
            HSSFCell cell1 = row.createCell(0);
            HSSFCell cell2 = row.createCell(1);
            HSSFCell cell3 = row.createCell(2);
            HSSFCell cell4 = row.createCell(3);
            HSSFCell cell5 = row.createCell(4);
            HSSFCell cell6 = row.createCell(5);
            HSSFCell cell7 = row.createCell(6);

            cell1.setCellValue(pd.getFio());
            if (pd.getSubscription() != null) {
                Subscription s = pd.getSubscription();
                String subscrN = s.getSubscriptionNumber() != null ? s.getSubscriptionNumber() : "";
                cell2.setCellValue(subscrN);
                cell3.setCellValue(s.getState() == 0 ? "Заблокирован" : "Активен");
                cell4.setCellValue(SubscriptionType.forValue(s.getType()).note());
                cell5.setCellValue(SubscriptionPeriod.forValue(s.getPeriod()).note());
                if (s.getParticipationNumber() != null) {
                    String participations = Float.toString(s.getParticipationNumber());
                    participations = participations.replaceAll(".0", "");
                    cell6.setCellValue(participations);
                } else {
                    cell6.setCellValue("");
                }
                String participationsPresent = "0";
                if (s.getParticipationNumberPresent() != null) {
                    participationsPresent = Float.toString(s.getParticipationNumberPresent());
                }
                participationsPresent = participationsPresent.replaceAll(".0", "");
                cell7.setCellValue(participationsPresent);
            }

            i++;
        }
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);

    }

    private void formingEquipmentsreport(HSSFWorkbook workBook) {
        SimulatorMapper mapper = DbSessionManager.getMapper(SimulatorMapper.class);
        SimulatorExample example = new SimulatorExample();
        example.setOrderByClause("Ord");
        example.createCriteria().andActivityEqualTo(Boolean.TRUE);
        List<Simulator> list = mapper.selectByExample(example);

        HSSFSheet sheet = workBook.createSheet("Загрузка оборудования");
        generateHeaderRow(workBook, sheet, EQUIPMENTS_HEADERS);

        TimeTableMapper ttMapper = DbSessionManager.getMapper(TimeTableMapper.class);
        int i = 1;
        for (Simulator simulator : list) {
            TimeTableExample ttExample = new TimeTableExample();
            Criteria criteria = ttExample.createCriteria().andSimulatorIdEqualTo(simulator.getId());
            if (dateFromChooser.getDate() != null) {
                criteria.andDateGreaterThanOrEqualTo(dateFromChooser.getDate());
            }
            if (dateToChooser.getDate() != null) {
                criteria.andDateLessThanOrEqualTo(dateFromChooser.getDate());
            }

            int count = ttMapper.countByExample(ttExample);
            HSSFRow row = sheet.createRow(i);
            HSSFCell cellA = row.createCell(0);
            HSSFCell cellB = row.createCell(1);
            cellA.setCellValue(simulator.getName());
            cellB.setCellValue(Integer.toString(count));
            i++;
        }

        DbSessionManager.closeSession();

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
    }

    private void formingGlobalReport() {
        HSSFWorkbook workBook = new HSSFWorkbook();
        if (clientsReports.isSelected()) {
            formingClientsReport(workBook);
        }
        if (subscriptionsReports.isSelected()) {
            formingSubscriptionsreport(workBook);
        }
        if (equipmentsReports.isSelected()) {
            formingEquipmentsreport(workBook);
        }
        FileOutputStream fos = null;
        try {
            File file = new File("Общий отчет_" + Formatter.formatDate(new Date()) + ".xls");
            fos = new FileOutputStream(file);
            workBook.write(fos);
            fileChooser.setSelectedFile(file);
            NotifyDialog dialog = new NotifyDialog("Уведомление", "Отчет сформирован");
            dialog.setVisible(true);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals(END_SHIFT_REPORT)) {
            formingEndShiftReport();
        } else if (e.getActionCommand().equals(FORM_AND_PRINT)) {
            formingGlobalReport();
        }
    }

}
