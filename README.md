
# ⚡ PleXion Framework

PleXion is a lightweight, annotation-powered Java framework designed for modern full-stack application development. It starts as a fast, modular alternative to Spring for REST and plugin-based architectures and evolves into a full MVC + Reactive + Server-side React platform.

![PleXion Logo](assets/branding/plexion-logo.png)

---

## ✨ Features

- 🔌 **Modular Bootloader** with isolated plugin support
- ⚙️ **Annotation-Based Dependency Injection**
- 🌐 **REST Controller Support** (`@Get`, `@Post`, `@PathVariable`, `@RequestBody`)
- 🧩 **Plugin-ClassLoader Isolation** for hot-swappable modules
- 📦 **Lifecycle Management** via `@OnLoad`, `@OnDestroy`, etc.
- 📄 **JSON (Gson) Serialization** out-of-the-box
- 🔧 **Custom Reflection Utilities** for injection, proxies, and method resolution

---

## 📦 Phase Overview

| Phase | Description |
|-------|-------------|
| **Phase 1** | REST server + Open source release |
| **Phase 2** | Pebble-based MVC + Hibernate-powered database |
| **Phase 3** | Netty-based Reactive Engine + Doc Generator |
| **Phase 4** | Server-side React Rendering (GraalVM or Node.js Bridge) |

---

## 🚀 Getting Started

### ✅ Prerequisites
- Java 17+
- Maven

### 📥 Clone & Build

```bash
git clone https://github.com/your-org/plexion.git
cd plexion/PluginLoader
mvn clean install
```

### 🧪 Run Sample App

```bash
cd plexion-base
java -jar target/plexion-base.jar
```

### 🧩 Load a Plugin

Place your plugin `.jar` files in the `/plugins` directory. Each plugin must have a `plugin.properties` file.

---

## 🛠️ Annotations at a Glance

| Annotation | Purpose |
|------------|---------|
| `@RestController` | Registers a REST controller |
| `@Get`, `@Post`, etc. | Maps HTTP methods to Java methods |
| `@PathVariable` | Maps URL segments to parameters |
| `@RequestBody` | Deserializes JSON body into an object |
| `@Inject` | Injects beans or services |
| `@OnLoad` | Triggers post-initialization hooks |

---

## 📚 Documentation

See [ROADMAP.md](./ROADMAP.md) for the full development vision and milestones.

---

## 🤝 Contributing

We welcome community contributions! See [CONTRIBUTING.md](./CONTRIBUTING.md) for how to get involved.

---

## ⚖️ License

PleXion is open-source software licensed under the MIT License.
