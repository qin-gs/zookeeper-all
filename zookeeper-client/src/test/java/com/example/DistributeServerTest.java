package com.example;

import org.apache.zookeeper.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@DisplayName("分布式服务器端")
public class DistributeServerTest {

    private static final Logger log = LoggerFactory.getLogger(DistributeServerTest.class);

    @Test
    public void test() throws Exception {
        // 获取 zookeeper 连接
        ZooKeeper zookeeper = getZookeeper();
        // 注册服务器到 zookeeper (创建服务器对应的节点)
        register(zookeeper, "server3");
        // 启动业务逻辑
        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private void register(ZooKeeper zookeeper, String hostName) throws InterruptedException, KeeperException {
        zookeeper.create("/servers/" + hostName, hostName.getBytes(StandardCharsets.UTF_8),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        log.info("{} 已上线", hostName);
    }

    public ZooKeeper getZookeeper() throws IOException {
        return new ZooKeeper("localhost:2181", 600, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {

            }
        });
    }
}
