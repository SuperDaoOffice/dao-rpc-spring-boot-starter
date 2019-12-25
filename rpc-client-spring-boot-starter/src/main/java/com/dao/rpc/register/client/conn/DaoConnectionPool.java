package com.dao.rpc.register.client.conn;

import com.dao.rpc.common.exception.DaoException;
import com.dao.rpc.common.rpc.RemoteAddress;
import com.dao.rpc.register.client.config.DaoConnectionPoolProperties;
import com.dao.rpc.register.client.config.DaoConnectionProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author HuChiHui
 * @date 2019/12/24 上午 11:31
 * @description
 */
public class DaoConnectionPool {

    private static final Logger log = LoggerFactory.getLogger(DaoConnectionPool.class);

    private ArrayBlockingQueue<DaoConnection> idleQueue;

    private RemoteAddress remoteAddress;

    private DaoConnectionProperties connectionProperties;

    private AtomicInteger newConnectionCount = new AtomicInteger(0);

    private int maxCount;

    private int coreCount;

    private long maxWaitTime;

    private long maxIdleTime;

    private ReentrantLock lock;

    private ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1,
            new DaoThreadFactory("clearExpireConnection-job"));


    public DaoConnectionPool(DaoConnectionPoolProperties poolProperties, RemoteAddress remoteAddress) throws InterruptedException {
        validate(poolProperties);
        this.remoteAddress = remoteAddress;
        this.maxCount = poolProperties.getMaxCount();
        this.coreCount = poolProperties.getCoreCount();
        this.maxWaitTime = poolProperties.getMaxWaitTime();
        this.maxIdleTime = poolProperties.getMaxIdleTime();
        this.connectionProperties = poolProperties.getConnectionProperties();
        idleQueue = new ArrayBlockingQueue<>(maxCount, false);
        if (poolProperties.getInitCount() > 0) {
            for (int i = 0; i < poolProperties.getInitCount(); i++) {
                idleQueue.offer(createConnection());
            }
        }
        lock = new ReentrantLock();
        //开启线程，执行清理任务
        executor.scheduleAtFixedRate(new ClearConnectionTask(), 5000, 5000, TimeUnit.MILLISECONDS);
    }


    private void validate(DaoConnectionPoolProperties poolProperties) {
        if (poolProperties == null) {
            throw new DaoException("连接池属性为空");
        }
        if (poolProperties.getCoreCount() > poolProperties.getMaxCount()) {
            throw new DaoException("连接池核心连接数大于最大连接数");
        }
        if (poolProperties.getInitCount() > poolProperties.getMaxCount()) {
            throw new DaoException("连接池初始连接数大于最大连接数");
        }

    }

    /**
     * 获取连接
     *
     * @return
     * @throws InterruptedException
     */
    public DaoConnection getConnection() throws InterruptedException {
        DaoConnection connection = idleQueue.poll();
        if (connection == null) {
            long lockTimeMills = System.currentTimeMillis();
            if (lock.tryLock(maxWaitTime, TimeUnit.MILLISECONDS)) {
                try {
                    if (newConnectionCount.get() >= maxCount) {
                        System.out.println(Thread.currentThread().getName() + "无连接可用, 等待连接");
                        connection = idleQueue.poll(maxWaitTime - (System.currentTimeMillis() - lockTimeMills),
                                TimeUnit.MILLISECONDS);
                    } else {
                        System.out.println(Thread.currentThread().getName() + "无连接可用, 新建连接");
                        connection = createConnection();
                    }

                } finally {
                    lock.unlock();
                }
            }
        }
        if (connection != null) {
            connection.setExpireTime(System.currentTimeMillis() + this.maxIdleTime);
        }
        return connection;

    }


    /**
     * 创建连接，有过期时间
     *
     * @return
     */
    private DaoConnection createConnection() throws InterruptedException {
        long expireTime = System.currentTimeMillis() + this.maxIdleTime;
        DaoConnection connection = DaoConnection.createConnection(connectionProperties, expireTime, idleQueue);
        connection.connect(remoteAddress);
        newConnectionCount.getAndIncrement();
        return connection;
    }

    class ClearConnectionTask implements Runnable {


        @Override
        public void run() {
            if (idleQueue.size() > coreCount) {
                System.out.println(Thread.currentThread().getName() + " 开始清理任务");
                Iterator<DaoConnection> iterator = idleQueue.iterator();
                while (iterator.hasNext()) {
                    DaoConnection connection = iterator.next();
                    if (connection.getExpireTime() <= System.currentTimeMillis()) {
                        if (idleQueue.size() > coreCount) {
                            connection.close();
                            iterator.remove();
                            newConnectionCount.decrementAndGet();
                        }
                    }
                }
                System.out.println("清理结果： 队列空闲连接数： " + idleQueue.size());
            }
        }
    }
}
