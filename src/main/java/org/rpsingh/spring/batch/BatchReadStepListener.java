package org.rpsingh.spring.batch;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.StepListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class BatchReadStepListener implements ItemReadListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(StepListener.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public BatchReadStepListener(MongoTemplate jdbcTemplate) {
        this.mongoTemplate = jdbcTemplate;
    }

    @Override
    public void beforeRead() {
            log.info("before read");
    }

    @Override
    public void afterRead(Object item) {
        log.info("after read");
    }

    @Override
    public void onReadError(Exception ex) {
        log.info("on read error");
    }
}
