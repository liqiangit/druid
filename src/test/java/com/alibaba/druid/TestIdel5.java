package com.alibaba.druid;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import javax.management.ObjectName;

import com.alibaba.druid.pool.DruidDataSource;

import junit.framework.TestCase;

public class TestIdel5 extends TestCase {

    public void test_idle2() throws Exception {
//    	OracleDriver driver=(OracleDriver)Class.forName("oracle.jdbc.OracleDriver").newInstance();
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@10.128.18.222:1521/orcl");
//        dataSource.setDriver(driver);
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUsername("cloudwalk");
        dataSource.setPassword("cloudwalk");
        dataSource.setValidationQuery("select 1 from dual");
        dataSource.setInitialSize(50);
        dataSource.setMaxActive(300);
        dataSource.setMinIdle(20);
        dataSource.setMinEvictableIdleTimeMillis(300000); 
        dataSource.setTimeBetweenEvictionRunsMillis(60000); 
        dataSource.setTestWhileIdle(true);
//        dataSource.setTestOnBorrow(false);
//        dataSource.setTestOnReturn(false);
//        dataSource.setValidationQueryTimeout(1);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        dataSource.setFilters("stat");

        ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource,
                                                                 new ObjectName("com.alibaba:type=DataSource"));

//        {
//            Assert.assertEquals(0, dataSource.getCreateCount());
//            Assert.assertEquals(0, dataSource.getActiveCount());
//
//            Connection conn = dataSource.getConnection();
//
//            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
//            Assert.assertEquals(1, dataSource.getActiveCount());
//
//            conn.close();
//            Assert.assertEquals(0, dataSource.getDestroyCount());
//            Assert.assertEquals(2, dataSource.getCreateCount());
//            Assert.assertEquals(0, dataSource.getActiveCount());
//        }
//
//        {
//            int count = 14;
//            Connection[] connections = new Connection[count];
//            for (int i = 0; i < count; ++i) {
//                connections[i] = dataSource.getConnection();
//                Assert.assertEquals(i + 1, dataSource.getActiveCount());
//            }
//
//            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
//
//            for (int i = 0; i < count; ++i) {
//                connections[i].close();
//                Assert.assertEquals(count - i - 1, dataSource.getActiveCount());
//            }
//
//            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
//            Assert.assertEquals(0, dataSource.getActiveCount());
//        }
        for (int i = 0; i < 300; ++i) {
            Connection conn = dataSource.getConnection();


            Thread.sleep(10);
            conn.close();
        }
        System.out.println("开始睡眠6分钟");
//        Thread.sleep(6*60*1000);//22分钟
        System.out.println("睡眠结束");
        concurrent(dataSource, 100, 1000);
        concurrent(dataSource, 1, 1000 * 1000);
        Thread.sleep(1000 * 10);
        concurrent(dataSource, 1, 1000 * 1000);
        Thread.sleep(1000 * 10);
        concurrent(dataSource, 1000, 1000 * 1000);


        Thread.sleep(1000 * 100);
        dataSource.close();

    }

    private void concurrent(final DruidDataSource dataSource, int threadCount, final int loopCount)
                                                                                                   throws InterruptedException {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < loopCount; ++i) {
                            Connection conn = null;
                            long start=System.currentTimeMillis();
                            conn = dataSource.getConnection();
                            long end=System.currentTimeMillis();
                            if(end-start>30*1000){
                            	System.out.println("等待30秒未拿到连接"+(end-start));
                            }
                            Thread.sleep(1000);
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }

        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }
        startLatch.countDown();
        System.out.println("concurrent start...");
        endLatch.await();
        System.out.println("concurrent end");
    }
}
