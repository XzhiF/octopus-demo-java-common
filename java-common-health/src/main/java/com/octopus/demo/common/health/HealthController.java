package com.octopus.demo.common.health;

import com.octopus.demo.common.bean.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller exposing GET /health liveness probe.
 * Stateless — no dependencies injected.
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public R<HealthResponse> health() {
        return R.ok(HealthResponse.up());
    }
}
