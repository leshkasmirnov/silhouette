/**
 * © Alexey Smirnov, 2013
 */
package ru.rybinsk.silhouette.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Срок абонемента.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 * 
 */
public enum SubscriptionPeriod {

    Once(0) {
        @Override
        public String note() {
            return "Разовый";
        }
    },

    ThreeMonths(1) {
        @Override
        public String note() {
            return "3 месяца";
        }
    },

    SixMonths(2) {
        @Override
        public String note() {
            return "6 месяцев";
        }
    },

    Year(3) {
        @Override
        public String note() {
            return "Годовой";
        }
    },

    Unlimit(4) {
        @Override
        public String note() {
            return "Безлимит";
        }
    };

    private static Map<Integer, SubscriptionPeriod> enumValues = new HashMap<Integer, SubscriptionPeriod>();

    static {
        for (SubscriptionPeriod item : SubscriptionPeriod.values()) {
            enumValues.put(item.getValue(), item);
        }
    }

    private SubscriptionPeriod(int value) {
        this.value = value;
    }

    private int value;

    public abstract String note();

    public int getValue() {
        return value;
    }

    public static SubscriptionPeriod forValue(int value) {
        return enumValues.get(value);
    }

}
