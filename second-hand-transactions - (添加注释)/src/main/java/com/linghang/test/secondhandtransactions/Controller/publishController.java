package com.linghang.test.secondhandtransactions.Controller;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import com.linghang.test.secondhandtransactions.Entity.Commodity;
import com.linghang.test.secondhandtransactions.Service.CommodityService;
import com.linghang.test.secondhandtransactions.common.PublishForm;
import com.linghang.test.secondhandtransactions.utils.DebounceUtil;
import com.linghang.test.secondhandtransactions.utils.Result;
import com.linghang.test.secondhandtransactions.utils.UidUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 商品发布控制器
 * 负责处理商品发布相关的HTTP请求，包含防抖、敏感词过滤和数据持久化功能
 */
@Tag(name = "商品发布接口", description = "提供二手商品发布功能，包含防重复提交和敏感词校验")
@RestController
@RequestMapping("/publishController")
public class publishController {
    // 注入商品服务层对象，用于商品信息的持久化操作
    @Resource
    private CommodityService commodityService;

    // 防抖超时时间设置为5秒，防止用户短时间内重复发布
    private static final long DEBOUNCE_TIMEOUT = 1000;

    /**
     * 处理商品发布请求
     * 包含用户身份验证、防抖检查、敏感词过滤和商品信息保存功能
     *
     * @param form 发布表单，包含商品名称、价格、联系方式等信息
     * @return 发布结果，成功返回"发布成功"，失败返回具体原因
     */
    @Operation(summary = "发布二手商品", description = "用户发布新的二手商品信息，需进行敏感词校验和防重复提交检查")
    @PostMapping("/publish") // 使用POST方法符合资源创建的语义规范
    public Result<String> publish(@RequestBody PublishForm form) {
        // 1. 获取当前登录用户的唯一标识（从安全上下文获取）
        String uid = UidUtils.getUsernameFromSecurityContext();

        // 2. 防抖检查：基于用户ID和操作标识，防止5秒内重复提交
        if (DebounceUtil.shouldDebounce(uid, "publish_commodity", DEBOUNCE_TIMEOUT)) {
            return Result.fail("操作过于频繁，请稍后再试");
        }

        // 3. 敏感词过滤检查：分别校验联系方式、商品名和介绍内容
        // 校验联系方式中的敏感词
//        String contact = form.getContact();
//        if (SensitiveWordHelper.contains(contact)) {
//            return Result.fail("联系方式包含敏感信息，请重新输入");
//        }

        // 校验商品名称中的敏感词
        String commodityName = form.getName();
        if (SensitiveWordHelper.contains(commodityName)) {
            return Result.fail("商品名包含敏感信息，请重新输入");
        }

        // 校验商品介绍中的敏感词
        String introduction = form.getIntroduce();
        if (SensitiveWordHelper.contains(introduction)) {
            return Result.fail("介绍内容包含敏感信息，请重新输入");
        }

        // 4. 执行发布操作：将表单数据转换为商品实体并保存到数据库
        try {
            commodityService.save(new Commodity(
                    uid,                // 发布者ID
                    form.getName(),     // 商品名称
                    form.getPrice(),    // 商品价格
                    form.getDate(),     // 发布日期
                    form.getContact(),  // 联系方式
                    form.getStatus(),   // 商品状态（如：在售、已售出）
                    form.getType(),     // 商品分类
                    form.getIntroduce(),// 商品介绍
                    form.getImagePath() // 商品图片路径
            ));

            return Result.ok("发布成功");
        } catch (Exception e) {
            // 捕获保存过程中的异常，返回具体错误信息
            return Result.fail("发布失败：" + e.getMessage());
        }
    }
}
