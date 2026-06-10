/**
 * Health check module providing a GET /health liveness endpoint.
 *
 * Includes:
 * - HealthResponse: immutable record with status field
 * - HealthController: REST controller exposing /health
 * - HealthAutoConfiguration: Spring Boot auto-configuration registering the controller
 *
 * This is a lightweight liveness probe, not a deep health check.
 * For advanced health monitoring (K8s readiness, component aggregation),
 * downstream applications should use Spring Boot Actuator.
 *
 * Path conflict: if your application maps a different controller class to /health,
 * set {@code octopus.health.enabled=false} or provide a HealthController-typed bean
 * to avoid ambiguous handler method errors.
 */
package com.octopus.demo.common.health;
