package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author indiketa
 */
public interface DeGiro {

    DCashFunds getCashFunds() throws DeGiroException;

    DLastTransactions getLastTransactions() throws DeGiroException;

    List<DOrder> getOrders() throws DeGiroException;

    DOrdersHistory getOrdersHistory(ZonedDateTime from, ZonedDateTime to) throws DeGiroException;

    DPortfolio getPortfolio() throws DeGiroException;
    
    DPortfolioSummary getPortfolioSummary() throws DeGiroException;

    DTransactions getTransactions(ZonedDateTime from, ZonedDateTime to) throws DeGiroException;

    void setPriceListener(DPriceListener priceListener);

    void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException;

    void subscribeToPrice(List<String> vwdIssueId) throws DeGiroException;
    void subscribeToPriceByProductsId(List<String> productIds) throws DeGiroException;

    void clearPriceSubscriptions();

    DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException;

    DProducts getProducts(List<String> productIds) throws DeGiroException;

    DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException;

    DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException;

    DPlacedOrder deleteOrder(String orderId) throws DeGiroException;

    DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException;

}
