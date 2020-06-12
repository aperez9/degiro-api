package cat.indiketa.degiro.utils;

import cat.indiketa.degiro.log.DLog;
import cat.indiketa.degiro.model.*;
import cat.indiketa.degiro.model.DCashFunds.DCashFund;
import cat.indiketa.degiro.model.DLastTransactions.DTransaction;
import cat.indiketa.degiro.model.DPortfolio.DProductRow;
import cat.indiketa.degiro.model.raw.*;
import cat.indiketa.degiro.model.raw.DRawPortfolio.Value;
import cat.indiketa.degiro.model.raw.DRawPortfolio.Value_;
import com.google.common.base.CaseFormat;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author indiketa
 */
public class DUtils {

    private static final SimpleDateFormat HM_FORMAT = new SimpleDateFormat("HH:mm:ss");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_FORMAT2 = new SimpleDateFormat("dd-MM-yyyy");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");

    public static DPortfolio convert(DRawPortfolio rawPortfolio) {
        DPortfolio portfolio = new DPortfolio();
        List<DProductRow> active = new LinkedList<>();
        List<DProductRow> inactive = new LinkedList<>();

        for (Value value : rawPortfolio.getPortfolio().getValue()) {
            DProductRow productRow = convertProduct(value);

            if (productRow.getSize() == 0) {
                inactive.add(productRow);
            } else {
                active.add(productRow);
            }
        }

        portfolio.setActive(active);
        portfolio.setInactive(inactive);

        return portfolio;

    }

