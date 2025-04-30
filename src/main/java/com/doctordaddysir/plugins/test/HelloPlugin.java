package com.doctordaddysir.plugins.test;

import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.plugins.base.Plugin;

@PluginInfo(name = "HelloPlugin", description = "A simple plugin that prints a hello message.")
public class HelloPlugin implements Plugin {
    @Override
    public void execute() {
        System.out.println("Hello from HelloPlugin!");
    }
}
