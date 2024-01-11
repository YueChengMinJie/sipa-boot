package com.sipa.boot.mqttv3.core;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.eclipse.paho.client.mqttv3.*;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.sipa.boot.mqttv3.property.MqttProperty;
import com.sipa.boot.mqttv3.subscriber.MqttSubscriber;
import com.sipa.boot.mqttv3.subscriber.TopicPair;

import lombok.extern.slf4j.Slf4j;

/**
 * Establish a connection and subscribe to topics.
 *
 * @author caszhou
 * @date 2022/6/23
 */
@Slf4j
public class MqttConnector implements DisposableBean {
    public final static Map<String, IMqttAsyncClient> MQTT_CLIENT_MAP = new HashMap<>();

    public final static Map<String, Integer> MQTT_DEFAULT_QOS_MAP = new HashMap<>();

    public static String defaultClientId;

    public static int defaultPublishQos;

    public static IMqttAsyncClient getDefaultClient() {
        if (StringUtils.hasText(defaultClientId)) {
            return MQTT_CLIENT_MAP.get(defaultClientId);
        } else if (!MQTT_CLIENT_MAP.isEmpty()) {
            return MQTT_CLIENT_MAP.values().iterator().next();
        }
        return null;
    }

    public static int getDefaultQosById(String clientId) {
        if (StringUtils.hasText(clientId)) {
            return MQTT_DEFAULT_QOS_MAP.getOrDefault(clientId, 0);
        } else {
            return defaultPublishQos;
        }
    }

    /**
     * Get from {@link MqttConnector#MQTT_CLIENT_MAP} by client id.
     * <p>
     * Call {@link MqttConnector#getDefaultClient()} if client id is if {@code null}.
     *
     * @param clientId
     *            id
     * @return IMqttAsyncClient
     * @see MqttConnector#getDefaultClient()
     */
    public static IMqttAsyncClient getClientById(String clientId) {
        if (StringUtils.hasText(clientId)) {
            return MQTT_CLIENT_MAP.get(clientId);
        } else {
            return getDefaultClient();
        }
    }

