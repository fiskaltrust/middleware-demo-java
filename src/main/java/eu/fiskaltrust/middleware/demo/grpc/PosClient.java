package eu.fiskaltrust.middleware.demo.grpc;

import fiskaltrust.ifPOS.v1.IPOS;
import fiskaltrust.ifPOS.v1.POSGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class PosClient {
    private static final Logger logger = Logger.getLogger(PosClient.class.getName());

    private final POSGrpc.POSBlockingStub blockingStub;
    private final ManagedChannel channel;

    public PosClient(String url) {
        channel = ManagedChannelBuilder.forTarget(url)
                .usePlaintext()
                .build();
        blockingStub = POSGrpc.newBlockingStub(channel);
    }

    public void shutdown() throws InterruptedException {
        channel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
    }

    public String echo(String message) {
        var request = IPOS.EchoRequest.newBuilder().setMessage(message).build();
        IPOS.EchoResponse response = blockingStub.echo(request);
        return response.getMessage();
    }

    public String journal(long journalType) {
        var request = IPOS.JournalRequest.newBuilder().setFtJournalType(journalType).build();
        Iterator<IPOS.JournalResponse> responses = blockingStub.journal(request);

        var chunks = new ArrayList<Integer>();
        responses.forEachRemaining(response -> {
            chunks.addAll(response.getChunkList());
        });

        var bytes = new byte[chunks.size()];
        for (int i = 0; i < chunks.size(); i++) {
            bytes[i] = chunks.get(i).byteValue();
        }

        return new String(bytes);
    }

    public IPOS.ReceiptResponse sign(IPOS.ReceiptRequest receiptRequest) {
        IPOS.ReceiptResponse response = blockingStub.sign(receiptRequest);
        return response;
    }


//    public void sign(String receiptRequestJson) {
//        logger.info("Receipt request: ");
//        var request = IPOS.ReceiptRequest.newBuilder();
//        request.
//        HelloRequest request = HelloRequest.newBuilder().setName(name).build();
//        HelloReply response;
//        try {
//            response = blockingStub.sayHello(request);
//        } catch (StatusRuntimeException e) {
//            logger.log(Level.WARNING, "RPC failed: {0}", e.getStatus());
//            return;
//        }
//        logger.info("Greeting: " + response.getMessage());
//    }
}