    public static DPortfolioSummary convertPortfolioSummary(DRawPortfolioSummary.TotalPortfolio row) {
        DPortfolioSummary portfolioSummary = new DPortfolioSummary();

        for (Value_ value : row.getValueNotNull()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {

                    case "portVal":
                    case "cash":
                    case "total":
                    case "pl":
                    case "plToday":
                    case "freeSpace":
                    case "reportFreeRuimte":
                    case "reportMargin":
                    case "reportPortfValue":
                    case "reportCashBal":
                    case "reportNetliq":
                    case "reportOverallMargin":
                    case "reportTotalLongVal":
                    case "reportDeficit":
                        BigDecimal bdValue = value.getValueDecimal();
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DPortfolioSummary.class.getMethod(methodName, BigDecimal.class).invoke(portfolioSummary, bdValue);
                        break;
                    case "reportCreationTime":
                        portfolioSummary.setReportCreationTime(HM_FORMAT.parse(value.getValueString()));
                        break;
                    case "freeSpaceNew":
                        BigDecimal bdMValue = value.getValueMapDecimal();
                        if (bdMValue.scale() > 4) {
                            bdMValue = bdMValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DPortfolioSummary.class.getMethod(methodName, BigDecimal.class).invoke(portfolioSummary, bdMValue);
                        break;
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | ParseException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of portfolioSummary", e);
            }
        }

        return portfolioSummary;
    }

    public static DProductRow convertProduct(Value row) {
        DProductRow productRow = new DProductRow();

        for (Value_ value : row.getValueNotNull()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "size":
                    case "change":
                    case "contractSize":
                        long longValue = value.getValueLong();
                        DProductRow.class.getMethod(methodName, long.class).invoke(productRow, longValue);
                        break;
                    case "id":
                    case "product":
                    case "currency":
                    case "exchangeBriefCode":
                    case "productCategory":
                    case "positionType":
                        String stringValue = value.getValueString();
                        DProductRow.class.getMethod(methodName, String.class).invoke(productRow, stringValue);
                        break;
                    case "lastUpdate":
                        break;
                    case "closedToday":
                    case "tradable":
                        Boolean booleanValue = value.getValueBoolean();
                        DProductRow.class.getMethod(methodName, boolean.class).invoke(productRow, booleanValue);
                        break;
                    case "price":
                    case "value":
                    case "closePrice":
                    case "realizedProductPl":
                    case "todayRealizedProductPl":
                    case "breakEvenPrice":
                        BigDecimal bdValue = value.getValueDecimal();
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DProductRow.class.getMethod(methodName, BigDecimal.class).invoke(productRow, bdValue);
                        break;
                    case "plBase":
                    case "todayPlBase":
                        BigDecimal bdMValue = value.getValueMapDecimal();
                        if (bdMValue.scale() > 4) {
                            bdMValue = bdMValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DProductRow.class.getMethod(methodName, BigDecimal.class).invoke(productRow, bdMValue);
                        break;
                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of portfolio", e);
            }
        }

        return productRow;
    }

    public static DCashFunds convert(DRawCashFunds rawCashFunds) {
        DCashFunds cashFunds = new DCashFunds();
        List<DCashFund> list = new LinkedList<>();

        for (Value value : rawCashFunds.getCashFunds().getValue()) {
            DCashFund cashFund = convertCash(value);
            list.add(cashFund);
        }

        cashFunds.setCashFunds(list);

        return cashFunds;

    }

    public static DCashFund convertCash(Value row) {

        DCashFund cashFund = new DCashFund();

        for (Value_ value : row.getValueNotNull()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "id":
                        Long longValue = value.getValueLong();
                        DCashFund.class.getMethod(methodName, long.class).invoke(cashFund, longValue);
                        break;
                    case "currencyCode":
                        String stringValue = value.getValueString();
                        DCashFund.class.getMethod(methodName, String.class).invoke(cashFund, stringValue);
                        break;
                    case "value":
                        BigDecimal bdValue = value.getValueDecimal();
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DCashFund.class.getMethod(methodName, BigDecimal.class).invoke(cashFund, bdValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of cash fund", e);
            }

        }
        return cashFund;
    }

    public static DOrders convert(DRawOrders rawOrders) {
        DOrders orders = new DOrders();
        List<DOrder> list = new LinkedList<>();

        for (Value value : rawOrders.getOrders().getValue()) {
            DOrder order = convertOrder(value);
            list.add(order);
        }

        orders.setOrders(list);

        return orders;

    }

    public static DOrder convertOrder(Value row) {

        DOrder order = new DOrder();

        for (Value_ value : row.getValueNotNull()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "contractType":
                    case "contractSize":
                        int intValue = value.getValueInt();
                        DOrder.class.getMethod(methodName, int.class).invoke(order, intValue);
                        break;
                    case "productId":
                    case "size":
                    case "quantity":
                        long longValue = value.getValueLong();
                        DOrder.class.getMethod(methodName, long.class).invoke(order, longValue);
                        break;
                    case "id":
                    case "product":
                    case "currency":
                        String stringValue = value.getValueString();
                        DOrder.class.getMethod(methodName, String.class).invoke(order, stringValue);
                        break;
                    case "buysell":
                        String stringValue2 = value.getValueString();
                        order.setBuysell(DOrderAction.getOrderByValue(stringValue2));
                        break;

                    case "date":
                        Calendar calendar = processDate(value.getValueString());
                        DOrder.class.getMethod(methodName, Calendar.class).invoke(order, calendar);
                        break;
                    case "orderTypeId":
                        order.setOrderType(DOrderType.getOrderByValue(value.getValueInt()));
                        break;
                    case "orderTimeTypeId":
                        order.setOrderTime(DOrderTime.getOrderByValue(value.getValueInt()));
                        break;
                    case "price":
                    case "stopPrice":
                    case "totalOrderValue":
                        BigDecimal bdValue = value.getValueDecimal();
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DOrder.class.getMethod(methodName, BigDecimal.class).invoke(order, bdValue);
                        break;

                    case "isModifiable":
                    case "isDeletable":
                        Boolean booleanValue = value.getValueBoolean();
                        DOrder.class.getMethod(methodName, boolean.class).invoke(order, booleanValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of order", e);
            }

        }
        return order;
    }

    private static Calendar processDate(String date) {
        Calendar parsed = null;

        date = Strings.nullToEmpty(date);

        if (date.contains(":")) {
            parsed = Calendar.getInstance();
            parsed.set(Calendar.HOUR_OF_DAY, Integer.parseInt(date.split(":")[0]));
            parsed.set(Calendar.MINUTE, Integer.parseInt(date.split(":")[1]));
            parsed.set(Calendar.SECOND, 0);
            parsed.set(Calendar.MILLISECOND, 0);
        } else if (date.contains("/")) {
            parsed = Calendar.getInstance();
            int month = Integer.parseInt(date.split("/")[1]) - 1;

            if (parsed.get(Calendar.MONTH) < month) {
                parsed.add(-1, Calendar.YEAR);
            }

            parsed.set(Calendar.MONTH, month);
            parsed.set(Calendar.DATE, Integer.parseInt(date.split("/")[1]));
            parsed.set(Calendar.HOUR_OF_DAY, 0);
            parsed.set(Calendar.MINUTE, 0);
            parsed.set(Calendar.SECOND, 0);
            parsed.set(Calendar.MILLISECOND, 0);

        } else {
        }
        return parsed;
    }

    public static DLastTransactions convert(DRawTransactions rawOrders) {
        DLastTransactions transactions = new DLastTransactions();
        List<DTransaction> list = new LinkedList<>();

        for (Value value : rawOrders.getTransactions().getValue()) {
            DTransaction order = convertTransaction(value);
            list.add(order);
        }

        transactions.setTransactions(list);

        return transactions;

    }

    public static DTransaction convertTransaction(Value row) {

        DTransaction transaction = new DTransaction();

        for (Value_ value : row.getValueNotNull()) {

            try {

                String methodName = "set" + CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_CAMEL, value.getName());

                switch (value.getName()) {
                    case "contractType":
                    case "contractSize":
                        int intValue = value.getValueInt();
                        DTransaction.class.getMethod(methodName, int.class).invoke(transaction, intValue);
                        break;
                    case "productId":
                    case "size":
                    case "quantity":
                    case "id":
                        long longValue = value.getValueLong();
                        DTransaction.class.getMethod(methodName, long.class).invoke(transaction, longValue);
                        break;
                    case "product":
                    case "currency":
                        String stringValue = (String) value.getValue();
                        DTransaction.class.getMethod(methodName, String.class).invoke(transaction, stringValue);
                        break;
                    case "buysell":
                        String stringValue2 = (String) value.getValue();
                        transaction.setBuysell(DOrderAction.getOrderByValue(stringValue2));
                        break;

                    case "date":
                        Calendar calendar = processDate((String) value.getValue());
                        DTransaction.class.getMethod(methodName, Calendar.class).invoke(transaction, calendar);
                        break;
                    case "orderType":
                        transaction.setOrderType(DOrderType.getOrderByValue(value.getValueInt()));
                        break;
                    case "orderTime":
                        transaction.setOrderTime(DOrderTime.getOrderByValue(value.getValueInt()));
                        break;
                    case "price":
                    case "stopPrice":
                    case "totalOrderValue":
                        BigDecimal bdValue = value.getValueDecimal();
                        if (bdValue.scale() > 4) {
                            bdValue = bdValue.setScale(4, RoundingMode.HALF_UP);
                        }
                        DTransaction.class.getMethod(methodName, BigDecimal.class).invoke(transaction, bdValue);
                        break;

                    case "isModifiable":
                    case "isDeletable":
                        Boolean booleanValue = value.getValueBoolean();
                        DTransaction.class.getMethod(methodName, boolean.class).invoke(transaction, booleanValue);
                        break;

                }
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                DLog.MANAGER.error("Error while setting value of order", e);
            }

        }
        return transaction;
    }

