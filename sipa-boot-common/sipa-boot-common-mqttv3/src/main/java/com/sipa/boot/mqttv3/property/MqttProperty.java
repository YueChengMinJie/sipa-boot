package com.sipa.boot.mqttv3.property;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;

import com.sipa.boot.core.constant.SipaBootConstant;

import lombok.Getter;
import lombok.Setter;

/**
 * @author caszhou
 * @date 2022/6/23
 */
@Setter
@Getter
@ConfigurationProperties(prefix = SipaBootConstant.Mqttv3.PREFIX)
public class MqttProperty extends ConnectionProperty {
    private Map<String, ConnectionProperty> clients = new LinkedHashMap<>();

    /**
     * 遍历所有的客户端配置
     *
     * @param biConsumer
     *            String, MqttConnectOptions
     */
    public void forEach(BiConsumer<String, MqttConnectOptions> biConsumer) {
        MqttConnectOptions defaultOptions = toOptions();
        if (defaultOptions != null) {
            biConsumer.accept(getClientId(), defaultOptions);
        }
        if (clients != null && !clients.isEmpty()) {
            // 先遍历一遍处理下clientId冲突的问题
            String[] clientIds = clients.keySet().toArray(new String[0]);
            for (String clientId : clientIds) {
                ConnectionProperty properties = clients.get(clientId);
                String localClientId = properties.getClientId();
                if (StringUtils.hasText(localClientId) && !localClientId.equals(clientId)) {
                    clients.remove(clientId);
                    clients.put(localClientId, properties);
                } else {
                    properties.setClientId(clientId);
                }
            }
            clients.forEach((id, prop) -> {
                MqttConnectOptions options = toOptions(id);
                if (options != null) {
                    biConsumer.accept(id, options);
                }
            });
        }
    }

    /**
     * 转为 MqttConnectOptions
     *
     * @return MqttConnectOptions对象
     */
    private MqttConnectOptions toOptions() {
        if (StringUtils.hasText(getClientId())) {
            return toOptions(getClientId());
        } else {
            return null;
        }
    }

    /**
     * 转为 MqttConnectOptions
     *
     * @return MqttConnectOptions对象
     */
    public MqttConnectOptions toOptions(String clientId) {
        ConnectionProperty properties = clients.get(clientId);
        if (properties == null) {
            if (clientId.equals(getClientId())) {
                properties = this;
            } else {
                return null;
            }
        }
        merge(properties);
        return toOptions(properties);
    }

    private void merge(ConnectionProperty target) {
        target.setUri(mergeValue(getUri(), target.getUri(), new String[] {"tcp://127.0.0.1:1883"}));
        target.setUsername(mergeValue(getUsername(), target.getUsername(), null));
        target.setPassword(mergeValue(getPassword(), target.getPassword(), null));
        target.setDefaultPublishQos(mergeValue(getDefaultPublishQos(), target.getDefaultPublishQos(), 2));
        target.setMaxReconnectDelay(mergeValue(getMaxReconnectDelay(), target.getMaxReconnectDelay(), 60));
        target.setKeepAliveInterval(mergeValue(getKeepAliveInterval(), target.getKeepAliveInterval(), 60));
        target.setConnectionTimeout(mergeValue(getConnectionTimeout(), target.getConnectionTimeout(), 30));
        target
            .setExecutorServiceTimeout(mergeValue(getExecutorServiceTimeout(), target.getExecutorServiceTimeout(), 10));
        target.setCleanSession(mergeValue(getCleanSession(), target.getCleanSession(), true));
        target.setAutomaticReconnect(mergeValue(getAutomaticReconnect(), target.getAutomaticReconnect(), true));
        target.setWill(mergeValue(getWill(), target.getWill(), null));
        target.setEnableSharedSubscription(
            mergeValue(getEnableSharedSubscription(), target.getEnableSharedSubscription(), true));
        if (target.getWill() != null && getWill() != null) {
            WillProperty will = getWill();
            WillProperty targetWill = target.getWill();
            targetWill.setTopic(mergeValue(will.getTopic(), targetWill.getTopic(), null));
            targetWill.setPayload(mergeValue(will.getPayload(), targetWill.getPayload(), null));
            targetWill.setQos(mergeValue(will.getQos(), targetWill.getQos(), 2));
            targetWill.setRetained(mergeValue(will.getRetained(), targetWill.getRetained(), false));
        }
    }

    private <T> T mergeValue(T parentValue, T targetValue, T defaultValue) {
        if (parentValue == null && targetValue == null) {
            return defaultValue;
        } else {
            return Objects.requireNonNullElse(targetValue, parentValue);
        }
    }

    private MqttConnectOptions toOptions(ConnectionProperty properties) {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setMaxInflight(properties.getMaxInflight());
        options.setMaxReconnectDelay(properties.getMaxReconnectDelay() * 1000);
        options.setKeepAliveInterval(properties.getKeepAliveInterval());
        // 暂时不支持配置，使用keepalive时间
        options.setConnectionTimeout(properties.getKeepAliveInterval());
        options.setCleanSession(properties.getCleanSession());
        options.setAutomaticReconnect(properties.getAutomaticReconnect());
        options.setExecutorServiceTimeout(properties.getExecutorServiceTimeout());
        options.setServerURIs(properties.getUri());
        if (StringUtils.hasText(properties.getUsername()) && StringUtils.hasText(properties.getPassword())) {
            options.setUserName(properties.getUsername());
            options.setPassword(properties.getPassword().toCharArray());
        }
        if (properties.getWill() != null) {
            WillProperty will = properties.getWill();
            if (StringUtils.hasText(will.getTopic()) && StringUtils.hasText(will.getPayload())) {
                options.setWill(will.getTopic(), will.getPayload().getBytes(StandardCharsets.UTF_8), will.getQos(),
                    will.getRetained());
            }
        }
        return options;
    }

    public int getDefaultPublishQos(String clientId) {
        if (clientId.equals(getClientId())) {
            return getDefaultPublishQos();
        } else {
            ConnectionProperty properties = clients.get(clientId);
            if (properties == null) {
                return 0;
            }
            return properties.getDefaultPublishQos();
        }
    }

    public boolean isSharedEnable(String clientId) {
        if (clientId.equals(getClientId())) {
            return getEnableSharedSubscription();
        } else {
            ConnectionProperty properties = clients.get(clientId);
            if (properties == null) {
                return false;
            }
            return properties.getEnableSharedSubscription();
        }
    }
}
