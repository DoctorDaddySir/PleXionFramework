package plugins;


import com.doctordaddysir.core.annotations.OnDestroy;
import com.doctordaddysir.core.annotations.OnError;
import com.doctordaddysir.core.annotations.OnLoad;
import com.doctordaddysir.core.annotations.PluginInfo;
import com.doctordaddysir.core.plugins.Plugin;

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
