/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public enum DurationType {
    Quarter(0) {
        @Override
        public String note() {
            return "Продолжительность до 15 минут";
        }
    },

    Half(1) {
        @Override
        public String note() {
            return "Продолжительность до 30 минут";
        }
    },

    Whole(2) {
        @Override
        public String note() {
            return "Продолжительность до 1 часа";
        }
    };

    private static Map<Integer, DurationType> enumValues = new HashMap<Integer, DurationType>();

    static {
        for (DurationType item : DurationType.values()) {
            enumValues.put(item.getValue(), item);
        }
    }

    private DurationType(int value) {
        this.value = value;
    }

    private int value;

    public abstract String note();

    public int getValue() {
        return value;
    }

    public static DurationType forValue(Integer value) {
        return enumValues.get(value);
    }
}
