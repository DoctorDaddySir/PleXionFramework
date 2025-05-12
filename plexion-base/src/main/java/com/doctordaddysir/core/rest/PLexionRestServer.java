package com.doctordaddysir.core.rest;

import com.doctordaddysir.core.reflection.annotations.Bean;
import com.doctordaddysir.core.reflection.annotations.Inject;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Bean
public class PLexionRestServer {
    private final int port = 8080;
    private Map<String, RouteDefinition> routes = new HashMap<>();

    public void start() {
        routes = ControllerScanner.collectControllersAndRoutes();
        Undertow server = Undertow
                .builder()
                .addHttpListener(port, "localhost")
                .setHandler(exchange -> {
                    if (exchange.isInIoThread()) {
                        ControllerDispatcher dispatcher = new ControllerDispatcher(routes);
                        exchange.dispatch(() -> dispatcher.handleRequest(exchange));
                        return;
                    }
                })
                .build();
        server.start();
        log.info("PLexion REST Server started on port {}.", port);
    }
}
