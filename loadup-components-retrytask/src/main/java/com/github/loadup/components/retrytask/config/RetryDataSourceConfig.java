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
import com.github.loadup.components.retrytask.enums.DbType;
import com.github.loadup.components.retrytask.enums.SqlType;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import lombok.Getter;
import lombok.Setter;

/**
 * the config of retry task datasource
 *
 * 
 * 
 */
@Getter
@Setter
public class RetryDataSourceConfig {

    /**
     * business type, users can define themselves
     *
     * DEFAULT(in default)
     */
    private String bizType = RetryTaskConstants.DEFAULT_BIZ_TYPE;

    /**
     * the mode of the relation of business tables and task tables
     *
     * SAME(in default, business tables and task tables are in the same database)
     * DIFFERENT(business tables and task tables are in the different database)
     */
    private String dbMode = "SAME";

    /**
     * datasource
     */
    private DataSource dataSource;

    /**
     * the prefix of table name
     */
    private String tablePrefix;

    /**
     * the sum of sub databases
     * only one database(in default)
     */
    private int dbNum = 1;

    /**
     * the sum of sub tables in every database
     * one table in every database(in default)
     */
    private int tableNumPerDb = 1;

    /**
     * sql sentence in every database
     *
     * key:  DbType-SqlType
     * value: sql sentence
     */
    private Map<String, String> sqlMap;
    private String              dbType;

    /**
     * initation function, initate every property
     */
    public void init() {
        initSqlMap();
    }

    /**
     * initate every sql sentence
     */
    private void initSqlMap() {

        //initial insert sentence
        String mysqlOriginalInsertSql
                = "insert into retry_task (task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,"
                + "up_to_max_execute_times_flag,processing_flag,biz_context,sofa_tracer,gmt_create,gmt_modified,priority,suspended) "
                + "values(:taskId,:bizType,:bizId,:executedTimes,:nextExecuteTime,:maxExecuteTimes,:upToMaxExecuteTimesFlag,"
                + ":processingFlag,:bizContext,:sofaTracer,:gmtCreate,:gmtModified,:priority,:suspended)";

        //initial update sentence
        String mysqlOriginalUpdateSql
                = "update retry_task set task_id=:taskId,biz_type=:bizType,biz_id=:bizId,executed_times=:executedTimes,"
                + "next_execute_time=:nextExecuteTime,max_execute_times=:maxExecuteTimes,"
                + "up_to_max_execute_times_flag=:upToMaxExecuteTimesFlag,processing_flag=:processingFlag,biz_context=:bizContext,"
                + "sofa_tracer=:sofaTracer,gmt_create=:gmtCreate,gmt_modified=:gmtModified,priority=:priority,suspended=:suspended where "
                + "task_id=:taskId ";

        //initial load sentence
        String mysqlOriginalLoadSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,sofa_tracer,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and processing_flag='F' and "
                        + "suspended!='T' and next_execute_time < now() order by priority desc"
                        + " limit :rowNum";

        //initial load by priority sentence
        String mysqlOriginalLoadByPrioritySql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,sofa_tracer,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and processing_flag='F' and "
                        + "suspended!='T' and next_execute_time < now() and priority=:priority "
                        + " limit :rowNum";

        // Unusual Orgi Load Sql
        String mysqlUnusualOrgiLoadSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,sofa_tracer,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and"
                        + " processing_flag='T' and suspended != 'T' and gmt_modified < DATE_SUB(NOW(), INTERVAL :extremeRetryTime "
                        + "MINUTE)   limit :rowNum";

        //initial lock sentence
        String mysqlOriginalLockSql
                = "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                + "processing_flag,biz_context,sofa_tracer,gmt_create,gmt_modified,priority,suspended from retry_task where biz_id=:bizId"
                + " and biz_type=:bizType for update";

        //initial load by id sentence
        String mysqlOriginalLoadByIdSql
                = "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                + "processing_flag,biz_context,sofa_tracer,gmt_create,gmt_modified,priority,suspended from retry_task where biz_id=:bizId"
                + " and biz_type=:bizType";

        //initial delete sentence
        String originaldeleteSql = "delete from retry_task where biz_id=:bizId and biz_type=:bizType ";

        //final sql sentence
        String mysqlFinalInsertSql = replaceTableName(mysqlOriginalInsertSql);

        String mysqlFinalUpdateSql = replaceTableName(mysqlOriginalUpdateSql);

        String mysqlFinalLoadSql = replaceTableName(mysqlOriginalLoadSql);

        String mysqlFinalLoadByPrioritySql = replaceTableName(mysqlOriginalLoadByPrioritySql);

        String mysqlFinalUnusualLoadSql = replaceTableName(mysqlUnusualOrgiLoadSql);

        String mysqlFinalLockSql = replaceTableName(mysqlOriginalLockSql);

        String mysqlFinalLoadByIdSql = replaceTableName(mysqlOriginalLoadByIdSql);

        String finalDeleteSql = replaceTableName(originaldeleteSql);

        sqlMap = new HashMap<String, String>();

        //mysql
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_INSERT.getCode(), mysqlFinalInsertSql);
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_UPDATE.getCode(), mysqlFinalUpdateSql);
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOAD.getCode(), mysqlFinalLoadSql);
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOAD_UNUSUAL.getCode(), mysqlFinalUnusualLoadSql);
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOCK_BY_ID.getCode(), mysqlFinalLockSql);
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOAD_BY_ID.getCode(), mysqlFinalLoadByIdSql);
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_DELETE.getCode(), finalDeleteSql);

        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR
                + SqlType.SQL_TASK_LOAD_BY_PRIORITY.getCode(), mysqlFinalLoadByPrioritySql);

    }

    /**
     * replace the table name in sql sentence
     *
     * @param sql sql
     * @return the sql sentence after replacing the table name
     */
    private String replaceTableName(String sql) {
        String tableName = tablePrefix + RetryTaskConstants.SUFFIX_TABLE_NAME;
        return sql.replaceAll(RetryTaskConstants.SUFFIX_TABLE_NAME, tableName);
    }

}
