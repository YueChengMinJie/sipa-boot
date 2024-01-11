package com.sipa.boot.mqttv3.core;

import java.util.Set;

import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.util.Assert;

import com.sipa.boot.mqttv3.property.ConnectionProperty;
import com.sipa.boot.mqttv3.property.MqttProperty;
import com.sipa.boot.mqttv3.subscriber.TopicPair;

/**
 * @author caszhou
 * @date 2022/6/23
 */
public class MqttAdapter {
    private MqttProperty mqttProperty;

    public final void setProperties(MqttProperty mqttProperty) {
        this.mqttProperty = mqttProperty;

        ClientRegistry clientRegistry = new ClientRegistry();
        beforeCreate(clientRegistry);
    }

    /**
     * 在创建客户端之前, 增删改客户端配置.
     *
     * @param registry
     *            ClientRegistry 添加或修改配置
     */
    public void beforeCreate(ClientRegistry registry) {
        //
    }

    /**
     * 创建客户端.
     *
     * @param clientId
     *            客户端ID
     * @param options
     *            MqttConnectOptions
     */
    public IMqttAsyncClient postCreate(String clientId, MqttConnectOptions options) throws MqttException {
        return new MqttAsyncClient(options.getServerURIs()[0], clientId, new MemoryPersistence());
    }

    /**
     * 在创建客户端后, 订阅主题前, 修改订阅的主题.
     *
     * @param clientId
     *            客户端ID
     * @param topicPairs
     *            订阅主题
     */
    public void beforeSubscribe(String clientId, Set<TopicPair> topicPairs) {
        //
    }

    public class ClientRegistry {
        /**
         * 添加新的client, 添加到配置末尾.
         *
         * @param clientId
         *            客户端ID
         * @param uri
         *            连接地址
         * @return ClientRegistry
         */
        public ClientRegistry add(String clientId, String... uri) {
            Assert.notNull(clientId, "clientId can not be null.");
            ConnectionProperty properties = new ConnectionProperty();
            if (uri != null && uri.length > 0) {
                properties.setUri(uri);
            } else {
                properties.setUri(null);
            }
            properties.setClientId(clientId);
            mqttProperty.getClients().put(clientId, properties);
            resetClientId(clientId);
            return this;
        }

        /**
         * 添加新的client, 添加到配置末尾.
         *
         * @param clientId
         *            客户端ID, 优先级比配置信息中的高
         * @param properties
         *            配置信息
         * @return ClientRegistry
         */
        public ClientRegistry add(String clientId, ConnectionProperty properties) {
            Assert.notNull(clientId, "clientId can not be null.");
            Assert.notNull(properties, "properties can not be null.");
            properties.setClientId(clientId);
            mqttProperty.getClients().put(clientId, properties);
            resetClientId(clientId);
            return this;
        }

        /**
         * 删除指定的客户端ID配置.
         *
         * @param clientId
         *            客户端ID
         * @return ClientRegistry
         */
        public ClientRegistry remove(String clientId) {
            Assert.notNull(clientId, "clientId can not be null.");
            mqttProperty.getClients().remove(clientId);
            resetClientId(clientId);
            return this;
        }

        /**
         * 清空所有客户端配置.
         *
         * @return ClientRegistry
         */
        public ClientRegistry clear() {
            mqttProperty.setClientId(null);
            mqttProperty.getClients().clear();
            return this;
        }

        /**
         * 设置默认.
         */
        public ClientRegistry setDefault(ConnectionProperty properties) {
            mqttProperty.setClientId(properties.getClientId());
            mqttProperty.setUsername(properties.getUsername());
            mqttProperty.setWill(properties.getWill());
            mqttProperty.setAutomaticReconnect(properties.getAutomaticReconnect());
            mqttProperty.setCleanSession(properties.getCleanSession());
            mqttProperty.setConnectionTimeout(properties.getConnectionTimeout());
            mqttProperty.setExecutorServiceTimeout(properties.getExecutorServiceTimeout());
            mqttProperty.setKeepAliveInterval(properties.getKeepAliveInterval());
            mqttProperty.setMaxReconnectDelay(properties.getMaxReconnectDelay());
            mqttProperty.setPassword(properties.getPassword());
            mqttProperty.setUri(properties.getUri());
            mqttProperty.setEnableSharedSubscription(properties.getEnableSharedSubscription());
            mqttProperty.setDefaultPublishQos(properties.getDefaultPublishQos());
            return this;
        }

        private void resetClientId(String clientId) {
            if (mqttProperty.getClientId() != null && clientId.equals(mqttProperty.getClientId())) {
                // 如果 clientId 和默认的 clientId 一样 则将默认的clientId清除掉
                // 原因：修改后的是通过clients集合保存的，如果与默认的clientId冲突, 则将默认的排除
                mqttProperty.setClientId(null);
            }
        }
    }
}
