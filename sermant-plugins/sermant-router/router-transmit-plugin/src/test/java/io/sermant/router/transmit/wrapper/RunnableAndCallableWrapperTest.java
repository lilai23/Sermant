/*
 * Copyright (C) 2024-2024 Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sermant.router.transmit.wrapper;

import io.sermant.core.utils.ReflectUtils;
import io.sermant.router.common.request.RequestData;
import io.sermant.router.common.request.RequestTag;
import io.sermant.router.common.utils.ThreadLocalUtils;
import io.sermant.router.transmit.BaseTest;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test RunnableAndCallableWrapper
 *
 * @author provenceee
 * @since 2024-01-16
 */
public class RunnableAndCallableWrapperTest extends BaseTest {
    @Test
    public void testRunCanTransmit() {
        RunnableAndCallableWrapper<Object> wrapper = new RunnableAndCallableWrapper<>(() -> {
            Assert.assertNotNull(ThreadLocalUtils.getRequestData());
            Assert.assertNotNull(ThreadLocalUtils.getRequestTag());
        }, null, new RequestTag(null), new RequestData(null, null, null), false);

        Assert.assertNull(ThreadLocalUtils.getRequestData());
        Assert.assertNull(ThreadLocalUtils.getRequestTag());

        wrapper.run();

        Assert.assertNull(ThreadLocalUtils.getRequestData());
        Assert.assertNull(ThreadLocalUtils.getRequestTag());
    }

    @Test
    public void testRunCannotTransmit() {
        // Initial conditions
        ThreadLocalUtils.setRequestTag(new RequestTag(null));
        ThreadLocalUtils.setRequestData(new RequestData(null, null, null));

        Assert.assertNotNull(ThreadLocalUtils.getRequestData());
        Assert.assertNotNull(ThreadLocalUtils.getRequestTag());

        RunnableWrapper<Object> wrapper = new RunnableWrapper<>(() -> {
            Assert.assertNull(ThreadLocalUtils.getRequestData());
            Assert.assertNull(ThreadLocalUtils.getRequestTag());
        }, new RequestTag(null), new RequestData(null, null, null), true);

        Assert.assertNull(ReflectUtils.getFieldValue(wrapper, "requestData").orElse(null));
        Assert.assertNull(ReflectUtils.getFieldValue(wrapper, "requestTag").orElse(null));

        wrapper.run();

        Assert.assertNull(ThreadLocalUtils.getRequestData());
        Assert.assertNull(ThreadLocalUtils.getRequestTag());
    }

    @Test
    public void testCallCanTransmit() throws Exception {
        Object obj = new Object();
        RunnableAndCallableWrapper<Object> wrapper = new RunnableAndCallableWrapper<>(null, () -> {
            Assert.assertNotNull(ThreadLocalUtils.getRequestData());
            Assert.assertNotNull(ThreadLocalUtils.getRequestTag());
            return obj;
        }, new RequestTag(null), new RequestData(null, null, null), false);

        Assert.assertNull(ThreadLocalUtils.getRequestData());
        Assert.assertNull(ThreadLocalUtils.getRequestTag());

        Assert.assertEquals(obj, wrapper.call());

        Assert.assertNull(ThreadLocalUtils.getRequestData());
        Assert.assertNull(ThreadLocalUtils.getRequestTag());
    }

    @Test
    public void testCallCannotTransmit() throws Exception {
        // Initial conditions
        ThreadLocalUtils.setRequestTag(new RequestTag(null));
        ThreadLocalUtils.setRequestData(new RequestData(null, null, null));

        Assert.assertNotNull(ThreadLocalUtils.getRequestData());
        Assert.assertNotNull(ThreadLocalUtils.getRequestTag());

        Object obj = new Object();
        CallableWrapper<Object> wrapper = new CallableWrapper<>(() -> {
            Assert.assertNull(ThreadLocalUtils.getRequestData());
            Assert.assertNull(ThreadLocalUtils.getRequestTag());
            return obj;
        }, new RequestTag(null), new RequestData(null, null, null), true);

        Assert.assertNull(ReflectUtils.getFieldValue(wrapper, "requestData").orElse(null));
        Assert.assertNull(ReflectUtils.getFieldValue(wrapper, "requestTag").orElse(null));

        Assert.assertEquals(obj, wrapper.call());

        Assert.assertNull(ThreadLocalUtils.getRequestData());
        Assert.assertNull(ThreadLocalUtils.getRequestTag());
    }
}
