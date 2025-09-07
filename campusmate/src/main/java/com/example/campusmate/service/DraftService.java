package com.example.campusmate.service;

import com.example.campusmate.entity.Draft;
import java.util.List;

/**
 * 草稿箱相关业务接口
 */
public interface DraftService {
    /**
     * 获取用户草稿列表
     * @param userId 用户ID
     * @param page 页码
     * @return 草稿列表
     */
    List<Draft> listDrafts(Long userId, int page);

    /**
     * 从草稿箱重新发布
     * @param draftId 草稿ID
     * @return 是否成功
     */
    boolean publishDraft(Long draftId);

    /**
     * 彻底删除草稿
     * @param draftId 草稿ID
     * @return 是否成功
     */
    boolean deleteDraft(Long draftId);
    boolean editDraft(Long draftId, Long userId, Draft draftUpdate);
}