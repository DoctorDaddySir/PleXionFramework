package com.doctordaddysir.plugins;

public interface Plugin {
    void execute();

    void onDestroy();

    void onError(Throwable t);

    void onLoad();
}
