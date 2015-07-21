/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.pojo;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public class SimulatorTT {

    private float cost;
    private String timeTable;

    public SimulatorTT(float cost, String timeTable) {
        super();
        this.cost = cost;
        this.timeTable = timeTable;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getTimeTable() {
        return timeTable;
    }

    public void setTimeTable(String timeTable) {
        this.timeTable = timeTable;
    }
}
