package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.DInvalidCredentialsException;
import cat.indiketa.degiro.exceptions.DUnauthorizedException;
import cat.indiketa.degiro.exceptions.DeGiroException;
import cat.indiketa.degiro.http.DCommunication;
import cat.indiketa.degiro.http.DCommunication.DResponse;
import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.*;
import cat.indiketa.degiro.model.raw.*;
import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.utils.DCredentials;
import cat.indiketa.degiro.utils.DUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author indiketa
 */
public class DeGiroImpl implements DeGiro {

    private final DCredentials credentials;
    private final DCommunication comm;
    private final DSession session;
    private final Gson gson;
    private DPriceListener priceListener;
    private long pollingInterval = TimeUnit.SECONDS.toMillis(15);
    private Timer pricePoller = null;
    private static final String BASE_TRADER_URL = "https://trader.degiro.nl";
    private final Set<String> subscribedVwdIssues;
    private final Type rawPriceData = new TypeToken<List<DRawVwdPrice>>() {
    }.getType();

    protected DeGiroImpl(DCredentials credentials, DSession session) {
        this.session = session;
        this.credentials = credentials;
        this.comm = new DCommunication(this.session);
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(DProductType.class, new DUtils.ProductTypeAdapter());
        builder.registerTypeAdapter(DOrderTime.class, new DUtils.OrderTimeTypeAdapter());
        builder.registerTypeAdapter(DOrderType.class, new DUtils.OrderTypeTypeAdapter());
        builder.registerTypeAdapter(DOrderAction.class, new DUtils.OrderActionTypeAdapter());
        builder.registerTypeAdapter(DOrderStatus.class, new DUtils.OrderStatusTypeAdapter());
        builder.registerTypeAdapter(DOrderOperation.class, new DUtils.OrderOperationTypeAdapter());
        builder.registerTypeAdapter(Date.class, new DUtils.DateTypeAdapter());
        this.gson = builder.create();
        this.subscribedVwdIssues = new HashSet<>(500);

    }

