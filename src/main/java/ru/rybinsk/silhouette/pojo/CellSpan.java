/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.pojo;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class CellSpan {

    private int row = 0;
    private int column = 0;
    private int rowSpan = 0;
    private int columnSpan = 0;

    public CellSpan(int row, int column, int rowSpan, int columnSpan) {
        this.column = column;
        this.columnSpan = columnSpan;
        this.row = row;
        this.rowSpan = rowSpan;
    }

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
    }

    public int getColumnSpan() {
        return columnSpan;
    }

    public void setColumnSpan(int columnSpan) {
        this.columnSpan = columnSpan;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRowSpan() {
        return rowSpan;
    }

    public void setRowSpan(int rowSpan) {
        this.rowSpan = rowSpan;
    }

}
