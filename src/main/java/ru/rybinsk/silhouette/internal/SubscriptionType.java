/**
 * © Alexey Smirnov, 2015
 */
package ru.rybinsk.silhouette.internal;

import java.util.HashMap;
import java.util.Map;

/**
 * Вид абонемента.
 * 
 * @author Alexey Smirnov (smirnov89@bk.ru)
 *
 */
public enum SubscriptionType {

    General(0) {
        @Override
        public String note() {
            return "Общий";
        }
    },

    Day(1) {
        @Override
        public String note() {
            return "Дневной";
        }
    },

    Lunch(2) {
        @Override
        public String note() {
            return "Ланч";
        }
    },
    Evening(3) {
        @Override
        public String note() {
            return "Вечерний";
        }
    },
    FirstHall(4) {
        @Override
        public String note() {
            return "1 зал";
        }
    },
    SecondHall(5) {
        @Override
        public String note() {
            return "2 зал";
        }
    };

    private static Map<Integer, SubscriptionType> enumValues = new HashMap<Integer, SubscriptionType>();

    static {
        for (SubscriptionType item : SubscriptionType.values()) {
            enumValues.put(item.getValue(), item);
        }
    }

    private SubscriptionType(int value) {
        this.value = value;
    }

    private int value;

    public abstract String note();

    public int getValue() {
        return value;
    }

    public static SubscriptionType forValue(Integer value) {
        return enumValues.get(value);
    }

}
