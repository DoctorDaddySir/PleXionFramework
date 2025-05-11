package com.doctordaddysir.core.utils.testClasses;

import com.doctordaddysir.core.annotations.Bean;
import com.doctordaddysir.core.annotations.Inject;
import com.doctordaddysir.core.annotations.Injectable;
import com.doctordaddysir.core.PlexionBootLoader;
import com.doctordaddysir.core.plugins.loaders.PluginLoader;
import com.doctordaddysir.core.ui.PlexionUI;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Bean
@Slf4j
@ToString
public class ReflectionPracticeClass {
    @Inject
    private final int port;
    @Inject
    private final boolean enabled;
    @Inject
    private final PluginLoader loader;
    @Inject
    private final PlexionBootLoader bootLoader;
    @Inject
    private final PlexionUI ui;


    @Injectable
    public ReflectionPracticeClass(@Inject int port, @Inject boolean enabled,
                                   @Inject PluginLoader loader,
                                   @Inject PlexionBootLoader bootLoader,
                                   @Inject PlexionUI ui) {
        this.port = port;
        this.enabled = enabled;
        this.loader = loader;
        this.bootLoader = bootLoader;
        this.ui = ui;
    }

}
