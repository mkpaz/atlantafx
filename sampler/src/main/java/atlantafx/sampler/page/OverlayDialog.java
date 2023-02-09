/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static atlantafx.base.theme.Styles.BUTTON_CIRCLE;
import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.base.theme.Styles.TITLE_4;

import atlantafx.base.controls.Spacer;
import atlantafx.sampler.util.Containers;
import java.util.Objects;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public abstract class OverlayDialog<T extends Region> extends VBox {

    protected static final int CONTENT_CHILD_INDEX = 1;

    protected Label titleLabel;
    protected Button topCloseBtn;
    protected HBox headerBox;

    protected Button bottomCloseBtn;
    protected HBox footerBox;

    protected Runnable onCloseCallback;

    public OverlayDialog() {
        createView();
    }

    protected void createView() {
        titleLabel = new Label();
        titleLabel.getStyleClass().addAll(TITLE_4, "title");

        topCloseBtn = new Button("", new FontIcon(Material2AL.CLOSE));
        topCloseBtn.getStyleClass().addAll(BUTTON_ICON, BUTTON_CIRCLE, FLAT, "close-button");
        topCloseBtn.setOnAction(e -> close());

        headerBox = new HBox(10);
        headerBox.getStyleClass().add("header");
        headerBox.setAlignment(Pos.CENTER_LEFT);
        headerBox.getChildren().setAll(
            titleLabel,
            new Spacer(),
            topCloseBtn
        );
        VBox.setVgrow(headerBox, Priority.NEVER);

        bottomCloseBtn = new Button("Close");
        bottomCloseBtn.getStyleClass().add("form-action");
        bottomCloseBtn.setOnAction(e -> close());
        bottomCloseBtn.setCancelButton(true);

        footerBox = new HBox(10);
        footerBox.getStyleClass().add("footer");
        footerBox.setAlignment(Pos.CENTER_RIGHT);
        footerBox.getChildren().setAll(
            new Spacer(),
            bottomCloseBtn
        );
        VBox.setVgrow(footerBox, Priority.NEVER);

        // IMPORTANT: this guarantees client will use correct width and height
        Containers.usePrefWidth(this);
        Containers.usePrefHeight(this);

        // if you're updating this line, update setContent() method as well
        getChildren().setAll(headerBox, footerBox);

        getStyleClass().add("overlay-dialog");
    }

    protected void setContent(T content) {
        Objects.requireNonNull(content);
        VBox.setVgrow(content, Priority.ALWAYS);

        // content have to be placed exactly between header and footer
        if (getChildren().size() == 2) {
            // add new content
            getChildren().add(CONTENT_CHILD_INDEX, content);
        } else if (getChildren().size() == 3) {
            // overwrite existing content
            getChildren().set(CONTENT_CHILD_INDEX, content);
        } else {
            throw new UnsupportedOperationException("Content cannot be placed because of unexpected children size. "
                + "You should override 'OverlayDialog#setContent()' and place it manually.");
        }
    }

    protected void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void close() {
        if (onCloseCallback != null) {
            onCloseCallback.run();
        }
    }

    public Runnable getOnCloseRequest() {
        return onCloseCallback;
    }

    public void setOnCloseRequest(Runnable handler) {
        this.onCloseCallback = handler;
    }
}
