/*
 * Copyright (C) 2021-2022 Huawei Technologies Co., Ltd. All rights reserved.
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

package io.sermant.router.config.strategy.match;

import io.sermant.router.common.utils.CollectionUtils;
import io.sermant.router.config.strategy.ValueMatchStrategy;

import java.util.List;

/**
 * Not less than the matching policy
 *
 * @author provenceee
 * @since 2021-10-23
 */
public class NoLessValueMatchStrategy implements ValueMatchStrategy {
    @Override
    public boolean isMatch(List<String> values, String arg) {
        try {
            return !CollectionUtils.isEmpty(values) && Long.parseLong(arg) >= Long.parseLong(values.get(0));
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
