package ru.rybinsk.silhouette.binding;

import ru.rybinsk.silhouette.pojo.CellSpan;

public interface SpanModel {

    public CellSpan getCellSpanAt(int rowIndex, int columnIndex);

    public boolean isCellSpanOn();

}
