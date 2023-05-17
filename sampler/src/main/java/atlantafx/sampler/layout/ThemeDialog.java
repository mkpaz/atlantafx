package atlantafx.sampler.layout;

import atlantafx.sampler.theme.SamplerTheme;
import atlantafx.sampler.theme.ThemeManager;
import atlantafx.sampler.util.NodeUtils;
import java.util.Objects;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.TilePane;
import javafx.scene.layout.VBox;

final class ThemeDialog extends OverlayDialog<VBox> {

    private final TilePane thumbnailsPane = new TilePane(20, 20);
    private final ToggleGroup thumbnailsGroup = new ToggleGroup();

    public ThemeDialog() {
        super();

        setId("theme-dialog");
        setTitle("Select a theme");
        setContent(createContent());
        NodeUtils.toggleVisibility(footerBox, false);

        updateThumbnails();

        thumbnailsGroup.selectedToggleProperty().addListener((obs, old, val) -> {
            System.out.println(0);
            System.out.println(val.getUserData().getClass().getName());
            if (val != null && val.getUserData() instanceof SamplerTheme theme) {
                System.out.println(1);
                ThemeManager.getInstance().setTheme(theme);
            }
        });
    }

    private VBox createContent() {
        thumbnailsPane.setAlignment(Pos.TOP_CENTER);
        thumbnailsPane.setPrefColumns(3);
        thumbnailsPane.setStyle("-color-thumbnail-border:-color-border-subtle;");

        var root = new VBox(thumbnailsPane);
        root.setPadding(new Insets(20));

        return root;
    }

    private void updateThumbnails() {
        var tm = ThemeManager.getInstance();

        thumbnailsPane.getChildren().clear();
        tm.getRepository().getAll().forEach(theme -> {
            var thumbnail = new ThemeThumbnail(theme);
            thumbnail.setToggleGroup(thumbnailsGroup);
            thumbnail.setUserData(theme);
            thumbnail.setSelected(Objects.equals(
                tm.getTheme().getName(),
                theme.getName()
            ));
            thumbnailsPane.getChildren().add(thumbnail);
        });
    }
}
