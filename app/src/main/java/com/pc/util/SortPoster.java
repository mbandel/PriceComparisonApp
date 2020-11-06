package com.pc.util;

import com.pc.model.Poster;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
            Integer rating1 = t1.getRatingValue();
            Integer rating2 = t2.getRatingValue();
            if (sortAsc)
                return rating1.compareTo(rating2);
            else return -rating1.compareTo(rating2);
        });
        return list;
    }

    public static List<Poster> filterByStores(List<Poster> list, List<String> stores){
        return list.stream().filter(store -> {
            assert store != null;
            return stores.contains(store.getStore().getName());
        }).collect(Collectors.toList());
    }
}
