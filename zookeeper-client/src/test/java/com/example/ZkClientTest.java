package com.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@DisplayName("ziClient 测试")
public class ZkClientTest {

    private static final Logger log = LoggerFactory.getLogger(ZkClientTest.class);
    private static ZooKeeper zkClient;

    @BeforeAll
    public static void test() throws IOException {
        // 不能有空格
        String connect = "localhost:2181";
        // String connect = "localhost:2181,localhost:2182";
        zkClient = new ZooKeeper(connect, 60, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                try {
                    List<String> children = zkClient.getChildren("/first", true);
                    log.info("children: {}", children);
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Test
    public void createNode() throws InterruptedException, KeeperException {
        // 创建一个节点
        String s = zkClient.create("/first/part10", "qqq".getBytes(StandardCharsets.UTF_8),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
    }

    @Test
    public void watch() throws Exception {
        Stat exists = zkClient.exists("/first", true);

        List<String> children = zkClient.getChildren("/first", true);
        log.info("{}", children);

        System.in.read();
    }


}
