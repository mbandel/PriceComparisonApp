package com.pc.util;

import com.pc.R;

public class IconManager {

    public static int setIcon(String categoryName){
        if (categoryName.equals("słodycze"))
            return R.drawable.ic_candy;
        else if (categoryName.equals("pieczywo"))
            return R.drawable.ic_bread;
        else if (categoryName.equals("napoje"))
            return R.drawable.ic_drinks;
        else if (categoryName.equals("warzywa"))
            return R.drawable.ic_vegetable;
        else if (categoryName.equals("owoce"))
            return R.drawable.ic_fruit;
        else if (categoryName.equals("przyprawy"))
            return R.drawable.spices;
        else if (categoryName.equals("alkohole"))
            return R.drawable.ic_alcohol;
        else if (categoryName.equals("nabiał"))
            return R.drawable.ic_milk;
        else if (categoryName.equals("mięso"))
            return R.drawable.ic_meat;
        else if (categoryName.equals("ryby"))
            return R.drawable.ic_fish;
        else return R.drawable.green_bg;
    }
}
