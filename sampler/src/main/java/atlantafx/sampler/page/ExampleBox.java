/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import java.util.Objects;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.PseudoClass;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;

// This widget emulates TabPane behavior, because TabPane itself doesn't work as it should:
// https://bugs.openjdk.org/browse/JDK-8145490
public final class ExampleBox extends VBox {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private final ObjectProperty<Label> selectedTab = new SimpleObjectProperty<>();
    private final ToggleSwitch stateToggle;

    public ExampleBox(Node preview, Snippet snippet) {
        this(preview, snippet, null);
    }

    public ExampleBox(Node preview, Snippet snippet, @Nullable Node description) {
        super();

        Objects.requireNonNull(preview, "preview");
        Objects.requireNonNull(snippet, "snippet");

        var previewTab = createTabLabel("Preview");
        var codeTab = createTabLabel("Code");

        var copyBtn = new Button("copy source code");
        copyBtn.setCursor(Cursor.HAND);
        copyBtn.getStyleClass().addAll(Styles.FLAT, Styles.SMALL);

        stateToggle = new ToggleSwitch();
        HBox.setMargin(stateToggle, new Insets(0, 0, 0, 10));

        var tabs = new HBox(
            previewTab, codeTab,
            new Spacer(), copyBtn, stateToggle
        );
        tabs.getStyleClass().add("tabs");
        tabs.setAlignment(Pos.CENTER_LEFT);

        var content = new VBox();

        getStyleClass().add("example-box");
        if (description != null) {
            getChildren().add(description);
        }

        getChildren().addAll(tabs, content);

        // == INIT ==

        stateToggle.selectedProperty().addListener((obs, old, val) -> {
            if (selectedTab.get() == previewTab) {
                content.getChildren().forEach(c -> c.setDisable(val));
            }
        });

        copyBtn.setOnAction(e -> {
            var cc = new ClipboardContent();
            cc.putString(snippet.getSourceCode());
            Clipboard.getSystemClipboard().setContent(cc);
        });

        selectedTab.addListener((obs, old, val) -> {
            if (val == codeTab) {
                stateToggle.setDisable(true);
                content.getChildren().setAll(snippet.render());
            } else {
                stateToggle.setDisable(false);
                content.getChildren().setAll(preview);
            }

            if (old != null) {
                old.pseudoClassStateChanged(SELECTED, false);
            }
            if (val != null) {
                val.pseudoClassStateChanged(SELECTED, true);
            }
        });

        selectedTab.set(previewTab);
    }

    public void setAllowDisable(boolean allow) {
        stateToggle.setDisable(!allow);
        stateToggle.setVisible(allow);
        stateToggle.setManaged(allow);
    }

    private Label createTabLabel(String title) {
        var label = new Label(title);
        label.setOnMouseClicked(e -> selectedTab.set(label));
        label.setPrefWidth(120);
        label.setAlignment(Pos.CENTER);
        return label;
    }
}
