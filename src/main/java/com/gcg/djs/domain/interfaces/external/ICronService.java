package com.gcg.djs.domain.interfaces.external;

import java.time.Instant;

public interface ICronService {
    Instant calculateNextRun(String cronExpression);
}
