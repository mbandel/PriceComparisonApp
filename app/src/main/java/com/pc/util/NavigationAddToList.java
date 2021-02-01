package com.pc.util;

import com.pc.model.Poster;
import com.pc.model.ShoppingList;

public interface NavigationAddToList {
    void addToList();
    void selectList(ShoppingList shoppingList);
    void exit();
}
