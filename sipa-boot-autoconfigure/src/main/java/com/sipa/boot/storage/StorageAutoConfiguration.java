package com.sipa.boot.storage;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.sipa.boot.storage.aspect.FileStorageAspect;
import com.sipa.boot.storage.platform.*;
import com.sipa.boot.storage.property.StorageProperty;
import com.sipa.boot.storage.recorder.DefaultFileRecorder;
import com.sipa.boot.storage.recorder.FileRecorder;
import com.sipa.boot.storage.service.FileStorageService;
import com.sipa.boot.storage.tika.DefaultTikaFactory;
import com.sipa.boot.storage.tika.TikaFactory;

import cn.hutool.core.collection.CollUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author caszhou
 * @date 2022/12/23
 */
@Slf4j
@Configuration
@AllArgsConstructor
@ConditionalOnClass(StorageProperty.class)
@EnableConfigurationProperties(StorageProperty.class)
public class StorageAutoConfiguration implements WebMvcConfigurer {
    private final StorageProperty property;

    private final ApplicationContext applicationContext;

    /**
     * 配置本地存储的访问地址
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        for (StorageProperty.Local local : this.property.getLocal()) {
            if (local.getEnableAccess()) {
                registry.addResourceHandler(local.getPathPatterns())
                    .addResourceLocations("file:" + local.getBasePath());
            }
        }
        for (StorageProperty.LocalPlus local : this.property.getLocalPlus()) {
            if (local.getEnableAccess()) {
                registry.addResourceHandler(local.getPathPatterns())
                    .addResourceLocations("file:" + local.getStoragePath());
            }
        }
    }

    /**
     * 本地存储 Bean
     */
    @Bean
    public List<LocalFileStorage> localFileStorageList() {
        return this.property.getLocal().stream().map(local -> {
            if (!local.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", local.getPlatform());
            LocalFileStorage localFileStorage = new LocalFileStorage();
            localFileStorage.setPlatform(local.getPlatform());
            localFileStorage.setBasePath(local.getBasePath());
            localFileStorage.setDomain(local.getDomain());
            return localFileStorage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 本地存储升级版 Bean
     */
    @Bean
    public List<LocalPlusFileStorage> localPlusFileStorageList() {
        return this.property.getLocalPlus().stream().map(local -> {
            if (!local.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", local.getPlatform());
            LocalPlusFileStorage localFileStorage = new LocalPlusFileStorage();
            localFileStorage.setPlatform(local.getPlatform());
            localFileStorage.setBasePath(local.getBasePath());
            localFileStorage.setDomain(local.getDomain());
            localFileStorage.setStoragePath(local.getStoragePath());
            return localFileStorage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 华为云 OBS 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.obs.services.ObsClient")
    public List<HuaweiObsFileStorage> huaweiObsFileStorageList() {
        return this.property.getHuaweiObs().stream().map(obs -> {
            if (!obs.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", obs.getPlatform());
            HuaweiObsFileStorage storage = new HuaweiObsFileStorage();
            storage.setPlatform(obs.getPlatform());
            storage.setAccessKey(obs.getAccessKey());
            storage.setSecretKey(obs.getSecretKey());
            storage.setEndPoint(obs.getEndpoint());
            storage.setBucketName(obs.getBucketName());
            storage.setDomain(obs.getDomain());
            storage.setBasePath(obs.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 阿里云 OSS 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.aliyun.oss.OSS")
    public List<AliyunOssFileStorage> aliyunOssFileStorageList() {
        return this.property.getAliyunOss().stream().map(oss -> {
            if (!oss.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", oss.getPlatform());
            AliyunOssFileStorage storage = new AliyunOssFileStorage();
            storage.setPlatform(oss.getPlatform());
            storage.setAccessKey(oss.getAccessKey());
            storage.setSecretKey(oss.getSecretKey());
            storage.setEndPoint(oss.getEndpoint());
            storage.setBucketName(oss.getBucketName());
            storage.setDomain(oss.getDomain());
            storage.setBasePath(oss.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 七牛云 Kodo 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.qiniu.storage.UploadManager")
    public List<QiniuKodoFileStorage> qiniuKodoFileStorageList() {
        return this.property.getQiniuKodo().stream().map(kodo -> {
            if (!kodo.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", kodo.getPlatform());
            QiniuKodoFileStorage storage = new QiniuKodoFileStorage();
            storage.setPlatform(kodo.getPlatform());
            storage.setAccessKey(kodo.getAccessKey());
            storage.setSecretKey(kodo.getSecretKey());
            storage.setBucketName(kodo.getBucketName());
            storage.setDomain(kodo.getDomain());
            storage.setBasePath(kodo.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 腾讯云 COS 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.qcloud.cos.COSClient")
    public List<TencentCosFileStorage> tencentCosFileStorageList() {
        return this.property.getTencentCos().stream().map(cos -> {
            if (!cos.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", cos.getPlatform());
            TencentCosFileStorage storage = new TencentCosFileStorage();
            storage.setPlatform(cos.getPlatform());
            storage.setSecretId(cos.getSecretId());
            storage.setSecretKey(cos.getSecretKey());
            storage.setRegion(cos.getRegion());
            storage.setBucketName(cos.getBucketName());
            storage.setDomain(cos.getDomain());
            storage.setBasePath(cos.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 百度云 BOS 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.baidubce.services.bos.BosClient")
    public List<BaiduBosFileStorage> baiduBosFileStorageList() {
        return this.property.getBaiduBos().stream().map(bos -> {
            if (!bos.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", bos.getPlatform());
            BaiduBosFileStorage storage = new BaiduBosFileStorage();
            storage.setPlatform(bos.getPlatform());
            storage.setAccessKey(bos.getAccessKey());
            storage.setSecretKey(bos.getSecretKey());
            storage.setEndPoint(bos.getEndpoint());
            storage.setBucketName(bos.getBucketName());
            storage.setDomain(bos.getDomain());
            storage.setBasePath(bos.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 又拍云 USS 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.upyun.RestManager")
    public List<UpyunUssFileStorage> upyunUssFileStorageList() {
        return this.property.getUpyunUss().stream().map(uss -> {
            if (!uss.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", uss.getPlatform());
            UpyunUssFileStorage storage = new UpyunUssFileStorage();
            storage.setPlatform(uss.getPlatform());
            storage.setUsername(uss.getUsername());
            storage.setPassword(uss.getPassword());
            storage.setBucketName(uss.getBucketName());
            storage.setDomain(uss.getDomain());
            storage.setBasePath(uss.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * MinIO 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "io.minio.MinioClient")
    public List<MinIoFileStorage> minioFileStorageList() {
        return this.property.getMinio().stream().map(minio -> {
            if (!minio.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", minio.getPlatform());
            MinIoFileStorage storage = new MinIoFileStorage();
            storage.setPlatform(minio.getPlatform());
            storage.setAccessKey(minio.getAccessKey());
            storage.setSecretKey(minio.getSecretKey());
            storage.setEndPoint(minio.getEndpoint());
            storage.setBucketName(minio.getBucketName());
            storage.setDomain(minio.getDomain());
            storage.setBasePath(minio.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * AWS 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.amazonaws.services.s3.AmazonS3")
    public List<AwsS3FileStorage> amazonS3FileStorageList() {
        return this.property.getAwsS3().stream().map(s3 -> {
            if (!s3.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", s3.getPlatform());
            AwsS3FileStorage storage = new AwsS3FileStorage();
            storage.setPlatform(s3.getPlatform());
            storage.setAccessKey(s3.getAccessKey());
            storage.setSecretKey(s3.getSecretKey());
            storage.setRegion(s3.getRegion());
            storage.setEndPoint(s3.getEndpoint());
            storage.setBucketName(s3.getBucketName());
            storage.setDomain(s3.getDomain());
            storage.setBasePath(s3.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * FTP 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = {"org.apache.commons.net.ftp.FTPClient", "cn.hutool.extra.ftp.Ftp"})
    public List<FtpFileStorage> ftpFileStorageList() {
        return this.property.getFtp().stream().map(ftp -> {
            if (!ftp.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", ftp.getPlatform());
            FtpFileStorage storage = new FtpFileStorage();
            storage.setPlatform(ftp.getPlatform());
            storage.setHost(ftp.getHost());
            storage.setPort(ftp.getPort());
            storage.setUser(ftp.getUser());
            storage.setPassword(ftp.getPassword());
            storage.setCharset(ftp.getCharset());
            storage.setConnectionTimeout(ftp.getConnectionTimeout());
            storage.setSoTimeout(ftp.getSoTimeout());
            storage.setServerLanguageCode(ftp.getServerLanguageCode());
            storage.setSystemKey(ftp.getSystemKey());
            storage.setIsActive(ftp.getIsActive());
            storage.setDomain(ftp.getDomain());
            storage.setBasePath(ftp.getBasePath());
            storage.setStoragePath(ftp.getStoragePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * SFTP 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = {"com.jcraft.jsch.ChannelSftp", "cn.hutool.extra.ftp.Ftp"})
    public List<SftpFileStorage> sftpFileStorageList() {
        return this.property.getSftp().stream().map(sftp -> {
            if (!sftp.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", sftp.getPlatform());
            SftpFileStorage storage = new SftpFileStorage();
            storage.setPlatform(sftp.getPlatform());
            storage.setHost(sftp.getHost());
            storage.setPort(sftp.getPort());
            storage.setUser(sftp.getUser());
            storage.setPassword(sftp.getPassword());
            storage.setPrivateKeyPath(sftp.getPrivateKeyPath());
            storage.setCharset(sftp.getCharset());
            storage.setConnectionTimeout(sftp.getConnectionTimeout());
            storage.setDomain(sftp.getDomain());
            storage.setBasePath(sftp.getBasePath());
            storage.setStoragePath(sftp.getStoragePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * WebDAV 存储 Bean
     */
    @Bean
    @ConditionalOnClass(name = "com.github.sardine.Sardine")
    public List<WebDavFileStorage> webDavFileStorageList() {
        return this.property.getWebDav().stream().map(sftp -> {
            if (!sftp.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", sftp.getPlatform());
            WebDavFileStorage storage = new WebDavFileStorage();
            storage.setPlatform(sftp.getPlatform());
            storage.setServer(sftp.getServer());
            storage.setUser(sftp.getUser());
            storage.setPassword(sftp.getPassword());
            storage.setDomain(sftp.getDomain());
            storage.setBasePath(sftp.getBasePath());
            storage.setStoragePath(sftp.getStoragePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    @Bean
    @ConditionalOnClass(name = "com.google.cloud.storage.Storage")
    public List<GoogleCloudStorage> googleCloudStorageList() {
        return this.property.getGoogleCloud().stream().map(googleCloud -> {
            if (!googleCloud.getEnableStorage()) {
                return null;
            }
            log.info("加载存储平台：{}", googleCloud.getPlatform());
            GoogleCloudStorage storage = new GoogleCloudStorage();
            storage.setPlatform(googleCloud.getPlatform());
            storage.setProjectId(googleCloud.getProjectId());
            storage.setBucketName(googleCloud.getBucketName());
            storage.setCredentialsPath(googleCloud.getCredentialsPath());
            storage.setDomain(googleCloud.getDomain());
            storage.setBasePath(googleCloud.getBasePath());
            return storage;
        }).filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 当没有找到 FileRecorder 时使用默认的 FileRecorder
     */
    @Bean
    @ConditionalOnMissingBean(FileRecorder.class)
    public FileRecorder fileRecorder() {
        log.warn("没有找到 FileRecorder 的实现类，文件上传之外的部分功能无法正常使用，必须实现该接口才能使用完整功能！");
        return new DefaultFileRecorder();
    }

    /**
     * Tika 工厂类型，用于识别上传的文件的 MINE
     */
    @Bean
    @ConditionalOnMissingBean(TikaFactory.class)
    public TikaFactory tikaFactory() {
        return new DefaultTikaFactory();
    }

    /**
     * 文件存储服务
     */
    @Bean
    public FileStorageService fileStorageService(FileRecorder fileRecorder,
        List<List<? extends FileStorage>> fileStorageLists, List<FileStorageAspect> aspectList,
        TikaFactory tikaFactory) {
        this.initDetect();
        FileStorageService service = new FileStorageService();
        service.setFileStorageList(new CopyOnWriteArrayList<>());
        fileStorageLists.forEach(service.getFileStorageList()::addAll);
        service.setFileRecorder(fileRecorder);
        service.setProperties(this.property);
        service.setAspectList(new CopyOnWriteArrayList<>(aspectList));
        service.setTikaFactory(tikaFactory);
        return service;
    }

    /**
     * 对 FileStorageService 注入自己的代理对象，不然会导致针对 FileStorageService 的代理方法不生效
     */
    @EventListener(ContextRefreshedEvent.class)
    public void onContextRefreshedEvent() {
        FileStorageService service = this.applicationContext.getBean(FileStorageService.class);
        service.setSelf(service);
    }

    public void initDetect() {
        String template = "检测到{}配置，但是没有找到对应的依赖库，所以无法加载此存储平台！";
        if (CollUtil.isNotEmpty(this.property.getHuaweiObs()) && doesNotExistClass("com.obs.services.ObsClient")) {
            log.warn(template, "华为云 OBS ");
        }
        if (CollUtil.isNotEmpty(this.property.getAliyunOss()) && doesNotExistClass("com.aliyun.oss.OSS")) {
            log.warn(template, "阿里云 OSS ");
        }
        if (CollUtil.isNotEmpty(this.property.getQiniuKodo()) && doesNotExistClass("com.qiniu.storage.UploadManager")) {
            log.warn(template, "七牛云 Kodo ");
        }
        if (CollUtil.isNotEmpty(this.property.getTencentCos()) && doesNotExistClass("com.qcloud.cos.COSClient")) {
            log.warn(template, "腾讯云 COS ");
        }
        if (CollUtil.isNotEmpty(this.property.getBaiduBos()) && doesNotExistClass("com.baidubce.services.bos.BosClient")) {
            log.warn(template, "百度云 BOS ");
        }
        if (CollUtil.isNotEmpty(this.property.getUpyunUss()) && doesNotExistClass("com.upyun.RestManager")) {
            log.warn(template, "又拍云 USS ");
        }
        if (CollUtil.isNotEmpty(this.property.getMinio()) && doesNotExistClass("io.minio.MinioClient")) {
            log.warn(template, " MinIO ");
        }
        if (CollUtil.isNotEmpty(this.property.getAwsS3()) && doesNotExistClass("com.amazonaws.services.s3.AmazonS3")) {
            log.warn(template, " AmazonS3 ");
        }
        if (CollUtil.isNotEmpty(this.property.getFtp()) && (doesNotExistClass("org.apache.commons.net.ftp.FTPClient")
            || doesNotExistClass("cn.hutool.extra.ftp.Ftp"))) {
            log.warn(template, " FTP ");
        }
        if (CollUtil.isNotEmpty(this.property.getFtp())
            && (doesNotExistClass("com.jcraft.jsch.ChannelSftp") || doesNotExistClass("cn.hutool.extra.ftp.Ftp"))) {
            log.warn(template, " SFTP ");
        }
        if (CollUtil.isNotEmpty(this.property.getWebDav()) && doesNotExistClass("com.github.sardine.Sardine")) {
            log.warn(template, " WebDAV ");
        }
        if (CollUtil.isNotEmpty(this.property.getGoogleCloud()) && doesNotExistClass("com.google.cloud.storage.Storage")) {
            log.warn(template, " 谷歌云存储 ");
        }
    }

    /**
     * 判断是否没有引入指定 Class
     */
    public static boolean doesNotExistClass(String name) {
        try {
            Class.forName(name);
            return false;
        } catch (ClassNotFoundException e) {
            return true;
        }
    }
}
