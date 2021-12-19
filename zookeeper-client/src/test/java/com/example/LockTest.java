package com.example;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.junit.jupiter.api.DisplayName;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@DisplayName("分布式锁")
public class LockTest {

    private ZooKeeper zookeeper;
    private String waitPath;
    private final CountDownLatch waitLatch = new CountDownLatch(1);
    private final CountDownLatch connectLatch = new CountDownLatch(1);
    private String currentNode;

    public void t() {

    }

    public void test() throws Exception {
        // 获取连接
        getZookeeper();
        connectLatch.await();
        // 判断 /locks 节点是否存在
        Stat exists = zookeeper.exists("/locks", false);
        if (exists != null) {
            // 创建根节点
            zookeeper.create("/locks", "locks".getBytes(StandardCharsets.UTF_8),
                    ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void lock() throws InterruptedException, KeeperException {
        // 创建临时带序号的节点
        currentNode = zookeeper.create("/locks/seq-", null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        // 判断当前节点是不是最小的，是的话获取到锁，否则监听上一个节点
        List<String> children = zookeeper.getChildren("/locks", false);
        if (children.size() == 1) {
        } else {
            Collections.sort(children);
            // 获取节点在集合中的位置
            String seq = currentNode.substring("/locks/".length());
            int i = children.indexOf(seq);
            if (i < 0) {
                throw new RuntimeException();
            } else if (i == 0) {
            } else {
                // 监听前一个节点
                waitPath = "/locks/" + children.get(i - 1);
                zookeeper.getData(waitPath, true, new Stat());
                // 等待前一个节点完成
                waitLatch.await();
            }
        }
    }

    public void unlock() throws InterruptedException, KeeperException {
        // 删除节点，通知下一个节点
        zookeeper.delete(currentNode, -1);
    }

    public void getZookeeper() throws IOException {
        zookeeper = new ZooKeeper("localhost:2181", 600, new Watcher() {
            @Override
            public void process(WatchedEvent watchedEvent) {
                if (watchedEvent.getState() == Event.KeeperState.SyncConnected) {
                    connectLatch.countDown();
                }
                if (watchedEvent.getType().equals(Event.EventType.NodeDeleted)
                        && watchedEvent.getPath().equals(waitPath)) {
                    waitLatch.countDown();
                }

            }
        });
    }
}
