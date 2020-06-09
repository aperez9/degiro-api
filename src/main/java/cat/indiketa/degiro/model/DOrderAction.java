/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import java.util.Arrays;

/**
 * @author indiketa
 */
public enum DOrderAction {
    BUY(0, "B"),
    SELL(1, "S");

    private final int value;
    private final String strValue;

    private DOrderAction(int value, String strValue) {
        this.value = value;
        this.strValue = strValue;
    }

    public int getValue() {
        return value;
    }

    public String getStrValue() {
        return strValue;
    }

    public static DOrderAction getOrderByValue(int value) {
        return Arrays.stream(values())
                .filter(x -> x.value == value)
                .findFirst()
                .orElse(null);
    }

    public static DOrderAction getOrderByValue(String value) {
        return Arrays.stream(values())
                .filter(x -> x.strValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

}
