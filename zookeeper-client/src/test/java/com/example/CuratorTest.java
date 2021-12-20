package com.example;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.concurrent.TimeUnit;

@DisplayName("curator 功能测试")
public class CuratorTest {

    @Test
    public void test() throws InterruptedException {
        // 可重入锁
        InterProcessMutex mutex1 = new InterProcessMutex(getCuratorFramework(), "/locks");
        InterProcessMutex mutex2 = new InterProcessMutex(getCuratorFramework(), "/locks");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mutex1.acquire();
                    System.out.println("线程1 获取到锁");

                    mutex1.acquire();
                    System.out.println("线程1 获取到锁");

                    TimeUnit.SECONDS.sleep(2);

                    mutex1.release();
                    System.out.println("线程1 释放锁");

                    mutex1.release();
                    System.out.println("线程1 释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mutex2.acquire();
                    System.out.println("线程2 获取到锁");

                    mutex2.acquire();
                    System.out.println("线程2 获取到锁");

                    TimeUnit.SECONDS.sleep(2);

                    mutex2.release();
                    System.out.println("线程2 释放锁");

                    mutex2.release();
                    System.out.println("线程2 释放锁");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        TimeUnit.SECONDS.sleep(Integer.MAX_VALUE);
    }

    private CuratorFramework getCuratorFramework() {
        ExponentialBackoffRetry retry = new ExponentialBackoffRetry(3000, 3);
        CuratorFramework client = CuratorFrameworkFactory.builder().connectString("localhost:2181")
                .connectionTimeoutMs(6000)
                .sessionTimeoutMs(6000)
                .retryPolicy(retry)
                .build();
        // 启动客户端
        client.start();
        System.out.println("zookeeper 启动成功");
        return client;
    }
}
