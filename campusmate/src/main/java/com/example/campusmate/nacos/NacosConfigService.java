package com.example.campusmate.nacos;

import com.alibaba.nacos.api.NacosFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.stereotype.Service;
import org.yaml.snakeyaml.Yaml;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
public class NacosConfigService {

    private final NacosConfigProperties nacosConfigProperties;
    private final Yaml yaml = new Yaml();

    public NacosConfigService(NacosConfigProperties nacosConfigProperties) {
        this.nacosConfigProperties = nacosConfigProperties;
    }

    /**
     * 根据namespace创建对应的ConfigService
     */
    private ConfigService createConfigService(String namespace) throws NacosException {
        Properties properties = new Properties();
        properties.setProperty("serverAddr", nacosConfigProperties.getServerAddr());

        // 设置命名空间，public命名空间为空字符串
        if (namespace != null && !namespace.isEmpty()) {
            properties.setProperty("namespace", namespace);
        }



        return NacosFactory.createConfigService(properties);
    }

    /**
     * 获取指定服务的配置内容
     */
    public String getConfigContent(String dataId, String group, String namespace) throws NacosException {
        // 处理默认值
        String actualGroup = getOrDefault(group, nacosConfigProperties.getDefaultGroup());
        String actualNamespace = getOrDefault(namespace, nacosConfigProperties.getDefaultNamespace());

        ConfigService configService = createConfigService(actualNamespace);
        return configService.getConfig(dataId, actualGroup, 5000);
        // 每次都创建新的ConfigService实例
//        try () {
//
//        }
    }

    /**
     * 获取指定服务的配置键值
     */
    public Object getConfigValue(String dataId, String key, String group, String namespace) throws NacosException {
        String content = getConfigContent(dataId, group, namespace);
        if (content == null || content.isEmpty()) {
            return null;
        }

        // 解析YAML
        Map<String, Object> configMap = yaml.load(content);
        // 处理多级键（如spring.datasource.url）
        String[] keyParts = key.split("\\.");
        Map<String, Object> currentMap = configMap;

        for (int i = 0; i < keyParts.length; i++) {
            String part = keyParts[i];
            if (!currentMap.containsKey(part)) {
                return null; // 键不存在
            }

            if (i == keyParts.length - 1) {
                return currentMap.get(part); // 返回最终值
            }

            // 进入下一级（必须是Map类型）
            Object value = currentMap.get(part);
            if (!(value instanceof Map)) {
                return null; // 中间节点不是Map，无法继续
            }
            currentMap = (Map<String, Object>) value;
        }
        return null;
    }

    /**
     * 更新指定服务的配置内容
     */
    public boolean updateConfigContent(String dataId, String content, String group, String namespace) throws NacosException {
        String actualGroup = getOrDefault(group, nacosConfigProperties.getDefaultGroup());
        String actualNamespace = getOrDefault(namespace, nacosConfigProperties.getDefaultNamespace());

        // 每次都创建新的ConfigService实例
        ConfigService configService = createConfigService(actualNamespace);
        return configService.publishConfig(dataId, actualGroup, content);
    }

    /**
     * 更新指定服务的配置键值
     */
    public boolean updateConfigValue(String dataId, String key, Object value, String group, String namespace) throws NacosException {
        // 1. 先获取现有配置
        String content = getConfigContent(dataId, group, namespace);
        Map<String, Object> configMap = new HashMap<>();

        // 如果配置存在，解析现有内容；否则创建新Map
        if (content != null && !content.isEmpty()) {
            Object loaded = yaml.load(content);
            if (loaded instanceof Map) {
                configMap = (Map<String, Object>) loaded;
            }
        }

        // 2. 更新指定键（支持多级键）
        String[] keyParts = key.split("\\.");
        Map<String, Object> currentMap = configMap;

        for (int i = 0; i < keyParts.length; i++) {
            String part = keyParts[i];
            if (i == keyParts.length - 1) {
                currentMap.put(part, value); // 设置最终值
                break;
            }

            // 中间节点如果不存在，创建新Map
            if (!currentMap.containsKey(part) || !(currentMap.get(part) instanceof Map)) {
                currentMap.put(part, new HashMap<>());
            }
            currentMap = (Map<String, Object>) currentMap.get(part);
        }

        // 3. 转换为YAML并更新
        String updatedContent = yaml.dump(configMap);
        return updateConfigContent(dataId, updatedContent, group, namespace);
    }

    /**
     * 删除指定服务的配置
     */
    public boolean deleteConfig(String dataId, String group, String namespace) throws NacosException {
        String actualGroup = getOrDefault(group, nacosConfigProperties.getDefaultGroup());
        String actualNamespace = getOrDefault(namespace, nacosConfigProperties.getDefaultNamespace());

        // 每次都创建新的ConfigService实例
        ConfigService configService = createConfigService(actualNamespace);
        return configService.removeConfig(dataId, actualGroup);
    }

    // 工具方法：获取值或使用默认值
    private String getOrDefault(String value, String defaultValue) {
        return (value == null || value.isEmpty()) ? defaultValue : value;
    }
}
