import cat.indiketa.degiro.DeGiro;
import cat.indiketa.degiro.DeGiroFactory;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.model.*;
import cat.indiketa.degiro.utils.DCredentials;
import com.google.common.collect.Lists;
import com.google.gson.GsonBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Ignore
public class ConnectTest {

    static DeGiro degiro;

    @BeforeClass
    public static void setUp() {
        degiro = DeGiroFactory.newInstance(getCredentials());
    }

    @Test
    public void ordersTest() throws DeGiroException {
        List<DOrder> orders = degiro.getOrders();
        Assert.assertNotNull(orders);
    }

    @Test
    public void ordersHistoryTest() throws DeGiroException {
        DOrdersHistory history = degiro.getOrdersHistory(ZonedDateTime.now().minusDays(5), ZonedDateTime.now());
        Assert.assertNotNull(history);
    }

    @Test
    public void porfolioTest() throws DeGiroException {
        DPortfolio porfolio = degiro.getPortfolio();
        Assert.assertNotNull(porfolio);
    }

    @Test
    public void porfolioSummaryTest() throws DeGiroException {
        DPortfolioSummary porfolioSummary = degiro.getPortfolioSummary();
        Assert.assertNotNull(porfolioSummary);
    }

    @Test
    public void cashFundsTest() throws DeGiroException {
        DCashFunds cashFunds = degiro.getCashFunds();
        Assert.assertNotNull(cashFunds);
    }

    @Test
    public void lastTransactionsTest() throws DeGiroException {
        DLastTransactions lastTransactions = degiro.getLastTransactions();
        Assert.assertNotNull(lastTransactions);

        // Get transactions between dates
        ZonedDateTime to = ZonedDateTime.now();
        ZonedDateTime from = ZonedDateTime.now().minusMonths(3);
        DTransactions transactions = degiro.getTransactions(from, to);
        Assert.assertNotNull(transactions);
    }

    @Test
    public void searchTest() throws DeGiroException {
        // Search products by text, signature:
        // DProductSearch searchProducts(String text, DProductType type, int limit, int offset);
        DProductSearch ps = degiro.searchProducts("apple", DProductType.ALL, 10, 0);
        Assert.assertNotNull(ps);
        for (DProduct product : ps.getProducts()) {
            System.out.println(product.getId() + " " + product.getName());
        }
    }

    @Test
    public void productsTest() throws DeGiroException {
        // Get product info by id, signature:
        // DProducts getProducts(List<Long> productIds);
        List<String> productIds = Lists.newArrayList();
//        productIds.add("332035");
//        productIds.add("1156998");
//        productIds.add("1153605");
        productIds.add("1446558");
        DProducts products = degiro.getProducts(productIds);
        Assert.assertNotNull(products);

        for (DProduct value : products.getData().values()) {
            System.out.println("ID: " + value.getId() + ", NAME: " + value.getName() + ", CLOSE: " +
                    value.getClosePrice() + ", VWDID: " + value.getVwdId() + ", VWDIDTYPE: " + value.getVwdIdentifierType() +
                    ", VWDMODULEID: " + value.getVwdModuleId());
        }
    }

    @Test
    @Ignore
    public void checkOrderTest() throws DeGiroException {
        // Generate a new order. Signature:
        DNewOrder order = new DNewOrder(DOrderAction.BUY, DOrderType.LIMITED, DOrderTime.DAY, 121032, 20, new BigDecimal("0.10"), null);
        DOrderConfirmation confirmation = degiro.checkOrder(order);
        Assert.assertNotNull(confirmation);
        Assert.assertNotNull(confirmation.getConfirmationId());
    }

    @Test
    @Ignore
    public void orderTest() throws DeGiroException {

        // Generamos la orden
        DNewOrder newOrder = new DNewOrder(DOrderAction.BUY, DOrderType.LIMITED, DOrderTime.DAY,
                121032, 20, new BigDecimal("0.10"), null);
        DOrderConfirmation confirmation = degiro.checkOrder(newOrder);
        Assert.assertNotNull(confirmation);
        Assert.assertNotNull(confirmation.getConfirmationId());

        // Confirmamos la orden
        DPlacedOrder placed = degiro.confirmOrder(newOrder, confirmation.getConfirmationId());
        Assert.assertNotNull(placed);
        if (placed.getStatus() != 0) {
            throw new RuntimeException("Order not placed: " + placed.getStatusText());
        }

        // Consultamos la orden
        List<DOrder> orders = degiro.getOrders();
        DOrder order = orders.stream().filter(x -> x.getId().equals(placed.getOrderId()))
                .findFirst().orElseThrow(() -> new NoSuchElementException(String.format("No existe la orden con id %s", placed.getOrderId())));

        // Actualizamos la orden
        // DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop);
        DPlacedOrder updated = degiro.updateOrder(order, new BigDecimal("0.11"), null);
        Assert.assertNotNull(updated);
        if (updated.getStatus() != 0) {
            throw new RuntimeException("Order not updated: " + updated.getStatusText());
        }

        // Eliminamos la orden
        DPlacedOrder deleted = degiro.deleteOrder(order.getId());
        Assert.assertNotNull(deleted);
        if (deleted.getStatus() != 0) {
            throw new RuntimeException("Order not deleted: " + deleted.getStatusText());
        }
    }

    @Test
    @Ignore
    public void priceListenerTest() throws DeGiroException, InterruptedException {
        degiro.setPriceListener(price -> System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(price)));

        // Create a vwdIssueId list. Note that vwdIssueId is NOT a productId (vwdIssueId is a DProduct field).
        List<String> vwdIssueIds = Lists.newArrayList();
        vwdIssueIds.add("AAPL.BATS,E");
        vwdIssueIds.add("F.BATS,E");
        vwdIssueIds.add("TSLA.BATS,E");
        vwdIssueIds.add("APA.BATS,E");

//        vwdIssueIds.add("280100001");
//        vwdIssueIds.add("955002401");
//        vwdIssueIds.add("955000506");

        degiro.subscribeToPrice(vwdIssueIds); // Callable multiple times with different products.

        // You need some type of control loop, background thread, etc... to prevent JVM termination (out of this scope)
        while (true) {
            Thread.sleep(1000);
        }
    }

    @Test
    @Ignore
    public void priceListenerByProductTest() throws DeGiroException, InterruptedException {
        degiro.setPriceListener(price -> System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(price)));

        // Create a vwdIssueId list. Note that vwdIssueId is NOT a productId (vwdIssueId is a DProduct field).
        List<String> productsId = Lists.newArrayList();
        productsId.add("120833");
        productsId.add("353187");
        productsId.add("121658");
        degiro.subscribeToPriceByProductsId(productsId); // Callable multiple times with different products.

        // You need some type of control loop, background thread, etc... to prevent JVM termination (out of this scope)
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static DCredentials getCredentials() {
        return new DCredentials() {
            @Override
            public String getUsername() {
                return "";
            }

            @Override
            public String getPassword() {
                return "";
            }
        };
    }
}
