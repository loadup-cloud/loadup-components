
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

/**
 * 重试任务数据项
 * 
 * 
 * 
 */
public class RetryTaskDataItem implements DataLoadFilterItem {

    /** serialVersionUID */
    private static final long serialVersionUID = 6638190384777209203L;

    /** 分库分表位（对于单表该属性为空） */
    private String            shardingIdx;

    /** 业务类型 */
    private String            bizType;

    /**
     * 数据库分库数量
     * 默认单库
     */
    private int               dbNum            = 1;

    /**
     * 每个库的分表数量
     * 默认单表
     */
    private int               tableNumPerDb    = 1;

    /**
     * 表名前缀
     */
    private String            tablePrefix;

    /**
     * SQL方言，适配不同类型数据库的sql语法
     * 
     * 默认-MYSQL
     * 可选-OB
     */
    private String            dbType;

    /**
     * 默认构造函数
     */
    public RetryTaskDataItem() {
    }

    // ~~~ 容器方法

    /**
     * Getter method for property <tt>shardingIdx</tt>.
     * 
     * @return property value of shardingIdx
     */
    public String getShardingIdx() {
        return shardingIdx;
    }

    /**
     * Setter method for property <tt>shardingIdx</tt>.
     * 
     * @param shardingIdx value to be assigned to property shardingIdx
     */
    public void setShardingIdx(String shardingIdx) {
        this.shardingIdx = shardingIdx;
    }

    /**
     * Getter method for property <tt>bizType</tt>.
     * 
     * @return property value of bizType
     */
    public String getBizType() {
        return bizType;
    }

    /**
     * Setter method for property <tt>bizType</tt>.
     * 
     * @param bizType value to be assigned to property bizType
     */
    public void setBizType(String bizType) {
        this.bizType = bizType;
    }

    /**
     * Getter method for property <tt>dbNum</tt>.
     * 
     * @return property value of dbNum
     */
    public int getDbNum() {
        return dbNum;
    }

    /**
     * Setter method for property <tt>dbNum</tt>.
     * 
     * @param dbNum value to be assigned to property dbNum
     */
    public void setDbNum(int dbNum) {
        this.dbNum = dbNum;
    }

    /**
     * Getter method for property <tt>tableNumPerDb</tt>.
     * 
     * @return property value of tableNumPerDb
     */
    public int getTableNumPerDb() {
        return tableNumPerDb;
    }

    /**
     * Setter method for property <tt>tableNumPerDb</tt>.
     * 
     * @param tableNumPerDb value to be assigned to property tableNumPerDb
     */
    public void setTableNumPerDb(int tableNumPerDb) {
        this.tableNumPerDb = tableNumPerDb;
    }

    /**
     * Getter method for property <tt>tablePrefix</tt>.
     * 
     * @return property value of tablePrefix
     */
    public String getTablePrefix() {
        return tablePrefix;
    }

    /**
     * Setter method for property <tt>tablePrefix</tt>.
     * 
     * @param tablePrefix value to be assigned to property tablePrefix
     */
    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
    }

    /**
     * Getter method for property <tt>dbType</tt>.
     * 
     * @return property value of dbType
     */
    public String getDbType() {
        return dbType;
    }

    /**
     * Setter method for property <tt>dbType</tt>.
     * 
     * @param dbType value to be assigned to property dbType
     */
    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

}
