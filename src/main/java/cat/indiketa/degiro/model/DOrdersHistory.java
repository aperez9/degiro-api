package cat.indiketa.degiro.model;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class DOrdersHistory {

    private List<DOrderHistory> data = null;
    private long status;
    private String statusText;

    public List<DOrderHistory> getData() {
        return data;
    }

    public void setData(List<DOrderHistory> data) {
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

    public static class DOrderHistory {

        private String orderId;
        private DOrderAction buysell;
        private Date created;
        //currentTradedSize
        private boolean isActive;
        //last
        private DOrderTime orderTimeTypeId;
        private DOrderType orderTypeId;
        private BigDecimal price;
        private long productId;
        private long size;
        private DOrderStatus status;
        private BigDecimal stopPrice;
        //totalTradedSize
        private DOrderOperation type;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public DOrderAction getBuysell() {
            return buysell;
        }

        public void setBuysell(DOrderAction buysell) {
            this.buysell = buysell;
        }

        public Date getCreated() {
            return created;
        }

        public void setCreated(Date created) {
            this.created = created;
        }

        public boolean isActive() {
            return isActive;
        }

        public void setActive(boolean active) {
            isActive = active;
        }

        public DOrderTime getOrderTimeTypeId() {
            return orderTimeTypeId;
        }

        public void setOrderTimeTypeId(DOrderTime orderTimeTypeId) {
            this.orderTimeTypeId = orderTimeTypeId;
        }

        public DOrderType getOrderTypeId() {
            return orderTypeId;
        }

        public void setOrderTypeId(DOrderType orderTypeId) {
            this.orderTypeId = orderTypeId;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public long getProductId() {
            return productId;
        }

        public void setProductId(long productId) {
            this.productId = productId;
        }

        public long getSize() {
            return size;
        }

        public void setSize(long size) {
            this.size = size;
        }

        public DOrderStatus getStatus() {
            return status;
        }

        public void setStatus(DOrderStatus status) {
            this.status = status;
        }

        public BigDecimal getStopPrice() {
            return stopPrice;
        }

        public void setStopPrice(BigDecimal stopPrice) {
            this.stopPrice = stopPrice;
        }

        public DOrderOperation getType() {
            return type;
        }

        public void setType(DOrderOperation type) {
            this.type = type;
        }
    }

}
