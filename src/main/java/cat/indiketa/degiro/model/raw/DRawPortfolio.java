package cat.indiketa.degiro.model.raw;

import com.google.gson.internal.LinkedTreeMap;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author indiketa
 */
public class DRawPortfolio {

    private Portfolio portfolio;

    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public static class Portfolio {

        private Long lastUpdated;
        private String name;
        private List<Value> value = null;
        private Boolean isAdded;

        public Long getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(Long lastUpdated) {
            this.lastUpdated = lastUpdated;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Value> getValue() {
            return value;
        }

        public void setValue(List<Value> value) {
            this.value = value;
        }

        public Boolean getIsAdded() {
            return isAdded;
        }

        public void setIsAdded(Boolean isAdded) {
            this.isAdded = isAdded;
        }

    }

    public static class Value {

        private String name;
        private List<Value_> value = null;
        private boolean isAdded;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Value_> getValue() {
            return value;
        }

        public List<Value_> getValueNotNull() {
            return value.stream().filter(Value_::hasValue).collect(Collectors.toList());
        }

        public void setValue(List<Value_> value) {
            this.value = value;
        }

        public boolean isIsAdded() {
            return isAdded;
        }

        public void setIsAdded(boolean isAdded) {
            this.isAdded = isAdded;
        }

    }

    public static class Value_ {

        private String name;
        private Object value;
        private boolean isAdded;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Object getValue() {
            return value;
        }

        public boolean hasValue() {
            return value != null;
        }

        public String getValueString() {
            if (value instanceof String) {
                return (String) value;
            } else {
                throw new IllegalStateException(String.format("No se ha podido hacer la conversión de %s a String del valor %s", name, value));
            }
        }

        public Long getValueLong() {
            if (value instanceof Double) {
                return ((Double) value).longValue();
            } else if (value instanceof String) {
                return Long.valueOf((String) value);
            } else {
                throw new IllegalStateException(String.format("No se ha podido hacer la conversión de %s a Long del valor %s", name, value));
            }
        }

        public BigDecimal getValueDecimal() {
            if (value instanceof Double) {
                return BigDecimal.valueOf((Double) value);
            } else {
                throw new IllegalStateException(String.format("No se ha podido hacer la conversión de %s a BigDecimal del valor %s", name, value));
            }
        }

        public Integer getValueInt() {
            if (value instanceof Integer) {
                return (Integer) value;
            } else if (value instanceof Double) {
                return ((Double) value).intValue();
            } else {
                throw new IllegalStateException(String.format("No se ha podido hacer la conversión de %s a Integer del valor %s", name, value));
            }
        }

        public BigDecimal getValueMapDecimal() {
            if (value instanceof LinkedTreeMap) {
                LinkedTreeMap<String, Double> map = (LinkedTreeMap) value;
                return BigDecimal.valueOf(map.values().stream().findFirst().orElse(null));
            } else {
                throw new IllegalStateException(String.format("No se ha podido hacer la conversión de %s a Map del valor %s", name, value));
            }
        }

        public Boolean getValueBoolean() {
            if (value instanceof String) {
                return Boolean.parseBoolean(getValueString());
            } else if (value instanceof Boolean) {
                return (Boolean) value;
            } else {
                throw new IllegalStateException(String.format("No se ha podido hacer la conversión de %s a Boolean del valor %s", name, value));
            }
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public boolean isIsAdded() {
            return isAdded;
        }

        public void setIsAdded(boolean isAdded) {
            this.isAdded = isAdded;
        }

    }

}
