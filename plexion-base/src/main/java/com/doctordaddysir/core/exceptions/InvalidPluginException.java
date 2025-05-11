package com.doctordaddysir.core.exceptions;

import com.doctordaddysir.core.plugins.loaders.PluginLoader;
import lombok.Getter;

@Getter
public class InvalidPluginException extends Exception {
    private final PluginLoader.PluginLoadResult result;

    public InvalidPluginException(PluginLoader.PluginLoadResult result) {
        this.result = result;
    }

    @Override
    public String getMessage() {
        return result.name();
    }
}
