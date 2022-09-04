package atlantafx.sampler.page.showcase;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.page.AbstractPage;
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

import static atlantafx.base.theme.Styles.ACCENT;

public abstract class ShowcasePage extends AbstractPage {

    protected static final PseudoClass SHOWCASE_MODE = PseudoClass.getPseudoClass("showcase-mode");
    protected final StackPane showcase = new StackPane();
    protected final HBox expandBox = new HBox(10);
    protected final HBox collapseBox = new HBox(10);

    public ShowcasePage() {
        createShowcaseLayout();
    }

    protected void createShowcaseLayout() {
        var expandBtn = new Button("Expand");
        expandBtn.setGraphic(new FontIcon(Feather.MAXIMIZE_2));
        expandBtn.getStyleClass().add(ACCENT);
        expandBtn.setOnAction(e -> {
            expandBtn.getScene().getRoot().pseudoClassStateChanged(SHOWCASE_MODE, true);
            VBox.setVgrow(showcase, Priority.ALWAYS);
            expandBox.setVisible(false);
            expandBox.setManaged(false);
            collapseBox.setVisible(true);
            collapseBox.setManaged(true);
        });
        expandBox.getChildren().setAll(new Spacer(), expandBtn, new Spacer());
        expandBox.setAlignment(Pos.CENTER_LEFT);

        var collapseBtn = new Hyperlink("Exit showcase mode");
        collapseBtn.setOnAction(e -> {
            expandBtn.getScene().getRoot().pseudoClassStateChanged(SHOWCASE_MODE, false);
            VBox.setVgrow(showcase, Priority.NEVER);
            expandBox.setVisible(true);
            expandBox.setManaged(true);
            collapseBox.setVisible(false);
            collapseBox.setManaged(false);
        });
        collapseBox.getChildren().setAll(new FontIcon(Feather.MINIMIZE_2), collapseBtn);
        collapseBox.setAlignment(Pos.CENTER_LEFT);
        collapseBox.setPadding(new Insets(5));
        collapseBox.setVisible(false);
        collapseBox.setManaged(false);

        sourceCodeToggleBtn.setVisible(false);
        sourceCodeToggleBtn.setManaged(false);

        userContent.getChildren().setAll(showcase, expandBox, collapseBox);
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
