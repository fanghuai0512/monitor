package com.xinwei.monitor.cache;

import com.google.common.collect.Maps;
import com.xinwei.monitor.po.Store;

import java.util.Map;

public class StoreCache {

    public static final Map<String, Store> storeMap = Maps.newConcurrentMap();

    public static void add(Store store){
        storeMap.put(store.getName(),store);
    }

    public static Store get(String storeName){
        return storeMap.get(storeName);
    }

}
