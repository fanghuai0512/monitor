package com.xinwei.monitor.util;

import com.xinwei.monitor.thread.task.MonitorAmazonProductPriceTask;
import com.xinwei.monitor.thread.task.ManageMonitorProductTask;
import com.xinwei.monitor.thread.task.ManageWalmartProductTask;
import com.xinwei.monitor.thread.task.MonitorProductToCacheTask;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class ThreadPoolUtils {

    public  static void startProductToCache(){
        ScheduledExecutorService  scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(new MonitorProductToCacheTask(), 1, 1, TimeUnit.SECONDS);
    }

    /**
     * 监控商品价格
     * newFixedThreadPool
     * 1.创建一个指定工作线程数量的线程池。每当提交一个任务就创建一个工作线程，如果工作线程数量达到线程池初始的最大数，则将提交的任务存入到池队列中。
     * 2.具有线程池提高程序效率和节省创建线程时所耗的开销的优点
     * 3.在线程池空闲时，即线程池中没有可运行任务时，它不会释放工作线程，还会占用一定的系统资源
     */
    public static void startMonitorProduct(){
        //创建一个限流器，每秒访问的线程个数
        //限流器中的容量为20，如果容量没有，则等待1秒继续消费（为了固定请求速率）
        ScheduledExecutorService  scheduledExecutorService = Executors.newScheduledThreadPool(2);
        scheduledExecutorService.scheduleAtFixedRate(new MonitorAmazonProductPriceTask(),5,1,TimeUnit.SECONDS);
    }

    /**
     * 处理监控商品信息
     * newCachedThreadPool 特点
     * 1.工作线程的创建数量几乎没有限制(其实也有限制的,数目为Integer.MAX_VALUE), 这样可灵活的往线程池中添加线程。
     * 2.如果长时间没有往线程池中提交任务，即如果工作线程空闲了指定的时间(默认为1分钟)，则该工作线程将自动终止。
     *    终止后，如果你又提交了新的任务，则线程池重新创建一个工作线程。
     * 3.在使用CachedThreadPool时，一定要注意控制任务的数量，否则，由于大量线程同时运行，很有会造成系统瘫痪。
     */
    public static void startManageProduct()  {
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(20);
        scheduledExecutorService.scheduleAtFixedRate(new ManageMonitorProductTask(),5,1,TimeUnit.SECONDS);
    }

    /**
     * RateLimiter.create(20.0,1,TimeUnit.SECONDS) 限流器中的容量为20，如果容量没有，则等待1秒继续消费（为了固定请求速率）
     * scheduleAtFixedRate
     *      创建并执行在给定的初始延迟之后，以给定的时间间隔执行周期性动作。
     *      即在 initialDelay 初始延迟后，initialDelay+period 执行第一次，initialDelay + 2 * period  执行第二次，依次类推。
     */
    public static void startWalmartProduct(){
        //创建一个限流器，每秒访问的线程个数
        ScheduledExecutorService  scheduledExecutorService = Executors.newScheduledThreadPool(20);
        scheduledExecutorService.scheduleAtFixedRate(
                new ManageWalmartProductTask(),
                5,
                1,
                TimeUnit.SECONDS);
    }


}
