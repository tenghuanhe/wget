package com.github.axet.wget;

public class LimitThreadPoolTest {

    public static void main(String[] args) {
        try {
            LimitThreadPool l = new LimitThreadPool(1);

            for (int i = 0; i < 100000; i++) {
                System.out.println(i + "enter " + Thread.currentThread().getId());
                l.blockExecute(new Runnable() {
                    @Override
                    public void run() {
                        System.out.println("done " + Thread.currentThread().getId());
                    }
                });
                System.out.println("exit " + Thread.currentThread().getId());
            }
            
            l.waitUntilTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}