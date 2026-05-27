package com.octopus.demo.common.auth;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserContextTest {

    @Test
    void getUserId_returnsNullWhenNotSet() {
        assertNull(UserContext.getUserId());
    }

    @Test
    void setUserId_thenGetUserId_returnsValue() {
        UserContext.setUserId(42L);
        assertEquals(42L, UserContext.getUserId());
        UserContext.clear();
    }

    @Test
    void clear_removesUserId() {
        UserContext.setUserId(100L);
        UserContext.clear();
        assertNull(UserContext.getUserId());
    }

    @Test
    void setUserId_withNull_storesNull() {
        UserContext.setUserId(null);
        assertNull(UserContext.getUserId());
        UserContext.clear();
    }

    @Test
    void threadLocal_isolationBetweenThreads() throws InterruptedException {
        UserContext.setUserId(1L);

        Thread other = new Thread(() -> {
            assertNull(UserContext.getUserId());
            UserContext.setUserId(2L);
            assertEquals(2L, UserContext.getUserId());
            UserContext.clear();
        });
        other.start();
        other.join();

        assertEquals(1L, UserContext.getUserId());
        UserContext.clear();
    }
}
