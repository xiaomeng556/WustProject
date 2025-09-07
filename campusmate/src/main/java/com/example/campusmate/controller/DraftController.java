package com.example.campusmate.controller;

import com.example.campusmate.entity.Draft;
import com.example.campusmate.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import com.example.campusmate.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import com.example.campusmate.Utils.UidUtils;

/**
 * 草稿控制器
 * 处理与草稿相关的API请求，包括查看草稿列表、发布草稿、删除草稿、编辑草稿等操作。
 */
@RestController
@RequestMapping("/api/drafts")
@CrossOrigin(origins = "*")
@Tag(name = "草稿", description = "处理与草稿相关的API请求，包括查看草稿列表、发布草稿、删除草稿、编辑草稿等操作。")
public class DraftController {

    @Autowired
    private DraftService draftService;

    /**
     * 获取当前用户的草稿列表
     * @param page 当前页码，默认值为1
     * @return ApiResponse<List<Draft>> 返回当前用户的草稿列表
     */
    @Operation(summary = "获取草稿列表", description = "获取当前用户的草稿列表，支持分页。")
    @GetMapping
    public ApiResponse<List<Draft>> listDrafts(
            @Parameter(description = "当前页码，默认值为1") @RequestParam(defaultValue = "1") int page) {
        // 获取当前登录用户的UID
        Long uid = UidUtils.getUid();

        // 获取草稿列表
        List<Draft> list = draftService.listDrafts(uid, page);

        // 返回草稿列表
        return ApiResponse.success(list);
    }

    /**
     * 发布草稿
     * @param draftId 草稿ID
     * @return ApiResponse<Boolean> 返回操作结果，true表示成功，false表示失败
     */
    @Operation(summary = "发布草稿", description = "将指定草稿发布。")
    @PostMapping("/{draftId}/publish")
    public ApiResponse<Boolean> publishDraft(
            @Parameter(description = "草稿ID") @PathVariable Long draftId) {
        // 调用服务层方法发布草稿
        boolean result = draftService.publishDraft(draftId);

        // 返回操作结果
        return ApiResponse.success(result);
    }

    /**
     * 删除指定草稿
     * @param draftId 草稿ID
     * @return ApiResponse<Boolean> 返回操作结果，true表示成功，false表示失败
     */
    @Operation(summary = "删除草稿", description = "删除指定的草稿。")
    @DeleteMapping("/{draftId}")
    public ApiResponse<Boolean> deleteDraft(
            @Parameter(description = "草稿ID") @PathVariable Long draftId) {
        // 调用服务层方法删除草稿
        boolean result = draftService.deleteDraft(draftId);

        // 返回操作结果
        return ApiResponse.success(result);
    }

    /**
     * 编辑指定草稿
     * @param draftId 草稿ID
     * @param draftUpdate 包含更新内容的Draft对象
     * @return ApiResponse<Boolean> 返回操作结果，true表示成功，false表示失败
     */
    @Operation(summary = "编辑草稿", description = "编辑指定草稿，确保用户有权限修改草稿。")
    @PutMapping("/{draftId}")
    public ApiResponse<Boolean> editDraft(
            @Parameter(description = "草稿ID") @PathVariable Long draftId,
            @Parameter(description = "包含更新内容的Draft对象") @RequestBody Draft draftUpdate) {
        // 获取当前登录用户的UID
        Long uid = UidUtils.getUid();

        // 调用服务层方法编辑草稿（确保用户有权限修改该草稿）
        boolean result = draftService.editDraft(draftId, uid, draftUpdate);

        // 如果没有权限或草稿不存在，返回失败信息
        if (!result) {
            return ApiResponse.fail("无权限或草稿不存在");
        }

        // 返回操作成功
        return ApiResponse.success(true);
    }
}
