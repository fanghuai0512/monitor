package com.xinwei.monitor.util;

import java.math.BigDecimal;

public class BigDecimalUtils {

    public static BigDecimal getRandom(BigDecimal minBigDecimal,BigDecimal maxBigDecimal){
       return new BigDecimal(Math.random() * (maxBigDecimal.doubleValue() - minBigDecimal.doubleValue()) + minBigDecimal.doubleValue());
    }
}
