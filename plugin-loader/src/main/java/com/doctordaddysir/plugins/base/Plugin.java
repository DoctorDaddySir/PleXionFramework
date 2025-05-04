package com.doctordaddysir.plugins.base;

public interface Plugin {
    void execute();
    void onDestroy();
    void onError(Throwable t);
    void onLoad();
}
