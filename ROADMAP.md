
# 🛣️ PleXion Framework Roadmap (Expanded)

PleXion is a modular, annotation-driven Java framework evolving into a full-stack, Spring-alternative platform. This roadmap outlines development phases with integrated architectural goals and emerging features.

---

## ✅ Phase 1: REST Server + Initial Open Source Release

### 🎯 Goals
- Full REST controller and plugin support
- First open-source release

### 📌 Features
- `@RestController`, `@Get`, `@Post`, `@PathVariable`, `@RequestBody`
- JSON serialization via Gson
- `ResponseEntity` abstraction with status codes
- Centralized error handling
- Pre/post request filter system (middleware hooks)
- Lifecycle tracing logs
- Basic plugin management API
- Begin config file support (properties/yaml)

---

## ⚙️ Phase 2: MVC + Database Integration

### 🎯 Goals
- Add view rendering and database layer
- Begin layered injection and lifecycle system

### 📌 Features
- Pebble-based SSR via `@Controller` and `ModelAndView`
- Static asset routing
- Hibernate ORM support with `@Entity`, `@Repository`
- In-memory DB (H2) support
- `@Config` annotation for property injection
- Bean scopes + lifecycle (`@PostConstruct`, `@PreDestroy`)
- Annotation-based validation (`@NotNull`, `@Min`, etc.)
- Dev console (`/__plexion`) for live route/bean/plugin inspection

---

## ⚛️ Phase 3: Reactive Core + Documentation Generator

### 🎯 Goals
- Enable high-throughput Netty support
- Introduce live documentation generator

### 📌 Features
- Toggleable Undertow/Netty support
- Reactive-style method return compatibility
- Lifecycle hook compatibility with reactive flow
- `@Doc` annotation for classes, fields, and methods
- Generate Markdown/JSON route and bean documentation
- Optional Pebble-rendered HTML doc UI
- Foundation for modular startup (`@EnableX` annotations)

---

## ⚛️🎨 Phase 4: Server-Side React Rendering

### 🎯 Goals
- Flexible JSX rendering via GraalVM/Node.js

### 📌 Features
- `ReactRenderer` interface with polyglot or Node bridge
- Precompiled `.jsx` rendering or on-the-fly
- Output hydration-compatible HTML
- Coexistence with Pebble-based rendering

---

## 🔐 Phase 5: Security and Scheduling

### 🎯 Goals
- Add route-level auth and job scheduler

### 📌 Features
- `@Secured("ROLE")` annotations
- JWT support and auth integration
- Custom pluggable auth providers
- `@Scheduled` support with time-based triggers
- Configurable task thread pool

---

## 🧰 Phase 6: Auto-Configuration & Developer Tools

### 🎯 Goals
- Improve DX and default behaviors

### 📌 Features
- `@EnableX` annotations to activate modules
- Optional classpath-based auto-configuration
- `PlexionHttpClient` for REST calls
- CLI tool (`plex`) to inspect plugins/config/routes
- Runtime dashboard UI (`Plexion Studio`)

---

## 🧩 Phase 7: Plugin Marketplace and Ecosystem

### 🎯 Goals
- Enable external plugin discovery, installation, and versioning

### 📌 Features
- Secure plugin registry and manifest format
- Plugin versioning and dependency resolver
- GUI interface for managing community plugin ecosystem

---

**Status:** Actively evolving — see [CONTRIBUTING.md](./CONTRIBUTING.md) for how to get involved!
