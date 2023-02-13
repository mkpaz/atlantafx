/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.sampler.util.Animations;
import atlantafx.sampler.util.Containers;
import atlantafx.sampler.util.NodeUtils;
import java.util.Objects;
import java.util.function.Consumer;
import javafx.animation.Timeline;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.event.Event;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class Overlay extends StackPane {

    public static final String STYLE_CLASS = "overlay";

    private ScrollPane scrollPane;
    private AnchorPane edgeContentWrapper;
    private StackPane centerContentWrapper;

    private final ReadOnlyBooleanWrapper onFrontProperty = new ReadOnlyBooleanWrapper(false);
    private final Timeline fadeInTransition = Animations.fadeIn(this, Duration.millis(100));
    private final Timeline fadeOutTransition = Animations.fadeOut(this, Duration.millis(200));

    private HPos currentContentPos;

    public Overlay() {
        createView();
    }

    private void createView() {
        edgeContentWrapper = new AnchorPane();
        edgeContentWrapper.getStyleClass().add("scrollable-content");

        centerContentWrapper = new StackPane();
        centerContentWrapper.getStyleClass().add("scrollable-content");
        centerContentWrapper.setAlignment(Pos.CENTER);

        scrollPane = new ScrollPane();
        Containers.setScrollConstraints(scrollPane,
            ScrollPane.ScrollBarPolicy.AS_NEEDED, true,
            ScrollPane.ScrollBarPolicy.NEVER, true
        );
        scrollPane.setMaxHeight(10_000); // scroll pane won't work without height specified

        // ~

        Consumer<Event> hideAndConsume = e -> {
            removeContent();
            toBack();
            e.consume();
        };

        // hide overlay by pressing ESC (only works when overlay or one of its children has focus,
        // that's why we requesting it in the toFront())
        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                hideAndConsume.accept(e);
            }
        });

        // hide overlay by clicking outside content area
        setOnMouseClicked(e -> {
            Pane content = getContent();
            Node eventSource = e.getPickResult().getIntersectedNode();
            if (e.getButton() == MouseButton.PRIMARY && content != null
                && !NodeUtils.isDescendant(content, eventSource)) {
                hideAndConsume.accept(e);
            }
        });

        getChildren().add(scrollPane);
        getStyleClass().add(STYLE_CLASS);
    }

    public Pane getContent() {
        return NodeUtils.getChildByIndex(getContentWrapper(), 0, Pane.class);
    }

    private Pane getContentWrapper() {
        return currentContentPos == HPos.CENTER ? centerContentWrapper : edgeContentWrapper;
    }

    public void setContent(Pane content, HPos pos) {
        Objects.requireNonNull(content);
        Objects.requireNonNull(pos);

        // clear old content
        if (pos != currentContentPos) {
            removeContent();
            currentContentPos = pos;
        }

        switch (pos) {
            case LEFT -> {
                edgeContentWrapper.getChildren().setAll(content);
                Containers.setAnchors(content, new Insets(0, -1, 0, 0));
            }
            case RIGHT -> {
                edgeContentWrapper.getChildren().setAll(content);
                Containers.setAnchors(content, new Insets(0, 0, 0, -1));
            }
            case CENTER -> {
                centerContentWrapper.getChildren().setAll(content);
                StackPane.setAlignment(content, Pos.CENTER);
            }
        }

        scrollPane.setContent(getContentWrapper());
    }

    public void removeContent() {
        getContentWrapper().getChildren().clear();
    }

    public boolean contains(Pane content) {
        return content != null
            && getContentWrapper().getChildren().size() > 0
            && getContentWrapper().getChildren().get(0).equals(content);
    }

    @Override
    public void toFront() {
        if (onFrontProperty.get()) {
            return;
        }
        super.toFront();
        fadeInTransition.playFromStart();
        onFrontProperty.set(true);
    }

    @Override
    public void toBack() {
        if (!onFrontProperty.get()) {
            return;
        }
        super.toBack();
        fadeOutTransition.playFromStart();
        onFrontProperty.set(false);
    }

    public ReadOnlyBooleanProperty onFrontProperty() {
        return onFrontProperty.getReadOnlyProperty();
    }

    public boolean isOnFront() {
        return onFrontProperty().get();
    }
}
