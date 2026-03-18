package dev.zwazel.game.game.websocket;

import dev.zwazel.game.security.config.ApplicationSecurityConfig;
import org.springframework.web.bind.annotation.GetMapping;

public class WebSocketTest {
    @GetMapping("/testrun")
    private String testRun() {
        ApplicationSecurityConfig applicationSecurityConfig = new ApplicationSecurityConfig();
        return "a";
    }
}
