package com.linghang.test.secondhandtransactions.utils;

import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.linghang.test.secondhandtransactions.configure.TencentTmsProperties;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.tms.v20200713.TmsClient;
import com.tencentcloudapi.tms.v20200713.models.TextModerationRequest;
import com.tencentcloudapi.tms.v20200713.models.TextModerationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import java.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
@Slf4j
@Component
public class TencentTmsUtils {
    @Autowired
    private TencentTmsProperties tencentTmsProperties;

    /**
     * 敏感词过滤
     *
     * @param content
     * @return
     */
    public Boolean getTmsResult(String content) {
        if (StringUtils.isEmpty(content) || StringUtils.isEmpty(content.trim())) {
            return Boolean.TRUE;
        }

        Credential cre = new Credential(tencentTmsProperties.getSecretId(), tencentTmsProperties.getSecretkey());
        TmsClient client = new TmsClient(cre, "ap-shanghai");
        TextModerationRequest request = new TextModerationRequest();
        request.setBizType(tencentTmsProperties.getBizType());

        // 修复Base64编码
        try {
            String base64Content = Base64.getEncoder().encodeToString(content.getBytes("UTF-8"));
            request.setContent(base64Content);

            TextModerationResponse response = client.TextModeration(request);
            if (!ObjectUtils.isEmpty(response)) {
                String suggestion = response.getSuggestion();
                String label = response.getLabel();
                log.info("敏感词类型：" + label + " 处理结果：" + suggestion);
                return "Pass".equals(suggestion);
            }
        } catch (UnsupportedEncodingException | TencentCloudSDKException e) {
            log.error("Sensitive word detection failed, because: ", e);
        }

        // 增加日志记录，明确返回false的原因
        log.warn("内容检测失败，默认返回非敏感，内容: {}", content);
        return Boolean.FALSE;
    }
}