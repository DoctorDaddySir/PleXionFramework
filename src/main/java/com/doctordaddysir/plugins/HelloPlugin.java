package plugins;

import com.doctordaddysir.annotations.OnDestroy;
import com.doctordaddysir.annotations.OnError;
import com.doctordaddysir.annotations.OnLoad;
import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.base.Plugin;

@PluginInfo(name = "HelloPlugin", description = "A simple plugin that prints a hello message.")
public class HelloPlugin implements Plugin {
    @Override
    public void execute() {
        System.out.println("Hello from HelloPlugin!");
    }

    @Override
    @OnDestroy
    public void onDestroy() {
        System.out.println("Goodbye from HelloPlugin!");
    }

    @Override
    @OnError
    public void onError(Throwable t) {
        System.err.println("An error occurred in HelloPlugin: " + t.getMessage());
    }

    @Override
    @OnLoad
    public void onLoad() {
        System.out.println("Loading HelloPlugin!");
    }
}