    // for reconnect
    private final ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2);

    private MqttProperty properties;

    private MqttAdapter adapter;

    public void start(MqttProperty properties, MqttAdapter adapter) {
        adapter.setProperties(properties);
        // sort subscribe by order.
        MqttSubscribeProcessor.SUBSCRIBERS.sort(Comparator.comparingInt(MqttSubscriber::getOrder));
        // create clients
        this.properties = properties;
        this.adapter = adapter;
        this.connect();
    }

    /**
     * 根据配置建立连接.
     */
    public void connect() {
        connect(false);
    }

    /**
     * 根据配置建立连接.
     *
     * @param force
     *            强制建立新的连接，如果存在旧连接则断开.
     */
    public void connect(boolean force) {
        properties.forEach((id, options) -> {
            try {
                if (MQTT_CLIENT_MAP.containsKey(id)) {
                    if (force) {
                        disconnect(id);
                    } else {
                        return;
                    }
                }
                IMqttAsyncClient client = adapter.postCreate(id, options);
                if (client != null) {
                    MQTT_CLIENT_MAP.put(client.getClientId(), client);
                    MQTT_DEFAULT_QOS_MAP.put(client.getClientId(),
                        this.properties.getClients().get(client.getClientId()).getDefaultPublishQos());
                    if (!StringUtils.hasText(defaultClientId)) {
                        defaultClientId = client.getClientId();
                        defaultPublishQos = MQTT_DEFAULT_QOS_MAP.get(client.getClientId());
                        log.info("Default mqtt client is [{}], default mqtt qos is [{}]", defaultClientId,
                            defaultPublishQos);
                    }
                    // connect to mqtt server.
                    scheduled.schedule(new ReConnect(client, options), 1, TimeUnit.MILLISECONDS);
                }
            } catch (MqttException e) {
                log.error("connection fail", e);
            }
        });
    }

    /**
     * 关闭指定的客户端.
     */
    public void disconnect(String clientId) {
        Assert.notNull(clientId, "disconnect client id can not be null.");
        try {
            IMqttAsyncClient client = MqttConnector.MQTT_CLIENT_MAP.get(clientId);
            client.disconnect();
            MqttConnector.MQTT_CLIENT_MAP.remove(clientId);
            MqttConnector.MQTT_DEFAULT_QOS_MAP.remove(clientId);
        } catch (MqttException ignored) {
        }

        if (clientId.equals(MqttConnector.defaultClientId)) {
            if (!MqttConnector.MQTT_CLIENT_MAP.isEmpty()) {
                MqttConnector.defaultClientId = MqttConnector.MQTT_CLIENT_MAP.keySet().iterator().next();
            } else {
                MqttConnector.defaultClientId = null;
            }
        }
    }

    /**
     * 建立连接.
     */
    private void connect(IMqttAsyncClient client, MqttConnectOptions options) {
        try {
            client.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    try {
                        log.info("Connect success. client_id is [{}], brokers is [{}].", client.getClientId(),
                            String.join(",", options.getServerURIs()));
                        subscribe(client);
                    } catch (Exception e) {
                        log.error("connection onSuccess fail", e);
                    }
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    try {
                        log.error("Connect failure. client_id is [{}], brokers is [{}]. retry after {} ms.",
                            client.getClientId(), String.join(",", options.getServerURIs()),
                            options.getMaxReconnectDelay());
                        scheduled.schedule(new ReConnect(client, options), options.getMaxReconnectDelay(),
                            TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("connection onFailure fail", e);
                    }
                }
            });
            client.setCallback(new MqttCallbackExtended() {
                private final String clientId = client.getClientId();

                @Override
                public void connectComplete(boolean reconnect, String serverURI) {
                    if (reconnect) {
                        log.info("Mqtt reconnection success.");
                        subscribe(client);
                    }
                }

                @Override
                public void connectionLost(Throwable cause) {
                    log.warn("Mqtt connection lost", cause);
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    for (MqttSubscriber subscriber : MqttSubscribeProcessor.SUBSCRIBERS) {
                        subscriber.accept(clientId, topic, message);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //
                }
            });
        } catch (MqttException e) {
            log.error("connection fail", e);
        }
    }

    /**
     * 订阅.
     */
    private void subscribe(IMqttAsyncClient client) {
        String clientId = client.getClientId();
        boolean sharedEnable = this.properties.isSharedEnable(clientId);
        try {
            Set<TopicPair> topicPairs = mergeTopics(clientId, sharedEnable);
            this.adapter.beforeSubscribe(clientId, topicPairs);
            if (topicPairs.isEmpty()) {
                log.warn("There is no topic has been found for client [{}].", clientId);
                return;
            }
            StringJoiner sj = new StringJoiner(",");
            String[] topics = new String[topicPairs.size()];
            int[] qosAry = new int[topicPairs.size()];
            int i = 0;
            for (TopicPair topicPair : topicPairs) {
                topics[i] = topicPair.getTopic(sharedEnable);
                qosAry[i] = topicPair.getQos();
                sj.add("('" + topics[i] + "', " + qosAry[i] + ")");
                ++i;
            }
            client.subscribe(topics, qosAry);
            log.info("Mqtt client [{}] subscribe success. topics : " + sj, clientId);
        } catch (MqttException e) {
            log.error("Mqtt client [{}] subscribe failure.", clientId, e);
        }
    }

    /**
     * 合并相似的主题(实际没啥用) merge the same topic
     *
     * @param clientId
     *            clientId
     * @return TopicPairs
     */
    private Set<TopicPair> mergeTopics(String clientId, boolean sharedEnable) {
        Set<TopicPair> topicPairs = new HashSet<>();
        for (MqttSubscriber subscriber : MqttSubscribeProcessor.SUBSCRIBERS) {
            if (subscriber.contains(clientId)) {
                topicPairs.addAll(subscriber.getTopics());
            }
        }
        if (topicPairs.isEmpty()) {
            return topicPairs;
        }
        TopicPair[] pairs = new TopicPair[topicPairs.size()];
        for (TopicPair topic : topicPairs) {
            for (int i = 0; i < pairs.length; ++i) {
                TopicPair pair = pairs[i];
                if (pair == null) {
                    pairs[i] = topic;
                    break;
                }
                if (pair.getQos() != topic.getQos()) {
                    continue;
                }
                String temp = pair.getTopic(sharedEnable).replace('+', '\u0000').replace("#", "\u0000/\u0000");
                if (MqttTopic.isMatched(topic.getTopic(sharedEnable), temp)) {
                    pairs[i] = topic;
                    continue;
                }
                temp = topic.getTopic(sharedEnable).replace('+', '\u0000').replace("#", "\u0000/\u0000");
                if (MqttTopic.isMatched(pair.getTopic(sharedEnable), temp)) {
                    break;
                }
            }
        }
        return Arrays.stream(pairs).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    @Override
    public void destroy() {
        log.info("Shutting down mqtt clients.");
        MQTT_CLIENT_MAP.forEach((id, client) -> {
            try {
                if (client.isConnected()) {
                    client.disconnect();
                }
            } catch (Exception e) {
                log.error("Mqtt disconnect error: {}", e.getMessage(), e);
            }
            try {
                client.close();
            } catch (Exception e) {
                log.error("Mqtt close error: {}", e.getMessage(), e);
            }
        });
        MQTT_CLIENT_MAP.clear();
    }

    private class ReConnect implements Runnable {
        final IMqttAsyncClient client;

        final MqttConnectOptions options;

        ReConnect(IMqttAsyncClient client, MqttConnectOptions options) {
            this.client = client;
            this.options = options;
        }

        @Override
        public void run() {
            connect(client, options);
        }
    }
}
