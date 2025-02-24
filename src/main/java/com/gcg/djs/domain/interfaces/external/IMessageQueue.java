package com.gcg.djs.domain.interfaces.external;

public interface IMessageQueue<T> extends AutoCloseable {
    void EnqueueMessage(T message);
    T DequeueMessage();
}