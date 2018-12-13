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
import java.util.List;
import java.util.Scanner;

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
            validateInput(option);
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

    private static void validateInput(int option) {
        switch (option) {
            case 1:
                displayCountries();
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
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

    private static List<Country> fetchAllCountries() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();

        CriteriaQuery<Country> criteria = builder.createQuery(Country.class);

        criteria.from(Country.class);

        List<Country> countries = session.createQuery(criteria).getResultList();

        session.close();

        return countries;
    }

    private static Country getCountryInput(Scanner scanner) {
        String name = getStringInput(scanner, "Enter the name ");
        CountryBuilder countryBuilder = new CountryBuilder(name);
        if (getStringInput(scanner, "Would you like to add the internet user stat (y) ? : ")
                                            .equalsIgnoreCase("y")) {
            countryBuilder.withInternetUsers(getDoubleInput(scanner, "Enter the internet users stat: "));
        }

        if (getStringInput(scanner, "Would you like to add the adult literacy stat (y) ? : ")
                .equalsIgnoreCase("y")) {
            countryBuilder.withInternetUsers(getDoubleInput(scanner, "Enter the adult literacy stat: "));
        }

        return countryBuilder.build();
    }

    private static String getStringInput(Scanner scanner, String message) {
        System.out.printf("%n%s", message);
        while (!scanner.hasNextLine()) {
            System.out.println("That's not a correct input. Try again!");
            scanner.next();
        }
        return scanner.nextLine();
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
