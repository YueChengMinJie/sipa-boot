package com.sipa.boot.core.exception.system;

import com.sipa.boot.core.constant.SipaConstant;
import com.sipa.boot.core.exception.EProjectModule;
import com.sipa.boot.core.exception.api.IErrorCode;
import com.sipa.boot.core.exception.api.IProjectModule;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author caszhou
 * @date 2019/4/24
 */
@Getter
@AllArgsConstructor
public enum ESystemErrorCode implements IErrorCode {
    /**
     * common
     */
    SUCCESS("00", "ok"),

    INTERFACE_INNER_INVOKE_ERROR("01", "内部服务调用错误"),

    SERVER_ERROR("02", "服务端错误"),

    CLIENT_ERROR("03", "客户端错误"),

    ASSERT("04", "校验错误"),

    HTTP_CLIENT_ERROR("05", "调用外部服务错误"),

    HTTP_SERVER_ERROR("06", "调用外部服务错误"),

    NOT_FOUND("07", "非法请求"),

    GATEWAY_ERROR("08", "网关错误"),

    GATEWAY_TIMEOUT("09", "网关超时"),

    BAD_GATEWAY("0a", "服务未启动"),

    UNAVAILABLE("0b", "服务不可用"),

    // extension
    EXTENSION_INTERFACE_NAME_ILLEGAL("10", "{0} The name of the extension point interface is invalid, have to {1} end"),

    EXTENSION_ILLEGAL("11", "Please specify an extension point interface to {0}"),

    EXTENSION_DEFINE_DUPLICATE("12", "The extension point definition is repeated {0}"),

    EXTENSION_NOT_FOUND("13", "Extension point not found，ExtensionPoint: {0}, BizScenario: {1}"),

    FAIL_CALLBACK("14", "Cannot fire event [{0}] on current state [{1}] with context [{2}]"),

    // state machine
    TRANSITION_ALREADY_EXIST("20", "{0} already Exist, you can not add another one"),

    STATE_MACHINE_NOT_READY("21", "State machine is not built yet, can not work"),

    INTERNAL_STATE_MUST_SAME("22", "Internal transition source state {0} and target state {1} must be same"),

    NO_STATE_MACHINE_INSTANCE("23", "There is no stateMachine instance for {0}, please build it first"),

    DUPLICATE_MACHINE_ID("24", "The state machine with id {0} is already built, no need to build again"),

    // idp
    NOT_LOGGED_IN("30", "登陆校验失败"),

    ABNORMAL_CHARACTER("31", "角色校验失败"),

    PERMISSION_EXCEPTION("32", "权限校验失败"),

    AUTHENTICATION_ERROR("33", "鉴权失败"),

    NOT_TOKEN("34", "无登陆权限"),

    INVALID_TOKEN("35", "无登陆权限"),

    TOKEN_TIMEOUT("36", "登陆已过期"),

    BE_REPLACED("37", "账号被顶下线"),

    KICK_OUT("38", "账号被踢下线"),

    // lock
    ETCD_CANNOT_CONNECT("40", "Lock etcd node cannot connect error"),

    ETCD_HTTP_FAIL("41", "Etcd http fail"),

    ETCD_HTTP_ERROR("42", "Etcd cluster related error or param error, code {0}"),

    PLEASE_INIT("43", "请先初始化"),

    URL_IS_EMPTY("44", "Lock EtcdClient URL can not be empty"),

    CLUSTER_NAME_IS_EMPTY("45", "ClusterName is must param, please add clusterName param"),

    TIMEOUT_IS_NOT_RIGHT("46", "timeout 取值区间【{0},{1}】"),

    REDISSON_CANNOT_SET_NOT_REENTRANT("47", "redisson是一把可重入锁，不能设置不可重入"),

    // storage
    FILE_UPLOAD_FAILED("50", "文件上传失败! platform [{0}], filename [{1}]"),

    FILE_DOWNLOAD_FAILED("51", "文件下载失败! platform [{0}], filename [{1}]"),

    FILE_DOWNLOAD_FAILED_NOT_EXISTS("52", "文件不存在，下载失败! platform [{0}], filename [{1}]"),

