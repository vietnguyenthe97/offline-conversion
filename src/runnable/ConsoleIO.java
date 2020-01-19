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
            "6. Tai du lieu offline len facebook" + lineBreak +
            "7. Thuc hien auto lan luot cac buoc theo thu tu: 1 -> 2 -> 3 -> 5 -> 6";

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
                    while (true) {
                        System.out.println("Ban co 2 lua chon: ");
                        System.out.println("1. Xuat san pham cha (parent products) co trong hoa don (bills) trong vong 62 ngay co facebookId < 0 ");
                        System.out.println("2. Xuat tat ca san pham cha (parent products) co trong hoa don (bills) trong vong 62 ngay");
                        System.out.print("Lua chon cua ban la? (1/2): ");
                        Scanner keyInput = new Scanner(System.in);
                        String keyPress = keyInput.nextLine();

                        if (keyPress.equals("1")) {
                            System.out.println("Ban chon phuong an 1...");
                            transactionService.setGetFromAllBills(false);
                            transactionService.exportNhanhvnProducts();
                            break;
                        }

                        if (keyPress.equals("2")) {
                            System.out.println("Ban chon phuong an 2...");
                            transactionService.setGetFromAllBills(true);
                            transactionService.exportNhanhvnProducts();
                            break;
                        }

                        System.out.println("Lua chon khong ho tro, quay ve menu...");
                        break;
                    }
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

                case 7: {
                    productDataService.getAndPersistAllProducts();
                    billDataService.getAndPersistAllBills();
                    gomhangProductService.getAndPersistProductsFromCsvFile();
                    transactionService.updateFacebookId();
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
            System.out.println();
        } while (option != 0);
        input.close();
    }

    public static void main(String[] args) throws IOException, SQLException, 
    CsvDataTypeMismatchException, CsvRequiredFieldEmptyException {
        ConsoleIO consoleApp = new ConsoleIO();
        consoleApp.consoleApplication();
    }
}
