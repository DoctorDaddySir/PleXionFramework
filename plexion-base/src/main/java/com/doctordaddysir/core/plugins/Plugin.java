package com.doctordaddysir.core.plugins;

public interface Plugin {
    void execute();

    void onDestroy();

    void onError(Throwable t);

    void onLoad();
}
