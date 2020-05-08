package eu.fiskaltrust.middleware.demo.grpc;

import bcl.Bcl;
import eu.fiskaltrust.middleware.demo.grpc.models.ReceiptRequestContainer;
import eu.fiskaltrust.middleware.demo.grpc.util.DateUtil;
import fiskaltrust.ifPOS.v1.IPOS;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ReceiptExampleRepository {

    private final String exampleDirectory;
    private final String cashboxId;

    public ReceiptExampleRepository(String exampleDirectory, String cashboxId) {
        this.exampleDirectory = exampleDirectory;
        this.cashboxId = cashboxId;
    }

    public List<ReceiptRequestContainer> getAll() {
        try {
            return Files.walk(Paths.get(exampleDirectory))
                    .filter(x -> Files.isRegularFile(x) && x.toString().toLowerCase().endsWith(".json"))
                    .map(x -> new ReceiptRequestContainer(x.getName(x.getNameCount() - 2).toString(), getReceiptRequest(x.toString())))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private IPOS.ReceiptRequest getReceiptRequest(String path) {
        try (FileReader reader = new FileReader(path)) {
            JSONParser jsonParser = new JSONParser();
            JSONObject receiptRequest = (JSONObject) jsonParser.parse(reader);

            return parseReceiptRequest(receiptRequest);
        } catch (IOException | ParseException | java.text.ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    private IPOS.ReceiptRequest parseReceiptRequest(JSONObject obj) throws java.text.ParseException {


        IPOS.ReceiptRequest.Builder builder = IPOS.ReceiptRequest.newBuilder()
                .setFtCashBoxID(cashboxId);

        setField((String) obj.get("ftQueueID"), builder::setFtQueueID);
        setField((String) obj.get("ftPosSystemId"), builder::setFtPosSystemId);
        setField((String) obj.get("cbTerminalID"), builder::setCbTerminalID);
        setField((String) obj.get("cbReceiptReference"), builder::setCbReceiptReference);
        setField(parseDatetime((String) obj.get("cbReceiptMoment")), builder::setCbReceiptMoment);
        setField((Long) obj.get("ftReceiptCase"), builder::setFtReceiptCase);
        setField((String) obj.get("ftReceiptCaseData"), builder::setFtReceiptCaseData);
        setField((String) obj.get("cbUser"), builder::setCbUser);
        setField((String) obj.get("cbArea"), builder::setCbArea);
        setField((String) obj.get("cbCustomer"), builder::setCbCustomer);
        setField((String) obj.get("cbSettlement"), builder::setCbSettlement);
        setField((String) obj.get("cbPreviousReceiptReference"), builder::setCbPreviousReceiptReference);
        setField(parseDecimal(obj.get("cbReceiptAmount")), builder::setCbReceiptAmount);
        JSONArray chargeItems = (JSONArray) obj.get("cbChargeItems");

        for (Object chargeItem : chargeItems) {
            if (chargeItem instanceof JSONObject) {
                builder.addCbChargeItems(parseChargeItem((JSONObject) chargeItem));
            }
        }

        JSONArray payItems = (JSONArray) obj.get("cbPayItems");
        for (Object payItem : payItems) {
            if (payItem instanceof JSONObject) {
                builder.addCbPayItems(parsePayItem((JSONObject) payItem));
            }
        }

        return builder.build();
    }

    private IPOS.PayItem parsePayItem(JSONObject obj) throws java.text.ParseException {
        IPOS.PayItem.Builder builder = IPOS.PayItem.newBuilder();

        setField((Long) obj.get("Position"), builder::setPosition);
        setField((Long) obj.get("ftPayItemCase"), builder::setFtPayItemCase);
        setField((String) obj.get("ftPayItemCaseData"), builder::setFtPayItemCaseData);
        setField(parseDecimal(obj.get("Quantity")), builder::setQuantity);
        setField(parseDecimal(obj.get("Amount")), builder::setAmount);
        setField((String) obj.get("AccountNumber"), builder::setAccountNumber);
        setField((String) obj.get("CostCenter"), builder::setCostCenter);
        setField((String) obj.get("MoneyGroup"), builder::setMoneyGroup);
        setField((String) obj.get("MoneyNumber"), builder::setMoneyNumber);
        setField((String) obj.get("Description"), builder::setDescription);
        setField(parseDatetime((String) obj.get("Moment")), builder::setMoment);

        return builder.build();
    }

    private IPOS.ChargeItem parseChargeItem(JSONObject obj) throws java.text.ParseException {
        IPOS.ChargeItem.Builder builder = IPOS.ChargeItem.newBuilder();

        setField((Long) obj.get("Position"), builder::setPosition);
        setField((Long) obj.get("ftChargeItemCase"), builder::setFtChargeItemCase);
        setField((String) obj.get("ftChargeItemCaseData"), builder::setFtChargeItemCaseData);
        setField(parseDecimal(obj.get("Quantity")), builder::setQuantity);
        setField(parseDecimal(obj.get("Amount")), builder::setAmount);
        setField(parseDecimal(obj.get("VATRate")), builder::setVATRate);
        setField(parseDecimal(obj.get("VATAmount")), builder::setVATAmount);
        setField(parseDecimal(obj.get("UnitQuantity")), builder::setUnitQuantity);
        setField(parseDecimal(obj.get("UnitQuantity")), builder::setUnitPrice);
        setField((String) obj.get("AccountNumber"), builder::setAccountNumber);
        setField((String) obj.get("CostCenter"), builder::setCostCenter);

        setField((String) obj.get("ProductGroup"), builder::setProductGroup);
        setField((String) obj.get("ProductNumber"), builder::setProductNumber);
        setField((String) obj.get("Description"), builder::setDescription);
        setField((String) obj.get("Unit"), builder::setUnit);
        setField((String) obj.get("ProductBarcode"), builder::setProductBarcode);
        setField(parseDatetime((String) obj.get("Moment")), builder::setMoment);

        return builder.build();
    }

    private <T> void setField(T value, Consumer<T> set) {
        if (value != null) {
            set.accept(value);
        }
    }

    private Bcl.Decimal parseDecimal(Object obj) {
        if (obj == null)
            return null;

        String str = obj.toString().replace("-", "");

        // TODO: Replace this workaround with a proper serialization and also include the HI bits, instead of just cutting after 15 characters
        str = str.length() > 15 ? str.substring(0, 15) : str;
        String[] split = str.split(Pattern.quote("."));
        int precision = split.length == 2 ? split[1].length() : 0;
        int shift = str.startsWith("-") ? 0 : 1;

        return Bcl.Decimal.newBuilder()
                .setLo(Long.parseLong(str.replace(".", "")))
                .setSignScale(precision << shift)
                .build();
    }

    private Bcl.DateTime parseDatetime(String value) throws java.text.ParseException {
        if (value == null)
            return null;

        Date date = DateUtil.parse(value);
        return Bcl.DateTime.newBuilder()
                .setKindValue(2)
                .setScaleValue(4)
                .setValue(date.getTime())
                .build();
    }
}
