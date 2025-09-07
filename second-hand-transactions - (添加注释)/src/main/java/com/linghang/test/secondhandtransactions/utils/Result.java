package com.linghang.test.secondhandtransactions.utils;

/**
 * ClassName: Result
 * Package: com.atguigu.campus.utils
 * Description:
 *
 * @author ziqiu
 * @Create: 2023/2/4 - 16:15  16:15
 * @Version: v1.0
 */


import com.linghang.test.secondhandtransactions.utils.ResultCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 全局统一返回结果类
 */
@Data
@Schema(name = "GlobalResult", description = "全局统一返回结果")
public class Result<T> {

    @Schema(description = "返回码", example = "200")
    private Integer code;

    @Schema(description = "返回消息", example = "操作成功")
    private String message;

    @Schema(description = "返回数据")
    private T data;

    public Result() {}

    // 返回数据
    protected static <T> Result<T> build(T data) {
        Result<T> result = new Result<>();
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    public static <T> Result<T> build(T body, ResultCodeEnum resultCodeEnum) {
        Result<T> result = build(body);
        result.setCode(resultCodeEnum.getCode());
        result.setMessage(resultCodeEnum.getMessage());
        return result;
    }

    public static <T> Result<T> ok() {
        return Result.ok(null);
    }

    /**
     * 操作成功
     * @param data 返回数据
     * @param <T> 数据类型
     * @return 统一返回结果
     */
    public static <T> Result<T> ok(T data) {
        return build(data, ResultCodeEnum.SUCCESS);
    }

    public static <T> Result<T> fail() {
        return Result.fail(null);
    }

    /**
     * 操作失败
     * @param data 返回数据
     * @param <T> 数据类型
     * @return 统一返回结果
     */
    public static <T> Result<T> fail(T data) {
        return build(data, ResultCodeEnum.FAIL);
    }

    public Result<T> message(String msg) {
        this.setMessage(msg);
        return this;
    }

    public Result<T> code(Integer code) {
        this.setCode(code);
        return this;
    }

    public boolean isOk() {
        return this.getCode() != null && this.getCode().equals(ResultCodeEnum.SUCCESS.getCode());
    }
}
