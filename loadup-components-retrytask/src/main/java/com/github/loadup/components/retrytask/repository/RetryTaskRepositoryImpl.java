package com.github.loadup.components.retrytask.repository;

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

import com.github.loadup.components.retrytask.config.RetryTaskFactory;
import com.github.loadup.components.retrytask.constant.RetryTaskConstants;
import com.github.loadup.components.retrytask.enums.SqlType;
import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.utils.ResultSetUtil;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * the implement of retry task repository
 */
@Slf4j
@Component("retryTaskRepository")
public class RetryTaskRepositoryImpl implements RetryTaskRepository {

    /**
     * the factory of retry strategy
     */
    @Autowired
    private RetryTaskFactory           retryTaskFactory;
    @Resource
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private String finSql(SqlType sqlType) {
        String sqlKey = retryTaskFactory.getDbType() + RetryTaskConstants.INTERVAL_CHAR + sqlType.getCode();
        return retryTaskFactory.getSqlMap().get(sqlKey);
    }

    /**
     * @see RetryTaskRepository#insert(RetryTask)
     */
    @Override
    public void insert(RetryTask retryTask) {
        String sql = finSql(SqlType.SQL_TASK_INSERT);
        Map<String, Object> paramMap = convter2InsertMap(retryTask);
        namedParameterJdbcTemplate.update(sql, paramMap);
    }

    /**
     * @see RetryTaskRepository#lockByBizId(java.lang.String, java.lang.String)
     */
    @Override
    public RetryTask lockByBizId(String bizId, String bizType) {
        String sql = finSql(SqlType.SQL_TASK_LOCK_BY_ID);
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("bizId", bizId);
        paramMap.put("bizType", bizType);
        RetryTask retryTask = null;
        try {
            retryTask = (RetryTask) namedParameterJdbcTemplate.queryForObject(sql, paramMap,
                    (RowMapper) (rs, rowNum) -> convertResultSet2RetryTask(rs));
        } catch (EmptyResultDataAccessException e) {
            // do nothing.  only catch.
        }

        return retryTask;
    }

    /**
     * @see RetryTaskRepository#loadByBizId(java.lang.String, java.lang.String)
     */
    @Override
    public RetryTask loadByBizId(String bizId, String bizType) {
        String sql = finSql(SqlType.SQL_TASK_LOAD_BY_ID);
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("bizId", bizId);
        paramMap.put("bizType", bizType);

        RetryTask retryTask = null;
        try {
            retryTask = (RetryTask) namedParameterJdbcTemplate.queryForObject(sql, paramMap,
                    (RowMapper) (rs, rowNum) -> convertResultSet2RetryTask(rs));
        } catch (EmptyResultDataAccessException e) {
            // do nothing.  only catch.
        }

        return retryTask;
    }

    /**
     * @see RetryTaskRepository#delete(java.lang.String, java.lang.String)
     */
    @Override
    public void delete(String bizId, String bizType) {

        String sql = finSql(SqlType.SQL_TASK_DELETE);
        Map<String, String> paramMap = new HashMap<>(2);
        paramMap.put("bizId", bizId);
        paramMap.put("bizType", bizType);
        namedParameterJdbcTemplate.update(sql, paramMap);
    }

    /**
     * @see RetryTaskRepository#update(RetryTask)
     */
    @Override
    public void update(RetryTask retryTask) {
        String sql = finSql(SqlType.SQL_TASK_UPDATE);
        Map<String, Object> paramMap = convter2InsertMap(retryTask);
        namedParameterJdbcTemplate.update(sql, paramMap);
    }

    @Override
    public List<RetryTask> load(String bizType, int rowNum) {
        Map<String, Object> paramMap = new HashMap<>(2);
        paramMap.put("bizType", bizType);
        paramMap.put("rowNum", rowNum);

        String sql = finSql(SqlType.SQL_TASK_LOAD);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<RetryTask> retryTasks = namedParameterJdbcTemplate.query(sql, paramMap,
                (RowMapper) (rs, rowNum1) -> convertResultSet2RetryTask(rs));
        return retryTasks;
    }

