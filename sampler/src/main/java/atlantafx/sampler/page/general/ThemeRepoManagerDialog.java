/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.layout.ModalDialog;
import java.io.File;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2MZ;

class ThemeRepoManagerDialog extends ModalDialog {

    private final ThemeRepoManager repoManager = new ThemeRepoManager();

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

        setId("theme-repo-manager-dialog");
        header.setTitle("Theme Manager");
        content.setBody(repoManager);
        content.setMinSize(800, 500);
        content.setMaxSize(800, 500);

        var footer = createDefaultFooter();
        footer.getChildren().add(0, addBtn);
        content.setFooter(footer);
    }

    public ThemeRepoManager getContent() {
        return repoManager;
    }
}
