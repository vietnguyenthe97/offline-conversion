package runnable;

import com.opencsv.exceptions.CsvDataTypeMismatchException;
import com.opencsv.exceptions.CsvRequiredFieldEmptyException;
import gomhangvn.data.service.GomhangProductService;
import nhanhvn.data.services.BillDataService;
import nhanhvn.data.services.ProductDataService;
import nhanhvn.data.services.TransactionService;
import offlineconversion.data.service.UploadOfflineEventService;
import shared.persistence.ServiceFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

public class ConsoleIO {
    private BillDataService billDataService;
    private ProductDataService productDataService;
    private GomhangProductService gomhangProductService;
    private TransactionService transactionService;
    private UploadOfflineEventService uploadOfflineEventService;
    private final String lineBreak = "\n";
    private final String menu = "******Service options******" + lineBreak +
            "0. Thoat chuong trinh" + lineBreak +
            "1. Lay va luu du lieu san pham tu nhanhvn" + lineBreak +
            "2. Lay va luu du lieu hoa don tu nhanhvn" + lineBreak +
            "3. Tai va luu du lieu cua san pham tu gomhangvn" + lineBreak +
            "4. Xuat du lieu san pham tu nhanhvn sang csv file" + lineBreak +
            "5. Cap nhat facebookId tu csv file" + lineBreak +
            "6. Tai du lieu offline len facebook";

    private void initializeServices() {
        billDataService = (BillDataService) ServiceFactory.createNhanhvnService("bill");
        productDataService =  (ProductDataService) ServiceFactory.createNhanhvnService("product");
        transactionService = (TransactionService) ServiceFactory.createNhanhvnService("transaction");
        gomhangProductService = ServiceFactory.createGomhangService("product");
        uploadOfflineEventService = ServiceFactory.createUploadOfflineEventService("upload");
    }

    private void printMenu() {
        System.out.println(menu);
        System.out.print("Chon dich vu: ");
    }

    private void printDone() {
        System.out.println("Dich vu hoan thanh, tro ve menu chinh...");
    }

    private void printUnsupportedService() {
        System.out.println("Dich vu hien tai khong ho tro, xin thu lai!");
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
                    System.out.println("Input sai format");
                    System.out.print("Xin hay thu lai: ");
                }
            }

            switch(option) {
                case 0: {
                    System.out.println("Thoat chuong trinh...");
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
                    while (true) {
                        System.out.println("Xin dam bao ban da co file: " + transactionService.getAbsoluteImportPath());
                        System.out.print("Tien hanh mapping? (y/n): ");
                        Scanner keyInput = new Scanner(System.in);
                        String keyPress = keyInput.nextLine();
                        if (keyPress.equalsIgnoreCase("Y") || keyPress.equalsIgnoreCase("YES")) {
                            System.out.println("Bat dau thuc hien mapping...");
                            transactionService.updateFacebookId();
                            break;
                        } else {
                                if (keyPress.equalsIgnoreCase("N") || keyPress.equalsIgnoreCase("NO")) {
                                    System.out.println("Ban da tu choi mapping.");
                                    break;
                                } else {
                                    System.out.println("Sai format, xin nhap yes/y de thuc hien mapping, hoac no/n de thoat dich vu.");
                                }
                        }
                    }
                    printDone();
                	break;
                }

                case 6: {
                    uploadOfflineEventService.uploadAllBills();
                    printDone();
                    break;
                }

                default: {
                    printUnsupportedService();
                    break;
                }
            }
            input.nextLine();
            System.out.print("\n\n");
        } while (option != 0);
        input.close();
    }

    public static void main(String[] args) throws IOException, SQLException, 
    CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        ConsoleIO consoleApp = new ConsoleIO();
        consoleApp.consoleApplication();
    }
}
