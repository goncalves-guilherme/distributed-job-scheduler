package com.gcg.djs.application.restapi;

import com.gcg.djs.domain.interfaces.external.ILog;
import org.slf4j.Logger;

public final class Slf4jLog implements ILog {
    private final Logger logger;
    public Slf4jLog(Logger logger){
        this.logger = logger;
    }

    @Override
    public void log(String message) {
        this.logger.info(message);
    }

    @Override
    public void logError(String message, Throwable throwable) {
        this.logger.error(message, throwable);
    }

    @Override
    public void logWarning(String message) {
        this.logger.warn(message);
    }
}
