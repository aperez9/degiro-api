package cat.indiketa.degiro.model;

import java.util.List;

/**
 * @author indiketa
 */
public class DOrderConfirmation {

    private String confirmationId;
    private List<TransactionFees> transactionFees;

    public DOrderConfirmation() {
    }

    public String getConfirmationId() {
        return confirmationId;
    }

    public void setConfirmationId(String confirmationId) {
        this.confirmationId = confirmationId;
    }

    public List<TransactionFees> getTransactionFees() {
        return transactionFees;
    }

    public void setTransactionFees(List<TransactionFees> transactionFees) {
        this.transactionFees = transactionFees;
    }

    public static class TransactionFees {
        private double amount;
        private String currency;
        private String id;

        public TransactionFees() {
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

}
