package com.sipa.boot.core.tool.uid;

import java.net.InetAddress;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Twitter_Snowflake<br>
 * SnowFlake的结构如下(每部分用-分开):<br>
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 0 - 000000000 - 000000000000<br>
 * 1位标识，由于long基本类型在Java中是带符号的，最高位是符号位，正数是0，负数是1，所以id一般是正数，最高位是0<br>
 * 41位时间截(毫秒级)，注意，41位时间截不是存储当前时间的时间截，而是存储时间截的差值（当前时间截 -
 * 开始时间截得到的值），这里的的开始时间截，一般是我们的id生成器开始使用的时间，由我们程序来指定的（如下下面程序IdWorker类的startTime属性）。41位的时间截，可以使用69年，年T = (1L << 41) /
 * (1000L * 60 * 60 * 24 * 365) = 69<br>
 * 10位的数据机器位，可以部署在1024个节点，包括1位data center id和9位worker id<br>
 * 12位序列，毫秒内的计数，12位的计数顺序号支持每个节点每毫秒(同一机器，同一时间截)产生4095个ID序号<br>
 * 加起来刚好64位，为一个Long型。<br>
 * SnowFlake的优点是:</br>
 * 0.整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞(由数据中心ID和机器ID作区分)，并且效率较高。</br>
 * 1.支持自定义允许时间回拨的范围</br>
 * 2.支撑根据IP末尾数据作为workerId</br>
 * 3.解决跨毫秒起始值每次为0开始的情况（避免末尾必定为偶数，而不便于取余使用问题）</br>
 *
 * @author caszhou
 * @date 2019-05-13
 */
public class SnowflakeIdWorker {
    // 开始时间截 (2023-4-23 11:8:5)
    private static final long START_TIME = 1682219286L;

    // 支持30年 = 946080000
    // 0011 1000 0110 0100 0000 1001 0000 0000
    private static final long MAX_TIME_SUPPORT_MINUS_BITS = 11L;

    // 支持30年 = 946080000
    // 0011 1000 0110 0100 0000 1001 0000 0000
    private static final long MAX_WORKER_SUPPORT_MINUS_BITS = 5L;

    // uid的位数
    private static final long ID_BITS = 64L - MAX_TIME_SUPPORT_MINUS_BITS - MAX_WORKER_SUPPORT_MINUS_BITS;

    // 高置位第一位
    private static final long HIGH_BITS = 1L;

    // 时间戳的位数
    private static final long TS_BITS = 41L - MAX_TIME_SUPPORT_MINUS_BITS;

    // 数据中心id所占的位数
    private static final long DATA_CENTER_ID_BITS = 1L;

    // 机器id所占的位数
    private static final long WORKER_ID_BITS = 9L - MAX_WORKER_SUPPORT_MINUS_BITS;

    // 压测标识 1位
    private static final long PRESSURE_MEASUREMENT_BITS = 1L;

    // 序列在id中占的位数
    // 单机支持4096TPS（每次序列号随到0）
    public static final long SEQUENCE_BITS =
        ID_BITS - HIGH_BITS - TS_BITS - DATA_CENTER_ID_BITS - WORKER_ID_BITS - PRESSURE_MEASUREMENT_BITS;

    // 支持的最大数据中心id，结果是1
    public static final long MAX_DATA_CENTER = ~(-1L << DATA_CENTER_ID_BITS);

    // 支持的最大机器id，结果是511
    public static final long MAX_WORKER = ~(-1L << WORKER_ID_BITS);

    // 生成序列的掩码
    public static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    // 压测标识向左移
    public static final long PRESSURE_MEASUREMENT_FLAG_SHIFT = SEQUENCE_BITS;

    // 机器ID向左移
    public static final long WORKER_ID_SHIFT = PRESSURE_MEASUREMENT_FLAG_SHIFT + PRESSURE_MEASUREMENT_BITS;

    // 数据标识id向左移
    public static final long DATA_CENTER_ID_SHIFT = WORKER_ID_SHIFT + WORKER_ID_BITS;

    // 时间截向左移
    public static final long TIMESTAMP_LEFT_SHIFT = DATA_CENTER_ID_SHIFT + DATA_CENTER_ID_BITS;

    // 默认数据中心id
    private static final long DEFAULT_DATA_CENTER = 0L;

    // 默认机器id
    // IP地址后2位相加
    private static int ipCode = 0;

    // 默认序列
    // 保证离散，便于分库分表
    private final ThreadLocalRandom tlr = ThreadLocalRandom.current();

    // 数据中心ID(0~1)
    private final long dataCenterId;

