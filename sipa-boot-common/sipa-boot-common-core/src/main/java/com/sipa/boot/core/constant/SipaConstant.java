package com.sipa.boot.core.constant;

import java.time.format.DateTimeFormatter;

import com.sipa.boot.core.secure.IdpUser;

/**
 * @author caszhou
 * @date 2019-02-01
 */
public interface SipaConstant {
    String GLOBAL_MSG = "请求错误，请稍后重试";

    // 内网流量网关
    String CLIENT_IP_HEADER = "X-Sipa-Client-Ip";

    // 传递
    String MERCHANT_IP_HEADER = "X-Merchant-Id";

    // 内网流量网关
    String CANARY_HEADER = "X-Sipa-Canary";

    // 服务本身
    String COMPANY_ID_HEADER = "X-Company-Id";

    // 服务本身
    String USER_ID_HEADER = "X-User-Id";

    // 传递
    String REQUEST_ID_HEADER = "X-Request-Id";

    // 传递
    String REQUEST_FROM_HEADER = "X-Request-From";

    // SLB
    String SLB_CLIENT_IP_HEADER = "RemoteIp";

    String REFERER_HEADER = "Referer";

    String HOST_HEADER = "Host";

    String AUTH_KEY = "Authorization";

    String TOKEN_PREFIX = "Bearer";

    String SA_KEY = "sa-same-token";

    String[] FILTER_HEADER_KEYS = {AUTH_KEY, IdpUser.REST_WEB_KEY, SA_KEY};

    interface Env {
        String DEFAULT_PROPERTY_SOURCE = "sipaPropertySource";

        String VERSION = "sipa.version";

        String DEFAULT_VERSION = "0.0.0";
    }

    interface Symbol {
        String PERCENTAGE = "%";

        String UNDERLINE = "_";

        String LINE = "-";

        String COMMA = ",";

        String WELL = "#";

        String COLON = ":";

        String ACROSS = "-";

        String UPRIGHT = "|";

        String POINT = ".";

        String ASTERISK = "*";

        String LEFT_BRACKET = "(";

        String RIGHT_BRACKET = ")";

        String LEFT_CURLY_BRACES = "{";

        String RIGHT_CURLY_BRACES = "}";

        String EMPTY = " ";

        String SLASH = "/";

        String BACKSLASH = "\\";

        String BLANK = "";

        String DOUBLE_ASTERISK = "**";

        String EQUAL = "=";

        String JOIN = ", ";

        String BEACON = "!";
    }

    interface StringValue {
        String STRING_N_VALUE_1 = "-1";

        String STRING_VALUE_0 = "0";

        String STRING_VALUE_1 = "1";

        String STRING_VALUE_2 = "2";

        String STRING_VALUE_3 = "3";

        String STRING_VALUE_4 = "4";

        String STRING_VALUE_5 = "5";

        String STRING_VALUE_6 = "6";

        String STRING_VALUE_7 = "7";

        String STRING_VALUE_8 = "8";
    }

    interface StringDoubleValue {
        String STRING_VALUE_1 = "01";

        String STRING_VALUE_2 = "02";

        String STRING_VALUE_3 = "03";

        String STRING_VALUE_4 = "04";

        String STRING_VALUE_5 = "05";

        String STRING_VALUE_6 = "06";

        String STRING_VALUE_7 = "07";

        String STRING_VALUE_8 = "08";
    }

    interface Number {
        int N_INT_1 = -1;

        int INT_0 = 0;

        int INT_1 = 1;

        int INT_2 = 2;

        int INT_3 = 3;

        int INT_4 = 4;

        int INT_5 = 5;

        int INT_6 = 6;

        int INT_7 = 7;

        int INT_8 = 8;

        int INT_9 = 9;

        int INT_10 = 10;

        int INT_13 = 13;

        int INT_14 = 14;

        int INT_15 = 15;

        int INT_16 = 16;

