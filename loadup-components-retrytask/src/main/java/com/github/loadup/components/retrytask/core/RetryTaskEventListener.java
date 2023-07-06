package com.github.loadup.components.retrytask.core;

import com.github.loadup.capability.common.util.core.StringPool;
import com.github.loadup.components.retrytask.annotation.RetryTaskListener;
import com.github.loadup.components.retrytask.config.RetryDataSourceConfig;
import com.github.loadup.components.retrytask.config.RetryStrategyConfig;
import com.github.loadup.components.retrytask.config.RetryStrategyFactory;
import com.github.loadup.components.retrytask.config.RetryTaskConfigProperties;
import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import com.github.loadup.components.retrytask.enums.DbType;
import com.github.loadup.components.retrytask.enums.ScheduleExecuteType;
import com.github.loadup.components.retrytask.enums.SqlType;
import com.github.loadup.components.retrytask.manager.AsyncTaskStrategyExecutor;
import com.github.loadup.components.retrytask.manager.DefaultTaskStrategyExecutor;
import com.github.loadup.components.retrytask.manager.SyncTaskStrategyExecutor;
import com.github.loadup.components.retrytask.manager.TaskStrategyExecutor;
import com.github.loadup.components.retrytask.manager.TaskStrategyExecutorFactory;
import com.github.loadup.components.retrytask.spi.RetryTaskExecuteSPI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.sql.DataSource;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class RetryTaskEventListener implements ApplicationListener<ApplicationStartedEvent> {
    @Resource
    private RetryTaskConfigProperties   retryTaskConfigProperties;
    @Resource
    private RetryStrategyFactory        retryStrategyFactory;
    @Resource
    private TaskStrategyExecutorFactory taskStrategyExecutorFactory;
    @Resource
    private DefaultTaskStrategyExecutor defaultTaskStrategyExecutor;
    @Resource
    private SyncTaskStrategyExecutor    syncTaskStrategyExecutor;
    @Resource
    private AsyncTaskStrategyExecutor   asyncTaskStrategyExecutor;
    @Resource
    private DataSource                  dataSource;

    @Override
    public void onApplicationEvent(ApplicationStartedEvent event) {
        init();
        if (event.getApplicationContext().getParent() == null) {
            Map<String, Object> beans = event.getApplicationContext().getBeansWithAnnotation(
                    com.github.loadup.components.retrytask.annotation.RetryTaskListener.class);
            for (Object bean : beans.values()) {
                System.err.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>" + bean == null ? "null" : bean.getClass().getName());
                RetryTaskListener listener = AnnotationUtils.findAnnotation(bean.getClass(), RetryTaskListener.class);
                String taskType = listener.taskType();
                RetryStrategyConfig retryStrategyConfig = retryStrategyFactory.buildRetryStrategyConfig(taskType);
                retryStrategyConfig.setListener((RetryTaskExecuteSPI) bean);
            }
            System.err.println("=====ContextRefreshedEvent=====" + event.getSource().getClass().getName());
        }
    }

    public void init() {
        Map<String, RetryStrategyConfig> retryStrategyConfigs = retryStrategyFactory.getRetryStrategyConfigs();
        Map<String, RetryDataSourceConfig> retryDataSourceConfigs = retryStrategyFactory.getRetryDataSourceConfigs();
        retryTaskConfigProperties.getStrategyList().forEach(s -> {
            if (retryStrategyConfigs.containsKey(s.getBizType())) {
                throw new RuntimeException("retry strategy exist!bizType=" + s.getBizType());
            }
            //s.setListener((RetryTaskExecuteSPI) ApplicationContextAwareUtil.getBean(s.getListenerName()));
            retryStrategyConfigs.put(s.getBizType(), s);
        });
        retryStrategyFactory.setRetryStrategyConfigs(retryStrategyConfigs);

        List<RetryDataSourceConfig> dataSourceList = retryTaskConfigProperties.getDataSourceList();
        if (CollectionUtils.isEmpty(dataSourceList)) {
            dataSourceList.add(new RetryDataSourceConfig("ALL"));
        }
        dataSourceList.forEach(s -> {
            if (StringUtils.equals("ALL", s.getBizType())) {
                retryStrategyConfigs.keySet().forEach(bizType -> {
                    RetryDataSourceConfig c = new RetryDataSourceConfig();
                    c.setBizType(bizType);
                    c.setDataSource(dataSource);
                    initSqlMap(c);
                    retryDataSourceConfigs.put(bizType, c);
                });
            } else if (s.getBizType().contains(StringPool.COMMA)) {
                String[] types = s.getBizType().split(StringPool.COMMA);
                s.setDataSource(dataSource);
                initSqlMap(s);
                for (String type : types) {
                    retryDataSourceConfigs.put(type, s);
                }
            } else {
                s.setDataSource(dataSource);
                initSqlMap(s);
                retryDataSourceConfigs.put(s.getBizType(), s);
            }
        });
        retryStrategyFactory.setRetryDataSourceConfigs(retryDataSourceConfigs);

        Map<String, TaskStrategyExecutor> taskStrategyExecutorMap = new HashMap<>();
        taskStrategyExecutorMap.put(ScheduleExecuteType.DEFAULT.getCode(), defaultTaskStrategyExecutor);
        taskStrategyExecutorMap.put(ScheduleExecuteType.SYNC.getCode(), syncTaskStrategyExecutor);
        taskStrategyExecutorMap.put(ScheduleExecuteType.ASYNC.getCode(), asyncTaskStrategyExecutor);
        taskStrategyExecutorFactory.setTaskStrategyExecutors(taskStrategyExecutorMap);
    }

    /**
     * initate every sql sentence
     */
    private void initSqlMap(RetryDataSourceConfig retryDataSourceConfig) {

        //initial insert sentence
        String mysqlOriginalInsertSql =
                "insert into retry_task (task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,"
                        + "up_to_max_execute_times_flag,processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended) "
                        + "values(:taskId,:bizType,:bizId,:executedTimes,:nextExecuteTime,:maxExecuteTimes,:upToMaxExecuteTimesFlag,"
                        + ":processingFlag,:bizContext,:gmtCreate,:gmtModified,:priority,:suspended)";

        //initial update sentence
        String mysqlOriginalUpdateSql =
                "update retry_task set task_id=:taskId,biz_type=:bizType,biz_id=:bizId,executed_times=:executedTimes,"
                        + "next_execute_time=:nextExecuteTime,max_execute_times=:maxExecuteTimes,"
                        + "up_to_max_execute_times_flag=:upToMaxExecuteTimesFlag,processing_flag=:processingFlag,biz_context=:bizContext,"
                        + "gmt_create=:gmtCreate,gmt_modified=:gmtModified,priority=:priority,suspended=:suspended where "
                        + "task_id=:taskId ";

        //initial load sentence
        String mysqlOriginalLoadSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and processing_flag='F' and "
                        + "suspended!='T' and next_execute_time < now() order by priority desc" + " limit :rowNum";

        //initial load by priority sentence
        String mysqlOriginalLoadByPrioritySql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and processing_flag='F' and "
                        + "suspended!='T' and next_execute_time < now() and priority=:priority " + " limit :rowNum";

        // Unusual Orgi Load Sql
        String mysqlUnusualOrgiLoadSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended"
                        + " from retry_task where biz_type=:bizType and up_to_max_execute_times_flag='F' and"
                        + " processing_flag='T' and suspended != 'T' and gmt_modified < DATE_SUB(NOW(), INTERVAL :extremeRetryTime "
                        + "MINUTE)   limit :rowNum";

        //initial lock sentence
        String mysqlOriginalLockSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended from retry_task where biz_id=:bizId"
                        + " and biz_type=:bizType for update";

        //initial load by id sentence
        String mysqlOriginalLoadByIdSql =
                "select task_id,biz_type,biz_id,executed_times,next_execute_time,max_execute_times,up_to_max_execute_times_flag,"
                        + "processing_flag,biz_context,gmt_create,gmt_modified,priority,suspended from retry_task where biz_id=:bizId"
                        + " and biz_type=:bizType";

        //initial delete sentence
        String originaldeleteSql = "delete from retry_task where biz_id=:bizId and biz_type=:bizType ";

        Map<String, String> sqlMap = new HashMap<String, String>();

        //mysql
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_INSERT.getCode(),
                replaceTableName(mysqlOriginalInsertSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_UPDATE.getCode(),
                replaceTableName(mysqlOriginalUpdateSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_LOAD.getCode(),
                replaceTableName(mysqlOriginalLoadSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_LOAD_UNUSUAL.getCode(),
                replaceTableName(mysqlUnusualOrgiLoadSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_LOCK_BY_ID.getCode(),
                replaceTableName(mysqlOriginalLockSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_LOAD_BY_ID.getCode(),
                replaceTableName(mysqlOriginalLoadByIdSql, retryDataSourceConfig.getTablePrefix()));
        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_DELETE.getCode(),
                replaceTableName(originaldeleteSql, retryDataSourceConfig.getTablePrefix()));

        sqlMap.put(DbType.MYSQL.getCode() + RetryTaskConstants.INTERVAL_CHAR + SqlType.SQL_TASK_LOAD_BY_PRIORITY.getCode(),
                replaceTableName(mysqlOriginalLoadByPrioritySql, retryDataSourceConfig.getTablePrefix()));
        retryDataSourceConfig.setSqlMap(sqlMap);
    }

    private String replaceTableName(String sql, String tablePrefix) {
        if (StringUtils.isBlank(tablePrefix)) {
            return sql;
        }
        String tableName = tablePrefix + RetryTaskConstants.SUFFIX_TABLE_NAME;
        return sql.replaceAll(RetryTaskConstants.SUFFIX_TABLE_NAME, tableName);
    }

}
