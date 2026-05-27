package com.octopus.demo.common.auth;

import org.junit.jupiter.api.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.junit.jupiter.api.Assertions.*;

class RequireAuthTest {

    @Test
    void annotation_targetsMethodAndType() {
        Target target = RequireAuth.class.getAnnotation(Target.class);
        ElementType[] value = target.value();
        assertTrue(java.util.Set.of(value).contains(ElementType.METHOD));
        assertTrue(java.util.Set.of(value).contains(ElementType.TYPE));
    }

    @Test
    void annotation_retentionIsRuntime() {
        Retention retention = RequireAuth.class.getAnnotation(Retention.class);
        assertEquals(RetentionPolicy.RUNTIME, retention.value());
    }

    @Test
    void required_defaultsToTrue() {
        RequireAuth auth = DummyClass.class.getAnnotation(RequireAuth.class);
        assertTrue(auth.required());
    }

    @RequireAuth
    static class DummyClass {}
}
