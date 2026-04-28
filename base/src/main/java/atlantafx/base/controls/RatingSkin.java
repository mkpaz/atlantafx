/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.control.SkinBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

/**
 * The default skin for the {@link Rating} control.
 *
 * <p>Uses a two-layer layout for partial rating support:
 * a background pane with unfilled stars and a foreground pane with filled stars
 * clipped to represent the current rating value.
 */
public class RatingSkin extends SkinBase<Rating> {

    protected final StackPane container = new StackPane();
    protected final Pane backgroundPane = new HBox();
    protected final Pane foregroundPane = new HBox();
    protected final Rectangle clipRect = new Rectangle();

    private double hoverRating = -1;

    protected RatingSkin(Rating control) {
        super(control);

        backgroundPane.getStyleClass().add("background");
        foregroundPane.getStyleClass().add("foreground");
        foregroundPane.setMouseTransparent(true);

        foregroundPane.setClip(clipRect);

        container.getStyleClass().add("container");
        container.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        container.getChildren().setAll(backgroundPane, foregroundPane);
        getChildren().setAll(container);

        rebuildStars();
        updateClip();
        updateCursor();

        registerChangeListener(control.ratingProperty(), o -> updateClip());
        registerChangeListener(control.maxProperty(), o -> rebuildStars());
        registerChangeListener(control.editableProperty(), o -> updateCursor());

        backgroundPane.widthProperty().addListener(o -> updateClip());

        container.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (getSkinnable().isEditable()) {
                getSkinnable().setRating(calculateRating(e));
            }
        });

        container.addEventHandler(MouseEvent.MOUSE_MOVED, e -> {
            if (getSkinnable().isEditable()) {
                hoverRating = calculateRating(e);
                updateClip();
            }
        });

        container.addEventHandler(MouseEvent.MOUSE_EXITED, e -> {
            hoverRating = -1;
            updateClip();
        });
    }

    private void rebuildStars() {
        backgroundPane.getChildren().clear();
        foregroundPane.getChildren().clear();

        int max = getSkinnable().getMax();
        for (int i = 0; i < max; i++) {
            backgroundPane.getChildren().add(createStar());
            foregroundPane.getChildren().add(createStar());
        }
        updateClip();
    }

    protected Region createStar() {
        var star = new Region();
        star.getStyleClass().add("star");
        star.setMouseTransparent(true);
        return star;
    }

    private void updateClip() {
        Rating control = getSkinnable();
        double r = hoverRating >= 0 ? hoverRating : control.getRating();
        int max = control.getMax();
        if (max <= 0 || backgroundPane.getWidth() <= 0) {
            clipRect.setWidth(0);
            clipRect.setHeight(backgroundPane.getHeight());
            return;
        }
        double totalWidth = backgroundPane.getWidth();
        double fraction = Math.min(r / max, 1.0);
        clipRect.setWidth(totalWidth * fraction);
        clipRect.setHeight(backgroundPane.getHeight());
    }

    private void updateCursor() {
        container.setCursor(getSkinnable().isEditable() ? Cursor.HAND : Cursor.DEFAULT);
    }

    private double calculateRating(MouseEvent event) {
        Rating control = getSkinnable();
        Point2D local = backgroundPane.sceneToLocal(event.getSceneX(), event.getSceneY());
        double x = local.getX();
        double w = backgroundPane.getWidth();
        int max = control.getMax();
        if (w <= 0 || max <= 0) return 0;

        double r = (x / w) * max;
        if (!control.isPartialRating()) {
            r = Math.ceil(r);
        }
        return Math.clamp(max, 0, r);
    }

    @Override
    protected void layoutChildren(double contentX, double contentY,
                                  double contentWidth, double contentHeight) {
        super.layoutChildren(contentX, contentY, contentWidth, contentHeight);
        container.layout();
        updateClip();
    }

    @Override
    public void dispose() {
        backgroundPane.widthProperty().removeListener(o -> {});
        unregisterChangeListeners(getSkinnable().ratingProperty());
        unregisterChangeListeners(getSkinnable().maxProperty());
        unregisterChangeListeners(getSkinnable().editableProperty());
        super.dispose();
    }
}
