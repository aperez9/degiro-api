/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cat.indiketa.degiro.model;

import java.util.Arrays;

public enum DOrderStatus {
    CONFIRMED("CONFIRMED"),
    REJECTED("REJECTED");

    // TODO: Pending??

    private final String strValue;

    DOrderStatus(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return strValue;
    }

    public static DOrderStatus getOrderByValue(String value) {
        return Arrays.stream(values())
                .filter(x -> x.strValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }

}
