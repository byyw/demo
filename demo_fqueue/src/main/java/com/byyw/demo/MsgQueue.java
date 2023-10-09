package com.byyw.demo;

import com.byyw.demo.queue.SMQ;

import cn.hutool.core.util.StrUtil;

public class MsgQueue {
    private static final String testDataTopic = "testData";

    /**
     * 向队列放入数据，支持多线程。
     */
    public static void putTestData(String msg) {
        SMQ.push(testDataTopic, msg);

    }

    /**
     * 从队列取出数据，支持多线程。
     */
    public static String getTestData() {
        String poll = SMQ.pop(testDataTopic);
        if (StrUtil.isNotBlank(poll)) {
            return poll;
        }
        return null;
    }

    /**
     * 获取队列大小
     */
    public static long getTestDataSize() {
        return SMQ.size(testDataTopic);
    }

    public static void main(String[] args) {
        // new Thread(() -> {
        // while (true) {
        // try {
        // putTestData("123");
        // Thread.sleep(5);
        // } catch (Exception e) {
        // // TODO: handle exception
        // }
        // }
        // }).start();

        // new Thread(() -> {
        // while (true) {

        // try {
        // getTestData();
        // Thread.sleep(10);
        // } catch (Exception e) {
        // // TODO: handle exception
        // }
        // }
        // }).start();

        // new Thread(() -> {
        // while (true) {
        // try {
        // System.out.println(getTestDataSize());
        // Thread.sleep(1000);
        // } catch (Exception e) {
        // }
        // }
        // }).start();
        for (int i = 0; i < 100; i++) {
            putTestData("testDataTopic");
        }
        SMQ.close();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }
}
