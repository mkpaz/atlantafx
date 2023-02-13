/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.DANGER;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.TEXT_SMALL;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.layout.Priority.ALWAYS;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.theme.SamplerTheme;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.theme.ThemeRepository;
import atlantafx.sampler.util.Containers;
import java.io.File;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import javafx.concurrent.Task;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

@SuppressWarnings("UnnecessaryLambda")
class ThemeRepoManager extends VBox {

    private static final Executor THREAD_POOL = Executors.newFixedThreadPool(3);
    private static final ThemeRepository REPO = ThemeManager.getInstance().getRepository();

    private VBox themeList;

    private final Consumer<SamplerTheme> deleteHandler = theme -> {
        REPO.remove(theme);
        themeList.getChildren().removeIf(c -> {
            if (c instanceof ThemeCell cell) {
                return Objects.equals(theme.getName(), cell.getTheme().getName());
            }
            return false;
        });
    };

    public ThemeRepoManager() {
        super();
        createView();
    }

    private void createView() {
        themeList = new VBox();
        themeList.getStyleClass().add("theme-list");
        REPO.getAll().forEach(theme -> themeList.getChildren().add(themeCell(theme)));

        var scrollPane = new ScrollPane(themeList);
        Containers.setScrollConstraints(scrollPane, AS_NEEDED, true, AS_NEEDED, true);
        scrollPane.setMaxHeight(4000);
        VBox.setVgrow(scrollPane, ALWAYS);

        var infoBox = new HBox(new Label("Default or selected themes can not be removed."));
        infoBox.getStyleClass().add("info");

        setId("theme-repo-manager");
        getChildren().addAll(scrollPane, infoBox);
    }

    public void update() {
        themeList.getChildren().forEach(c -> {
            if (c instanceof ThemeCell cell) {
                cell.update();
            }
        });
    }

    public void addFromFile(File file) {
        SamplerTheme theme = REPO.addFromFile(file);
        themeList.getChildren().add(themeCell(theme));
    }

    private ThemeCell themeCell(SamplerTheme theme) {
        var cell = new ThemeCell(theme);
        cell.setOnDelete(deleteHandler);
        return cell;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class ThemeCell extends HBox {

        private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

        private final SamplerTheme theme;

        private Button deleteBtn;
        private Consumer<SamplerTheme> deleteHandler;

        public ThemeCell(SamplerTheme theme) {
            this.theme = theme;
            createView();
        }

        private void createView() {
            setAlignment(Pos.CENTER_LEFT);
            getStyleClass().add("theme");

            // == TITLE ==

            var text = new Text(theme.getName());
            text.getStyleClass().addAll("text");

            String path = theme.getPath();
            if (path.length() > 64) {
                path = "..." + path.substring(path.length() - 64);
            }

            var subText = new Text(path);
            subText.getStyleClass().addAll(TEXT_SMALL, "sub-text");

            var titleBox = new VBox(5);
            titleBox.getStyleClass().addAll("title");
            titleBox.getChildren().setAll(text, subText);

            getChildren().addAll(titleBox, new Spacer(), new Region(/* placeholder */));

            // == PREVIEW ==

            var task = new Task<Map<String, String>>() {
                @Override
                protected Map<String, String> call() throws Exception {
                    return theme.parseColors();
                }
            };

            task.setOnSucceeded(e -> {
                var colors = task.getValue();

                if (colors != null && !colors.isEmpty()) {
                    var style = new StringBuilder();
                    var presence = 0;

                    presence += appendStyleIfPresent(colors, style, "-color-bg-default");
                    presence += appendStyleIfPresent(colors, style, "-color-fg-default");
                    presence += appendStyleIfPresent(colors, style, "-color-fg-emphasis");
                    presence += appendStyleIfPresent(colors, style, "-color-accent-emphasis");
                    presence += appendStyleIfPresent(colors, style, "-color-success-emphasis");
                    presence += appendStyleIfPresent(colors, style, "-color-danger-emphasis");

                    // all colors must be present for preview
                    if (presence == 6) {
                        var previewBox = new HBox();
                        previewBox.setAlignment(Pos.CENTER_LEFT);
                        previewBox.getStyleClass().add("preview");
                        previewBox.setStyle(style.toString());
                        previewBox.getChildren().setAll(
                            previewLabel("A", "-color-bg-default", "-color-fg-default"),
                            previewLabel("B", "-color-accent-emphasis", "-color-fg-emphasis"),
                            previewLabel("C", "-color-success-emphasis", "-color-fg-emphasis"),
                            previewLabel("D", "-color-danger-emphasis", "-color-fg-emphasis")
                        );

                        getChildren().set(2, previewBox);
                    }
                }
            });

            task.setOnFailed(
                e -> System.err.println("[ERROR] Unable to parse \"" + theme.getName()
                    + "\" theme colors. Either CSS not valid or file isn't readable.")
            );

            THREAD_POOL.execute(task);

            // == CONTROLS ==

            deleteBtn = new Button("", new FontIcon(Material2OutlinedAL.DELETE));
            deleteBtn.getStyleClass().addAll(BUTTON_ICON, BUTTON_CIRCLE, FLAT, DANGER);
            deleteBtn.setOnAction(e -> {
                if (deleteHandler != null) {
                    deleteHandler.accept(theme);
                }
            });

            var controlsBox = new HBox();
            controlsBox.getStyleClass().add("controls");
            controlsBox.setAlignment(Pos.CENTER_RIGHT);
            controlsBox.getChildren().add(deleteBtn);

            getChildren().add(3, controlsBox);
        }

        public SamplerTheme getTheme() {
            return theme;
        }

        public void update() {
            boolean isSelectedTheme = isSelectedTheme();
            pseudoClassStateChanged(SELECTED, isSelectedTheme);
            deleteBtn.setDisable(theme.isProjectTheme() || isSelectedTheme);
        }

        public void setOnDelete(Consumer<SamplerTheme> handler) {
            this.deleteHandler = handler;
        }

        private boolean isSelectedTheme() {
            return Objects.equals(ThemeManager.getInstance().getTheme().getName(), theme.getName());
        }

        private int appendStyleIfPresent(Map<String, String> colors, StringBuilder sb, String colorName) {
            var value = colors.get(colorName);
            if (value != null) {
                sb.append(colorName);
                sb.append(":");
                sb.append(value);
                sb.append(";");
                return 1;
            }
            return 0;
        }

        private Label previewLabel(String text, String bg, String fg) {
            var label = new Label(text);
            label.setStyle(String.format("-fx-background-color:%s;-fx-text-fill:%s;", bg, fg));
            return label;
        }
    }
}
