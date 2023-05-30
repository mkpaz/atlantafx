/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;

import atlantafx.base.controls.Breadcrumbs;
import atlantafx.base.controls.MaskTextField;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.util.NodeUtils;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.stream.Stream;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

public final class OverviewPage extends ScrollPane implements Page {

    public static final String NAME = "Overview";
    private final VBox wrapper;

    @Override
    public String getName() {
        return NAME;
    }

    public OverviewPage() {
        super();

        try {
            wrapper = new VBox();
            wrapper.setAlignment(Pos.TOP_CENTER);
            wrapper.getStyleClass().add(Styles.BG_DEFAULT);

            var loader = new FXMLLoader(
                Resources.getResource("fxml/overview.fxml").toURL()
            );
            Parent fxmlContent = loader.load();
            ((Pane) fxmlContent).setMaxWidth(Page.MAX_WIDTH);
            VBox.setVgrow(fxmlContent, Priority.ALWAYS);
            wrapper.getChildren().setAll(fxmlContent);

            NodeUtils.setScrollConstraints(this, AS_NEEDED, true, AS_NEEDED, true);
            setMaxHeight(20_000);
            setContent(wrapper);
        } catch (IOException e) {
            throw new RuntimeException("Unable to load FXML file", e);
        }

        setId("blueprints");
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
    public @Nullable URI getJavadocUri() {
        return null;
    }

    @Override
    public Node getSnapshotTarget() {
        return wrapper;
    }

    @Override
    public void reset() {
    }

    ///////////////////////////////////////////////////////////////////////////

    public static class Controller implements Initializable {
        public @FXML MaskTextField phoneTf;
        public @FXML Breadcrumbs<String> breadcrumbs;

        @Override
        public void initialize(URL url, ResourceBundle resourceBundle) {
            phoneTf.setText("(415) 273-91-64");
            var items = Stream.generate(() -> FAKER.science().element())
                .limit(3)
                .toList();
            Breadcrumbs.BreadCrumbItem<String> root = Breadcrumbs.buildTreeModel(
                items.toArray(String[]::new)
            );
            breadcrumbs.setSelectedCrumb(root);
        }
    }
}
