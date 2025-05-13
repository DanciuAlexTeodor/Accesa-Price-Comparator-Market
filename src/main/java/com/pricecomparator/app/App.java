package com.pricecomparator.app;

import java.util.*;

import com.pricecomparator.service.BasketOptimizer;
import com.pricecomparator.service.BestDiscounts;

public class App {

    private static final int OPTION_OPTIMIZE_BASKET = 1;
    private static final int OPTION_BEST_DISCOUNTS = 2;
    private static final int OPTION_EXIT = 0;



    private static final Map<Integer, List<String>> PREDEFINED_BASKETS = new LinkedHashMap<>();
    private static final Map<Integer,String> PREDEFINED_DATES = new LinkedHashMap<>();
    private static final Map<Integer,String> PREDEFINED_STORES = new LinkedHashMap<>();

    static {
        PREDEFINED_BASKETS.put(1, List.of("P001", "P020", "P028", "P034"));
        PREDEFINED_BASKETS.put(2, List.of("P031", "P040", "P008"));
        PREDEFINED_BASKETS.put(3, List.of("P021", "P043", "P026", "P046"));
    }

    static {
        PREDEFINED_DATES.put(1,"2025-05-01");
        PREDEFINED_DATES.put(2,"2025-05-08");
    }

    static {
        PREDEFINED_STORES.put(1,"Kaufland");
        PREDEFINED_STORES.put(2,"Lidl");
        PREDEFINED_STORES.put(3,"Profi");
        PREDEFINED_STORES.put(4,"All stores");
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printMenu();

            int choice = readInt(scanner, "Enter your option: ");

            switch (choice) {
                case OPTION_OPTIMIZE_BASKET:
                    handleBasketOptimization(scanner);
                    break;
                case OPTION_BEST_DISCOUNTS:
                    handleBestDiscounts(scanner);
                case OPTION_EXIT:
                    System.out.println("Goodbye!");
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void printMenu() {
        System.out.println("\n==== Price Comparator Menu ====");
        System.out.println(OPTION_OPTIMIZE_BASKET + ") Split basket into cheapest shopping lists");
        System.out.println(OPTION_BEST_DISCOUNTS + ") Show top discounts");
        System.out.println(OPTION_EXIT + ") Exit");
    }

    private static int readInt(Scanner scanner, String prompt) {
        System.out.print(prompt);
        while (!scanner.hasNextInt()) {
            scanner.next(); 
            System.out.print("Please enter a valid number: ");
        }
        return scanner.nextInt();
    }

    private static List<String> getDesiredBasket(Scanner scanner)
    {
        System.out.println("\nSelect a basket:");
        PREDEFINED_BASKETS.forEach((key, basket) ->
            System.out.println(key + ") " + basket)
        );
        System.out.println("0) Enter custom product IDs");

        int basketChoice = readInt(scanner, "Your choice: ");
        List<String> basket;

        if (PREDEFINED_BASKETS.containsKey(basketChoice)) {
            return PREDEFINED_BASKETS.get(basketChoice);
        } else if (basketChoice == 0) {
            scanner.nextLine(); // clear newline
            System.out.print("Enter product IDs (comma separated, e.g., P001,P020,P028): ");
            String[] ids = scanner.nextLine().trim().split(",");
            basket = Arrays.asList(ids);
            return basket;
        } else {
            System.out.println("Invalid choice. Returning to main menu.");
            return null;
        }
    }

    private static String getDesiredDate(Scanner scanner)
    { 
        scanner.nextLine(); 
        System.out.print("\nSelect a date: \n");
        PREDEFINED_DATES.forEach((key,date) -> 
            System.out.println(key + ")" + date));

        int dateChoice = readInt(scanner, "Your choice: ");
        if(PREDEFINED_DATES.containsKey(dateChoice)){
            return PREDEFINED_DATES.get(dateChoice);
        }
        else{
            System.out.println("Invalid choice. Returning to main menu");
            return null;
        }
    }

    private static void handleBasketOptimization(Scanner scanner) {

        List<String> basket;
        basket = getDesiredBasket(scanner);
        if(basket == null || basket.isEmpty()) return;
        
        String date;
        date = getDesiredDate(scanner);
        if(date == null || date.isEmpty()) return ;

        BasketOptimizer.optimizeBasketSplit(basket, date);
    }

    private static String getDesiredStore(Scanner scanner)
    {
        String store;
        System.out.println("\nChoose store:");
        

        PREDEFINED_STORES.forEach((key,value) -> 
            System.out.println(key + ")" + value)
        );

        int storeOption = readInt(scanner, "Your choice: ");

        if(PREDEFINED_STORES.containsKey(storeOption)){
            return PREDEFINED_STORES.get(storeOption);
        }
        else{
            System.out.println("Invalid choice. Returning to main menu");
            return null;
        }
    }

 

    private static void handleBestDiscounts(Scanner scanner)
    {
        String date;
        date = getDesiredDate(scanner);
        if(date == null || date.isEmpty()) return ;

        String store;
        store =  getDesiredStore(scanner);
        if(store == null || store.isEmpty()) return ;

        int numberOfOffers = readInt(scanner, "Enter number of offers:");

        BestDiscounts.showBestDiscounts(store,date, numberOfOffers);
    }
}
