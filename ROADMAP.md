
# 🛣️ PleXion Framework Roadmap

The PleXion Framework is a lightweight, annotation-driven Java framework evolving toward full-stack modularity. This roadmap outlines the major development phases from RESTful foundations to server-side React rendering.

---

## ✅ Phase 1: REST Server + Initial Open Source Release

### 🎯 Goals
- Complete REST routing, request handling, and JSON support.
- Publish the framework with clean documentation and examples.

### 📌 Features
- `@RestController`, `@Get`, `@Post`, etc.
- `@RequestBody` and `@PathVariable` parsing
- `ResponseEntity` and status code abstraction
- JSON serialization/deserialization via Gson
- Basic CLI UI (deprecated after REST integration)
- Plugin lifecycle hooks exposed via REST

### 📦 Deliverables
- Public GitHub repo with `v0.1.0` release
- Developer docs and examples
- Minimal unit test suite
- MIT or Apache-2.0 license

---

## ⚙️ Phase 2: MVC + Database Integration

### 🎯 Goals
- Add server-side rendering (SSR) via Pebble template engine.
- Introduce Hibernate-powered persistence layer.

### 📌 Features
- `@Controller` + Pebble view rendering
- `ModelAndView` support
- Static asset routing
- `@Entity`, `@Repository`, `@Service` tiered injection
- In-memory DB (H2) for dev
- Config file support (YAML/properties)

---

## ⚛️ Phase 3: Reactive Core + Documentation Generator

### 🎯 Goals
- Shift to a Netty-based non-blocking reactive server.
- Auto-generate documentation using annotation metadata.

### 📌 Features
- Non-blocking Netty server with backpressure support
- Reactive-style controller return types
- Dual-mode (blocking/reactive) runtime toggle
- `@Doc` annotation for classes, fields, and methods
- Route docs in Markdown/JSON
- Optional Pebble-rendered HTML docs

---

## ⚛️🎨 Phase 4: Server-Side React Rendering

### 🎯 Goals
- Support server-side React via a pluggable rendering interface.

### 📌 Features
- `ReactRenderer` abstraction
- Support for GraalVM polyglot, Node bridge, or embedded JS engine
- `.jsx` or precompiled view execution
- Pebble/React rendering coexistence
- HTML hydration output support

---

**Roadmap Status:** Actively evolving. For feature requests or contributions, see [CONTRIBUTING.md](./CONTRIBUTING.md).

