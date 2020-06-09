package cat.indiketa.degiro.model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author indiketa
 */
public class DTransactions {

    private List<DTransaction> data = null;
    private long status;
    private String statusText;

    public List<DTransaction> getData() {
        return data;
    }

    public void setData(List<DTransaction> data) {
        this.data = data;
    }

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public static class DTransaction {

        private long id;
        private long productId;
        private Date date;
        private DOrderAction buysell;
        private DOrderType orderTypeId;
        private double price;
        private long quantity;
        private double total;
        private double totalInBaseCurrency;
        private double totalPlusFeeInBaseCurrency;
        private double feeInBaseCurrency;
        private double fxRate;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public DOrderAction getBuysell() {
            return buysell;
        }

        public void setBuysell(DOrderAction buysell) {
            this.buysell = buysell;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public long getQuantity() {
            return quantity;
        }

        public void setQuantity(long quantity) {
            this.quantity = quantity;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getTotalInBaseCurrency() {
            return totalInBaseCurrency;
        }

        public void setTotalInBaseCurrency(double totalInBaseCurrency) {
            this.totalInBaseCurrency = totalInBaseCurrency;
        }

        public double getTotalPlusFeeInBaseCurrency() {
            return totalPlusFeeInBaseCurrency;
        }

        public void setTotalPlusFeeInBaseCurrency(double totalPlusFeeInBaseCurrency) {
            this.totalPlusFeeInBaseCurrency = totalPlusFeeInBaseCurrency;
        }

        public double getFeeInBaseCurrency() {
            return feeInBaseCurrency;
        }

        public void setFeeInBaseCurrency(double feeInBaseCurrency) {
            this.feeInBaseCurrency = feeInBaseCurrency;
        }

        public double getFxRate() {
            return fxRate;
        }

        public void setFxRate(double fxRate) {
            this.fxRate = fxRate;
        }

        public DOrderType getOrderTypeId() {
            return orderTypeId;
        }

        public void setOrderTypeId(DOrderType orderTypeId) {
            this.orderTypeId = orderTypeId;
        }
    }

}
