/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.util;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

/**
 * Утилитный клас для вывода текста на печать.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class Printer implements Printable {
    private String text;
    private static Printer instance;

    public static Printer getInstance() {
        if (instance == null) {
            instance = new Printer();
        }

        return instance;
    }

    public void print(String text) throws PrinterException {
        this.text = text;

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintable(this);
        if (printerJob.printDialog()) {
            printerJob.print();
        }
    }

    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex == 0) {
            Font font = graphics.getFont();
            Font newFont = new Font(font.getName(), font.getStyle(), font.getSize() - 1);
            graphics.setFont(newFont);
            if (text.contains("\n")) {
                int x = 40;
                int y = 50;
                for (String line : text.split("\n")) {
                    graphics.drawString(line, x, y += graphics.getFontMetrics().getHeight());
                }

            } else {
                graphics.drawString(text, 100, 100);
            }

            return PAGE_EXISTS;
        }

        return NO_SUCH_PAGE;
    }

}
