package com.github.loadup.components.retrytask.config;

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

import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * The strategy factory of retry commponet
 *
 * 
 * 
 */
@Component
@Slf4j
public class RetryStrategyFactory {

    /**
     * Extension Config Name
     */
    private static final String                             EXT_RETRY_DATASOURCE_CONFIG = "retryDataSourceConfig";
    /**
     * Extension Config Name
     */
    private static final String                             EXT_RETRY_STRATEGY_CONFIG   = "retryStrategyConfig";
    /**
     * retry commponet datasource config. key: bizType
     */
    private              Map<String, RetryDataSourceConfig> retryDataSourceConfigs      = new HashMap<String, RetryDataSourceConfig>();
    /**
     * retry stategy config.key: bizType
     */
    private              Map<String, RetryStrategyConfig>   retryStrategyConfigs        = new HashMap<String, RetryStrategyConfig>();
    /**
     * default shedule thread pool
     */
    private              ThreadPoolTaskExecutor             scheduleThreadPool;

    /**
     * obtain the retry datasource config by business type
     *
     * @param bizType business type
     * @return RetryDataSourceConfig
     */
    public RetryDataSourceConfig obtainRetryDataSourceConfig(String bizType) {

        // match by bizType first
        if (retryDataSourceConfigs.get(bizType) != null) {
            return retryDataSourceConfigs.get(bizType);
        }

        // match by 'DEFAULT' second
        if (retryDataSourceConfigs.get(RetryTaskConstants.DEFAULT_BIZ_TYPE) != null) {
            return retryDataSourceConfigs.get(RetryTaskConstants.DEFAULT_BIZ_TYPE);
        }

        return null;
    }

    /**
     * obtain the retry strategy config by business type
     *
     * @param bizType business type
     * @return RetryStrategyConfig
     */
    public RetryStrategyConfig obtainRetryStrategyConfig(String bizType) {

        // match by bizType first
        if (retryStrategyConfigs.get(bizType) != null) {
            return retryStrategyConfigs.get(bizType);
        }

        // match by 'DEFAULT' second
        if (retryStrategyConfigs.get(RetryTaskConstants.DEFAULT_BIZ_TYPE) != null) {
            return retryStrategyConfigs.get(RetryTaskConstants.DEFAULT_BIZ_TYPE);
        }

        return null;
    }

    /**
     * build New RetryDataSourceConfig
     *
     * @param config  config
     * @param bizType bizType
     * @return retryDataSourceConfig
     */
    private RetryDataSourceConfig buildNewRetryDataSourceConfig(RetryDataSourceConfig config,
            String bizType) {
        RetryDataSourceConfig retryDataSourceConfig = new RetryDataSourceConfig();
        retryDataSourceConfig.setBizType(bizType);
        retryDataSourceConfig.setDataSource(config.getDataSource());
        retryDataSourceConfig.setDbMode(config.getDbMode());
        retryDataSourceConfig.setDbNum(config.getDbNum());
        retryDataSourceConfig.setTableNumPerDb(config.getTableNumPerDb());
        retryDataSourceConfig.setTablePrefix(config.getTablePrefix());
        retryDataSourceConfig.setSqlMap(config.getSqlMap());

        return retryDataSourceConfig;
    }

    // ~~~ getters and setters

    /**
     * Getter method for property <tt>retryDataSourceConfigs</tt>.
     *
     * @return property value of retryDataSourceConfigs
     */
    public Map<String, RetryDataSourceConfig> getRetryDataSourceConfigs() {
        return retryDataSourceConfigs;
    }

    /**
     * Setter method for property <tt>retryDataSourceConfigs</tt>.
     *
     * @param retryDataSourceConfigs value to be assigned to property retryDataSourceConfigs
     */
    public void setRetryDataSourceConfigs(Map<String, RetryDataSourceConfig> retryDataSourceConfigs) {
        this.retryDataSourceConfigs = retryDataSourceConfigs;
    }

    /**
     * Getter method for property <tt>retryStrategyConfigs</tt>.
     *
     * @return property value of retryStrategyConfigs
     */
    public Map<String, RetryStrategyConfig> getRetryStrategyConfigs() {
        return retryStrategyConfigs;
    }

    /**
     * Setter method for property <tt>retryStrategyConfigs</tt>.
     *
     * @param retryStrategyConfigs value to be assigned to property retryStrategyConfigs
     */
    public void setRetryStrategyConfigs(Map<String, RetryStrategyConfig> retryStrategyConfigs) {
        this.retryStrategyConfigs = retryStrategyConfigs;
    }

    /**
     * Getter method for property <tt>scheduleThreadPool</tt>.
     *
     * @return property value of scheduleThreadPool
     */
    public ThreadPoolTaskExecutor getScheduleThreadPool() {
        return scheduleThreadPool;
    }

    /**
     * Setter method for property <tt>scheduleThreadPool</tt>.
     *
     * @param scheduleThreadPool value to be assigned to property scheduleThreadPool
     */
    public void setScheduleThreadPool(ThreadPoolTaskExecutor scheduleThreadPool) {
        this.scheduleThreadPool = scheduleThreadPool;
    }

}
