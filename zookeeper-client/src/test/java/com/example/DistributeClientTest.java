package com.example;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@DisplayName("分布式客户端")
public class DistributeClientTest {
    private static final Logger log = LoggerFactory.getLogger(DistributeClientTest.class);

    private ZooKeeper zookeeper;

    @Test
    public void test() throws Exception {
        // 获取 zookeeper 连接
        getZookeeper();
        // 监听 /servers 下子节点的增加删除
        List<String> servers = getServerList();

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private List<String> getServerList() throws InterruptedException, KeeperException {
        List<String> children = zookeeper.getChildren("/servers", true);

        List<String> servers = new ArrayList<>();
        for (String child : children) {
            byte[] data = zookeeper.getData("/servers/" + child, false, null);
            servers.add(new String(data));
        }
        log.info("{}", servers);
        return servers;
    }

    public void getZookeeper() throws IOException {
        zookeeper = new ZooKeeper("localhost:2181", 600, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    getServerList();
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
