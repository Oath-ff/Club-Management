package com.project;


import org.junit.jupiter.api.Test;

public class ThreadLocalTes {

    @Test
    public void testThreadLocalSetAndGet() {

      //提供一个
      ThreadLocal tl =new ThreadLocal();

      //开启两个线程
      new Thread(()->{
          tl.set("xiaofang");
          System.out.println(Thread.currentThread().getName()+":"+tl.get());
          System.out.println(Thread.currentThread().getName()+":"+tl.get());
          System.out.println(Thread.currentThread().getName()+":"+tl.get());
      },"lanse").start();
      new Thread(()->{
          tl.set("xiaozhe");
          System.out.println(Thread.currentThread().getName()+":"+tl.get());
          System.out.println(Thread.currentThread().getName()+":"+tl.get());
          System.out.println(Thread.currentThread().getName()+":"+tl.get());
      },"lvse").start();

    }

}
