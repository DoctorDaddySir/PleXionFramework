package com.doctordaddysir.base;

import com.doctordaddysir.annotations.OnDestroy;
import com.doctordaddysir.annotations.OnError;
import com.doctordaddysir.annotations.OnLoad;

public interface Plugin {
    void execute();
    void onDestroy();
    void onError(Throwable t);
    void onLoad();
}
