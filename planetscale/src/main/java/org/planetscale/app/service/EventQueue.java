package org.planetscale.app.service;

import java.util.function.Consumer;

public interface EventQueue {
    void registerConsumer(Consumer<Event> consumer);

    void publish(Event message);

    sealed interface Event {
    }

    record TotalUserCount(String requestId) implements Event {
    }

    record EmailDomainCount(String requestId) implements Event {
    }
}
