package com.sipa.boot.lock.client.etcd;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;

/**
 * etcd 心跳任务：用于节点探活，节点重入
 * 
 * @author caszhou
 * @date 2023/4/20
 */
@Slf4j
class EtcdHeartbeatTask extends Thread {
    private final EtcdClient etcdClient;

    EtcdHeartbeatTask(EtcdClient etcdClient) {
        this.etcdClient = etcdClient;
    }

    @Override
    public void run() {
        while (true) {
            try {
                TimeUnit.SECONDS.sleep(10);
                testAvailableEtcdNodes();
                testBrokenEtcdNodes();
            } catch (Exception e) {
                log.error("EtcdHeartbeatTask.run", e);
            }
        }
    }

    /**
     * 节点探活
     */
    private void testAvailableEtcdNodes() {
        List<String> availableEtcdNodes = new ArrayList<>(etcdClient.getAvailableEtcdNodes());
        for (String etcdNode : availableEtcdNodes) {
            try {
                URI uri = new URI(etcdNode);
                testSocketConnect(uri.getHost(), uri.getPort());
            } catch (Exception e) {
                etcdClient.setBrokenEtcdNode(etcdNode);
                log.warn("Lock etcd node [{}] broken", etcdNode);
            }
        }
    }

    /**
     * 失联节点 重连
     */
    private void testBrokenEtcdNodes() {
        List<String> brokenEtcdNodes = new ArrayList<>(etcdClient.getBrokenEtcdNodes());
        for (String etcdNode : brokenEtcdNodes) {
            try {
                URI uri = new URI(etcdNode);
                testSocketConnect(uri.getHost(), uri.getPort());
                etcdClient.setAvailableEtcdNode(etcdNode);
                log.info("Lock etcd node [{}] available", etcdNode);
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 心跳链接
     */
    private void testSocketConnect(String host, int port) throws IOException {
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), 100);
        socket.close();
    }
}
