# 🧩 Java Plugin Framework

A lightweight, modular plugin execution framework built using Java Reflection.  
This system supports dynamic class loading, annotation-based metadata, and runtime plugin discovery from a designated directory.

---

## 🚀 Features

- 🔍 **Dynamic Class Discovery**: Scans the `plugins/` folder for compiled `.class` files
- 🧠 **Annotation-Driven Metadata**: Uses `@PluginInfo` to identify and describe plugins
- 🛠️ **Interface Enforcement**: All plugins must implement the `Plugin` interface
- ♻️ **Runtime Registration**: Automatically registers valid plugins for execution
- 📂 **Recursive Directory Support**: Handles nested directory structures within `plugins/`
- 🧼 **Clear Logging**: Rejected classes are logged with meaningful reasons

---

## 🏗️ Plugin Requirements

To be recognized by the system, each plugin class must:

- Be placed in the `plugins` package
- Be compiled with the flag:
  ```bash
  javac -d plugins YourPlugin.java
  ```
- Be located in the `./plugins/plugins/` directory after compilation
- Be annotated with `@PluginInfo`
- Implement the `Plugin` interface

### ✅ Example:

```java
package plugins;

import com.doctordaddysir.Plugin;
import com.doctordaddysir.annotations.PluginInfo;

@PluginInfo(name = "HelloPlugin", version = "1.0")
public class HelloPlugin implements Plugin {
    public void execute() {
        System.out.println("Hello from plugin!");
    }
}
```

---

## 📁 Project Structure

```
project-root/
├─ plugins/
│  └─ plugins/
│     ├─ HelloPlugin.class
│     └─ GoodbyePlugin.class
├─ src/
│  ├─ com/doctordaddysir/
│  │  ├─ Main.java
│  │  ├─ PluginController.java
│  │  ├─ PluginDirectoryLoader.java
│  │  └─ ...
```

---

## 🧪 Running the Application

```bash
# Compile plugins
javac -d plugins src/plugins/HelloPlugin.java

# Compile framework
javac -d out src/com/doctordaddysir/**/*.java

# Run
java -cp out com.doctordaddysir.Main
```

---

## 🧩 Creating New Plugins

1. Create a class in the `plugins` package
2. Annotate it with `@PluginInfo`
3. Implement the `Plugin` interface
4. Compile using `javac -d plugins`
5. Drop the resulting `.class` file into `./plugins/plugins/`

✅ That’s it — your plugin will be auto-registered at runtime!

---

## 📜 License

This project is open-sourced under the [MIT License](LICENSE).

---

## 🙋 Contributing

If you'd like to contribute:

- Fork the repo
- Follow the plugin structure and conventions
- Submit a pull request with your feature or fix

---

## 📬 Contact

Maintained by Trent Shelton 
Email: `p3rb34r@gmail.com`  
GitHub: [https://github.com/yourusername/plugin-framework](https://github.com/yourusername/plugin-framework)
