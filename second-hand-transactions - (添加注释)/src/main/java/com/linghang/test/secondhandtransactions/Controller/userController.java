package com.linghang.test.secondhandtransactions.Controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.linghang.test.secondhandtransactions.Entity.User_collection;
import com.linghang.test.secondhandtransactions.Service.CommodityService;
import com.linghang.test.secondhandtransactions.Service.UserCollectionService;
import com.linghang.test.secondhandtransactions.Service.UsersService;
import com.linghang.test.secondhandtransactions.common.PublishForm;
import com.linghang.test.secondhandtransactions.utils.DebounceUtil;
import com.linghang.test.secondhandtransactions.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.linghang.test.secondhandtransactions.utils.UidUtils.getUsernameFromSecurityContext;

/**
 * 用户操作控制器
 * 处理用户相关的商品管理、收藏管理等功能
 */
@Tag(name = "用户操作接口", description = "提供用户对商品的删除、更新、收藏等操作功能")
@RestController
@RequestMapping("/userController")
public class userController {

    @Resource
    private CommodityService productService;
    @Resource
    private CommodityService commodityService;  // 商品服务接口

    @Resource
    private UsersService usersService;  // 用户服务接口
    @Resource
    private UserCollectionService userCollectionService;

    // 防抖超时设置（毫秒）
    private static final long DELETE_TIMEOUT = 2000;
    private static final long UPDATE_TIMEOUT = 3000;
    private static final long COLLECTION_TIMEOUT = 2000;
    private static final long FILTER_DEBOUNCE_TIMEOUT = 2000;

    @GetMapping("/uid/{current}/{size}")
    public Result<Page<Commodity>> getProductsByUId(
            @PathVariable long current,
            @PathVariable long size) {
        String uid = getUsernameFromSecurityContext();

        if (DebounceUtil.shouldDebounce(uid, "filter_by_category", FILTER_DEBOUNCE_TIMEOUT)) {
            return Result.<Page<Commodity>>fail().message("筛选过于频繁，请稍后再试");
        }

        // 3. 执行筛选逻辑
        Page<Commodity> result = commodityService.getProductsByUid(uid,current,size);

        return Result.ok(result);
    }

    /**
     * 删除商品
     * 仅允许删除当前用户发布的商品
     *
     * @param pid 商品ID
     * @return 操作结果
     */
    @Operation(summary = "删除商品", description = "删除指定ID的商品，仅允许删除自己发布的商品")
    @DeleteMapping("/delete/{pid}")
    public Result<String> delete(
            @Parameter(description = "商品ID", required = true) @PathVariable Long pid) {

        // 获取当前登录用户ID
        String uid = getUsernameFromSecurityContext();

        // 校验商品是否存在且属于当前用户
        boolean isOwner = commodityService.checkCommodityOwner(pid, uid);
        if (!isOwner) {
            return Result.fail("您只能删除自己发布的商品");
        }

        // 执行单条删除
        boolean success = commodityService.removeById(pid);
        return success ? Result.ok("删除成功") : Result.fail("删除失败，商品可能不存在");
    }

    /**
     * 查询商品详情
     * 根据商品ID查询商品详细信息，用于更新前的回显
     *
     * @param pid 商品ID
     * @return 商品详情
     */

    @Operation(summary = "查询商品是否被收藏", description = "根据商品ID查询该商品是否存在于用户收藏表中")
    @GetMapping("/select/{pid}")
    public Result<Object> select(
            @Parameter(description = "商品ID", required = true) @PathVariable Long pid
           ) {
        String uid=getUsernameFromSecurityContext();
        // 创建查询条件，同时匹配商品ID和用户ID
        QueryWrapper<User_collection> wrapper = Wrappers.query();
        wrapper.eq("pid", pid)
                .eq("uid",uid); // 假设用户ID字段名为user_id

        // 执行查询，判断是否存在该收藏记录
        boolean isCollected = userCollectionService.exists(wrapper);
          if (isCollected) {
            // 商品已被收藏
            return Result.ok("商品已收藏");
        } else {
            // 商品未被收藏
            return Result.ok( "商品未收藏");
        }
    }

