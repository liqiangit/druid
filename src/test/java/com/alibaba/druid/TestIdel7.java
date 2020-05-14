package com.alibaba.druid;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.management.ObjectName;

import com.alibaba.druid.pool.DruidDataSource;

import junit.framework.TestCase;
//root/lq_!QAZxsw2
//lq_!QAZxsw2
//顾圣意
public class TestIdel7 extends TestCase {

    public void test_idle2() throws Exception {
    	System.out.println((1588853063802L-1588852943818L)/1000L);
//    	PooledConnection connection
//    	OracleDriver driver=(OracleDriver)Class.forName("oracle.jdbc.OracleDriver").newInstance();
        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@10.128.18.222:1521/orcl");
//        dataSource.setDriver(driver);
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
//        dataSource.setUsername("cloudwalk");
//        dataSource.setPassword("cloudwalk");
        dataSource.setUsername("ibis");
        dataSource.setPassword("12345678");
        dataSource.setValidationQuery("select 1 from dual");
//        初始化时建立物理连接的个数。初始化发生在显示调用init方法，或者第一次getConnection时
        dataSource.setInitialSize(50);
//        最大连接池数量
        dataSource.setMaxActive(300);
//        最小连接池数量
        dataSource.setMinIdle(20);
        
//        dataSource.setRemoveAbandoned(true);
//        dataSource.setRemoveAbandonedTimeout(80);
//        dataSource.setLogAbandoned(true);

//        连接保持空闲而不被驱逐的最长时间
        dataSource.setMinEvictableIdleTimeMillis(300000); 
        dataSource.setMaxEvictableIdleTimeMillis(420000);
        dataSource.setTimeBetweenEvictionRunsMillis(60000); 
//        有两个含义：
//        1) Destroy线程会检测连接的间隔时间，如果连接空闲时间大于等于minEvictableIdleTimeMillis则关闭物理连接 2) testWhileIdle的判断依据，详细看testWhileIdle属性的说明
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setTestOnReturn(false);
//        dataSource.setConnectionProperties("connectTimeout=900000;socketTimeout=900000");
//        dataSource.setConnectionProperties("oracle.net.CONNECT_TIMEOUT=900000;oracle.jdbc.ReadTimeout=900000");
//        dataSource.setValidationQueryTimeout(2000);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxPoolPreparedStatementPerConnectionSize(20);
        dataSource.setFilters("stat");
        //超时不还杀死
        System.out.println(String.format("removeAbandoned=%s", dataSource.isRemoveAbandoned()));
        System.out.println(String.format("maxEvictableIdleTimeMillis=%s", dataSource.getMaxEvictableIdleTimeMillis()));

        
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
        
//        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(300);
//		final CountDownLatch endLatch = new CountDownLatch(300);
//
//        for (int i = 0; i < 300; ++i) {
//        	cachedThreadPool.execute(new Runnable() {
//				
//				@Override
//				public void run() {
//		            try {
//						Connection conn = dataSource.getConnection();
//						//
//						String str=dataSource.getPoolingCount()+"\t"+dataSource.getActiveCount()+"\t"+dataSource.getConnectCount();
//						Thread.sleep(10);
//						conn.close();
//						System.out.println(str+"-------------------"+dataSource.getPoolingCount()+"\t"+dataSource.getActiveCount()+"\t"+dataSource.getConnectCount());
//						System.out.println();
//						endLatch.countDown();
//					} catch (SQLException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					} catch (InterruptedException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}					
//				}
//			});
//
//        }
//        endLatch.await();
//        Thread.sleep(5000);
        dataSource.init();
        System.out.println(+System.currentTimeMillis());
        System.out.println(dataSource.getPoolingCount());
        System.out.println(dataSource.getConnectCount());
        System.out.println("开始睡眠6分钟");
        Thread.sleep(5*60*1000);//22分钟
        System.out.println("睡眠结束");
        System.out.println(dataSource.getPoolingCount());
        System.out.println(dataSource.getConnectCount());
//        concurrent(dataSource, 100, 1000);
//        concurrent(dataSource, 1, 1000 * 1000);
//        Thread.sleep(1000 * 10);
//        concurrent(dataSource, 1, 1000 * 1000);
//        Thread.sleep(1000 * 10);
//        concurrent(dataSource, 1000, 1000 * 1000);


        Thread.sleep(1000 * 100);
        dataSource.close();

    }
    private void concurrent(final DruidDataSource dataSource, int threadCount, final int loopCount)
			throws InterruptedException {
        ExecutorService cachedThreadPool = Executors.newFixedThreadPool(threadCount);
		final CountDownLatch endLatch = new CountDownLatch(loopCount);

        for (int i = 0; i < loopCount; i++) {
        	cachedThreadPool.execute(new Runnable() {
				
				@Override
				public void run() {
					Connection conn = null;
					Statement stmt = null;
					try {
						long start = System.currentTimeMillis();
						conn = dataSource.getConnection();
						conn.setAutoCommit(false);
						stmt = conn.createStatement();
//						stmt.executeUpdate(String.format("insert into IBIS_TEMP(ID,REMARK) VALUES('%s','%s')",
//								new Random().nextInt(10) + "", UUID.randomUUID().toString()));
						stmt.executeUpdate("select 1 from IBIS_TEMP");
						stmt.close();
						long end = System.currentTimeMillis();
//						System.out.println(end - start);
						if (end - start > 30 * 1000) {
							System.out.println("等待30秒未拿到连接" + (end - start));
						}
						if (end - start > 5*60 * 1000) {
							System.err.println("等待5分钟未拿到连接" + (end - start));
						}
						Thread.sleep(1000);
						conn.commit();
					} catch (Exception e) {
						if (conn != null) {
							try {
								conn.rollback();
							} catch (SQLException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
						e.printStackTrace();
					} finally {
						endLatch.countDown();
						if (stmt != null) {
							try {
								stmt.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						if (conn != null) {
							try {
								conn.close();
							} catch (SQLException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
			}
			});
		}
        endLatch.await();
    }
    private void concurrent2(final DruidDataSource dataSource, int threadCount, final int loopCount)
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
							Statement stmt = null;
							try {
								long start = System.currentTimeMillis();
								conn = dataSource.getConnection();
								conn.setAutoCommit(false);
								stmt = conn.createStatement();
								stmt.executeUpdate(String.format("insert into IBIS_TEMP(ID,REMARK) VALUES('%s','%s')",
										i + "", UUID.randomUUID().toString()));
								stmt.close();
								long end = System.currentTimeMillis();
								System.out.println(end - start);
								if (end - start > 30 * 1000) {
									System.err.println("等待30秒未拿到连接" + (end - start));
								}
								Thread.sleep(1000);
								conn.commit();
							} catch (Exception e) {
								if (conn != null) {
									conn.rollback();
								}
								e.printStackTrace();
							} finally {
								if (stmt != null) {
									stmt.close();
								}
								if (conn != null) {
									conn.close();
								}
							}
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