    @Override
    public DPortfolio getPortfolio() throws DeGiroException {
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "portfolio=0", null);
            DRawPortfolio rawPortfolio = gson.fromJson(getResponseData(response), DRawPortfolio.class);
            return DUtils.convert(rawPortfolio);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving portfolio", e);
        }
    }

    @Override
    public DPortfolioSummary getPortfolioSummary() throws DeGiroException {
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "totalPortfolio=0", null);
            DRawPortfolioSummary rawPortfolioSummary = gson.fromJson(getResponseData(response), DRawPortfolioSummary.class);
            return DUtils.convertPortfolioSummary(rawPortfolioSummary.getTotalPortfolio());
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving portfolio", e);
        }
    }

    @Override
    public DCashFunds getCashFunds() throws DeGiroException {
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "cashFunds=0", null);
            DRawCashFunds rawCashFunds = gson.fromJson(getResponseData(response), DRawCashFunds.class);
            return DUtils.convert(rawCashFunds);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving cash funds", e);
        }
    }

    // TODO: https://trader.degiro.nl/reporting/secure/v3/orders
    @Override
    public List<DOrder> getOrders() throws DeGiroException {
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "orders=0", null);
            DRawOrders rawOrders = gson.fromJson(getResponseData(response), DRawOrders.class);
            return DUtils.convert(rawOrders).getOrders();
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving orders", e);
        }
    }

    @Override
    public DOrdersHistory getOrdersHistory(ZonedDateTime from, ZonedDateTime to) throws DeGiroException {
        ensureLogged();

        try {
            String fromStr = URLEncoder.encode(from.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), StandardCharsets.UTF_8.toString());
            String toStr = URLEncoder.encode(to.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), StandardCharsets.UTF_8.toString());

            DResponse response = comm.getUrlData(session.getConfig().getReportingUrl(),
                    "v4/order-history?fromDate=" + fromStr +
                            "&toDate=" + toStr + "&intAccount=" +
                            session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null);
            return gson.fromJson(getResponseData(response, false), DOrdersHistory.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving orders history", e);
        }
    }

    @Override
    public DLastTransactions getLastTransactions() throws DeGiroException {
        ensureLogged();

        try {
            DResponse response = comm.getData(session, "transactions=0", null);
            DRawTransactions rawTransactions = gson.fromJson(getResponseData(response), DRawTransactions.class);
            return DUtils.convert(rawTransactions);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving transactions", e);
        }
    }

    @Override
    public DTransactions getTransactions(ZonedDateTime from, ZonedDateTime to) throws DeGiroException {
        ensureLogged();

        try {
            String fromStr = URLEncoder.encode(from.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), StandardCharsets.UTF_8.toString());
            String toStr = URLEncoder.encode(to.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), StandardCharsets.UTF_8.toString());

            DResponse response = comm.getUrlData(session.getConfig().getReportingUrl(),
                    "v4/transactions?orderId=&product=&fromDate=" + fromStr +
                            "&toDate=" + toStr + "&groupTransactionsByOrder=false&intAccount=" +
                            session.getClient().getIntAccount() + "&sessionId=" + session.getJSessionId(), null);
            return gson.fromJson(getResponseData(response, false), DTransactions.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving transactions", e);
        }
    }

    @Override
    public void setPricePollingInterval(int duration, TimeUnit unit) throws DeGiroException {
        if (pricePoller != null) {
            throw new DeGiroException("Price polling interval must be set before adding price watches");
        }
        pollingInterval = unit.toMillis(duration);
    }

    @Override
    public void setPriceListener(DPriceListener priceListener) {
        this.priceListener = priceListener;
    }

    @Override
    public synchronized void subscribeToPrice(List<String> vwdIssueId) throws DeGiroException {
        ensureVwdSession();

        if (priceListener == null) {
            throw new DeGiroException("PriceListener not set");
        }

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

            StringBuilder requestedIssues = new StringBuilder();
            for (String issueId : vwdIssueId) {
                requestedIssues.append(("req(XXX.BidPrice);req(XXX.AskPrice);req(XXX.LastPrice);req(XXX.LastTime)").replace("XXX", issueId + ""));
            }

            if (vwdIssueId.hashCode() != subscribedVwdIssues.hashCode()) {
                subscribedVwdIssues.addAll(vwdIssueId);
            }

            HashMap<String, String> data = Maps.newHashMap();
            data.put("controlData", requestedIssues.toString());

            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/" + session.getVwdSession(), data, headers);
            String responseData = getResponseData(response, false);
            if (response.getStatus() != 200) {
                DLog.MANAGER.error(String.format("Error subscribiendo a las issues: %s. Data: %s",
                        Joiner.on(", ").join(vwdIssueId), responseData));
            }

            DLog.MANAGER.info("Subscribed successfully for issues " + Joiner.on(", ").join(vwdIssueId));

        } catch (IOException e) {
            throw new DeGiroException("IOException while subscribing to issues", e);
        }

        if (pricePoller == null) {
            pricePoller = new Timer("PRICE-POLLER", true);
            pricePoller.scheduleAtFixedRate(new DPriceTimerTask(vwdIssueId), 0, pollingInterval);
        }
    }

    @Override
    public void subscribeToPriceByProductsId(List<String> productIds) throws DeGiroException {
        DProducts dProducts = getProducts(productIds);
        if (dProducts != null) {
            List<String> issuesId = dProducts.getData().values().stream()
                    .filter(x -> x.getVwdIdentifierType().equals("vwdkey"))
                    .map(DProduct::getVwdId)
                    .collect(Collectors.toList());
            subscribeToPrice(issuesId);
        }
    }

    @Override
    public synchronized void clearPriceSubscriptions() {
        session.setVwdSession(null);
        subscribedVwdIssues.clear();
        pricePoller.cancel();
        pricePoller = null;
    }

    @Override
    public DProducts getProducts(List<String> productIds) throws DeGiroException {
        ensureLogged();
        try {
            List<Header> headers = new ArrayList<>(1);
            ArrayList<String> productIdStr = new ArrayList<>(productIds.size());
            for (String productId : productIds) {
                productIdStr.add(productId + "");
            }
            DResponse response = comm.getUrlData(session.getConfig().getProductSearchUrl(),
                    "v5/products/info?intAccount=" + session.getClient().getIntAccount() +
                            "&sessionId=" + session.getJSessionId(), productIdStr, headers);
            return gson.fromJson(getResponseData(response, false), DProducts.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving product information", e);
        }
    }

    @Override
    public DProductSearch searchProducts(String text, DProductType type, int limit, int offset) throws DeGiroException {

        if (Strings.isNullOrEmpty(text)) {
            throw new DeGiroException("Nothing to search");
        }

        ensureLogged();
        try {

            String qs = "&searchText=" + text;

            if (type != null && type.getTypeCode() != 0) {
                qs += "&productTypeId=" + type.getTypeCode();
            }
            qs += "&limit=" + limit;
            if (offset > 0) {
                qs += "&offset=" + offset;
            }

            DResponse response = comm.getUrlData(session.getConfig().getProductSearchUrl(),
                    String.format("v5/products/lookup?intAccount=%d&sessionId=%s%s",
                            session.getClient().getIntAccount(), session.getJSessionId(), qs), null);
            return gson.fromJson(getResponseData(response), DProductSearch.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving product information", e);
        }
    }

    @Override
    public DOrderConfirmation checkOrder(DNewOrder order) throws DeGiroException {

        if (order == null) {
            throw new DeGiroException("Order was null (no order to check)");
        }

        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(),
                    String.format("v5/checkOrder;jsessionid=%s?intAccount=%d&sessionId=%s",
                            session.getJSessionId(), session.getClient().getIntAccount(), session.getJSessionId()),
                    order.toMap());
            return gson.fromJson(getResponseData(response), DOrderConfirmation.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }
    }

    @Override
    public DPlacedOrder confirmOrder(DNewOrder order, String confirmationId) throws DeGiroException {

        if (order == null) {
            throw new DeGiroException("Order was null (no order to check)");
        }

        if (Strings.isNullOrEmpty(confirmationId)) {
            throw new DeGiroException("ConfirmationId was empty");
        }

        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(),
                    String.format(
                            "v5/order/%s;jsessionid=%s?intAccount=%d&sessionId=%s",
                            confirmationId, session.getJSessionId(), session.getClient().getIntAccount(), session.getJSessionId()),
                    order.toMap());
            return gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }
    }

    @Override
    public DPlacedOrder deleteOrder(String orderId) throws DeGiroException {

        if (Strings.isNullOrEmpty(orderId)) {
            throw new DeGiroException("orderId was empty");
        }

        ensureLogged();
        try {
            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(),
                    String.format("v5/order/%s;jsessionid=%s?intAccount=%d&sessionId=%s",
                            orderId, session.getJSessionId(), session.getClient().getIntAccount(), session.getJSessionId()),
                    null, null, "DELETE");
            return gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }
    }

    @Override
    public DPlacedOrder updateOrder(DOrder order, BigDecimal limit, BigDecimal stop) throws DeGiroException {

        if (order == null) {
            throw new NullPointerException("Order was null");
        }

        ensureLogged();
        try {

            DResponse response = comm.getUrlData(session.getConfig().getTradingUrl(),
                    String.format("v5/order/%s;jsessionid=%s?intAccount=%d&sessionId=%s",
                            order.getId(), session.getJSessionId(), session.getClient().getIntAccount(), session.getJSessionId()),
                    order.update(limit, stop).toMap(), null, "PUT");
            return gson.fromJson(getResponseData(response), DPlacedOrder.class);
        } catch (IOException e) {
            throw new DeGiroException("IOException while checking order", e);
        }
    }

    private void ensureLogged() throws DeGiroException {
        if (Strings.isNullOrEmpty(session.getJSessionId())) {
            login();
        }
    }

    private void login() throws DeGiroException {

        try {
            DLogin login = new DLogin();
            login.setUsername(credentials.getUsername());
            login.setPassword(credentials.getPassword());

            DResponse response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/login", login);

            if (response.getStatus() != 200) {
                if (response.getStatus() == 400) {
                    throw new DInvalidCredentialsException();
                } else {
                    throw new DeGiroException("Bad login HTTP status " + response.getStatus());
                }
            }

            response = comm.getUrlData(BASE_TRADER_URL, "/login/secure/config", null);
            session.setConfig(gson.fromJson(getResponseData(response), DConfig.class));

            response = comm.getUrlData(session.getConfig().getPaUrl(), "client?sessionId=" + session.getJSessionId(), null);
            session.setClient(gson.fromJson(getResponseData(response), DClient.class));

        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving user information", e);
        }

    }

    private void ensureVwdSession() throws DeGiroException {
        ensureVwdSession(false);
    }

    private void ensureVwdSession(boolean refresh) throws DeGiroException {
        ensureLogged();
        if (refresh || session.getVwdSession() == null) {
            getVwdSession();
            if (!subscribedVwdIssues.isEmpty()) {
                subscribeToPrice(Lists.newArrayList(subscribedVwdIssues));
            }
        }
    }

    private void getVwdSession() throws DeGiroException {

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));
            HashMap<String, String> data = Maps.newHashMap();
            data.put("referrer", "https://trader.degiro.nl");
            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS",
                    "/request_session?version=1.0.20170315&userToken=" + session.getClient().getId(), data, headers);
            HashMap map = gson.fromJson(getResponseData(response, false), HashMap.class);
            session.setVwdSession((String) map.get("sessionId"));
        } catch (IOException e) {
            throw new DeGiroException("IOException while retrieving vwd session", e);
        }
    }

    private void checkPriceChanges(List<String> vwdIssueId) throws DeGiroException {
        ensureVwdSession(true);

        try {
            List<Header> headers = new ArrayList<>(1);
            headers.add(new BasicHeader("Origin", session.getConfig().getTradingUrl()));

            DResponse response = comm.getUrlData("https://degiro.quotecast.vwdservices.com/CORS", "/" + session.getVwdSession(), null, headers);
            List<DRawVwdPrice> data = gson.fromJson(getResponseData(response, false), rawPriceData);

            List<DPrice> prices = DUtils.convert(vwdIssueId, data);

            if (priceListener != null) {
                for (DPrice price : prices) {
                    priceListener.priceChanged(price);
                }
            }

        } catch (IOException e) {
            throw new DeGiroException("IOException while subscribing to issues", e);
        }

        if (pricePoller == null) {
            pricePoller = new Timer("Prices", true);
            pricePoller.scheduleAtFixedRate(new DPriceTimerTask(vwdIssueId), 0, pollingInterval);
        }
    }

    private String getResponseData(DResponse response) throws DeGiroException {
        return getResponseData(response, true);
    }

    private String getResponseData(DResponse response, boolean processData) throws DeGiroException {

        DLog.WIRE.info(response.getMethod() + " " + response.getUrl() + " >> HTTP " + response.getStatus());

        if (response.getStatus() == 401) {
            DLog.MANAGER.warn("Session expired, clearing session tokens");
            session.clearSession();
            throw new DUnauthorizedException();
        }

        if (response.getStatus() == 200 || response.getStatus() == 201) {
            return processData ? processData(response.getText()) : response.getText();
        } else {
            throw new DeGiroException("Unexpected HTTP Status " + response.getStatus() +
                    ": " + response.getMethod() + " " + response.getUrl() + " TEXT: " + response.getText());
        }
    }

    private String processData(String text) {
        JsonObject object = gson.fromJson(text, JsonObject.class);
        return (object.has("data") && object.size() == 1) ? object.get("data").toString() : text;
    }

    public DSession getSession() {
        return session;
    }

    private class DPriceTimerTask extends TimerTask {

        private List<String> vwdIssueId;

        public DPriceTimerTask(List<String> vwdIssueId) {
            this.vwdIssueId = vwdIssueId;
        }

        @Override
        public void run() {
            try {
                DeGiroImpl.this.checkPriceChanges(vwdIssueId);
            } catch (Exception e) {
                DLog.MANAGER.error("Exception while updating prices", e);
            }
        }
    }

}
