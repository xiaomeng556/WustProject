package com.example.campusmate.nacos;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component("CustomNacosConfigProperty")
@ConfigurationProperties(prefix = "spring.cloud.nacos.config")
public class NacosConfigProperties {
    // Nacos服务器地址
    private String serverAddr;
    // 默认命名空间（留空则使用public）
    private String defaultNamespace = "";
    // 默认分组
    private String defaultGroup = "DEFAULT_GROUP";

    // getter和setter
    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    public void setDefaultNamespace(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}