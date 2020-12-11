package com.pc.util;

import com.pc.model.ShoppingList;
import com.pc.model.Store;

public interface NavigationAddShoppingList {
    void goToMap();
    void goToEdition(Store store);
    void addShoppingList(ShoppingList shoppingList);
}
