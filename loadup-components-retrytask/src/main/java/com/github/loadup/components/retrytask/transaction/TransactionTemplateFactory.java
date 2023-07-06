
package com.github.loadup.components.retrytask.transaction;

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
import java.util.HashMap;
import java.util.Map;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

/**
 * 事务模板工厂
 * 
 * 
 * 
 */
@Component
public class TransactionTemplateFactory {

    /** 事务模板集合,Key为bizType */
    private Map<String, TransactionTemplate> transactionTemplates = new HashMap<String, TransactionTemplate>();

    /** 重试策略工厂 */
    private RetryStrategyFactory retryStrategyFactory;

    /**
     * 根据bizType获取对应的事务模板
     * 
     * @param bizType bizType
     * @return  事务模板
     */
    public TransactionTemplate obtainTemplate(String bizType) {

        if (transactionTemplates.get(bizType) != null) {
            return transactionTemplates.get(bizType);
        }

        synchronized (this) {

            if (transactionTemplates.get(bizType) != null) {

                return transactionTemplates.get(bizType);
            }

            TransactionTemplate transactionTemplate = new TransactionTemplate();
            DataSourceTransactionManager transactionManager = new DataSourceTransactionManager();
            transactionTemplate.setTransactionManager(transactionManager);
            RetryDataSourceConfig retryDataSourceConfig = retryStrategyFactory
                .buildRetryDataSourceConfig(bizType);
            transactionManager.setDataSource(retryDataSourceConfig.getDataSource());

            transactionTemplates.put(bizType, transactionTemplate);
        }
        return transactionTemplates.get(bizType);
    }

    // ~~~  getters and setters

    /**
     * Getter method for property <tt>retryStrategyFactory</tt>.
     * 
     * @return property value of retryStrategyFactory
     */
    public RetryStrategyFactory getRetryStrategyFactory() {
        return retryStrategyFactory;
    }

    /**
     * Setter method for property <tt>retryStrategyFactory</tt>.
     * 
     * @param retryStrategyFactory value to be assigned to property retryStrategyFactory
     */
    public void setRetryStrategyFactory(RetryStrategyFactory retryStrategyFactory) {
        this.retryStrategyFactory = retryStrategyFactory;
    }

}
