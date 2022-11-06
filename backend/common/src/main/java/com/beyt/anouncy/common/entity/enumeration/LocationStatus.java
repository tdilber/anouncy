package com.beyt.anouncy.common.entity.enumeration;

/**
 * Created by tdilber at 12-Feb-19
 */
public enum LocationStatus {
    NONE(-1, "Tanımsız"),
    ACTIVE(0, "Aktif"),
    INACTIVE(1, "Pasif");

    private int value = -1;
    private String meaning;

    LocationStatus(int value, String meaning) {
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
