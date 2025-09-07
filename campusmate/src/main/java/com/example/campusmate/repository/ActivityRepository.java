package com.example.campusmate.repository;

import com.example.campusmate.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 活动表数据库操作接口
 */
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    /**
     * 根据创建者ID查询活动列表
     * @param creatorId 创建者用户ID
     * @return 活动列表
     */
    List<Activity> findByCreatorId(Long creatorId);
    /**
     * 根据校区查询活动
     */
    List<Activity> findByCampus(String campus);

    /**
     * 根据活动类型查询
     */
    List<Activity> findByType(String type);

    /**
     * 根据状态查询活动
     */
    List<Activity> findByStatus(String status);

    /**
     * 根据校区和类型查询活动
     */
    List<Activity> findByCampusAndType(String campus, String type);

    /**
     * 根据校区和状态查询活动
     */
    List<Activity> findByCampusAndStatus(String campus, String status);

    /**
     * 根据类型和状态查询活动
     */
    List<Activity> findByTypeAndStatus(String type, String status);

    /**
     * 根据校区、类型和状态查询活动
     */
    List<Activity> findByCampusAndTypeAndStatus(String campus, String type, String status);

    /**
     * 根据校区、学院和状态查询活动
     */
    List<Activity> findByCampusAndCollegeAndStatus(String campus, String college, String status);

    /**
     * 根据学院和状态查询活动
     */
    List<Activity> findByCollegeAndStatus(String college, String status);

    /**
     * 根据学院和类型查询活动
     */
    List<Activity> findByCollegeAndType(String college, String type);

    /**
     * 根据学院、类型和状态查询活动
     */
    List<Activity> findByCollegeAndTypeAndStatus(String college, String type, String status);

    /**
     * 根据校区、学院、类型和状态查询活动
     */
    List<Activity> findByCampusAndCollegeAndTypeAndStatus(String campus, String college, String type, String status);

    /**
     * 查询即将过期的活动（活动时间在当前时间之后）
     */
    @Query("SELECT a FROM Activity a WHERE a.activityTime > :now AND a.status = 'PUBLISHED'")
    List<Activity> findUpcomingActivities(@Param("now") LocalDateTime now);

    /**
     * 查询已过期的活动
     */
    @Query("SELECT a FROM Activity a WHERE a.activityTime < :now AND a.status = 'PUBLISHED'")
    List<Activity> findExpiredActivities(@Param("now") LocalDateTime now);

    /**
     * 分页查询活动（支持多条件筛选）
     */
    @Query("SELECT a FROM Activity a WHERE " +
            "((:campuses) IS NULL OR a.campus IN (:campuses)) AND " +
            "((:colleges) IS NULL OR a.college IN (:colleges)) AND " +
            "((:types) IS NULL OR a.type IN (:types)) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Activity> findActivitiesWithFilters(
            @Param("campuses") List<String> campuses,
            @Param("colleges") List<String> colleges,
            @Param("types") List<String> types,
            @Param("status") String status,
            Pageable pageable);

    /**
     * 分页查询活动（支持模糊匹配筛选）
     */
    @Query("SELECT a FROM Activity a WHERE " +
            "(:campus IS NULL OR a.campus LIKE %:campus%) AND " +
            "(:college IS NULL OR a.college LIKE %:college%) AND " +
            "(:type IS NULL OR a.type LIKE %:type%) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Activity> findActivitiesWithFuzzyFilters(
            @Param("campus") String campus,
            @Param("college") String college,
            @Param("type") String type,
            @Param("status") String status,
            Pageable pageable);

    /**
     * 根据学院查询活动
     */
    List<Activity> findByCollege(String college);

    /**
     * 根据学院和校区查询活动
     */
    List<Activity> findByCollegeAndCampus(String college, String campus);

    /**
     * 分页查询活动（支持学院筛选，包含"全部学院"的活动）
     * 当选择特定学院时，也会显示college为null、空字符串或"全部学院"的活动
     */
    @Query("SELECT a FROM Activity a WHERE " +
            "(:campus IS NULL OR a.campus = :campus) AND " +
            "(:college IS NULL OR a.college = :college OR a.college IS NULL OR a.college = '' OR a.college = '全部学院') AND " +
            "(:type IS NULL OR a.type = :type) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Activity> findActivitiesWithCollegeFilter(
            @Param("campus") String campus,
            @Param("college") String college,
            @Param("type") String type,
            @Param("status") String status,
            Pageable pageable);

    /**
     * 分页模糊搜索活动（支持标题、描述、校区、学院、类型、状态）
     */
    @Query("SELECT a FROM Activity a WHERE " +
            "(:keyword IS NULL OR a.title LIKE %:keyword% OR a.description LIKE %:keyword%) AND " +
            "((:campuses) IS NULL OR a.campus IN (:campuses)) AND " +
            "((:colleges) IS NULL OR a.college IN (:colleges)) AND " +
            "((:types) IS NULL OR a.type IN (:types)) AND " +
            "(:status IS NULL OR a.status = :status)")
    Page<Activity> searchByKeyword(
            @Param("keyword") String keyword,
            @Param("campuses") List<String> campuses,
            @Param("colleges") List<String> colleges,
            @Param("types") List<String> types,
            @Param("status") String status,
            Pageable pageable);
}