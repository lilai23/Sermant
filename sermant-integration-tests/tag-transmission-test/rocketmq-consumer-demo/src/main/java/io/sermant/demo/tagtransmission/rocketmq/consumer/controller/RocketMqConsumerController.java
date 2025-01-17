/*
 * Copyright (C) 2023-2023 Huawei Technologies Co., Ltd. All rights reserved.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package io.sermant.demo.tagtransmission.rocketmq.consumer.controller;

import io.sermant.demo.tagtransmission.rocketmq.consumer.RocketMqConsumer;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 消息中间件消费者controller
 *
 * @author daizhenyu
 * @since 2023-09-28
 **/
@RestController
@RequestMapping(value = "rocketMqConsumer")
public class RocketMqConsumerController {
    /**
     * 查询rocketmq消费者消费消息后返回的流量标签透传
     *
     * @return string
     */
    @RequestMapping(value = "queryRocketMqTag", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
    public String queryRocketmqTag() {
        String trafficTag = RocketMqConsumer.ROCKETMQ_TAG_MAP.get("rocketmqTag");

        // 删除流量标签，以免干扰下一次测试查询
        RocketMqConsumer.ROCKETMQ_TAG_MAP.remove("rocketmqTag");
        return trafficTag;
    }
}