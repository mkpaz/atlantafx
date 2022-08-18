/* SPDX-License-Identifier: MIT */
package atlantafx.sampler;

import atlantafx.sampler.layout.ApplicationWindow;
import atlantafx.sampler.theme.ThemeManager;
import fr.brouillard.oss.cssfx.CSSFX;
import fr.brouillard.oss.cssfx.api.URIToPathConverter;
import fr.brouillard.oss.cssfx.impl.log.CSSFXLogger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

public class Launcher extends Application {

    public static final boolean IS_DEV_MODE = "DEV".equalsIgnoreCase(
            Resources.getPropertyOrEnv("atlantafx.mode", "ATLANTAFX_MODE")
    );

    private static class DefaultExceptionHandler implements Thread.UncaughtExceptionHandler {

        @Override
        public void uncaughtException(Thread t, Throwable e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Thread.currentThread().setUncaughtExceptionHandler(new DefaultExceptionHandler());

        if (IS_DEV_MODE) {
            System.out.println("[WARNING] Application is running in development mode.");
        }

        loadApplicationProperties();
        var root = new ApplicationWindow();
        var scene = new Scene(root, 1200, 768);

        var tm = ThemeManager.getInstance();
        tm.setTheme(scene, tm.getAvailableThemes().get(0));
        if (IS_DEV_MODE) { startCssFX(scene); }

        scene.getStylesheets().addAll(
                Resources.resolve("assets/fonts/index.css"),
                Resources.resolve("assets/styles/index.css")
        );

        stage.setScene(scene);
        stage.setTitle(System.getProperty("app.name"));
        loadIcons(stage);
        stage.setResizable(true);
        stage.setOnCloseRequest(t -> Platform.exit());

        Platform.runLater(() -> {
            stage.show();
            stage.requestFocus();
        });
    }

    private static void loadIcons(Stage stage) {
        int iconSize = 16;
        while (iconSize <= 1024) {
            // We could use the square icons for Windows here.
            stage.getIcons().add(new Image(Resources.getResourceAsStream("assets/icon-rounded-" + iconSize + ".png")));
            iconSize *= 2;
        }
    }

    private void loadApplicationProperties() {
        try {
            var properties = new Properties();
            properties.load(new InputStreamReader(Resources.getResourceAsStream("application.properties"), UTF_8));
            properties.forEach((key, value) -> System.setProperty(
                    String.valueOf(key),
                    String.valueOf(value)
            ));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void startCssFX(Scene scene) {
        URIToPathConverter fileUrlConverter = uri -> {
            try {
                if (uri != null && uri.startsWith("file:")) {
                    return Paths.get(URI.create(uri));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        };

        CSSFX.addConverter(fileUrlConverter).start();
        CSSFXLogger.setLoggerFactory(loggerName -> (level, message, args) ->
                System.out.println("[CSSFX] " + String.format(message, args))
        );
        CSSFX.start(scene);
    }
}
