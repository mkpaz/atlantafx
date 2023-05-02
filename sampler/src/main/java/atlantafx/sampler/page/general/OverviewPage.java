/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.util.Containers.setScrollConstraints;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;

import atlantafx.sampler.Resources;
import atlantafx.sampler.page.Page;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class OverviewPage extends ScrollPane implements Page {

    public static final String NAME = "Overview";

    @Override
    public String getName() {
        return NAME;
    }

    public OverviewPage() {
        super();

        try {
            var wrapper = new VBox();
            wrapper.setAlignment(Pos.TOP_CENTER);

            var loader = new FXMLLoader(
                Resources.getResource("assets/fxml/overview/index.fxml").toURL()
            );
            Parent fxmlContent = loader.load();
            ((Pane) fxmlContent).setMaxWidth(Page.MAX_WIDTH);
            VBox.setVgrow(fxmlContent, Priority.ALWAYS);
            wrapper.getChildren().setAll(fxmlContent);

            setScrollConstraints(this, AS_NEEDED, true, AS_NEEDED, true);
            setMaxHeight(20_000);
            setContent(wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML file", e);
        }

        setId("overview");
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return true;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return true;
    }

    @Override
    public void reset() {
    }
}
