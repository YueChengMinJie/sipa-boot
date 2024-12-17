# 可扩展的工业平台架构-快速启动套件

这里的配置仅仅是sipa的配置，非spring或者框架的配置。

## 设置hook文件夹

```shell
git config --local core.hooksPath .githooks/
```

## 不需要配置的启动器

- sipa-boot-extension-starter
- sipa-boot-extension-with-ark-starter
- sipa-boot-gateway-starter
- sipa-boot-lb-starter
- sipa-boot-mvc-starter
- sipa-boot-mybatis-starter
- sipa-boot-nacos-starter
- sipa-boot-rest-starter
- sipa-boot-retry-starter
- sipa-boot-rocketmq-starter
- sipa-boot-seata-starter
- sipa-boot-secure-gateway-starter
- sipa-boot-secure-gateway-starter
- sipa-boot-secure-server-starter
- sipa-boot-secure-ssr-starter
- sipa-boot-sentinel-gateway-starter
- sipa-boot-sentinel-server-starter
- sipa-boot-sofa-biz-starter
- sipa-boot-sofa-plugin-starter
- sipa-boot-starter
- sipa-boot-state-machine-starter
- sipa-boot-test-container-starter
- sipa-boot-iot-starter
- sipa-boot-ws-starter

## 需要配置的启动器

list类型的配置请查看配置的内部类。

