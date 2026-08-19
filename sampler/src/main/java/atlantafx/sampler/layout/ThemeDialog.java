/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.sampler.theme.SamplerTheme;
import atlantafx.sampler.theme.ThemeManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;

import java.util.Objects;

final class ThemeDialog extends ModalDialog {

    private final TilePane thumbnailsPane = new TilePane(20, 20);
    private final ToggleGroup thumbnailsGroup = new ToggleGroup();

    public ThemeDialog() {
        super();

        setId("theme-dialog");
        header.setTitle("Select a theme");
        content.setBody(createContent());
        content.setFooter(null);

        updateThumbnails();

        thumbnailsGroup.selectedToggleProperty().addListener((_, _, val) -> {
            if (val != null && val.getUserData() instanceof SamplerTheme theme) {
                ThemeManager.getInstance().setTheme(theme);
            }
        });
    }

    private ScrollPane createContent() {
        thumbnailsPane.setAlignment(Pos.TOP_CENTER);
        thumbnailsPane.setPrefColumns(3);
        thumbnailsPane.setStyle("-color-thumbnail-border:-color-border-subtle;");
        thumbnailsPane.setPadding(new Insets(0, 20, 0, 20));

        var root = new ScrollPane();
        root.setContent(thumbnailsPane);
        root.setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
        root.setFitToHeight(true);
        root.setHbarPolicy(ScrollBarPolicy.NEVER);
        root.setFitToWidth(false);
        root.setMaxHeight(600);

        return root;
    }

    public void updateThumbnails() {
        var tm = ThemeManager.getInstance();

        thumbnailsPane.getChildren().clear();
        tm.getRepository().getAll().forEach(theme -> {
            var thumbnail = new ThemeThumbnail(theme);
            thumbnail.setToggleGroup(thumbnailsGroup);
            thumbnail.setUserData(theme);
            thumbnail.setSelected(Objects.equals(
                tm.getTheme() != null ? tm.getTheme().getName() : null,
                theme.getName()
            ));
            thumbnailsPane.getChildren().add(thumbnail);
        });
    }
}
