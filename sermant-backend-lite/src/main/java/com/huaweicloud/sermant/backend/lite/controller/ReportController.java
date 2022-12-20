package com.huaweicloud.sermant.backend.lite.controller;

import com.huaweicloud.sermant.backend.lite.entity.ReportMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * 类描述
 *
 * @author lilai
 * @since 2022-12-20
 */
@RestController
@RequestMapping
public class ReportController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ReportController.class);
    private static final BlockingQueue<ReportMessage> queue = new ArrayBlockingQueue<>(1000);

    @PostMapping("/report")
    public void saveMessageInfo(@RequestBody ReportMessage reportMessage) throws InterruptedException {
        boolean offerResult = queue.offer(reportMessage);
        if(offerResult) {
            LOGGER.info("put reportMessage to queue success!");
        } else {
            LOGGER.info("put reportMessage to queue fail!");
        }
    }

    @GetMapping("/getMessage")
    public ReportMessage getMessageInfo() {
        LOGGER.info("get reportMessage from queue");
        return queue.poll();
    }
}