    FILE_DELETE_FAILED("53", "文件删除失败! platform [{0}], filename [{1}]"),

    FILE_EXISTS_FAILED("54", "文件查询是否存在失败! platform [{0}], filename [{1}]"),

    FILE_NOT_EXISTS("55", "文件在数据库中不存在"),

    THUMBNAIL_NOT_EXISTS("56", "缩略图不存在! platform [{0}], filename [{1}]"),

    THUMBNAIL_DOWNLOAD_FAILED("57", "缩略图下载失败! platform [{0}], filename [{1}]"),

    THUMBNAIL_DOWNLOAD_FAILED_NOT_EXISTS("58", "缩略图不存在，下载失败! platform [{0}], filename [{1}]"),

    CLIENT_NOT_AUTHORIZED("59", "客户端授权失败! platform [{0}], credentialsPath [{1}]"),

    CLIENT_CANNOT_CONNECTED("5a", "客户端连接失败! platform [{0}], host [{1}], port [{2}]"),

    NO_CORRESPONDING_STORAGE_PLATFORM_FOUND("5b", "没有找到对应的存储平台"),

    FILE_IS_EMPTY("5c", "文件为空"),

    STORAGE_PLATFORM_IS_EMPTY("5d", "存储平台为空"),

    FAILED_TO_CREATE_UPLOAD_PREPROCESSOR("5e", "根据 {0} 创建上传预处理器失败"),

    NO_CORRESPONDING_DOWNLOAD_TARGET_FOUND("5f", "没找到对应的下载目标, 请设置 target 参数"),

    IMAGE_PROCESSING_FAILED("5g", "图片处理失败"),

    FAILED_TO_GENERATE_THUMBNAIL("5h", "生成缩略图失败"),

    UNABLE_TO_DELETE_FILE("5j", "没有权限删除文件"),

    // creator
    FILE_TYPE_CANNOT_BE_EMPTY("70", "fileType不能为空"),

    FILE_NOT_FOUND("71", "找不到文件"),

    TEMPLATE_FILE_NOT_FOUND("72", "未找到模板文件：{0}"),

    SQL_CANNOT_BE_EMPTY("73", "sql不能为空"),

    ONLY_SELECT("74", "只能处理SQL查询语句"),

    SQL_PARSE_FAILED("75", "解析SQL条件发生错误, 请检查SQL语法"),

    XML_NOT_EXISTS("76", "XML文件不存在: {0}"),

    PACKAGE_IS_EMPTY("77", "包名为空"),

    NOT_VALID_PACKAGE_NAME("78", "不是合法的包名: {0}"),

    CANNOT_CONVERT_TO_PACKAGE_NAME("79", "无法将该路径转换为包名: {0}"),

    FAILED_TO_UPLOAD_TEMPLATE_FILE("7a", "上传模板文件失败"),

    CONFIGURATION_DIR_NOT_FOUND("7b", "配置主目录不存在: {0}"),

    CONFIGURATION_TO_IMPORT_WAS_NOT_FOUND("7c", "未找到待导入的源项目配置"),

    UNRECOGNIZED_SOURCE_CODE_PREFIX("7d", "无法识别的源码前缀: {0}"),

    BUILT_IN_CONFIGURATION_CANNOT_BE_DELETED("7e", "内置文件配置信息不能删除"),

    DATA_SOURCE_SQL_CANNOT_BE_EMPTY("7f", "数据源SQL不能为空"),

    DTO_ONLY_FROM_SQL("7g", "只能通过查询语句生成DTO对象, 请检查SQL"),

    ERROR_OCCURRED_EXECUTING_SQL("7h", "执行SQL发生错误"),

    CANNOT_WRITE_EMPTY_USER_CONFIG("7j", "不能写入空的用户配置"),

    // geetest
    GEETEST_VALIDATION_FAILED("90", "验证失败"),

    // default
    DEFAULT_ERROR("ZZ", SipaConstant.GLOBAL_MSG);

    private final String code;

    private final String msg;

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public IProjectModule getProjectModule() {
        return EProjectModule.SYSTEM;
    }
}
