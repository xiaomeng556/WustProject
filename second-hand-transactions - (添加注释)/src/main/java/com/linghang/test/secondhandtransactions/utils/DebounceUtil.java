package com.linghang.test.secondhandtransactions.utils;

    import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

    /**
     * 接口防抖工具类
     */
    public class DebounceUtil {
        // 存储每个用户对每个接口的最后请求时间
        private static final ConcurrentHashMap<String, Long> REQUEST_TIMESTAMPS = new ConcurrentHashMap<>();

        /**
         * 检查是否需要防抖
         * @param userId 用户ID
         * @param action 操作标识（如接口名）
         * @param timeout 超时时间(毫秒)
         * @return true-需要防抖(重复请求)，false-正常请求
         */
        public static boolean shouldDebounce(String userId, String action, long timeout) {
            // 组合键：用户ID+操作，确保不同用户间不互相影响
            String key = userId + ":" + action;
            long currentTime = System.currentTimeMillis();
            Long lastTime = REQUEST_TIMESTAMPS.get(key);

            // 如果存在上次请求且在有效期内，则需要防抖
            if (lastTime != null && currentTime - lastTime < timeout) {
                return true;
            }

            // 更新最后请求时间
            REQUEST_TIMESTAMPS.put(key, currentTime);
            return false;
        }

        /**
         * 检查是否需要防抖（默认5秒超时）
         */
        public static boolean shouldDebounce(String userId, String action) {
            return shouldDebounce(userId, action, TimeUnit.SECONDS.toMillis(5));
        }
    }


