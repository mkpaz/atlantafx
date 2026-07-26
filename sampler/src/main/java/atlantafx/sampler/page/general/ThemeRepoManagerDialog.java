/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.layout.ModalDialog;
import atlantafx.sampler.util.GitHubDownloader;
import atlantafx.sampler.util.PlatformUtils;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;
import java.util.stream.Stream;

class ThemeRepoManagerDialog extends ModalDialog {

    private final ThemeRepoManager repoManager = new ThemeRepoManager();
    private final Progress progress = new Progress();

    public ThemeRepoManagerDialog() {
        super();

        var addBtn = new Button("Add custom theme", new FontIcon(Material2MZ.PLUS));
        addBtn.getStyleClass().add(Styles.ACCENT);
        addBtn.setOnAction(e -> {
            var fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSS (*.css)", "*.css"));
            File file = fileChooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                repoManager.addFromFile(file);
            }
        });

        var downloadBtn = new Button("Download more themes", new FontIcon(Material2MZ.STAR));
        downloadBtn.getStyleClass().addAll(Styles.SUCCESS);
        downloadBtn.setOnAction(e -> {
            var task = new InstallExtraThemesTask(repoManager, getScene().getWindow());
            progress.bind(task);
            Thread.ofVirtual().name("theme-installer").start(task);
        });

        repoManager.getChildren().addFirst(progress);

        setId("theme-repo-manager-dialog");
        header.setTitle("Theme Manager");
        content.setBody(repoManager);
        content.setMinSize(800, 500);
        content.setMaxSize(800, 500);

        var footer = createDefaultFooter();
        footer.getChildren().add(0, addBtn);
        footer.getChildren().add(1, downloadBtn);
        content.setFooter(footer);
    }

    public ThemeRepoManager getContent() {
        return repoManager;
    }

    //*************************************************************************

    private static class Progress extends HBox {

        private final Label label = new Label();
        private final ProgressIndicator indicator = new ProgressIndicator();

        public Progress() {
            super();

            indicator.setMinWidth(20);
            indicator.setMaxWidth(20);

            setSpacing(10);
            setAlignment(Pos.CENTER_LEFT);
            getChildren().setAll(indicator, label);

            hide();
        }

        public void bind(Task<?> task) {
            show();
            label.textProperty().bind(task.messageProperty());

            task.setOnSucceeded(e -> dispose());
            task.setOnFailed(e -> dispose());
            task.setOnCancelled(e -> dispose());
        }

        private void dispose() {
            indicator.progressProperty().unbind();
            label.textProperty().unbind();
            hide();
        }

        private void show() {
            setVisible(true);
            setManaged(true);
        }

        private void hide() {
            setVisible(false);
            setManaged(false);
        }
    }

    private static class InstallExtraThemesTask extends Task<List<File>> {

        private final ThemeRepoManager repoManager;
        private final Window ownerWindow;

        public InstallExtraThemesTask(ThemeRepoManager repoManager, Window window) {
            this.repoManager = repoManager;
            this.ownerWindow = window;
        }

        @Override
        protected List<File> call() throws Exception {
            updateMessage("Downloading and installing themes...");

            Path tempDir = PlatformUtils.findTempDir();
            Path extractedPath;
            try {
                extractedPath = new GitHubDownloader()
                    .downloadLatestRelease("dlsc-software-consulting-gmbh", "atlantafx-themes", tempDir)
                    .toAbsolutePath();
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException("Unable to download from GitHub releases.", e);
            }

            Path src = extractedPath.resolve("src");
            if (Files.notExists(src) || !Files.isDirectory(src)) {
                throw new RuntimeException("The 'src' directory does not exist in: " + extractedPath);
            }

            List<Path> sourceFiles;
            try (Stream<Path> stream = Files.walk(src)) {
                sourceFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".css"))
                    .toList();
            }

            Path targetDir = PlatformUtils.findUserDataDir().resolve("themes");
            Files.createDirectories(targetDir);

            List<File> targetFiles = new ArrayList<>(sourceFiles.size());
            for (Path sourcePath : sourceFiles) {
                Path targetPath = targetDir.resolve(sourcePath.getFileName());
                Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
                targetFiles.add(targetPath.toFile());
            }

            return targetFiles;
        }

        @Override
        protected void succeeded() {
            super.succeeded();

            List<File> cssFiles = getValue();
            int total = cssFiles.size();
            int successCount = 0;
            var failures = new TreeSet<String>();

            for (File cssFile : cssFiles) {
                try {
                    repoManager.addFromFile(cssFile);
                    successCount++;
                } catch (Exception e) {
                    e.printStackTrace();
                    failures.add(cssFile.getName());
                }
            }

            String messageText = "Successfully installed %d themes.".formatted(successCount);
            if (!failures.isEmpty()) {
                messageText += "\n\nThe following themes cannot be installed: %s.".formatted(
                    String.join(", ", failures)
                );
            }

            var alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText("Installation Complete");
            alert.setContentText(messageText);
            alert.initOwner(ownerWindow);
            alert.initStyle(StageStyle.DECORATED);
            alert.showAndWait();
        }

        @Override
        protected void failed() {
            super.failed();
            var alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Installation Failed");
            alert.setContentText(getException().getMessage());
            alert.initOwner(ownerWindow);
            alert.showAndWait();
        }
    }
}