- sipa-boot-geetest-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.geetest",
      "type": "com.sipa.boot.geetest.property.GeetestProperty",
      "sourceType": "com.sipa.boot.geetest.property.GeetestProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.geetest.api-url",
      "type": "java.lang.String",
      "description": "api地址，默认为http:\/\/api.geetest.com",
      "sourceType": "com.sipa.boot.geetest.property.GeetestProperty",
      "defaultValue": "http:\/\/api.geetest.com"
    },
    {
      "name": "sipa-boot.geetest.geetest-id",
      "type": "java.lang.String",
      "description": "极验公钥",
      "sourceType": "com.sipa.boot.geetest.property.GeetestProperty"
    },
    {
      "name": "sipa-boot.geetest.geetest-key",
      "type": "java.lang.String",
      "description": "极验私钥",
      "sourceType": "com.sipa.boot.geetest.property.GeetestProperty"
    },
    {
      "name": "sipa-boot.geetest.register-url",
      "type": "java.lang.String",
      "description": "注册endpoint，默认为\/register.php",
      "sourceType": "com.sipa.boot.geetest.property.GeetestProperty",
      "defaultValue": "\/register.php"
    },
    {
      "name": "sipa-boot.geetest.validate-url",
      "type": "java.lang.String",
      "description": "二次验证endpoint，默认为\/validate.php",
      "sourceType": "com.sipa.boot.geetest.property.GeetestProperty",
      "defaultValue": "\/validate.php"
    }
  ],
  "hints": []
}
```

- sipa-boot-lock-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.lock",
      "type": "com.sipa.boot.lock.property.LockProperty",
      "sourceType": "com.sipa.boot.lock.property.LockProperty"
    },
    {
      "name": "sipa-boot.lock.etcd",
      "type": "com.sipa.boot.lock.property.LockProperty$EtcdLockProperty",
      "sourceType": "com.sipa.boot.lock.property.LockProperty",
      "sourceMethod": "getEtcd()"
    },
    {
      "name": "sipa-boot.lock.redis",
      "type": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "sourceType": "com.sipa.boot.lock.property.LockProperty",
      "sourceMethod": "getRedis()"
    },
    {
      "name": "sipa-boot.lock.redis.eviction",
      "type": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisEvictionLockProperty",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "sourceMethod": "getEviction()"
    },
    {
      "name": "sipa-boot.lock.redis.jmx",
      "type": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisJmxLockProperty",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "sourceMethod": "getJmx()"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.lock.etcd.cluster-name",
      "type": "java.lang.String",
      "description": "etcd集群名",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$EtcdLockProperty"
    },
    {
      "name": "sipa-boot.lock.etcd.urls",
      "type": "java.util.List<java.lang.String>",
      "description": "etcd集群url",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$EtcdLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.block",
      "type": "java.lang.Boolean",
      "description": "当连接池中没有可用连接时，获取连接的行为方式，默认为false",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": false
    },
    {
      "name": "sipa-boot.lock.redis.cluster",
      "type": "java.lang.Boolean",
      "description": "是否集群，默认false",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": false
    },
    {
      "name": "sipa-boot.lock.redis.cluster-ips",
      "type": "java.lang.String",
      "description": "集群ip",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.database",
      "type": "java.lang.Integer",
      "description": "第几个库",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.eviction.clazz",
      "type": "java.lang.String",
      "description": "驱逐class，默认为默认的class",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisEvictionLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.eviction.min-idle-time",
      "type": "java.time.Duration",
      "description": "驱逐的最小空闲时间，默认为30分钟",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisEvictionLockProperty",
      "defaultValue": "30m"
    },
    {
      "name": "sipa-boot.lock.redis.eviction.num",
      "type": "java.lang.Integer",
      "description": "每次逐出的最大数目，默认为3",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisEvictionLockProperty",
      "defaultValue": 3
    },
    {
      "name": "sipa-boot.lock.redis.eviction.run-time",
      "type": "java.time.Duration",
      "description": "逐出扫描的时间间隔，默认为1分钟",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisEvictionLockProperty",
      "defaultValue": "1m"
    },
    {
      "name": "sipa-boot.lock.redis.eviction.soft-min-idle-time",
      "type": "java.time.Duration",
      "description": "对象空闲多久后逐出，默认为15分钟",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisEvictionLockProperty",
      "defaultValue": "15m"
    },
    {
      "name": "sipa-boot.lock.redis.host",
      "type": "java.lang.String",
      "description": "主机名",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.jmx.enabled",
      "type": "java.lang.Boolean",
      "description": "是否开启jmx，默认为false",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisJmxLockProperty",
      "defaultValue": false
    },
    {
      "name": "sipa-boot.lock.redis.jmx.prefix",
      "type": "java.lang.String",
      "description": "jmx前缀",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty$RedisJmxLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.lifo",
      "type": "java.lang.Boolean",
      "description": "设置当连接池中有空闲连接时，从连接池中获取连接的方式，默认为true",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": true
    },
    {
      "name": "sipa-boot.lock.redis.max-idle",
      "type": "java.lang.Integer",
      "description": "连接池中最大的空闲连接数，默认为10",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": 10
    },
    {
      "name": "sipa-boot.lock.redis.max-total",
      "type": "java.lang.Integer",
      "description": "连接池中最大的连接数，默认为200",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": 200
    },
    {
      "name": "sipa-boot.lock.redis.max-wait",
      "type": "java.time.Duration",
      "description": "当连接池中连接数量达到上限且所有连接都在使用时，应用程序从连接池获取连接的最大等待时间，默认60秒",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": "60s"
    },
    {
      "name": "sipa-boot.lock.redis.min-idle",
      "type": "java.lang.Integer",
      "description": "连接池中最小的空闲连接数，默认为0",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": 0
    },
    {
      "name": "sipa-boot.lock.redis.mode",
      "type": "java.lang.String",
      "description": "集群类型，sharded or cluster",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.password",
      "type": "java.lang.String",
      "description": "密码",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.port",
      "type": "java.lang.Integer",
      "description": "端口",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty"
    },
    {
      "name": "sipa-boot.lock.redis.test-borrow",
      "type": "java.lang.Boolean",
      "description": "每次从连接池中获取连接时，检测连接是否可用的方式，默认为true",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": true
    },
    {
      "name": "sipa-boot.lock.redis.test-idle",
      "type": "java.lang.Boolean",
      "description": "在空闲时检查有效性，默认为true",
      "sourceType": "com.sipa.boot.lock.property.LockProperty$RedisLockProperty",
      "defaultValue": true
    }
  ],
  "hints": []
}
```

