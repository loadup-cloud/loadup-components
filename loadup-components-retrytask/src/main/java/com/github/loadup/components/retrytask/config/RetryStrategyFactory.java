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

import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import com.github.loadup.components.retrytask.enums.DbType;
import com.github.loadup.components.retrytask.enums.SqlType;
import com.github.loadup.components.retrytask.utils.ApplicationContextAwareUtil;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

/**
 * The strategy factory of retry commponet
 */
@Component
@Slf4j
@Getter
@Setter
public class RetryStrategyFactory implements ApplicationListener<ApplicationStartedEvent> {
    @Resource
    private RetryTaskConfigProperties retryTaskConfigProperties;

    private Map<String, RetryDataSourceConfig> retryDataSourceConfigs = new HashMap<>();
    /**
     * retry stategy config.key: bizType
     */
    private Map<String, RetryStrategyConfig>   retryStrategyConfigs   = new HashMap<>();
    /**
     * default shedule thread pool
     */
    private ThreadPoolTaskExecutor             scheduleThreadPool;

    /**
     * obtain the retry datasource config by business type
     *
     * @param bizType business type
     * @return RetryDataSourceConfig
     */
    public RetryDataSourceConfig buildRetryDataSourceConfig(String bizType) {

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
    public RetryStrategyConfig buildRetryStrategyConfig(String bizType) {

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

    public void init() {
        retryTaskConfigProperties.getStrategyList().forEach(s -> {
            if (retryStrategyConfigs.containsKey(s.getBizType())) {
                throw new RuntimeException("retry strategy exist!bizType=" + s.getBizType());
            }
            retryStrategyConfigs.put(s.getBizType(), s);
        });
        retryTaskConfigProperties.getDataSourceList().forEach(s -> {
            if (s.getBizType().contains(StringPool.COMMA)) {
                String[] types = s.getBizType().split(StringPool.COMMA);
                s.setDataSource((DataSource) ApplicationContextAwareUtil.getBean(s.getDatasourceName()));
                initSqlMap(s);
                for (String type : types) {
                    retryDataSourceConfigs.put(type, s);
                }
            } else {
                s.setDataSource((DataSource) ApplicationContextAwareUtil.getBean(s.getDatasourceName()));
                initSqlMap(s);
                retryDataSourceConfigs.put(s.getBizType(), s);
            }
        });
    }

    /**
     * initate every sql sentence
     */
    private void initSqlMap(RetryDataSourceConfig retryDataSourceConfig) {

        //initial insert sentence
        String mysqlOriginalInsertSql
                = "insert into retry_task (task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,"
                + "up_to_max_execute_times_flag,processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended) "
                + "values(:taskId,:bizType,:bizId,:executedTimes,:nextExecuteTime,:maxExecuteTimes,:upToMaxExecuteTimesFlag,"
                + ":processingFlag,:bizContext,:gmtCreate,:gmtModified,:priority,:suspended)";

        //initial update sentence
        String mysqlOriginalUpdateSql
                = "update retry_task set task_id=:taskId,biz_type=:bizType,biz_id=:bizId,executed_times=:executedTimes,"
                + "next_execute_time=:nextExecuteTime,max_execute_times=:maxExecuteTimes,"
                + "up_to_max_execute_times_flag=:upToMaxExecuteTimesFlag,processing_flag=:processingFlag,biz_context=:bizContext,"
                + "gmt_create=:gmtCreate,gmt_modified=:gmtModified,priority=:priority,suspended=:suspended where "
                + "task_id=:taskId ";

        //initial load sentence
        String mysqlOriginalLoadSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and processing_flag='F' and "
                        + "suspended!='T' and next_execute_time < now() order by priority desc"
                        + " limit :rowNum";

        //initial load by priority sentence
        String mysqlOriginalLoadByPrioritySql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and processing_flag='F' and "
                        + "suspended!='T' and next_execute_time < now() and priority=:priority "
                        + " limit :rowNum";

        // Unusual Orgi Load Sql
        String mysqlUnusualOrgiLoadSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and"
                        + " processing_flag='T' and suspended != 'T' and gmt_modified < DATE_SUB(NOW(), INTERVAL :extremeRetryTime "
                        + "MINUTE)   limit :rowNum";

        //initial lock sentence
        String mysqlOriginalLockSql
                = "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended from retry_task where biz_id=:bizId"
                + " and biz_type=:bizType for update";

        //initial load by id sentence
        String mysqlOriginalLoadByIdSql
                = "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended from retry_task where biz_id=:bizId"
                + " and biz_type=:bizType";

        //initial delete sentence
        String originaldeleteSql = "delete from retry_task where biz_id=:bizId and biz_type=:bizType ";

        Map<String, String> sqlMap = new HashMap<String, String>();

        //mysql
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_INSERT.getCode(), replaceTableName(mysqlOriginalInsertSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_UPDATE.getCode(), replaceTableName(mysqlOriginalUpdateSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOAD.getCode(), replaceTableName(mysqlOriginalLoadSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                        + SqlType.SQL_TASK_LOAD_UNUSUAL.getCode(),
                replaceTableName(mysqlUnusualOrgiLoadSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOCK_BY_ID.getCode(), replaceTableName(mysqlOriginalLockSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                        + SqlType.SQL_TASK_LOAD_BY_ID.getCode(),
                replaceTableName(mysqlOriginalLoadByIdSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_DELETE.getCode(), replaceTableName(originaldeleteSql, retryDataSourceConfig.getTablePrefix()));

        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                        + SqlType.SQL_TASK_LOAD_BY_PRIORITY.getCode(),
                replaceTableName(mysqlOriginalLoadByPrioritySql, retryDataSourceConfig.getTablePrefix()));
        retryDataSourceConfig.setSqlMap(sqlMap);
    }

    /**
     * replace the table name in sql sentence
     *
     * @param sql         sql
     * @param tablePrefix
     * @return the sql sentence after replacing the table name
     */
    private String replaceTableName(String sql, String tablePrefix) {
        if (StringUtils.isBlank(tablePrefix)) {
            return sql;
        }
        String tableName = tablePrefix + RetryTaskConstants.SUFFIX_TABLE_NAME;
        return sql.replaceAll(RetryTaskConstants.SUFFIX_TABLE_NAME, tableName);
    }

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        init();
    }
}
