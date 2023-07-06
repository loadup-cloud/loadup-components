
package com.github.loadup.components.retrytask.schedule;

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

import com.github.loadup.components.retrytask.config.RetryDataSourceConfig;
import com.github.loadup.components.retrytask.config.RetryStrategyFactory;
import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * Splitor of retry task
 * 
 * 
 * 
 */
@Component
@Slf4j
public class RetryTaskSplitor {

    /** 日志 */

    /** 重试组件策略工厂 */
    @Autowired
    private RetryStrategyFactory retryStrategyFactory;

    /**
     * split 
     * 
     * @return dataItems
     */
    public List<DataLoadFilterItem> split() {

        List<DataLoadFilterItem> dataItems = new ArrayList<DataLoadFilterItem>();
        Map<String, RetryDataSourceConfig> retryDataSourceConfigs = retryStrategyFactory
            .getRetryDataSourceConfigs();
        if (CollectionUtils.isEmpty(retryDataSourceConfigs)) {
            return dataItems;
        }

        for (String bizType : retryDataSourceConfigs.keySet()) {
            dataItems.addAll(constructDataItems(retryDataSourceConfigs.get(bizType)));
        }

        return dataItems;
    }

    /**
     * 根据数据源配置构建dataItems
     * 
     * @param retryDataSourceConfig retryDataSourceConfig
     * @return List<RetryTaskDataItem>
     */
    private List<RetryTaskDataItem> constructDataItems(RetryDataSourceConfig retryDataSourceConfig) {

        List<RetryTaskDataItem> retryTaskDataItems = new ArrayList<RetryTaskDataItem>();
        //总表数
        int totalTableNum = retryDataSourceConfig.getDbNum()
                            * retryDataSourceConfig.getTableNumPerDb();

        if (totalTableNum == 1) {
            //单表逻辑
            RetryTaskDataItem retryTaskDataItem = buildDateItem(retryDataSourceConfig);
            retryTaskDataItems.add(retryTaskDataItem);
        } else {
            //分库分表逻辑
            for (int i = 0; i < totalTableNum; i++) {
                RetryTaskDataItem retryTaskDataItem = buildDateItem(retryDataSourceConfig);
                //分表后缀为2位以0填充左边，例如02
                String index = Integer.toString(i);

                retryTaskDataItem.setShardingIdx("1");
                retryTaskDataItems.add(retryTaskDataItem);
            }
        }

        return retryTaskDataItems;
    }

    /**
     * 通过数据源配置构建dataItem
     * 
     * @param retryDataSourceConfig retryDataSourceConfig
     * @return RetryTaskDataItem
     */
    private RetryTaskDataItem buildDateItem(RetryDataSourceConfig retryDataSourceConfig) {

        RetryTaskDataItem retryTaskDataItem = new RetryTaskDataItem();
        retryTaskDataItem.setBizType(retryDataSourceConfig.getBizType());
        retryTaskDataItem.setDbNum(retryDataSourceConfig.getDbNum());
        retryTaskDataItem.setTableNumPerDb(retryDataSourceConfig.getTableNumPerDb());
        retryTaskDataItem.setTablePrefix(retryDataSourceConfig.getTablePrefix());

        return retryTaskDataItem;
    }

}