- sipa-boot-openapi-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.openapi",
      "type": "com.sipa.boot.openapi.property.OpenApiProperty",
      "sourceType": "com.sipa.boot.openapi.property.OpenApiProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.openapi.desc",
      "type": "java.lang.String",
      "description": "描述",
      "sourceType": "com.sipa.boot.openapi.property.OpenApiProperty"
    },
    {
      "name": "sipa-boot.openapi.version",
      "type": "java.lang.String",
      "description": "版本",
      "sourceType": "com.sipa.boot.openapi.property.OpenApiProperty"
    }
  ],
  "hints": []
}
```

- sipa-boot-sofa-host-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.ark-host",
      "type": "com.sipa.boot.ark.property.ArkHostProperty",
      "sourceType": "com.sipa.boot.ark.property.ArkHostProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.ark-host.extensions",
      "type": "java.util.List<com.sipa.boot.ark.property.ArkHostProperty$ExtensionProperty>",
      "description": "扩展点列表",
      "sourceType": "com.sipa.boot.ark.property.ArkHostProperty"
    }
  ],
  "hints": []
}
```

- sipa-boot-storage-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.storage",
      "type": "com.sipa.boot.storage.property.StorageProperty",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.storage.aliyun-oss",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$AliyunOss>",
      "description": "阿里云 OSS",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.aws-s3",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$AwsS3>",
      "description": "AWS S3",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.baidu-bos",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$BaiduBos>",
      "description": "百度云 BOS",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.default-platform",
      "type": "java.lang.String",
      "description": "默认存储平台，默认为阿里云",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty",
      "defaultValue": "aliyun"
    },
    {
      "name": "sipa-boot.storage.ftp",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$Ftp>",
      "description": "FTP",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.google-cloud",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$GoogleCloud>",
      "description": "谷歌云存储",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.huawei-obs",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$HuaweiObs>",
      "description": "华为云 OBS",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.local",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$Local>",
      "description": "本地存储",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.local-plus",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$LocalPlus>",
      "description": "本地存储plus",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.minio",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$MinIo>",
      "description": "MinIO USS",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.qiniu-kodo",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$QiniuKodo>",
      "description": "七牛云 Kodo",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.sftp",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$Sftp>",
      "description": "SFTP",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.tencent-cos",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$TencentCos>",
      "description": "腾讯云 COS",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.thumbnail-suffix",
      "type": "java.lang.String",
      "description": "缩略图后缀，例如【.min.jpg】【.png】，默认为.min.jpg",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty",
      "defaultValue": ".min.jpg"
    },
    {
      "name": "sipa-boot.storage.upyun-uss",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$UpyunUss>",
      "description": "又拍云 USS",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    },
    {
      "name": "sipa-boot.storage.web-dav",
      "type": "java.util.List<com.sipa.boot.storage.property.StorageProperty$WebDav>",
      "description": "WebDAV",
      "sourceType": "com.sipa.boot.storage.property.StorageProperty"
    }
  ],
  "hints": []
}
```

- sipa-boot-common-xxljob

```json
{
  "groups": [
    {
      "name": "sipa-boot.xxljob",
      "type": "com.sipa.boot.xxljob.property.XxljobProperty",
      "sourceType": "com.sipa.boot.xxljob.property.XxljobProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.xxljob.access-token",
      "type": "java.lang.String",
      "description": "token，默认为f03f6254-8143-4a89-b169-844f34a18787",
      "sourceType": "com.sipa.boot.xxljob.property.XxljobProperty"
    },
    {
      "name": "sipa-boot.xxljob.admin-addresses",
      "type": "java.lang.String",
      "description": "admin集群地址",
      "sourceType": "com.sipa.boot.xxljob.property.XxljobProperty"
    },
    {
      "name": "sipa-boot.xxljob.log-retention-days",
      "type": "java.lang.Integer",
      "description": "日志保留天数，默认为30天",
      "sourceType": "com.sipa.boot.xxljob.property.XxljobProperty"
    }
  ],
  "hints": []
}
```

## 选配的启动器

list类型的配置请查看配置的内部类。

- sipa-boot-es-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.es",
      "type": "com.sipa.boot.es.property.ElasticSearchProperty",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.es.connect-timeout",
      "type": "java.lang.Integer",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty",
      "defaultValue": 2000
    },
    {
      "name": "sipa-boot.es.connection-request-timeout",
      "type": "java.lang.Integer",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty",
      "defaultValue": 5000
    },
    {
      "name": "sipa-boot.es.host",
      "type": "java.lang.String",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty",
      "defaultValue": "172.19.65.94"
    },
    {
      "name": "sipa-boot.es.max-conn-total",
      "type": "java.lang.Integer",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty",
      "defaultValue": 30
    },
    {
      "name": "sipa-boot.es.port",
      "type": "java.lang.Integer",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty",
      "defaultValue": 9200
    },
    {
      "name": "sipa-boot.es.socket-timeout",
      "type": "java.lang.Integer",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty",
      "defaultValue": 2000
    },
    {
      "name": "sipa-boot.es.uris",
      "type": "java.util.List<java.lang.String>",
      "sourceType": "com.sipa.boot.es.property.ElasticSearchProperty"
    }
  ],
  "hints": []
}
```

