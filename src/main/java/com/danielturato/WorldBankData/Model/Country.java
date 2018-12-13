package com.danielturato.WorldBankData.Model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.text.DecimalFormat;

@Entity
public class Country {

    @Id
    private String code = //setCode();

    @Column(length = 32)
    private String name;

    @Column(length = 11)
    private Double internetUsers;

    @Column(length = 11)
    private Double adultLiteracyRate;

    public Country() {}

    public Country(CountryBuilder countryBuilder) {
        this.name = countryBuilder.name;
        this.internetUsers = countryBuilder.internetUsers;
        this.adultLiteracyRate = countryBuilder.adultLiteracyRate;
    }

    @Override
    public String toString() {
        String name = getName() != null? getName() : "--";
        String internetUsers = getInternetUsers() != null? new DecimalFormat("#0.##").format(getInternetUsers()) : "--";
        String adultLiteracyRate = getAdultLiteracyRate() != null? new DecimalFormat("#0.##").format(getAdultLiteracyRate()) : "--";

        return String.format("%-35s%-25s%s", name, internetUsers, adultLiteracyRate);
    }

    public String setCode() {
        for(int i = 0; i < name.length(); i++) {
            code += name.charAt(i);
            if (i == 2) {
                break;
            }
        }
        return code.toUpperCase();
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getInternetUsers() {
        return internetUsers;
    }

    public void setInternetUsers(Double internetUsers) {
        this.internetUsers = internetUsers;
    }

    public Double getAdultLiteracyRate() {
        return adultLiteracyRate;
    }

    public void setAdultLiteracyRate(Double adultLiteracyRate) {
        this.adultLiteracyRate = adultLiteracyRate;
    }

    public static class CountryBuilder {
        private String name;
        private String code;
        private Double internetUsers;
        private Double adultLiteracyRate;

        public CountryBuilder(String name) {
            this.name = name;
            String code = "";

        }

        public CountryBuilder withInternetUsers(Double internetUsers) {
            this.internetUsers = internetUsers;
            return this;
        }

        public CountryBuilder withAdultLiteracyRate(Double literacyRate) {
            this.adultLiteracyRate = literacyRate;
            return this;
        }

        public Country build() {
            return new Country(this);
        }


    }
}
