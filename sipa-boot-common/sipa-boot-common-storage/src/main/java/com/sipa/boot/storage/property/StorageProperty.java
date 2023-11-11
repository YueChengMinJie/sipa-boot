package com.sipa.boot.storage.property;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Data;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Data
@ConfigurationProperties(prefix = SipaBootConstant.Storage.PREFIX)
public class StorageProperty {
    /**
     * 默认存储平台，默认为阿里云
     */
    private String defaultPlatform = "minio";

    /**
     * 默认存储桶，默认为dev
     */
    private String defaultBucket = "dev";

    /**
     * 缩略图后缀，例如【.min.jpg】【.png】，默认为.min.jpg
     */
    private String thumbnailSuffix = ".min.jpg";

    /**
     * 本地存储
     */
    private List<Local> local = new ArrayList<>();

    /**
     * 本地存储plus
     */
    private List<LocalPlus> localPlus = new ArrayList<>();

    /**
     * 华为云 OBS
     */
    private List<HuaweiObs> huaweiObs = new ArrayList<>();

    /**
     * 阿里云 OSS
     */
    private List<AliyunOss> aliyunOss = new ArrayList<>();

    /**
     * 七牛云 Kodo
     */
    private List<QiniuKodo> qiniuKodo = new ArrayList<>();

    /**
     * 腾讯云 COS
     */
    private List<TencentCos> tencentCos = new ArrayList<>();

    /**
     * 百度云 BOS
     */
    private List<BaiduBos> baiduBos = new ArrayList<>();

    /**
     * 又拍云 USS
     */
    private List<UpyunUss> upyunUss = new ArrayList<>();

    /**
     * MinIO USS
     */
    private List<MinIo> minio = new ArrayList<>();

    /**
     * AWS S3
     */
    private List<AwsS3> awsS3 = new ArrayList<>();

    /**
     * FTP
     */
    private List<Ftp> ftp = new ArrayList<>();

    /**
     * SFTP
     */
    private List<Sftp> sftp = new ArrayList<>();

    /**
     * WebDAV
     */
    private List<WebDav> webDav = new ArrayList<>();

    /**
     * 谷歌云存储
     */
    private List<GoogleCloud> googleCloud = new ArrayList<>();

    /**
     * 本地存储
     */
    @Data
    public static class Local {
        /**
         * 本地存储路径
         */
        private String basePath = "";

        /**
         * 本地存储访问路径
         */
        private String[] pathPatterns = new String[0];

        /**
         * 启用本地存储
         */
        private Boolean enableStorage = false;

        /**
         * 启用本地访问
         */
        private Boolean enableAccess = false;

        /**
         * 存储平台
         */
        private String platform = "local";

        /**
         * 访问域名
         */
        private String domain = "";
    }

    /**
     * 本地存储升级版
     */
    @Data
    public static class LocalPlus {
        /**
         * 基础路径
         */
        private String basePath = "";

        /**
         * 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾
         */
        private String storagePath = "/";

        /**
         * 本地存储访问路径
         */
        private String[] pathPatterns = new String[0];

        /**
         * 启用本地存储
         */
        private Boolean enableStorage = false;

        /**
         * 启用本地访问
         */
        private Boolean enableAccess = false;

        /**
         * 存储平台
         */
        private String platform = "local";

        /**
         * 访问域名
         */
        private String domain = "";
    }

    /**
     * 华为云 OBS
     */
    @Data
    public static class HuaweiObs {
        /**
         * Access key
         */
        private String accessKey;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * 阿里云 OSS
     */
    @Data
    public static class AliyunOss {
        /**
         * Access key
         */
        private String accessKey;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "aliyun";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * 七牛云 Kodo
     */
    @Data
    public static class QiniuKodo {
        /**
         * Access key
         */
        private String accessKey;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * 腾讯云 COS
     */
    @Data
    public static class TencentCos {
        /**
         * Secret id
         */
        private String secretId;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 区域
         */
        private String region;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * 百度云 BOS
     */
    @Data
    public static class BaiduBos {
        /**
         * Access key
         */
        private String accessKey;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * 又拍云 USS
     */
    @Data
    public static class UpyunUss {
        /**
         * 用户名
         */
        private String username;

        /**
         * 密码
         */
        private String password;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * MinIO
     */
    @Data
    public static class MinIo {
        /**
         * Access key
         */
        private String accessKey;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * AWS S3
     */
    @Data
    public static class AwsS3 {
        /**
         * Access key
         */
        private String accessKey;

        /**
         * Secret key
         */
        private String secretKey;

        /**
         * 端点
         */
        private String endpoint;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 区域
         */
        private String region;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }

    /**
     * FTP
     */
    @Data
    public static class Ftp {
        /**
         * 主机
         */
        private String host;

        /**
         * 端口，默认21
         */
        private int port = 21;

        /**
         * 用户名，默认 anonymous（匿名）
         */
        private String user = "anonymous";

        /**
         * 密码，默认空
         */
        private String password = "";

        /**
         * 编码，默认UTF-8
         */
        private Charset charset = StandardCharsets.UTF_8;

        /**
         * 连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setConnectTimeout(int)}
         */
        private long connectionTimeout = 10 * 1000;

        /**
         * Socket连接超时时长，单位毫秒，默认10秒 {@link org.apache.commons.net.SocketClient#setSoTimeout(int)}
         */
        private long soTimeout = 10 * 1000;

        /**
         * 设置服务器语言，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#setServerLanguageCode(String)}
         */
        private String serverLanguageCode;

        /**
         * 服务器标识，默认空，{@link org.apache.commons.net.ftp.FTPClientConfig#FTPClientConfig(String)}
         * 例如：org.apache.commons.net.ftp.FTPClientConfig.SYST_NT
         */
        private String systemKey;

        /**
         * 是否主动模式，默认被动模式
         */
        private Boolean isActive = false;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";

        /**
         * 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾
         */
        private String storagePath = "/";
    }

    /**
     * SFTP
     */
    @Data
    public static class Sftp {
        /**
         * 主机
         */
        private String host;

        /**
         * 端口，默认22
         */
        private int port = 22;

        /**
         * 用户名
         */
        private String user;

        /**
         * 密码
         */
        private String password;

        /**
         * 私钥路径
         */
        private String privateKeyPath;

        /**
         * 编码，默认UTF-8
         */
        private Charset charset = StandardCharsets.UTF_8;

        /**
         * 连接超时时长，单位毫秒，默认10秒
         */
        private long connectionTimeout = 10 * 1000;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";

        /**
         * 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾
         */
        private String storagePath = "/";
    }

    /**
     * WebDAV
     */
    @Data
    public static class WebDav {
        /**
         * 服务器地址，注意“/”结尾，例如：<a href="http://192.168.1.105:8405/"/>
         */
        private String server;

        /**
         * 用户名
         */
        private String user;

        /**
         * 密码
         */
        private String password;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";

        /**
         * 存储路径，上传的文件都会存储在这个路径下面，默认“/”，注意“/”结尾
         */
        private String storagePath = "/";
    }

    @Data
    public static class GoogleCloud {
        /**
         * Project id
         */
        private String projectId;

        /**
         * 证书路径，兼容Spring的ClassPath路径、文件路径、HTTP路径等
         */
        private String credentialsPath;

        /**
         * 桶名
         */
        private String bucketName;

        /**
         * 访问域名
         */
        private String domain = "";

        /**
         * 启用存储
         */
        private Boolean enableStorage = false;

        /**
         * 存储平台
         */
        private String platform = "";

        /**
         * 基础路径
         */
        private String basePath = "";
    }
}