    @Override
    public List<RetryTask> loadByPriority(String bizType, String priority, int rowNum) {

        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("priority", priority);
        paramMap.put("bizType", bizType);
        paramMap.put("rowNum", rowNum);

        String sql = finSql(SqlType.SQL_TASK_LOAD_BY_PRIORITY);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<RetryTask> retryTasks = namedParameterJdbcTemplate.query(sql, paramMap,
                (RowMapper) (rs, rowNum1) -> convertResultSet2RetryTask(rs));

        return retryTasks;
    }

    @Override
    public List<RetryTask> loadUnuaualTask(String bizType, int extremeRetryTime, int rowNum) {
        Map<String, Object> paramMap = new HashMap<>(3);
        paramMap.put("bizType", bizType);
        paramMap.put("extremeRetryTime", extremeRetryTime);
        paramMap.put("rowNum", rowNum);

        String unusualSql = finSql(SqlType.SQL_TASK_LOAD_UNUSUAL);
        @SuppressWarnings({"unchecked", "rawtypes"})
        List<RetryTask> unusualRetryTasks = namedParameterJdbcTemplate.query(unusualSql, paramMap,
                (RowMapper) (rs, rowNum1) -> convertResultSet2RetryTask(rs));

        return unusualRetryTasks;
    }

    /**
     * 构建insert的参数
     *
     * @param retryTask retryTask
     * @return map
     */
    private Map<String, Object> convter2InsertMap(RetryTask retryTask) {

        Map<String, Object> resultMap = new HashMap<>();

        resultMap.put("taskId", retryTask.getTaskId());
        resultMap.put("bizId", retryTask.getBizId());
        resultMap.put("bizType", retryTask.getBizType());
        resultMap.put("executedTimes", retryTask.getExecutedTimes());
        resultMap.put("nextExecuteTime", retryTask.getNextExecuteTime());
        resultMap.put("maxExecuteTimes", retryTask.getMaxExecuteTimes());
        resultMap.put("upToMaxExecuteTimesFlag", retryTask.isUpToMaxExecuteTimesFlag() ? "T" : "F");
        resultMap.put("processingFlag", retryTask.isProcessingFlag() ? "T" : "F");
        resultMap.put("bizContext", StringUtils.defaultIfBlank(retryTask.getBizContext(), ""));
        resultMap.put("gmtCreate", retryTask.getGmtCreate());
        resultMap.put("gmtModified", retryTask.getGmtModified());
        resultMap.put("priority", StringUtils.defaultIfBlank(retryTask.getPriority(), ""));
        resultMap.put("suspended", retryTask.isSuspended() ? "T" : "F");

        return resultMap;
    }

    /**
     * convert sql result to retry task
     *
     * @param rs sql result
     * @return retry task
     * @throws SQLException SQLException
     */
    private RetryTask convertResultSet2RetryTask(ResultSet rs) throws SQLException {

        RetryTask retryTask = new RetryTask();

        retryTask.setTaskId(rs.getString("task_id"));
        retryTask.setBizType(rs.getString("biz_type"));
        retryTask.setBizId(rs.getString("biz_id"));
        retryTask.setExecutedTimes(rs.getInt("executed_times"));
        retryTask.setNextExecuteTime(ResultSetUtil.obtainDateValue(rs, "next_execute_time"));
        retryTask.setMaxExecuteTimes(rs.getInt("max_execute_times"));
        retryTask.setUpToMaxExecuteTimesFlag(
                StringUtils.equalsIgnoreCase(rs.getString("up_to_max_execute_times_flag"), "T"));
        retryTask.setProcessingFlag(
                StringUtils.equalsIgnoreCase(rs.getString("processing_flag"), "T"));
        retryTask.setBizContext(rs.getString("biz_context"));
        retryTask.setGmtCreate(ResultSetUtil.obtainDateValue(rs, "gmt_create"));
        retryTask.setGmtModified(ResultSetUtil.obtainDateValue(rs, "gmt_modified"));
        retryTask.setPriority(rs.getString("priority"));
        retryTask.setSuspended(
                StringUtils.equalsIgnoreCase(rs.getString("suspended"), "T"));
        return retryTask;
    }

}
