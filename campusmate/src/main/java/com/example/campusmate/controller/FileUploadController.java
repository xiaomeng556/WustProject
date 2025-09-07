package com.example.campusmate.controller;

import com.example.campusmate.dto.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import org.springframework.beans.factory.annotation.Value;
import java.nio.file.Paths;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * 文件上传控制器
 * 处理文件上传相关的API请求，包括接收上传的文件并保存到服务器指定位置。
 */
@RestController
@RequestMapping("/api")
@Tag(name = "文件上传", description = "处理文件上传相关的API请求，包括接收上传的文件并保存到服务器指定位置。")
public class FileUploadController {

    // 从配置文件读取上传目录（推荐，方便部署时修改）
    @Value("${file.upload-path}")
    private String uploadPath;

    /**
     * 上传文件接口
     * 该方法接收客户端上传的文件，保存到服务器指定的目录，并返回文件的访问URL。
     *
     * @param file 要上传的文件
     * @return ApiResponse<String> 返回文件的访问URL，如果上传成功
     */
    @Operation(summary = "上传文件", description = "接收客户端上传的文件，保存到服务器指定目录，并返回文件的访问URL。")
    @PostMapping("/upload")
    public ApiResponse<String> upload(
            @Parameter(description = "上传的文件") @RequestParam("file") MultipartFile file) {
        // 检查上传的文件是否为空
        if (file.isEmpty()) {
            return ApiResponse.fail("文件为空"); // 如果文件为空，返回错误
        }

        try {
            // 1. 生成文件名（避免重复）
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            // 2. 构建完整的上传目录和文件路径
            File uploadDir = new File(uploadPath);

            // 确保上传目录存在（如果目录不存在，则创建）
            if (!uploadDir.exists()) {
                boolean isDirCreated = uploadDir.mkdirs();
                if (!isDirCreated) {
                    return ApiResponse.fail("创建上传目录失败，请检查权限"); // 如果目录创建失败，返回错误
                }
            }

            // 3. 拼接文件路径（确保平台兼容性）
            File destFile = Paths.get(uploadDir.getAbsolutePath(), fileName).toFile();

            // 4. 保存文件到指定路径
            file.transferTo(destFile);

            // 5. 返回前端可访问的 URL（假设已配置静态资源映射）
            String accessUrl = "/uploads/" + fileName;
            return ApiResponse.success(accessUrl); // 返回文件的访问URL

        } catch (Exception e) {
            e.printStackTrace(); // 输出异常信息
            return ApiResponse.fail("上传失败: " + e.getMessage()); // 返回失败信息
        }
    }
}
