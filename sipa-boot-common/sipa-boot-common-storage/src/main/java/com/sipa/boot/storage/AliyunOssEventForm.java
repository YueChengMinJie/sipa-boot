package com.sipa.boot.storage;

import java.util.List;

import lombok.Data;

/**
 * @author guozhipeng
 * @date 2023/6/27 16:58 Version: 1.0
 */
@Data
public class AliyunOssEventForm {
    /**
     * 事件列表
     */
    private List<Event> events;

    @Data
    public static class Event {
        /**
         * 事件类型
         */
        private String eventName;

        /**
         * 事件源
         */
        private String eventSource;

        /**
         * 事件产生的时间 ISO 8601时间格式 如 2023-06-27T09:50:44.000Z
         */
        private String eventTime;

        /**
         * 事件协议的版本
         */
        private String eventVersion;

        /**
         * OSS事件内容
         */
        private Oss oss;

        /**
         * Bucket所在的地域
         */
        private String region;

        /**
         * 请求参数值
         */
        private RequestParameter requestParameters;

        /**
         * 返回参数值
         */
        private ResponseElement responseElements;

        /**
         * 用户信息
         */
        private UserIdentity userIdentity;

        /**
         * file组成要素 不包含etag 与po一致
         */
        @Data
        public static class Oss {
            /**
             * bucket参数内容
             */
            private Bucket bucket;

            /**
             * Object参数内容
             */
            private OssObject object;

            /**
             * OSS模式的版本号
             */
            private String ossSchemaVersion;

            /**
             * 事件匹配的规则ID
             */
            private String ruleId;

            @Data
            public static class Bucket {
                /**
                 * Bucket的唯一标识符
                 */
                private String arn;

                /**
                 * Bucket的名称
                 */
                private String name;

                /**
                 * 创建Bucket的用户ID
                 */
                private String ownerIdentity;

                /**
                 * 虚拟桶
                 */
                private String virtualBucket;
            }

            @Data
            public static class OssObject {
                /**
                 * Object的大小变化量
                 */
                private int deltaSize;

                /**
                 * Object的内容。对于Put Object请求创建的Object，ETag值是其内容的MD5值；对于其他方式创建的Object，
                 * ETag值是基于一定计算规则生成的唯一值，但不是其内容的MD5值。ETag值可以用于检查Object内容是否发生变化。
                 */
                private String eTag;

                /**
                 * Object的名称
                 */
                private String key;

                /**
                 * 文件开始读取的位置
                 */
                private int readFrom;

                /**
                 * 文件最后读取的位置
                 */
                private int readTo;

                /**
                 * Object的大小
                 */
                private int size;
            }
        }

        @Data
        public static class RequestParameter {
            /**
             * 请求的源IP
             */
            private String sourceIPAddress;
        }

        @Data
        public static class ResponseElement {
            /**
             * 请求对应的Request ID
             */
            private String requestId;
        }

        @Data
        public static class UserIdentity {
            /**
             * 请求发起者的UID
             */
            private String principalId;
        }
    }
}
