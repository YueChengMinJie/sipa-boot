package com.sipa.boot.mqttv3.publisher;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.springframework.util.Assert;

import com.sipa.boot.mqttv3.core.MqttConnector;
import com.sipa.boot.mqttv3.core.MqttConversionService;

public class MqttPublisher {
    /**
     * 发送消息到指定主题 qos=1
     *
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String topic, Object payload) throws MqttException {
        send(MqttConnector.defaultClientId, topic, payload, MqttConnector.defaultPublishQos, false, null);
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @param callback
     *            消息发送完成后的回调
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String topic, Object payload, IMqttActionListener callback) throws MqttException {
        send(MqttConnector.defaultClientId, topic, payload, MqttConnector.defaultPublishQos, false, callback);
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param clientId
     *            客户端ID
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String clientId, String topic, Object payload) throws MqttException {
        send(clientId, topic, payload, MqttConnector.getDefaultQosById(clientId), false, null);
    }

    /**
     * 发送消息到指定主题 qos=1
     *
     * @param clientId
     *            客户端ID
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @param callback
     *            消息发送完成后的回调
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String clientId, String topic, Object payload, IMqttActionListener callback) throws MqttException {
        send(clientId, topic, payload, MqttConnector.getDefaultQosById(clientId), false, callback);
    }

    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @param qos
     *            服务质量
     * @param retained
     *            保留消息
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String topic, Object payload, int qos, boolean retained) throws MqttException {
        send(MqttConnector.defaultClientId, topic, payload, qos, retained, null);
    }

    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param clientId
     *            客户端ID
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @param qos
     *            服务质量
     * @param retained
     *            保留消息
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String clientId, String topic, Object payload, int qos, boolean retained) throws MqttException {
        send(clientId, topic, payload, qos, retained, null);
    }

    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @param qos
     *            服务质量
     * @param retained
     *            保留消息
     * @param callback
     *            消息发送完成后的回调
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String topic, Object payload, int qos, boolean retained, IMqttActionListener callback)
        throws MqttException {
        send(MqttConnector.defaultClientId, topic, payload, qos, retained, callback);
    }

    /**
     * 发送消息到指定主题, 指定qos, retained
     *
     * @param clientId
     *            客户端ID
     * @param topic
     *            主题
     * @param payload
     *            消息内容
     * @param qos
     *            服务质量
     * @param retained
     *            保留消息
     * @param callback
     *            消息发送完成后的回调
     * @throws IllegalArgumentException
     *             if topic is empty
     * @throws NullPointerException
     *             if client not exists
     */
    public void send(String clientId, String topic, Object payload, int qos, boolean retained,
        IMqttActionListener callback) throws MqttException {
        Assert.isTrue(topic != null && !topic.trim().isEmpty(), "topic cannot be blank.");
        IMqttAsyncClient client = MqttConnector.getClientById(clientId);
        if (client == null) {
            return;
        }
        byte[] bytes = MqttConversionService.getSharedInstance().toBytes(payload);
        if (bytes == null) {
            return;
        }
        MqttMessage message = toMessage(bytes, qos, retained);
        client.publish(topic, message, null, callback);
    }

    private MqttMessage toMessage(byte[] payload, int qos, boolean retained) {
        MqttMessage message = new MqttMessage();
        message.setPayload(payload);
        message.setQos(qos);
        message.setRetained(retained);
        return message;
    }
}
