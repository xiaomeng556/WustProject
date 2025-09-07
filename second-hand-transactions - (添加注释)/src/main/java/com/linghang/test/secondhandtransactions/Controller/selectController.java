package com.linghang.test.secondhandtransactions.Controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.linghang.test.secondhandtransactions.Service.CommodityService;
import com.linghang.test.secondhandtransactions.utils.DebounceUtil;
import com.linghang.test.secondhandtransactions.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import static com.linghang.test.secondhandtransactions.utils.UidUtils.getUsernameFromSecurityContext;

/**
 * 商品查询控制器
 * 负责处理商品的各种查询请求，包括按名称、介绍模糊查询和按类型精确查询
 * 支持分页查询和防频繁查询（防抖）功能
 */
@RestController
@RequestMapping("/selectController")
@Tag(name = "商品查询接口", description = "提供商品搜索和筛选功能，支持分页和条件查询")
public class selectController {
    /**
     * 注入商品服务层对象
     * 用于执行商品查询的数据库操作
     */
    @Resource
    private CommodityService commodityService;
    /**
     * 查询接口防抖超时时间设置为3秒
     * 防止用户在短时间内频繁发起查询请求，减轻服务器压力
     */
    private static final long QUERY_DEBOUNCE_TIMEOUT = 1000;
    /**
     * 按商品名称和介绍内容进行模糊查询
     * 支持分页展示查询结果，结果按发布时间倒序排列（最新发布在前）
     *
     * @param pn       当前页码，从前端路径参数获取
     * @param pageSize 每页显示的记录数，从前端路径参数获取
     * @param txt     包含查询条件的表单对象，包含商品名称和介绍的关键字
     * @return 封装查询结果的Result对象，成功时返回分页数据，失败时返回错误信息
     */
    @Operation(
            summary = "按名称和介绍模糊查询商品",
            description = "根据商品名称和介绍内容进行模糊搜索，支持分页，结果按发布时间倒序排列",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "查询成功",
                            content = @Content(schema = @Schema(implementation = Result.class))
                    )
            }
    )
    @GetMapping("/byNameAndIntroduce/{type}/{status}/{pn}/{pageSize}")
    public Result<Object> selectByNameAndContact(
            @Parameter(description = "当前页码，从1开始", required = true, in = ParameterIn.PATH)
            @PathVariable("pn") Integer pn,

            @Parameter(description = "每页显示的记录数量，如10、20", required = true, in = ParameterIn.PATH)
            @PathVariable("pageSize") Integer pageSize,
            @Parameter(description = "商品类型", required = true, in = ParameterIn.PATH)
            @PathVariable("type") Integer type,
            @Parameter(description = "商品状态", required = true, in = ParameterIn.PATH)
            @PathVariable("status") Integer status,
            @Parameter(description = "查询条件表单，包含name（商品名称关键字）和introduce（介绍内容键字）", required = true)
            @RequestParam String txt
    ) {
        // 从查询表单中提取商品名称和介绍的关键字
        // 获取当前登录用户的唯一标识（用于防抖检查）
        String uid = getUsernameFromSecurityContext();
        // 防抖检查：判断用户是否在3秒内重复发起相同查询
        // 若频繁查询则返回提示信息，防止恶意请求
        if (DebounceUtil.shouldDebounce(uid, "query_by_name_introduce", QUERY_DEBOUNCE_TIMEOUT)) {
            return Result.fail("查询过于频繁，请稍后再试");
        }
        if (type < 0 && status < 0) {
            // 执行分页查询逻辑
            // 1. 创建分页对象，指定当前页码和每页大小
            // 2. 使用MyBatis-Plus的LambdaQueryWrapper构建查询条件：
            //    - 若名称关键字不为空，则对商品名称进行模糊匹配
            //    - 若介绍关键字不为空，则对商品介绍进行模糊匹配
            //    - 最终结果按发布时间（date字段）倒序排列
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            // 使用or()将两个like条件改为OR关系
                            .and(StrUtil.isNotBlank(txt), wrapper -> wrapper
                                    .like(Commodity::getName, txt)
                                    .or()
                                    .like(Commodity::getIntroduce, txt)
                            )
                            .orderByDesc(Commodity::getDate)
            );
            // 将查询到的分页数据封装到Result对象中返回
            System.out.println("总条数：" + page.getTotal()); // 应为1
            System.out.println("当前页码：" + page.getCurrent()); // 检查是否>1
            return Result.ok(page);
        } else if (type < 0 && status >= 0) {
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            .eq(Commodity::getStatus, status)
                            // 使用or()将两个like条件改为OR关系
                            .and(StrUtil.isNotBlank(txt), wrapper -> wrapper
                                    .like(Commodity::getName, txt)
                                    .or()
                                    .like(Commodity::getIntroduce, txt)
                            )
                            .orderByDesc(Commodity::getDate)
            );
            return Result.ok(page);
        } else if (type >= 0 && status < 0) {
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            .eq(Commodity::getType, type)
                            // 使用or()将两个like条件改为OR关系
                            .and(StrUtil.isNotBlank(txt), wrapper -> wrapper
                                    .like(Commodity::getName, txt)
                                    .or()
                                    .like(Commodity::getIntroduce, txt)
                            )
                            .orderByDesc(Commodity::getDate)
            );
            return Result.ok(page);
        } else {
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            .eq(Commodity::getType, type)
                            .eq(Commodity::getStatus, status)
                            // 使用or()将两个like条件改为OR关系
                            .and(StrUtil.isNotBlank(txt), wrapper -> wrapper
                                    .like(Commodity::getName, txt)
                                    .or()
                                    .like(Commodity::getIntroduce, txt)
                            )
                            .orderByDesc(Commodity::getDate)
            );
            return Result.ok(page);
        }
    }
    /**
     * 按商品类型进行精确查询
     * 支持分页展示查询结果，结果按发布时间倒序排列（最新发布在前）
     *
     * @param pn       当前页码，从前端路径参数获取
     * @param pageSize 每页显示的记录数，从前端路径参数获取
     * @param type     商品类型（如：电子产品、图书、服饰等），作为查询条件
     * @return 封装查询结果的Result对象，成功时返回分页数据，失败时返回错误信息
     */
    @Operation(
            summary = "按类型查询商品",
            description = "根据商品类型进行精确筛选，支持分页，结果按发布时间倒序排列",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "查询成功",
                            content = @Content(schema = @Schema(implementation = Result.class))
                    )
            }
    )
    @GetMapping("/byTypeOrStatus/{pn}/{pageSize}")
    public Result<Object> selectByType(
            @Parameter(description = "当前页码，从1开始", required = true, in = ParameterIn.PATH)
            @PathVariable("pn") Integer pn,

            @Parameter(description = "每页显示的记录数量，如10、20", required = true, in = ParameterIn.PATH)
            @PathVariable("pageSize") Integer pageSize,

            @Parameter(description = "商品类型（如：'electronics'、'books'等）", required = true)
            @RequestParam Integer type, Integer status
    ) {
        // 获取当前登录用户的唯一标识（用于防抖检查）
        String uid = getUsernameFromSecurityContext();
        // 防抖检查：判断用户是否在3秒内重复发起相同查询
        if (DebounceUtil.shouldDebounce(uid, "query_by_type", QUERY_DEBOUNCE_TIMEOUT)) {
            return Result.fail("查询过于频繁，请稍后再试");
        }
        if(type<-1&&status<-1){
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            .orderByDesc(Commodity::getDate)
            );
            return Result.ok(page);
        } else if (type<0&&status>=0) {
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            .eq(Commodity::getStatus, status)
                            .orderByDesc(Commodity::getDate)
            );
            return Result.ok(page);
        }else if (type>=0&&status<0) {
            Page<Commodity> page = commodityService.page(
                    new Page<>(pn, pageSize),
                    new LambdaQueryWrapper<Commodity>()
                            .eq(Commodity::getType, type)
                            .orderByDesc(Commodity::getDate)
            );
            return Result.ok(page);
        }else {
        // 执行分页查询逻辑
        // 1. 创建分页对象，指定当前页码和每页大小
        // 2. 使用MyBatis-Plus的LambdaQueryWrapper构建查询条件：
        //    - 精确匹配商品类型（type字段）
        //    - 最终结果按发布时间（date字段）倒序排列
        Page<Commodity> page = commodityService.page(
                new Page<>(pn, pageSize),
                new LambdaQueryWrapper<Commodity>()
                        .eq(Commodity::getType, type)
                        .eq(Commodity::getStatus, status)
                        .orderByDesc(Commodity::getDate)
        );
        // 将查询到的分页数据封装到Result对象中返回
        return Result.ok(page);
    }}
}
