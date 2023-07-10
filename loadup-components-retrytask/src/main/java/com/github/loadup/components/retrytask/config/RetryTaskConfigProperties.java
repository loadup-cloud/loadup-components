package com.github.loadup.components.retrytask.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

/**
 * @author Admin
 */
@ConfigurationProperties(prefix = "retrytask")
@Getter
@Setter
@Component
public class RetryTaskConfigProperties {
    /**
     * the prefix of table name
     */
    private String tablePrefix = "";
    private String dbType      = "MYSQL";

    @NestedConfigurationProperty
    private List<RetryStrategyConfig> taskList = new ArrayList<>();

}
