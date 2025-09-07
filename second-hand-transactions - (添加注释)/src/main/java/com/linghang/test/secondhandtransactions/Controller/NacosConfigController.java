package com.linghang.test.secondhandtransactions.Controller;

import com.alibaba.nacos.api.exception.NacosException;

import com.linghang.test.secondhandtransactions.nacos.NacosConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/nacos/config")
@Tag(name = "Nacos配置中心接口")
public class NacosConfigController {

    private final NacosConfigService nacosConfigService;

    public NacosConfigController(NacosConfigService nacosConfigService) {
        this.nacosConfigService = nacosConfigService;
    }

    /**
     * 获取指定服务的配置内容
     * @param dataId 服务标识（如user-service.yml）
     * @param group 分组（可选，默认使用配置的defaultGroup）
     * @param namespace 命名空间（可选，默认使用配置的defaultNamespace）
     */
    @GetMapping("/content")
    @Operation(summary = "获取指定服务的配置内容")
    public ResponseEntity<Map<String, Object>> getConfigContent(
            @RequestParam String dataId,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String namespace) {
        try {
            String content = nacosConfigService.getConfigContent(dataId, group, namespace);
            return successResponse("获取成功", content);
        } catch (NacosException e) {
            return errorResponse("获取失败：" + e.getMessage());
        }
    }

    /**
     * 获取指定服务的配置键值
     * @param dataId 服务标识
     * @param key 配置键（支持多级，如spring.datasource.url）
     * @param group 分组（可选）
     * @param namespace 命名空间（可选）
     */
    @GetMapping("/value")
    @Operation(summary = "获取指定服务的配置键值")
    public ResponseEntity<Map<String, Object>> getConfigValue(
            @RequestParam String dataId,
            @RequestParam String key,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String namespace) {
        try {
            Object value = nacosConfigService.getConfigValue(dataId, key, group, namespace);
            return successResponse("获取成功", value);
        } catch (NacosException e) {
            return errorResponse("获取失败：" + e.getMessage());
        }
    }

    /**
     * 更新指定服务的配置内容
     * @param dataId 服务标识
     * @param group 分组（可选）
     * @param namespace 命名空间（可选）
     * @param content 完整的配置内容（YAML格式）
     */
    @PostMapping("/content")
    @Operation(summary = "更新指定服务的配置内容")
    public ResponseEntity<Map<String, Object>> updateConfigContent(
            @RequestParam String dataId,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String namespace,
            @RequestBody String content) {
        try {
            boolean success = nacosConfigService.updateConfigContent(dataId, content, group, namespace);
            return success ? successResponse("更新成功", null) : errorResponse("更新失败");
        } catch (NacosException e) {
            return errorResponse("更新失败：" + e.getMessage());
        }
    }

    /**
     * 更新指定服务的配置键值
     * @param dataId 服务标识
     * @param key 配置键（支持多级）
     * @param value 新值
     * @param group 分组（可选）
     * @param namespace 命名空间（可选）
     */
    @PostMapping("/value")
    @Operation(summary = "更新指定服务的配置键值")
    public ResponseEntity<Map<String, Object>> updateConfigValue(
            @RequestParam String dataId,
            @RequestParam String key,
            @RequestParam String value,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String namespace) {
        try {
            boolean success = nacosConfigService.updateConfigValue(dataId, key, value, group, namespace);
            return success ? successResponse("更新成功", null) : errorResponse("更新失败");
        } catch (NacosException e) {
            return errorResponse("更新失败：" + e.getMessage());
        }
    }

    /**
     * 删除指定服务的配置
     * @param dataId 服务标识
     * @param group 分组（可选）
     * @param namespace 命名空间（可选）
     */
    @DeleteMapping
    @Operation(summary = "删除指定服务的配置")
    public ResponseEntity<Map<String, Object>> deleteConfig(
            @RequestParam String dataId,
            @RequestParam(required = false) String group,
            @RequestParam(required = false) String namespace) {
        try {
            boolean success = nacosConfigService.deleteConfig(dataId, group, namespace);
            return success ? successResponse("删除成功", null) : errorResponse("删除失败");
        } catch (NacosException e) {
            return errorResponse("删除失败：" + e.getMessage());
        }
    }

    // 统一响应工具方法
    private ResponseEntity<Map<String, Object>> successResponse(String message, Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", message);
        response.put("data", data);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private ResponseEntity<Map<String, Object>> errorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 500);
        response.put("message", message);
        response.put("data", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}