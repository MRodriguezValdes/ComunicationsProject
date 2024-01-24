package org.mybatis;

public class HC implements Runnable {
    private CH channel;

    public HC (CH channel) {
        this.channel = channel;
    }

    @Override
    public void run() {
        // Implement health checking logic
    }
}

