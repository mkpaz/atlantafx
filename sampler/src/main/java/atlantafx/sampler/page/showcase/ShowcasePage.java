package atlantafx.sampler.page.showcase;

import static atlantafx.base.theme.Styles.ACCENT;

import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.util.NodeUtils;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.StageStyle;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public abstract class ShowcasePage extends AbstractPage {

    protected static final PseudoClass SHOWCASE_MODE = PseudoClass.getPseudoClass("showcase-mode");
    protected final StackPane showcase = new StackPane();
    protected final HBox expandBox = new HBox(10);
    protected final HBox collapseBox = new HBox(10);

    public ShowcasePage() {
        createShowcaseLayout();
    }

    @Override
    public boolean canDisplaySourceCode() {
        return false;
    }

    protected void createShowcaseLayout() {
        var expandBtn = new Button("Expand");
        expandBtn.setGraphic(new FontIcon(Feather.MAXIMIZE_2));
        expandBtn.getStyleClass().add(ACCENT);
        expandBtn.setOnAction(e -> {
            expandBtn.getScene().getRoot().pseudoClassStateChanged(SHOWCASE_MODE, true);
            VBox.setVgrow(showcase, Priority.ALWAYS);
            NodeUtils.toggleVisibility(expandBox, false);
            NodeUtils.toggleVisibility(collapseBox, true);
        });
        expandBox.getChildren().setAll(expandBtn);
        expandBox.setAlignment(Pos.CENTER);

        var collapseBtn = new Hyperlink("Exit showcase mode");
        collapseBtn.setOnAction(e -> {
            expandBtn.getScene().getRoot().pseudoClassStateChanged(SHOWCASE_MODE, false);
            VBox.setVgrow(showcase, Priority.NEVER);
            NodeUtils.toggleVisibility(expandBox, true);
            NodeUtils.toggleVisibility(collapseBox, false);
        });
        collapseBox.getChildren().setAll(new FontIcon(Feather.MINIMIZE_2), collapseBtn);
        collapseBox.setAlignment(Pos.CENTER_LEFT);
        collapseBox.setPadding(new Insets(5));
        NodeUtils.toggleVisibility(collapseBox, false);

        setUserContent(new VBox(showcase, expandBox, collapseBox));
    }

    @SuppressWarnings("SameParameterValue")
    protected void showWarning(String header, String description) {
        var alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Error Dialog");
        alert.setHeaderText(header);
        alert.setContentText(description);
        alert.initOwner(getScene().getWindow());
        alert.initStyle(StageStyle.DECORATED);
        alert.showAndWait();
    }
}