        int INT_20 = 20;

        int INT_30 = 30;

        int INT_64 = 64;
    }

    interface StringBoolean {
        String TRUE = "true";

        String FALSE = "false";
    }

    interface TimeFormatKey {
        String DATE_DEFAULT = "yyyy-MM-dd";

        String TIME_DEFAULT = "HH:mm:ss";

        String DATE_TIME_DEFAULT = "yyyy-MM-dd HH:mm:ss";

        String DEFAULT_WITH_POINT_MILS = "yyyy-MM-dd HH:mm:ss.SSS";

        String DEFAULT_WITH_MILS = "yyyy-MM-dd HH:mm:ss:SSS";

        String DEFAULT_WITH_UL = "yyyy-MM-dd_HH:mm:ss";

        String DEFAULT_DATE = "yyyyMMdd";

        String DEFAULT_HOUR = "yyyyMMddHH";

        String DEFAULT_MINUTE = "yyyyMMddHHmm";

        String DEFAULT_SECOND = "yyyyMMddHHmmss";

        String HOUR = "yyyy-MM-dd HH";

        String MINUTE = "yyyy-MM-dd HH:mm";

        String SECOND = "yyyy-MM-dd HH:mm";
    }

    interface Formatter {
        DateTimeFormatter DATE_DEFAULT = DateTimeFormatter.ofPattern(TimeFormatKey.DATE_DEFAULT);

        DateTimeFormatter TIME_DEFAULT = DateTimeFormatter.ofPattern(TimeFormatKey.TIME_DEFAULT);

        DateTimeFormatter DATE_TIME_DEFAULT = DateTimeFormatter.ofPattern(TimeFormatKey.DATE_TIME_DEFAULT);
    }

    /**
     * Http status code constants
     */
    interface Http {
        int OK = 200;

        int CREATED = 201;

        int ACCEPTED = 202;

        int NO_CONTENT = 204;

        int RESET_CONTENT = 205;

        int PARTIAL_CONTENT = 206;

        int MULTI_STATUS = 207;

        int MOVED_PERMANENTLY = 301;

        int FOUND = 302;

        int SEE_OTHER = 303;

        int NOT_MODIFIED = 304;

        int USE_PROXY = 305;

        int TEMPORARY_REDIRECT = 307;

        int BAD_REQUEST = 400;

        int UNAUTHORIZED = 401;

        int PAYMENT_REQUIRED = 402;

        int FORBIDDEN = 403;

        int NOT_FOUND = 404;

        int METHOD_NOT_ALLOWED = 405;

        int NOT_ACCEPTABLE = 406;

        int PROXY_AUTHENTICATION_REQUIRED = 407;

        int REQUEST_TIMEOUT = 408;

        int CONFLICT = 409;

        int GONE = 410;

        int LENGTH_REQUIRED = 411;

        int PRECONDITION_FAILED = 412;

        int REQUEST_ENTITY_TOO_LARGE = 413;

        int REQUEST_URI_TOO_LONG = 414;

        int UNSUPPORTED_MEDIA_TYPE = 415;

        int REQUESTED_RANGE_NOT_SATISFIABLE = 416;

        int EXPECTATION_FAILED = 417;

        int INTERNAL_SERVER_ERROR = 500;

        int NOT_IMPLEMENTED = 501;

        int BAD_GATEWAY = 502;

        int SERVICE_UNAVAILABLE = 503;

        int GATEWAY_TIMEOUT = 504;

        int VERSION_NOT_SUPPORTED = 505;
    }

    interface Canary {
        String COOKIE = "Sipa-Canary";

        String ALWAYS = "always";

        String METADATA = "canary";
    }

    interface MybatisPlus {
        String LAST_ONE = "limit 1";
    }

    interface HttpHeaderValue {
        String JSON = "application/json";

        String JSON_UTF8 = "application/json; charset=utf-8";
    }
}
