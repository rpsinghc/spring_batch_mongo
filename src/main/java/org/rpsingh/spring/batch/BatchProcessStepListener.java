package org.rpsingh.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class BatchProcessStepListener implements ItemProcessListener {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BatchProcessStepListener(MongoTemplate jdbcTemplate) {
        this.mongoTemplate = jdbcTemplate;
    }


    @Override
    public void beforeProcess(Object item) {
        log.info("beforeProcess");
    }

    @Override
    public void afterProcess(Object item, Object result) {
        log.info("afterProcess");
    }

    @Override
    public void onProcessError(Object item, Exception exception) {
        log.info("onProcessError ");
    }
}
