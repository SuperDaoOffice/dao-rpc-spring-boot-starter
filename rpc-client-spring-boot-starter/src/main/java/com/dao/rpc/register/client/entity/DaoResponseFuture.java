package com.dao.rpc.register.client.entity;

import com.dao.rpc.common.exception.ExceptionEntity;
import com.dao.rpc.register.client.exception.DaoRegisterClientException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class DaoResponseFuture<T> implements Future<T> {

    private CountDownLatch latch = new CountDownLatch(1);

    private T response;

    private ExceptionEntity exceptionEntity;

    public void setResponse(T response) {
        this.response = response;
        latch.countDown();
    }

    public void setExceptionEntity(ExceptionEntity exceptionEntity) {
        this.exceptionEntity = exceptionEntity;
        latch.countDown();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null || exceptionEntity != null;
    }

    @Override
    public T get() throws InterruptedException {
        latch.await();
        validateException();
        return response;
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException {
        if (latch.await(timeout, unit)) {
            validateException();
            return response;
        }
        return null;
    }

    private void validateException() {
        if (exceptionEntity != null) {
            throw new DaoRegisterClientException("code: " + exceptionEntity.getCode() + "msg: " + exceptionEntity.getMsg());
        }
    }
}
