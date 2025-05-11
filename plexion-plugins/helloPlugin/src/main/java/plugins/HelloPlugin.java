package plugins;



import com.doctordaddysir.core.reflection.annotations.OnDestroy;
import com.doctordaddysir.core.reflection.annotations.OnError;
import com.doctordaddysir.core.reflection.annotations.OnLoad;
import com.doctordaddysir.core.reflection.annotations.PluginInfo;
import com.doctordaddysir.core.plugins.Plugin;

@PluginInfo(name = "HelloPlugin", description = "A simple plugin that prints a hello " +
        "message.")
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
