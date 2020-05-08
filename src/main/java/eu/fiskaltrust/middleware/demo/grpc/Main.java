
package eu.fiskaltrust.middleware.demo.grpc;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import eu.fiskaltrust.middleware.demo.grpc.models.ReceiptRequestContainer;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static final String DEFAULT_URL = "localhost:10103";

    public static void main(String[] args) throws InterruptedException, IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.print(String.format("Middleware URL (default: %s): ", DEFAULT_URL));
        String url = scanner.nextLine();
        if(url == null || url.isEmpty()) {
            url = DEFAULT_URL;
        }

        String cashboxId;
        do {
            System.out.print("Cashbox ID: ");
            cashboxId = scanner.nextLine();
        } while (cashboxId == null || cashboxId.isEmpty());

        PosClient client = null;
        try {
            client = new PosClient(url);
            client.echo("Hello World!");

            var receiptExamples = new ReceiptExampleRepository("receipt-examples/de", cashboxId).getAll();
            printMenu(client, receiptExamples);
        } finally {
            if (client != null) {
                client.shutdown();
            }
        }
    }

    private static void printMenu(PosClient client, List<ReceiptRequestContainer> receiptExamples) throws IOException {
        for (int i = 0; i < receiptExamples.size(); i++) {
            var name = receiptExamples.get(i).getName();
            var req = receiptExamples.get(i).getReceiptRequest();
            System.out.println(String.format("<%d> %s (%x)", i + 1, name, req.getFtReceiptCase()));
        }
        System.out.println(String.format("\r\n<%d>: Journal 0x0000000000000000 Version information", receiptExamples.size() + 1));
        System.out.println(String.format("<%d>: Journal 0x0000000000000001 ActionJournal in internal format", receiptExamples.size() + 2));
        System.out.println(String.format("<%d>: Journal 0x0000000000000002 ReceiptJournal in internal format", receiptExamples.size() + 3));
        System.out.println(String.format("<%d>: Journal 0x0000000000000003 QueueItemJournal in internal format", receiptExamples.size() + 4));
        System.out.println(String.format("\r\n<exit>: Close this program"));

        Scanner scanner = new Scanner(System.in);
        String inputStr = scanner.next();

        if (inputStr.equals("exit")) {
            System.exit(0);
        }

        int input = tryParse(inputStr);

        if (input > 0 && input <= receiptExamples.size()) {
            System.out.println("Receipt request: ");
            System.out.println(receiptExamples.get(input).getReceiptRequest());

            var response = client.sign(receiptExamples.get(input - 1).getReceiptRequest());

            System.out.println("Receipt response: ");
            System.out.println(response);
        } else if (input >= receiptExamples.size() + 1 && input <= receiptExamples.size() + 4) {
            var journalType = input - receiptExamples.size() - 1;
            var response = client.journal(journalType);

            System.out.println("Journal response: ");
            System.out.println(response);

            Gson gson = new GsonBuilder().setPrettyPrinting().setLenient().create();
            JsonElement json = JsonParser.parseString(response.trim());
            System.out.println(gson.toJson(json));
        } else {
            System.out.println("The given input is not supported.");
        }

        System.out.println("Press any key to continue.");
        System.in.read();
        System.out.println("\n\n");
        printMenu(client, receiptExamples);
    }

    private static int tryParse(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}
