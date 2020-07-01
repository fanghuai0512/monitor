package com.xinwei.monitor.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;

public class Constant {

    @AllArgsConstructor
    @Getter
    public enum Redis{
        PriceRule("price_rule",-1),
        WalmartStoreAccessToken("walmart_accessToken:",900);
        private String key;
        private long expire;
    }

    @AllArgsConstructor
    @Getter
    public enum StoreType{
        Walmart(1,"沃尔玛");

        private Integer type;
        private String name;
    }

    @AllArgsConstructor
    @Getter
    public enum RequestType{
        Get(1, HttpGet.METHOD_NAME),
        Post(2, HttpPost.METHOD_NAME),
        Put(3, HttpPut.METHOD_NAME);

        private Integer type;
        private String method;
        public static RequestType get(Integer requestType){
            RequestType[] values = RequestType.values();
            for (RequestType requestTypes: values) {
                if(requestTypes.getType() == requestType){
                    return requestTypes;
                }
            }
            return null;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum WalmartApi{

        GetToken("getToken","获取token"),
        UpdateStock("updateStock","更新库存"),
        UpdatePrice("updatePrice","更新价格");


        private String name;
        private String desc;
    }
}
