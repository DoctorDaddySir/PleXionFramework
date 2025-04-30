package com.doctordaddysir.plugins.test;

import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.plugins.base.Plugin;

@PluginInfo(name = "GoodbyePlugin", description = "A simple plugin that says goodbye")
public final class GoodbyePlugin implements Plugin {
    @Override
    public void execute() {
        System.out.println("Goodbye from GoodbyePlugin!");
    }
}
