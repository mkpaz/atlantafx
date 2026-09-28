# CONTRIBUTING

Thank you for considering contributing to the project!

This project is small and free of bureaucracy. The only pragmatic rules are:

- **No unverified AI commits.** You are welcome to use AI, but please review and test its output
carefully to ensure it aligns with the current project's code style and guidelines.

- **Ask before adding new controls.** If you want to contribute a new control, please open an issue
first to discuss it. Some controls are either very web-specific (e.g., `FileUpload`) or require
localization features that this project doesn't support.

- **Include UI demo & docs.** If you are contributing a new style or theme update, please add a
corresponding example to the Sampler demo app for visual testing and
[update the documentation](styles/README.md).

## Prerequisites

- JDK 25+
- Apache Maven 3.9.16

## Building and Running

To build and run the whole project, including packaged Sampler app image:

> [!NOTE]
> If you are using Intellij IDEA, you can use the shared Run Configurations.

```sh
mvn install
mvn javafx:run -pl sampler
```

If you want to use hot reload (update CSS without restarting the Sampler app), you have to start
app in development mode instead:

```sh
# start watching for SASS source code changes
# (mandatory BEFORE the Sampler start)
mvn compile -pl styles -Pdev

# run sampler in dev mode
mvn javafx:run -pl sampler -Pdev
```

You can also build each Maven module individually:

```sh
mvn install -N
mvn install -pl styles
mvn install -pl base
mvn javafx:run -pl sampler
```

## Code Style

If you want to contribute some Java code, you should be aware of two additional checks.

1. Maven Checkstyle Plugin

    The analysis will be performed automatically. You just need to read Maven output
    warnings, if any.

    Installing [checkstyle plugin](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea),
    which is supported by any IDE, makes it event simpler. Import `checkstyle.xml` to your code style
    settings to auto-configure project's code formatting and linting.

2. Maven ErrorProne plugin

   To perform  analysis, use the `lint` profile.

    ```sh
    mvn compile -Plint
    ```
