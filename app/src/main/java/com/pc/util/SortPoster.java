package com.pc.util;

import com.google.common.collect.FluentIterable;
import com.pc.model.Poster;

import java.util.Collections;
import java.util.List;

public class SortPoster {

    public static List<Poster> sortByPrice(List<Poster> list, boolean sortAsc){
        Collections.sort(list, (t1, t2) -> {
            if (sortAsc)
                return t1.getPrice().compareTo(t2.getPrice());
            else return -t1.getPrice().compareTo(t2.getPrice());
        });
        return list;
    }

    public static List<Poster> sortByRating(List<Poster> list, boolean sortAsc){
        Collections.sort(list, (t1, t2) -> {
            Integer rating1 = new Integer(t1.getRatingValue());
            Integer rating2 = new Integer(t2.getRatingValue());
            if (sortAsc)
                return rating1.compareTo(rating2);
            else return -rating1.compareTo(rating2);
        });
        return list;
    }

    public static List<Poster> filterByStores(List<Poster> list, List<String> stores){
        return FluentIterable.from(list).filter(store -> {
            assert store != null;
            return stores.contains(store.getStore().getName());
        }).toList();
    }
}
