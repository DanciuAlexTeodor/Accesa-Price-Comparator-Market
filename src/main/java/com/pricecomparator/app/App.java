package com.pricecomparator.app;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

import com.pricecomparator.service.BasketOptimizer;
import com.pricecomparator.service.BestDiscounts;
import com.pricecomparator.service.NewestDiscounts;
import com.pricecomparator.repository.MarketDataRepository;
import com.pricecomparator.repository.ProductRepository;
import com.pricecomparator.repository.DiscountRepository;
import com.pricecomparator.loader.MarketDataLoader;
import com.pricecomparator.model.Product;
import com.pricecomparator.model.Discount;

public class App {

    private static final int OPTION_OPTIMIZE_BASKET = 1;
    private static final int OPTION_BEST_DISCOUNTS = 2;
    private static final int OPTION_NEWEST_DISCOUNTS = 3;
    private static final int OPTION_EXIT = 0;

    private static final Map<Integer, List<String>> PREDEFINED_BASKETS = new LinkedHashMap<>();
    private static final Map<Integer,String> PREDEFINED_DATES = new LinkedHashMap<>();
    private static final Map<Integer,String> PREDEFINED_STORES = new LinkedHashMap<>();

    private static final MarketDataRepository marketDataRepository;
    private static final BasketOptimizer basketOptimizer;
    private static final BestDiscounts bestDiscounts;
    private static final NewestDiscounts newestDiscounts;

    static {
        Map<String, List<Product>> products = MarketDataLoader.loadAllProductForDate("2099-12-31");
        Map<String, List<Discount>> discounts = MarketDataLoader.loadAllDiscountsForDate("2099-12-31");
        marketDataRepository = new MarketDataRepository(
            new ProductRepository(products),
            new DiscountRepository(discounts)
        );
        basketOptimizer = new BasketOptimizer(marketDataRepository);
        bestDiscounts = new BestDiscounts(marketDataRepository);
        newestDiscounts = new NewestDiscounts(marketDataRepository);

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
                    break;
                case OPTION_NEWEST_DISCOUNTS:
                    System.out.println("Showing newest discounts...");
                    handleNewestDiscounts(scanner);
                    break;
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
        System.out.println(OPTION_NEWEST_DISCOUNTS + ") Show newest discounts");
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

    private static String getDesiredDate(Scanner scanner) { 
        scanner.nextLine(); 
       
            System.out.print("Enter today's date (YYYY-MM-DD): ");
            String customDate = scanner.nextLine().trim();
            try {
                LocalDate date = LocalDate.parse(customDate);
                LocalDate minDate = LocalDate.parse("2025-05-01");
                LocalDate maxDate = LocalDate.parse("2025-06-01");
                
                if (date.isBefore(minDate)) {
                    System.out.println("Error: Date cannot be before 2025-05-01");
                    return null;
                }
                if (date.isAfter(maxDate)) {
                    System.out.println("Error: Date cannot be after 2025-06-01");
                    return null;
                }
                return customDate;
            } catch (DateTimeParseException e) {
                System.out.println("Error: Invalid date format. Please use YYYY-MM-DD");
                return null;
            }
        
    }

    private static void handleBasketOptimization(Scanner scanner) {
        List<String> basket;
        basket = getDesiredBasket(scanner);
        if(basket == null || basket.isEmpty()) return;
        
        String date;
        date = getDesiredDate(scanner);
        if(date == null || date.isEmpty()) return;

        basketOptimizer.optimizeBasketSplit(basket, date);
    }

    private static String getDesiredStore(Scanner scanner)
    {
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

    private static void handleNewestDiscounts(Scanner scanner) {
        String store = getDesiredStore(scanner);
        if (store == null || store.isEmpty()) return;

        String todayDate = getDesiredDate(scanner);
        if (todayDate == null || todayDate.isEmpty()) return;


        newestDiscounts.showNewestDiscounts(store, todayDate);

    }

    private static void handleBestDiscounts(Scanner scanner)
    {
        String date = getDesiredDate(scanner);
        if (date == null || date.isEmpty()) return;

        String store = getDesiredStore(scanner);
        if (store == null || store.isEmpty()) return;

        int numberOfOffers = readInt(scanner, "Enter number of offers:");

        bestDiscounts.showBestDiscounts(store, date, numberOfOffers);
    }
}


