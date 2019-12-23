package runnable;

import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;
import nhanhvn.data.services.TransactionService;
import shared.persistence.ServiceFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;

public class ConsoleIO {
    private BillDataService billDataService;
    private ProductDataService productDataService;
    private GomhangProductService gomhangProductService;
    private TransactionService transactionService;
    private final String lineBreak = "\n";
    private final String menu = "******Service options******" + lineBreak +
            "0. Thoát chương trình" + lineBreak +
            "1. Lấy và lưu dữ liệu sản phẩm từ nhanhvn" + lineBreak +
            "2. Lấy và lưu dữ liệu hóa đơn từ nhanhvn" + lineBreak +
            "3. Tải và lưu dữ liệu sản phẩm từ gomhangvn" + lineBreak +
            "4. Xuất dữ liệu sản phẩm từ nhanhvn sang csv file" + lineBreak +
            "5. Cập nhật facebookId từ csv file";

    private void initializeServices() {
        billDataService = (BillDataService) ServiceFactory.createNhanhvnService("bill");
        productDataService =  (ProductDataService) ServiceFactory.createNhanhvnService("product");
        transactionService = (TransactionService) ServiceFactory.createNhanhvnService("transaction");
        gomhangProductService = ServiceFactory.createGomhangService("product");
    }

    private void printMenu() {
        System.out.println(menu);
        System.out.print("Please choose an option: ");
    }

    private void printDone() {
        System.out.println("Service finished, returning to menu.");
    }

    private void printUnsupportedService() {
        System.out.println("Current service is not supported, please try again!");
    }

    public void consoleApplication() throws IOException, SQLException, 
    CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        int option = -1;
        Scanner input = new Scanner(System.in);
        do {
            initializeServices();
            printMenu();
            while(true) {
                if (input.hasNextInt()) {
                    option = input.nextInt();
                    break;
                } else {
                    input.nextLine();
                    System.out.println("Invalid input, make sure to enter integer number only");
                    System.out.print("Please choose another option: ");
                }
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

                case 4: {
                	transactionService.exportNhanhvnProducts();
                    printDone();
                    break;
                }
                
                case 5: {
                	transactionService.updateFacebookId();
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

    public static void main(String[] args) throws IOException, SQLException, 
    CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        ConsoleIO consoleApp = new ConsoleIO();
        consoleApp.consoleApplication();
    }
}