    /**
     * 更新商品信息
     * 仅允许更新当前用户发布的商品，包含防抖处理
     *
     * @param form 商品更新表单数据
     * @param pid 商品ID
     * @return 操作结果
     */
    @Operation(summary = "更新商品信息", description = "更新指定ID的商品信息，仅允许更新自己发布的商品")
    @PostMapping("/update/{pid}")
    public Result<String> updateCommodity(
            @Parameter(description = "商品更新表单数据", required = true) @RequestBody PublishForm form,
            @Parameter(description = "商品ID", required = true) @PathVariable Long pid) {

        // 获取当前登录用户ID（注：此处为示例写死，实际应使用下方注释的动态获取方式）
         String uid=getUsernameFromSecurityContext();
        //String uid = "user004";

        // 防抖校验（用户+更新操作+商品ID）
        if (DebounceUtil.shouldDebounce(uid, "update:" + pid, UPDATE_TIMEOUT)) {
            return Result.fail("操作过于频繁，请稍后再试");
        }

        // 权限校验：检查商品是否存在及是否属于当前用户
        Commodity existingCommodity = commodityService.getById(pid);
        if (existingCommodity == null) {
            return Result.fail("商品不存在");
        }
        if (!existingCommodity.getUid().equals(uid)) {
            return Result.fail("您只能修改自己发布的商品");
        }

        // 构建商品对象
        Commodity commodity = new Commodity(
                pid, uid, form.getName(), form.getPrice(), form.getDate(),
                form.getContact(), form.getStatus(), form.getType(),
                form.getIntroduce(), form.getImagePath()
        );

        // 执行更新
        boolean success = commodityService.updateCommodity(commodity, uid);
        return success ? Result.ok("更新成功") : Result.fail("更新失败");
    }

    /**
     * 添加商品收藏
     * 为当前用户收藏指定商品，包含防抖处理
     *
     * @param pid 商品ID
     * @return 操作结果
     */
    @Operation(summary = "添加商品收藏", description = "为当前用户收藏指定ID的商品")
    @PostMapping("/collection/add")
    public Result<Boolean> addCollection(
            @Parameter(description = "商品ID", required = true) @RequestParam @Param("pid") Long pid) {

        // 获取当前登录用户ID
        String userId = getUsernameFromSecurityContext();

        // 防抖校验（用户+收藏操作+商品ID）
        if (DebounceUtil.shouldDebounce(userId, "collect:" + pid, COLLECTION_TIMEOUT)) {
            return Result.fail(false).message("操作过于频繁，请稍后再试");
        }

        // 执行收藏
        boolean success = usersService.addCollection(userId, pid);
        return success ? Result.ok(true).message("收藏成功") : Result.fail(false).message("收藏失败");
    }

    /**
     * 取消商品收藏
     * 取消当前用户对指定商品的收藏，包含防抖处理
     *
     * @param pid 商品ID
     * @return 操作结果
     */
    @Operation(summary = "取消商品收藏", description = "取消当前用户对指定ID商品的收藏")
    @DeleteMapping("/collection/remove")
    public Result<Boolean> removeCollection(
            @Parameter(description = "商品ID", required = true) @RequestParam Long pid) {

        // 获取当前登录用户ID（注：此处为示例写死，实际应使用下方注释的动态获取方式）
         String userId=getUsernameFromSecurityContext();
        

        // 防抖校验
        if (DebounceUtil.shouldDebounce(userId, "uncollect:" + pid, COLLECTION_TIMEOUT)) {
            return Result.fail(false).message("操作过于频繁，请稍后再试");
        }

        // 执行取消收藏
        boolean success = usersService.removeCollection(userId, pid);
        return success ? Result.ok(true).message("取消收藏成功") : Result.fail(false).message("取消收藏失败");
    }

    /**
     * 获取用户收藏列表
     * 查询当前用户收藏的所有商品信息
     *
     * @return 收藏列表
     */
    @Operation(summary = "分页获取用户收藏列表", description = "查询当前用户收藏的所有商品信息")
    @GetMapping("/collection/list")
    public Result<List<Commodity>> listCollection() {

        // 获取当前登录用户ID（注：此处为示例写死，实际应使用下方注释的动态获取方式）
         String uid=getUsernameFromSecurityContext();

        // 查询收藏列表
        List<User_collection> collections = usersService.listByUserId(uid);
        List<Commodity> resutls = new ArrayList<>();
        //根据id查询
        for(User_collection userCollection:collections){
          resutls.add(commodityService.getById(userCollection.getPid()));
        }
        return Result.ok(resutls);
    }
}