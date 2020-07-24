#Project Description
+ java
+ resources
# Monitor Process
## 0.开启一个线程池，定时将商品放入到内存中
```
ThreadPoolUtils
```
## 1.开启一个线程池，根据asin将亚马逊的监控数据放入到内存中
```
ThreadPoolUtils.startMonitorProduct 线程池
```
## 2.开启一个新的线程，处理亚马逊的监控数据，进行筛选处理
```
ThreadPoolUtils.startManageProduct
```
## 3.开启一个新的线程池，处理筛选好的数据
```
ThreadPoolUtils.startWalmartProduct
```