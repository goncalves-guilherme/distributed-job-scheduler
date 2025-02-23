package com.gcg.djs.domain.interfaces.external;

public interface IMessageQueue<T> {
    void EnqueueMessage(T message);
    T DequeueMessage();
}