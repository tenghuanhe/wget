package com.github.axet.wget;

public class LimitThreadPoolTest {

    public static void main(String[] args) {
        try {
            LimitThreadPool l = new LimitThreadPool(1);

            for (int i = 0; i < 1000; i++) {
                l.blockExecute(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}