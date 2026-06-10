package com.octopus.demo.common.health;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("HealthResponse")
class HealthResponseTest {

    @Test
    @DisplayName("up() returns response with status UP")
    void up_returnsStatusUp() {
        HealthResponse response = HealthResponse.up();
        assertEquals("UP", response.status());
    }

    @Test
    @DisplayName("up() returns record equal to manually constructed instance")
    void up_equalsManualConstruction() {
        HealthResponse fromFactory = HealthResponse.up();
        HealthResponse manual = new HealthResponse("UP");
        assertEquals(manual, fromFactory);
    }

    @Test
    @DisplayName("toString() contains status value")
    void toString_containsStatus() {
        HealthResponse response = HealthResponse.up();
        assertTrue(response.toString().contains("UP"));
    }
}
