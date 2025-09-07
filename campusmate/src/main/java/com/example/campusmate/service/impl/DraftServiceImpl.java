package com.example.campusmate.service.impl;

import com.example.campusmate.entity.Draft;
import com.example.campusmate.entity.Activity;
import com.example.campusmate.repository.DraftRepository;
import com.example.campusmate.repository.ActivityRepository;
import com.example.campusmate.service.DraftService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 草稿箱相关业务实现类
 */
@Service
public class DraftServiceImpl implements DraftService {
    @Autowired
    private DraftRepository draftRepository;

    @Autowired
    private ActivityRepository activityRepository;

    /**
     * 获取用户草稿列表
     * @param userId 用户ID
     * @param page 页码
     * @return 草稿列表
     */
    @Override
    public List<Draft> listDrafts(Long userId, int page) {
        return draftRepository.findByUserId(userId);
    }

    /**
     * 从草稿箱重新发布
     * @param draftId 草稿ID
     * @return 是否成功
     */
    @Override
    public boolean publishDraft(Long draftId) {
        Draft draft = draftRepository.findById(draftId).orElse(null);
        if (draft == null) {
            return false;
        }
        // 获取关联的活动
        Activity activity = activityRepository.findById(draft.getActivityId()).orElse(null);
        if (activity == null) {
            return false;
        }
        // 同步草稿内容到活动
        activity.setTitle(draft.getTitle());
        activity.setDescription(draft.getDescription());
        activity.setType(draft.getType());
        activity.setActivityTime(draft.getActivityTime());
        activity.setLocation(draft.getLocation());
        activity.setMinPeople(draft.getMinPeople());
        activity.setMaxPeople(draft.getMaxPeople());
        activity.setExpireTime(draft.getExpireTime());
        activity.setCampus(draft.getCampus());
        activity.setCollege(draft.getCollege());
        activity.setTags(draft.getTags());
        activity.setStatus("PUBLISHED");
        activity.setUpdatedAt(LocalDateTime.now());
        activity.setImageUrl(draft.getImageUrl());
        activityRepository.save(activity);
        // 删除草稿
        draftRepository.deleteById(draftId);
        return true;
    }

    /**
     * 彻底删除草稿
     * @param draftId 草稿ID
     * @return 是否成功
     */
    @Override
    public boolean deleteDraft(Long draftId) {
        Draft draft = draftRepository.findById(draftId).orElse(null);
        if (draft == null) {
            return false;
        }

        // 如果草稿有关联的活动，也删除活动
        if (draft.getActivityId() != null) {
            activityRepository.deleteById(draft.getActivityId());
        }

        // 删除草稿
        draftRepository.deleteById(draftId);
        return true;
    }

    /**
     * 创建草稿（将活动移至草稿箱）
     * @param activityId 活动ID
     * @param userId 用户ID
     * @param reason 原因
     * @return 是否成功
     */
    public boolean createDraft(Long activityId, Long userId, String reason) {
        // 检查是否已存在草稿
        Draft existingDraft = draftRepository.findByActivityId(activityId);
        if (existingDraft != null) {
            return false;
        }

        Draft draft = new Draft();
        draft.setUserId(userId);
        draft.setActivityId(activityId);
        draft.setReason(reason);
        draft.setCreatedAt(LocalDateTime.now());
        draftRepository.save(draft);

        return true;
    }

    /**
     * 获取草稿详情
     * @param draftId 草稿ID
     * @return 草稿信息
     */
    public Draft getDraftDetail(Long draftId) {
        return draftRepository.findById(draftId).orElse(null);
    }

    /**
     * 编辑草稿
     */
    @Override
    public boolean editDraft(Long draftId, Long userId, Draft draftUpdate) {
        Draft draft = draftRepository.findById(draftId).orElse(null);
        if (draft == null || !draft.getUserId().equals(userId)) {
            return false;
        }
        // 允许所有字段被修改
        if (draftUpdate.getReason() != null) draft.setReason(draftUpdate.getReason());
        if (draftUpdate.getTitle() != null) draft.setTitle(draftUpdate.getTitle());
        if (draftUpdate.getDescription() != null) draft.setDescription(draftUpdate.getDescription());
        if (draftUpdate.getType() != null) draft.setType(draftUpdate.getType());
        if (draftUpdate.getActivityTime() != null) draft.setActivityTime(draftUpdate.getActivityTime());
        if (draftUpdate.getLocation() != null) draft.setLocation(draftUpdate.getLocation());
        if (draftUpdate.getMinPeople() != null) draft.setMinPeople(draftUpdate.getMinPeople());
        if (draftUpdate.getMaxPeople() != null) draft.setMaxPeople(draftUpdate.getMaxPeople());
        if (draftUpdate.getExpireTime() != null) draft.setExpireTime(draftUpdate.getExpireTime());
        if (draftUpdate.getCampus() != null) draft.setCampus(draftUpdate.getCampus());
        if (draftUpdate.getCollege() != null) draft.setCollege(draftUpdate.getCollege());
        if (draftUpdate.getTags() != null) draft.setTags(draftUpdate.getTags());
        if (draftUpdate.getStatus() != null) draft.setStatus(draftUpdate.getStatus());
        if (draftUpdate.getUpdatedAt() != null) draft.setUpdatedAt(draftUpdate.getUpdatedAt());
        if (draftUpdate.getImageUrl() != null) draft.setImageUrl(draftUpdate.getImageUrl());
        draftRepository.save(draft);
        return true;
    }
}