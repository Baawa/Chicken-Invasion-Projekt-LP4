package com.chicken.invasion.oldstuff;

/**
 * Created by pedramshirmohammad on 16-05-25.
 */
public interface iItem {
    boolean isPurchased();
    String getName();
    void setPurchased(boolean purchased);
    int getPrice();
}
