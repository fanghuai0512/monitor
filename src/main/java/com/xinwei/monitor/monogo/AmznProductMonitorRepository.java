package com.xinwei.monitor.monogo;

import com.xinwei.monitor.po.AmznProductMonitor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AmznProductMonitorRepository extends MongoRepository<AmznProductMonitor,String > {
}
