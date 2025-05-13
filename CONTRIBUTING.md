
# 🤝 Contributing to PleXion Framework

Welcome! We're excited you're interested in contributing to PleXion — a lightweight, modular Java framework built to challenge traditional full-stack paradigms.

We value clean code, modular design, and innovative thinking. Whether you’re fixing a bug, adding a new feature, or improving documentation, you’re helping shape a Spring alternative with real community impact.

---

## 🧠 Contribution Guidelines

### 1. 🔧 Code Quality Standards
- Follow **Clean Code** principles (Robert C. Martin’s guidelines).
- All public methods/classes must include Javadoc.
- Method names should be descriptive; avoid abbreviations.
- Limit classes to **1 responsibility** — extract helper classes where needed.
- Handle exceptions gracefully; avoid `System.out.println()` for logging.

### 2. 📐 Formatting and Style
- Use 4-space indentation (no tabs).
- Brace style: Allman (each brace on a new line).
- Use `camelCase` for variables/methods, `PascalCase` for classes.
- Class and file names must match.
- Use `final` for method parameters and local variables where possible.

---

## 🚀 Development Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-org/plexion.git
cd plexion/PluginLoader
```

### 2. Build the Project
```bash
mvn clean install
```

### 3. Run Example Application
```bash
cd plexion-base
java -jar target/plexion-base.jar
```

### 4. Testing
PleXion currently uses minimal unit tests, but contributions to testing coverage are highly appreciated.

---

## 🛠️ Working on New Features

1. Fork the repository and clone your fork.
2. Create a new branch: `git checkout -b feature/my-feature`.
3. Write your code with inline documentation.
4. Format your code (`google-java-format` is recommended).
5. Run tests if applicable.
6. Submit a pull request with a clear description of your changes.

---

## 📝 Submitting Issues

If you find a bug or have a feature request, please [open an issue](https://github.com/your-org/plexion/issues) with:

- ✅ A clear title
- 📃 A detailed description
- 🔁 Steps to reproduce (if a bug)
- 🧠 Proposed changes or suggestions

---

## 💬 Communication

- For major feature proposals, please open an issue first to discuss your idea.
- Use clear commit messages: `fix:`, `feat:`, `refactor:`, `docs:` prefixes encouraged.

---

## 🙏 Thanks

Thanks for making PleXion better. Your contributions help us evolve into a powerful Spring alternative built by and for developers.

