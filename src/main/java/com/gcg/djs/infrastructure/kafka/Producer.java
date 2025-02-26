package com.gcg.djs.infrastructure.kafka;

public interface Producer<T> extends AutoCloseable {
    void produce(T entity);
}
