package com.xinwei.monitor.util;


import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtils {

   public static String randomDate(){
       String format = new SimpleDateFormat("yyyyMMddHHmmssSS").format(new Date());
       return  format+ Math.random()*100;
   }


}