    public static List<DPrice> convert(List<String> vwdIssuesId, List<DRawVwdPrice> data) {

        //Mapa de claves
        Map<String, List<DRawVwdPrice>> keyMap = data.stream().collect(Collectors.groupingBy(DRawVwdPrice::getM));

        // Lista de claves
        List<DRawVwdPrice> keys = keyMap.get("a_req")
                .stream().filter(x -> x.getV().size() == 2)
                .collect(Collectors.toList());

        // Lista de datos
        List<DRawVwdPrice> values = keyMap.entrySet()
                .stream().filter(x -> !x.getKey().equals("a_req"))
                .flatMap(x -> x.getValue().stream().filter(y -> y.getV().size() == 2))
                .collect(Collectors.toList());

        List<DPrice> prices = Lists.newArrayList();
        for (DRawVwdPrice rawPrice : keys) {
            processDRawVwdPrice(rawPrice, prices, vwdIssuesId, values);
        }

        return prices;
    }

    private static void processDRawVwdPrice(DRawVwdPrice rawPrice, List<DPrice> prices,
                                            List<String> vwdIssuesId, List<DRawVwdPrice> values) {

        // Buscamos en la lista de issues
        String keyValue = rawPrice.getV().get(0);
        String dataValue = rawPrice.getV().get(1);
        DPrice price = prices.stream()
                .filter(x -> keyValue.contains(x.getIssueId()))
                .findFirst().orElse(null);

        if (price == null) {
            Optional<String> vissueFind = vwdIssuesId.stream()
                    .filter(keyValue::contains).findFirst();
            if (vissueFind.isPresent()) {
                price = new DPrice();
                price.setIssueId(vissueFind.get());
                prices.add(price);
            } else {
                return;
            }
        }

        String key = keyValue.replace(price.getIssueId(), "");
        Optional<String> dataContent = getData(values, dataValue);
        if (dataContent.isPresent()) {
            switch (key) {
                case ".BidPrice":
                    price.setBid(Double.valueOf(dataContent.get()));
                    break;
                case ".AskPrice":
                    price.setAsk(Double.valueOf(dataContent.get()));
                    break;
                case ".LastPrice":
                    price.setLast(Double.valueOf(dataContent.get()));
                    break;
                case ".LastTime":
                    price.setLastTime(LocalTime.parse(dataContent.get()));
                    break;
            }
        }
    }

