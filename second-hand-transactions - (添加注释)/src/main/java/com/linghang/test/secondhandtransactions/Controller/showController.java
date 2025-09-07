package com.linghang.test.secondhandtransactions.Controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.linghang.test.secondhandtransactions.Entity.Users;
import com.linghang.test.secondhandtransactions.Service.CommodityService;
import com.linghang.test.secondhandtransactions.Service.UsersService;
import com.linghang.test.secondhandtransactions.utils.DebounceUtil;
import com.linghang.test.secondhandtransactions.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static com.linghang.test.secondhandtransactions.utils.UidUtils.getUsernameFromSecurityContext;


/**
 * 商品展示控制器
 * 负责处理商品查询、筛选等前端请求
 */
@Tag(name = "商品查询接口", description = "提供商品搜索和筛选功能，支持分页和条件查询")
@RestController
@RequestMapping("/showController")
public class showController {

    @Resource
    private CommodityService productService;
    @Resource
    private UsersService usersService;
    // 筛选接口防抖超时时间设置为2秒，防止频繁请求
    private static final long FILTER_DEBOUNCE_TIMEOUT = 1000;

    /**
     * 获取所有商品（分页）
     *
     * @param current 当前页码
     * @param size 每页显示数量
     * @return 分页的商品列表
     */
    @Operation(summary = "获取所有商品", description = "分页查询系统中所有的商品信息")
    @GetMapping("/all/{current}/{size}")//获取所有商品接口
    public Page<Commodity> getAllProducts(@PathVariable long current, @PathVariable long size) {
        String uid=getUsernameFromSecurityContext();
        if(usersService.findUid(uid)){
            usersService.updateById(new Users(uid));
        }else {
            usersService.save(new Users(uid));
        }

        return productService.getAllProducts(current, size);
    }
    /**
     * 按类别筛选商品（带防抖处理）
     *
     * @param category 商品类别
     * @param current 当前页码
     * @param size 每页显示数量
     * @return 分页的商品列表结果
     */
    @Operation(summary = "按类别筛选商品", description = "根据商品类别进行分页筛选，包含防抖处理防止频繁请求")
    @GetMapping("/category/{category}/{current}/{size}")
    public Result<Page<Commodity>> getProductsByCategory(
            @Parameter(description = "商品类别", required = true) @PathVariable String category,
            @Parameter(description = "当前页码", required = true) @PathVariable long current,
            @Parameter(description = "每页显示数量", required = true) @PathVariable long size) {
        // 获取当前用户标识，用于防抖处理
        String uid = getUsernameFromSecurityContext();

        // 防抖检查，若在指定时间内频繁请求则返回提示
        if (DebounceUtil.shouldDebounce(uid, "filter_by_category", FILTER_DEBOUNCE_TIMEOUT)) {
            return Result.<Page<Commodity>>fail().message("筛选过于频繁，请稍后再试");
        }

        // 执行筛选逻辑，返回分页结果
        Page<Commodity> result = productService.getProductsByCategory(category, current, size);
        return Result.ok(result);
    }
}