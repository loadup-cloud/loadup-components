package com.github.loadup.components.retrytask.utils;

/*-
 * #%L
 * loadup-components-retrytask
 * %%
 * Copyright (C) 2022 - 2023 loadup_cloud
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */


import com.github.loadup.capability.common.util.core.UniqueId;
import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import java.util.UUID;

/**
 * Id生成工具类
 */
public class IdUtil {

    private static final String VIRTUAL_ID_PREFIX = "00000000000000000000000000000000";

    /**
     * 生成Id，分库分表位取倒数11，12位
     *
     * @param shardingIdx 分库分表位
     * @return id
     */
    public static String generateId(String shardingIdx) {

        String uuid = UniqueId.getInstance().getUniqIDHash();
        return uuid + shardingIdx + RetryTaskConstants.ID_DEFAULT_SUFFIX;
    }

    /**
     * 生成虚拟Id，分库分表位取倒数11，12位
     *
     * @param shardingIdx 分库分表位
     * @return id
     */
    public static String generateVirtualId(String shardingIdx) {
        return VIRTUAL_ID_PREFIX + shardingIdx + RetryTaskConstants.ID_DEFAULT_SUFFIX;
    }

}