- sipa-boot-cache-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.cache",
      "type": "com.sipa.boot.cache.property.CacheProperty",
      "sourceType": "com.sipa.boot.cache.property.CacheProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.cache.cache-null-values",
      "type": "java.lang.Boolean",
      "description": "允许缓存空值，默认为false.",
      "sourceType": "com.sipa.boot.cache.property.CacheProperty",
      "defaultValue": false
    },
    {
      "name": "sipa-boot.cache.time-to-live",
      "type": "java.time.Duration",
      "description": "缓存过期时间。默认缓存永30天过期。",
      "sourceType": "com.sipa.boot.cache.property.CacheProperty",
      "defaultValue": "30d"
    }
  ],
  "hints": []
}
```

- sipa-boot-feign-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.feign",
      "type": "com.sipa.boot.feign.property.FeignProperty",
      "sourceType": "com.sipa.boot.feign.property.FeignProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.feign.enabled",
      "type": "java.lang.String",
      "description": "开关，默认为false",
      "sourceType": "com.sipa.boot.feign.property.FeignProperty",
      "defaultValue": "false"
    }
  ],
  "hints": []
}
```

- sipa-boot-wx-ma-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.wx.ma",
      "type": "com.sipa.boot.wxma.property.WxMaProperty",
      "sourceType": "com.sipa.boot.wxma.property.WxMaProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.wx.ma.configs",
      "type": "java.util.List<com.sipa.boot.wxma.property.WxMaProperty$MaConfig>",
      "description": "多个小程序配置信息",
      "sourceType": "com.sipa.boot.wxma.property.WxMaProperty"
    }
  ],
  "hints": []
}
```

- sipa-boot-wx-mp-starter

```json
{
  "groups": [
    {
      "name": "sipa-boot.wx.mp",
      "type": "com.sipa.boot.wxmp.property.WxMpProperty",
      "sourceType": "com.sipa.boot.wxmp.property.WxMpProperty"
    }
  ],
  "properties": [
    {
      "name": "sipa-boot.wx.mp.configs",
      "type": "java.util.List<com.sipa.boot.wxmp.property.WxMpProperty$MpConfig>",
      "description": "多个公众号配置信息",
      "sourceType": "com.sipa.boot.wxmp.property.WxMpProperty"
    }
  ],
  "hints": []
}
```
