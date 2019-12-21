package runnable;

import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;
import shared.persistence.ServiceFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleIO {
    private BillDataService billDataService;
    private ProductDataService productDataService;
    private GomhangProductService gomhangProductService;
    private final String lineBreak = "\n";
    private final String menu = "******Service options******" + lineBreak +
            "0. Exit the program" + lineBreak +
            "1. Get & store products from nhanhvn" + lineBreak +
            "2. Get & store bills from nhanhvn" + lineBreak +
            "3. Download & store products from gomhangvn";

    public ConsoleIO() {
        initializeServices();
    }

    private void initializeServices() {
        billDataService = (BillDataService) ServiceFactory.createNhanhvnService("bill");
        productDataService =  (ProductDataService) ServiceFactory.createNhanhvnService("product");
        gomhangProductService = ServiceFactory.createGomhangService("product");
    }

    private void printMenu() {
        System.out.println(menu);
        System.out.print("Please choose an option: ");
    }

    private void printDone() {
        System.out.println("Service runs successfully, returning to menu selection.");
    }

    private void printUnsupportedService() {
        System.out.println("Current service is not supported, please try again!");
    }

    public void consoleApplication() throws IOException, SQLException {
        int option = -1;
        Scanner input = new Scanner(System.in);
        do {
            printMenu();
            while(true) {
                if(input.hasNextInt()) {
                    option = input.nextInt();
                    break;
                } else {
                    input.nextLine();
                    System.out.println("Invalid input, make sure to enter integer number only");
                    System.out.print("Please choose another option: ");
                }
//                try {
//                    option = input.nextInt();
//                    break;
//                } catch (InputMismatchException exception) {
//
//                    input.nextLine();
//                    break;
//                }
            }

            switch(option) {
                case 0: {
                    System.out.println("Exit the program...");
                    break;
                }

                case 1: {
                    productDataService.getAndPersistAllProducts();
                    printDone();
                    break;
                }

                case 2: {
                    billDataService.getAndPersistAllBills();
                    printDone();
                    break;
                }

                case 3: {
                    gomhangProductService.getAndPersistProductsFromCsvFile();
                    printDone();
                    break;
                }

                default: {
                    printUnsupportedService();
                    break;
                }
            }
            input.nextLine();
        } while (option != 0);
        input.close();
    }

    public static void main(String[] args) throws IOException, SQLException {
        ConsoleIO consoleApp = new ConsoleIO();
        consoleApp.consoleApplication();
    }
}
