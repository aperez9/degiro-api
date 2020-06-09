package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author indiketa
 */
public class DPortfolio {

    public List<DProductRow> active;
    public List<DProductRow> inactive;

    public List<DProductRow> getActive() {
        return active;
    }

    public void setActive(List<DProductRow> active) {
        this.active = active;
    }

    public List<DProductRow> getInactive() {
        return inactive;
    }

    public void setInactive(List<DProductRow> inactive) {
        this.inactive = inactive;
    }

    public static class DProductRow {

        private String id;
        private String positionType;
        private long size;
        private BigDecimal price;
        private BigDecimal value;
        // Perdidas/Ganancias totales
        private BigDecimal realizedProductPl;
        private BigDecimal todayRealizedProductPl;
        private BigDecimal breakEvenPrice;
        private BigDecimal plBase;
        private BigDecimal todayPlBase;

        private String product;
        private long change;
        private Date lastUpdate;
        private String currency;
        private String exchangeBriefCode;
        private long contractSize;
        private boolean closedToday;
        private String productCategory;
        private boolean tradable;
        private BigDecimal closePrice;

        // TODO: positionType

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public long getChange() {
            return change;
        }

        public void setChange(long change) {
            this.change = change;
        }

        public BigDecimal getValue() {
            return value;
        }

        public void setValue(BigDecimal value) {
            this.value = value;
        }

        public Date getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(Date lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getExchangeBriefCode() {
            return exchangeBriefCode;
        }

        public void setExchangeBriefCode(String exchangeBriefCode) {
            this.exchangeBriefCode = exchangeBriefCode;
        }

        public long getContractSize() {
            return contractSize;
        }

        public void setContractSize(long contractSize) {
            this.contractSize = contractSize;
        }

        public boolean isClosedToday() {
            return closedToday;
        }

        public void setClosedToday(boolean closedToday) {
            this.closedToday = closedToday;
        }

        public String getProductCategory() {
            return productCategory;
        }

        public void setProductCategory(String productCategory) {
            this.productCategory = productCategory;
        }

        public boolean isTradable() {
            return tradable;
        }

        public void setTradable(boolean tradable) {
            this.tradable = tradable;
        }

        public BigDecimal getClosePrice() {
            return closePrice;
        }

        public void setClosePrice(BigDecimal closePrice) {
            this.closePrice = closePrice;
        }

        public String getPositionType() {
            return positionType;
        }

        public void setPositionType(String positionType) {
            this.positionType = positionType;
        }

        public BigDecimal getRealizedProductPl() {
            return realizedProductPl;
        }

        public void setRealizedProductPl(BigDecimal realizedProductPl) {
            this.realizedProductPl = realizedProductPl;
        }

        public BigDecimal getTodayRealizedProductPl() {
            return todayRealizedProductPl;
        }

        public void setTodayRealizedProductPl(BigDecimal todayRealizedProductPl) {
            this.todayRealizedProductPl = todayRealizedProductPl;
        }

        public BigDecimal getBreakEvenPrice() {
            return breakEvenPrice;
        }

        public void setBreakEvenPrice(BigDecimal breakEvenPrice) {
            this.breakEvenPrice = breakEvenPrice;
        }

        public BigDecimal getPlBase() {
            return plBase;
        }

        public void setPlBase(BigDecimal plBase) {
            this.plBase = plBase;
        }

        public BigDecimal getTodayPlBase() {
            return todayPlBase;
        }

        public void setTodayPlBase(BigDecimal todayPlBase) {
            this.todayPlBase = todayPlBase;
        }
    }

}
