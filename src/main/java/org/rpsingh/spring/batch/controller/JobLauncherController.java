package org.rpsingh.spring.batch.controller;

import org.rpsingh.spring.batch.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Ram Prakash Singh
 */

@RestController
public class JobLauncherController {


    @Autowired
    BatchService batchService;

    @PostMapping("/v1/job/launcher")
    public void handle() throws Exception {
           // batchService.startJobLaunch();
            batchService.launchJob();
    }



}