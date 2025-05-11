package com.doctordaddysir.core.rest;

import com.doctordaddysir.core.annotations.Bean;
import com.doctordaddysir.core.annotations.Inject;
import io.undertow.Undertow;
import io.undertow.util.Headers;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Bean
@NoArgsConstructor(force = true)
public class PLexionRestServer {
    private final int port = 8080;
    @Inject
    private final RouteRegistry routeRegistry;

    public void start() {
        routeRegistry.init();
        log.debug("Route registry initialized.");
        Undertow server = Undertow
                .builder()
                .addHttpListener(port, "localhost")
                .setHandler(exchange -> {
                    exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/plain");
                    exchange.getResponseSender().send("PLeXion REST Server");
                })
                .build();
        server.start();
        log.info("PLexion REST Server started on port {}.", port);
    }
}
