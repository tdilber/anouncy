package com.beyt.anouncy.location.enumeration;

/**
 * Created by tdilber at 02-Dec-18
 */
public enum LocationType {
    NONE(-1, "Tanımsız"),
    COUNTRY(0, "Ülke"),
    REGION(1, "Bölgesi"),
    CITY(2, "Şehir"),
    COUNTY(3, "İlçe"),
    TOWN(4, "Belde"),
    VILLAGE(5, "Köy"),
    STREET(6, "Mahalle");

    private int value = -1;
    private String meaning;

    LocationType(int value, String meaning) {
        this.value = value;
        this.meaning = meaning;
    }

    public int getValue() {
        return value;
    }

    public String getMeaning() {
        return meaning;
    }
}
