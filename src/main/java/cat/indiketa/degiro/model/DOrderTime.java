package cat.indiketa.degiro.model;

import java.util.Arrays;

/**
 *
 * @author indiketa
 */
public enum DOrderTime {
    DAY(1, "DAY"),
    PERMANENT(3, "GTC");

    private final int value;
    private final String strValue;

    private DOrderTime(int value, String strValue) {
        this.value = value;
        this.strValue = strValue;
    }

    public int getValue() {
        return value;
    }

    public String getStrValue() {
        return strValue;
    }

    public static DOrderTime getOrderByValue(int value) {
        return Arrays.stream(values())
                .filter(x -> x.value == value)
                .findFirst()
                .orElse(null);
    }

    public static DOrderTime getOrderByValue(String value) {
        return Arrays.stream(values())
                .filter(x -> x.strValue.equalsIgnoreCase(value))
                .findFirst()
                .orElse(null);
    }
}
