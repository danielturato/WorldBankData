package com.danielturato.WorldBankData;


import com.danielturato.WorldBankData.Model.Country;
import com.danielturato.WorldBankData.Model.Country.CountryBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.*;
import java.util.stream.Collectors;

public class Application {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        final ServiceRegistry registry = new StandardServiceRegistryBuilder().configure().build();
        return new MetadataSources(registry).buildMetadata().buildSessionFactory();
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        int option = 0;
        do {
            displayMenu();
            System.out.printf("%n%nEnter your choice: ");
            while (!input.hasNextInt()) {
                System.out.println("That's not a valid input! Try again");
                input.next();
            }
            option = input.nextInt();
            validateInput(option, input);
        } while (option != 6);
        System.exit(0);
    }

    private static void displayMenu() {
        System.out.printf("%n******WORLD BANK DATA******%n%n");
        System.out.println("1)     View data table");
        System.out.println("2)     View statistics");
        System.out.println("3)     Add a country");
        System.out.println("4)     Edit a country");
        System.out.println("5)     Delete a country");
        System.out.println("6)     Quit");
    }

    private static void validateInput(int option, Scanner scanner) {
        switch (option) {
            case 1:
                displayCountries();
                break;
            case 2:
                displayStats();
                break;
            case 3:
                addCountry(getCountryInput(scanner));
                break;
            case 4:
                editCountry(findCountryByCode(getStringInput(scanner, "Enter the code of the country you want to edit: ")), scanner);
                break;
            case 5:
                deleteCountry(findCountryByCode(getStringInput(scanner, "Enter the code for the country: ")));
                System.out.println("Deleting.....");
                break;
            case 6:
                break;
            default:
                System.out.printf("%nThat's not a valid menu choice! Try again%n");
                break;
        }
    }

    private static void displayCountries() {
        List<Country> countries = fetchAllCountries();
        System.out.println("Country                            Internet Users           Literacy");
        System.out.println("--------------------------------------------------------------------");
        countries.stream().forEach(System.out::println);
    }

    private static void displayStats() {
        List<Country> countries = fetchAllCountries();
        System.out.printf("%n******WORLD BANK DATA STATS******%n%n");
        System.out.println(calculateMaxAndMin(countries));
        System.out.println(calculateCorrelation(countries));
    }

    private static String calculateCorrelation(List<Country> countries) {
        countries = countries.stream()
                .filter(c -> (c.getAdultLiteracyRate() != null))
                .filter(c -> (c.getInternetUsers() != null))
                .collect(Collectors.toList());

        double sumOfIU = 0, sumOfAL = 0, sumOfIUAL = 0;
        double squareSumOfIU = 0, squareSumOfAL = 0;

        for (int i = 0; i < countries.size(); i++) {
            Country c = countries.get(i);

            sumOfIU = sumOfIU + c.getInternetUsers();
            sumOfAL = sumOfAL + c.getAdultLiteracyRate();

            sumOfIUAL = sumOfIUAL + c.getInternetUsers() * c.getAdultLiteracyRate();

            squareSumOfAL = squareSumOfAL + (c.getAdultLiteracyRate() * c.getAdultLiteracyRate());
            squareSumOfIU = squareSumOfIU + (c.getInternetUsers() * c.getInternetUsers());
        }

        int n = countries.size();

        float correlation = (float) (n * sumOfIUAL - sumOfAL * sumOfIU) /
                             (float) (Math.sqrt((n * squareSumOfAL - sumOfAL * sumOfAL) * (n * squareSumOfIU - sumOfIU * sumOfIU)));

        return String.format("Correlation Coefficient for the indicators : %f", correlation);
    }

    private static String calculateMaxAndMin(List<Country> countries) {
        Country minInternetUserC;
        Country maxInternetUserC;
        Country minAdultLiteracyC;
        Country maxAdultLiteracyC;

        minInternetUserC = countries.stream()
                            .filter(c -> (c.getInternetUsers() != null))
                            .min(Comparator.comparing(Country::getInternetUsers)).get();

        maxInternetUserC = countries.stream()
                            .filter(c -> (c.getInternetUsers() != null))
                            .max(Comparator.comparing(Country::getInternetUsers)).get();

        minAdultLiteracyC = countries.stream()
                            .filter(c -> (c.getAdultLiteracyRate() != null))
                            .min(Comparator.comparing(Country::getAdultLiteracyRate)).get();

        maxAdultLiteracyC = countries.stream()
                            .filter(c -> (c.getAdultLiteracyRate() != null))
                            .max(Comparator.comparing(Country::getAdultLiteracyRate)).get();

        return String.format("Minimum Adult Literacy Country is %s with : %f%n" +
                             "Minimum Internet Usage Country is %s with : %f%n" +
                             "Maximum Adult Literacy Country is %s with : %f%n" +
                             "Maximum Internet Usage Country is %s with : %f%n",
                             minAdultLiteracyC.getName(), minAdultLiteracyC.getAdultLiteracyRate(),
                             minInternetUserC.getName(), minInternetUserC.getInternetUsers(),
                             maxAdultLiteracyC.getName(), maxAdultLiteracyC.getAdultLiteracyRate(),
                             maxInternetUserC.getName(), maxInternetUserC.getInternetUsers());
    }

    private static List<Country> fetchAllCountries() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Country> criteria = builder.createQuery(Country.class);

        criteria.from(Country.class);

        List<Country> countries = session.createQuery(criteria).getResultList();

        session.close();

        return countries;
    }

    private static void addCountry(Country country) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(country);
        session.getTransaction().commit();
        session.close();
    }

    private static void updateCountry(Country country) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.update(country);
        session.getTransaction().commit();
        session.close();
    }

    private static Country findCountryByCode(String code) {
        Session session = sessionFactory.openSession();
        Country country = session.get(Country.class, code);
        session.close();
        return country;
    }

    private static void deleteCountry(Country country) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.delete(country);
        session.getTransaction().commit();
        session.close();
    }

    private static void editCountry(Country country, Scanner scanner) {
        String response = "";
        do {
            response = getStringInput(scanner, "What would you like to edit (name, internetusers, adultliteracy) : ");
        } while (!(response.equalsIgnoreCase("name") ||
                   response.equalsIgnoreCase("internetusers") ||
                   response.equalsIgnoreCase("adultliteracy")));
        response = response.toLowerCase();
        switch (response) {
            case "name":
                country.setName(getStringInput(scanner, "Enter a new name: "));
                break;
            case "internetusers":
                country.setInternetUsers(getDoubleInput(scanner, "Enter the new Internet User value: "));
                break;
            case "adultliteracy":
                country.setAdultLiteracyRate(getDoubleInput(scanner, "Enter a new Adult Literacy value: "));
                break;
            default:
                System.out.println("Error");
                break;
        }
        updateCountry(country);
    }

    private static Country getCountryInput(Scanner scanner) {
        String name = getStringInput(scanner, "Enter the name: ");
        String code = "";
        do {
            code = getStringInput(scanner, "Enter the code (3 chars): ");
        } while (!(code.length() == 3));
        CountryBuilder countryBuilder = new CountryBuilder(name, code);
        if (getStringInput(scanner, "Would you like to add the internet user stat (y) ? : ")
                                            .trim().equalsIgnoreCase("y")) {
            countryBuilder.withInternetUsers(getDoubleInput(scanner, "Enter the internet users stat: "));
        }

        if (getStringInput(scanner, "Would you like to add the adult literacy stat (y) ? : ")
                .trim().equalsIgnoreCase("y")) {
            countryBuilder.withInternetUsers(getDoubleInput(scanner, "Enter the adult literacy stat: "));
        }

        return countryBuilder.build();
    }

    private static String getStringInput(Scanner scanner, String message) {
        System.out.printf("%n%s", message);
        while (!scanner.hasNextLine()) {
            System.out.println("That's not a correct input. Try again!");
        }
        String input = scanner.next();
        return input;
    }

    private static Double getDoubleInput(Scanner scanner, String message) {
        System.out.printf("%n%s", message);
        while (!scanner.hasNextDouble()) {
            System.out.println("That's not a correct input. Try again!");
            scanner.next();
        }
        return scanner.nextDouble();
    }

}