    private static Optional<String> getData(List<DRawVwdPrice> values, String dataValue) {
        return values.stream()
                .filter(x -> x.getV().get(0).equals(dataValue)).findFirst()
                .map(x -> x.getV().get(1));
    }

    public static class ProductTypeAdapter extends TypeAdapter<DProductType> {

        @Override
        public DProductType read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            int value = reader.nextInt();
            return DProductType.getProductTypeByValue(value);
        }

        @Override
        public void write(JsonWriter writer, DProductType value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getTypeCode());
        }
    }

    public static class OrderTimeTypeAdapter extends TypeAdapter<DOrderTime> {

        @Override
        public DOrderTime read(JsonReader reader) throws IOException {
            switch (reader.peek()) {
                case NULL:
                    reader.nextNull();
                    return null;
                case STRING:
                    String strValue = reader.nextString();
                    return DOrderTime.getOrderByValue(strValue);
                case NUMBER:
                    int value = reader.nextInt();
                    return DOrderTime.getOrderByValue(value);
                default:
                    return null;
            }
        }

        @Override
        public void write(JsonWriter writer, DOrderTime value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class OrderTypeTypeAdapter extends TypeAdapter<DOrderType> {

        @Override
        public DOrderType read(JsonReader reader) throws IOException {
            switch (reader.peek()) {
                case NULL:
                    reader.nextNull();
                    return null;
                case STRING:
                    String strValue = reader.nextString();
                    return DOrderType.getOrderByValue(strValue);
                case NUMBER:
                    int value = reader.nextInt();
                    return DOrderType.getOrderByValue(value);
                default:
                    return null;
            }
        }

        @Override
        public void write(JsonWriter writer, DOrderType value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class OrderActionTypeAdapter extends TypeAdapter<DOrderAction> {

        @Override
        public DOrderAction read(JsonReader reader) throws IOException {
            switch (reader.peek()) {
                case NULL:
                    reader.nextNull();
                    return null;
                case STRING:
                    String strValue = reader.nextString();
                    return DOrderAction.getOrderByValue(strValue);
                case NUMBER:
                    int value = reader.nextInt();
                    return DOrderAction.getOrderByValue(value);
                default:
                    return null;
            }
        }

        @Override
        public void write(JsonWriter writer, DOrderAction value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class OrderOperationTypeAdapter extends TypeAdapter<DOrderOperation> {

        @Override
        public DOrderOperation read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();
            return DOrderOperation.getOrderByValue(value);
        }

        @Override
        public void write(JsonWriter writer, DOrderOperation value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class OrderStatusTypeAdapter extends TypeAdapter<DOrderStatus> {

        @Override
        public DOrderStatus read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();
            return DOrderStatus.getOrderByValue(value);
        }

        @Override
        public void write(JsonWriter writer, DOrderStatus value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(value.getStrValue());
        }
    }

    public static class DateTypeAdapter extends TypeAdapter<Date> {

        @Override
        public Date read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return null;
            }
            String value = reader.nextString();

            Date d = null;
            try {
                d = DATE_TIME_FORMAT.parse(value);
            } catch (ParseException e) {
                try {
                    d = DATE_FORMAT.parse(value);
                } catch (ParseException e2) {
                    DLog.MANAGER.warn("Date not parseable: " + value, e2);
                }
            }
            return d;
        }

        @Override
        public void write(JsonWriter writer, Date value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }

            writer.value(DATE_FORMAT.format(value));
        }
    }

}
