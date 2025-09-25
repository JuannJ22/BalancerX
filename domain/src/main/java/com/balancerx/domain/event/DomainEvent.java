package com.balancerx.domain.event;

import java.time.Instant;

public interface DomainEvent {
    Instant occurredOn();
}
