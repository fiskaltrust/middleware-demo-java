package eu.fiskaltrust.middleware.demo.grpc.models;

import fiskaltrust.ifPOS.v1.IPOS;

public class ReceiptRequestContainer {
    private String name;
    private IPOS.ReceiptRequest receiptRequest;

    public ReceiptRequestContainer(String name, IPOS.ReceiptRequest receiptRequest) {
        this.name = name;
        this.receiptRequest = receiptRequest;
    }

    public String getName() {
        return name;
    }

    public IPOS.ReceiptRequest getReceiptRequest() {
        return receiptRequest;
    }
}