    // 工作机器ID(0~511)
    private long workerId;

    // 毫秒内序列(0~4095)
    private long sequence;

    // 上次生成ID的时间截
    private long lastTimestamp = -1L;

    // 允许的时间回退偏移量
    private final long timeOffset;

    /**
     * 默认调用这个构造函数
     */
    SnowflakeIdWorker() {
        this(DEFAULT_DATA_CENTER, getIpCode(), 5L);
    }

    /**
     * @param workerId
     *            数据中心id
     */
    SnowflakeIdWorker(long workerId) {
        this(DEFAULT_DATA_CENTER, workerId, 5L);
    }

    /**
     * @param dataCenterId
     *            数据中心id
     */
    SnowflakeIdWorker(long dataCenterId, long workerId) {
        this(dataCenterId, workerId, 5L);
    }

    /**
     * 构造函数
     *
     * @param dataCenterId
     *            数据中心ID (0~1)
     * @param workerId
     *            工作ID (0~510(255+255))
     */
    SnowflakeIdWorker(long dataCenterId, long workerId, long timeOffset) {
        if (dataCenterId > MAX_DATA_CENTER || dataCenterId < 0) {
            throw new IllegalArgumentException(
                String.format("Datacenter id can't be greater than %d or less than 0", MAX_DATA_CENTER));
        }
        if (workerId > MAX_WORKER || workerId < 0) {
            throw new IllegalArgumentException(
                String.format("Worker id can't be greater than %d or less than 0", MAX_WORKER));
        }
        this.dataCenterId = dataCenterId;
        this.workerId = workerId;
        this.timeOffset = timeOffset;
    }

    /**
     * 获得下一个ID (该方法是线程安全的)
     * 
     * @return SnowflakeId
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次ID生成的时间戳，说明系统时钟回退过
        if (timestamp < lastTimestamp) {
            // 校验时间偏移回拨量
            long offset = lastTimestamp - timestamp;
            if (offset < timeOffset) {
                try {
                    // 时间回退timeOffset毫秒内，则允许等待2倍的偏移量后重新获取，解决小范围的时间回拨问题
                    this.wait(offset << 1);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            } else {
                // 如果时钟回拨太多, 那么换备用workerId尝试
                // 当前workerId和备用workerId的值的差值为512
                long interval = MAX_WORKER + 1;
                // 发生时钟回拨时, 计算备用workerId
                // 如果当前workerId小于512, 那么备用workerId=workerId+512; 否则备用workerId=workerId-512, 两个workerId轮换用
                if (this.workerId >= interval) {
                    this.workerId = this.workerId - interval;
                } else {
                    this.workerId = this.workerId + interval;
                }
            }
            // 再次获取
            timestamp = this.timeGen();
        }
        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = untilNextMillis(lastTimestamp);
            }
        } else {
            // 时间戳改变，随机生成允许范围内的序列起始值
            sequence = tlr.nextLong(SEQUENCE_MASK + 1);
        }
        // 上次生成ID的时间截
        lastTimestamp = timestamp;

        /*
         * 1.左移运算是为了将数值移动到对应的段(1、41、1、9，12那段因为本来就在最右，因此不用左移)
         * 2.然后对每个左移后的值(l0、la、lb、lc、sequence)做位或运算，是为了把各个短的数据合并起来，合并成一个二进制数 3.最后转换成10进制，就是最终生成的id
         */
        // 移位并通过或运算拼到一起组成64位的ID
        return ((timestamp - START_TIME) << TIMESTAMP_LEFT_SHIFT) | (dataCenterId << DATA_CENTER_ID_SHIFT)
            | (workerId << WORKER_ID_SHIFT) | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     * 
     * @param lastTimestamp
     *            上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long untilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     * 
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }

    /**
     * 用ip地址最后几个字节标示. ip网段需要是 192.168.0.0 - 192.168.255.255 或者 172.x.0.0 - 172.x.255.255 段，10段不可以！！！！！！
     * 
     * @return worker id
     */
    public static int getIpCode() {
        if (ipCode == 0) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                byte[] addressByte = inetAddress.getAddress();
                byte lastSecondIp = addressByte[addressByte.length - 2];
                byte lastIp = addressByte[addressByte.length - 1];
                ipCode = (0x000000FF & lastSecondIp) + (0x000000FF & lastIp);
            } catch (Exception e) {
                throw new RuntimeException("SnowflakeIdWorker getIpCode error", e);
            }
        }
        return ipCode;
    }

    public static void main(String[] args) {
        System.out.println(new SnowflakeIdWorker(0).nextId());
    }
}
