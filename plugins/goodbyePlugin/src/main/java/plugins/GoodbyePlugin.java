package plugins;

import com.doctordaddysir.annotations.OnDestroy;
import com.doctordaddysir.annotations.OnError;
import com.doctordaddysir.annotations.OnLoad;
import com.doctordaddysir.annotations.PluginInfo;
import com.doctordaddysir.base.Plugin;

@PluginInfo(name = "GoodbyePlugin", description = "A simple plugin that says goodbye")
public final class GoodbyePlugin implements Plugin {
    @Override
    public void execute() {
        System.out.println("Goodbye from GoodbyePlugin!");
    }

    @Override
    @OnDestroy
    public void onDestroy() {
        System.out.println("Goodbye from GoodbyePlugin!");
    }

    @Override
    @OnError
    public void onError(Throwable t) {
        System.err.println("An error occurred in GoodbyePlugin: " + t.getMessage());
    }

    @Override
    @OnLoad
    public void onLoad() {
        System.out.println("Loading GoodbyePlugin!");
    }


}
