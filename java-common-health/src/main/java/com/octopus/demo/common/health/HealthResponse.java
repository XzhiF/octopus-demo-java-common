package com.octopus.demo.common.health;

/**
 * Immutable health check response indicating service liveness.
 * Only contains status — timestamp is provided by the outer {@code R<T>} wrapper.
 */
public record HealthResponse(String status) {

    /**
     * Factory method returning a healthy (UP) response.
     */
    public static HealthResponse up() {
        return new HealthResponse("UP");
    }
}
