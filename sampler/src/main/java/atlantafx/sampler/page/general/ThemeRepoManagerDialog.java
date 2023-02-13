/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.OverlayDialog;
import java.io.File;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

class ThemeRepoManagerDialog extends OverlayDialog<ThemeRepoManager> {

    private final ThemeRepoManager repoManager = new ThemeRepoManager();

    public ThemeRepoManagerDialog() {
        setId("theme-repo-manager-dialog");
        setTitle("Theme Manager");
        setContent(repoManager);

        var addBtn = new Button("Add", new FontIcon(Material2MZ.PLUS));
        addBtn.getStyleClass().add(Styles.ACCENT);
        addBtn.setOnAction(e -> {
            var fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSS (*.css)", "*.css"));
            File file = fileChooser.showOpenDialog(getScene().getWindow());
            if (file != null) {
                repoManager.addFromFile(file);
            }
        });

        footerBox.getChildren().add(0, addBtn);
        footerBox.setAlignment(Pos.CENTER_LEFT);
    }

    public ThemeRepoManager getContent() {
        return repoManager;
    }
}
