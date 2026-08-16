/* SPDX-License-Identifier: MIT */

package atlantafx.spins;

import atlantafx.base.controls.Spin;
import atlantafx.base.controls.SpinSkin;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Skin;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;
import javafx.util.Subscription;
import org.jspecify.annotations.Nullable;

import java.util.Objects;

/**
 * Skin implementation for {@link Spin} representing an animated steaming coffee cup.
 */
public class CoffeeCupSpin implements Skin<Spin>, SpinSkin {

    public static final String STYLE_CLASS = "coffee-cup-spin";
    public static final double DEFAULT_DURATION = 1.0;
    public static final double DEFAULT_SIZE = 28.0;
    public static final double DEFAULT_STEAM_WIDTH = 4.0;
    public static final double DEFAULT_STEAM_ANGLE = -10.0;

    // geometric proportions relative to a base size of 48x40px
    protected static final double CUP_WIDTH_RATIO = 1.0;             // 48px
    protected static final double CUP_HEIGHT_RATIO = 0.833;          // 40px
    protected static final double TOP_CORNER_RADIUS_RATIO = 0.12;    // 12% top rounding
    protected static final double BOTTOM_CORNER_RADIUS_RATIO = 0.35; // 35% bottom rounding
    protected static final double HANDLE_WIDTH_RATIO = 0.333;        // 16px
    protected static final double HANDLE_HEIGHT_RATIO = 0.416;       // 20px
    protected static final double HANDLE_OFFSET_X_RATIO = 0.937;
    protected static final double HANDLE_OFFSET_Y_RATIO = 0.166;
    protected static final double HANDLE_BORDER_RATIO = 0.083;       // 4px

    protected static final double STEAM_HEIGHT_RATIO = 0.25;   // line height
    protected static final double STEAM_TRAVEL_Y_RATIO = 0.25; // rise height distance
    protected static final double STEAM_SPACING_RATIO = 0.22;  // spacing between lines

    protected Spin spin;
    protected Pane root;
    protected Path cupBody;
    protected Path handle;
    protected Rectangle steam1;
    protected Rectangle steam2;
    protected Rectangle steam3;

    protected Subscription subscription = Subscription.EMPTY;
    protected final ObjectProperty<@Nullable Timeline> timeline = new SimpleObjectProperty<>();
    protected boolean autostart = true;

    protected final double size;
    protected final double steamWidth;
    protected final double steamAngle;

    /**
     * Constructs a new {@code CoffeeCupSpin} with default base size, steam width, and steam angle.
     *
     * @param spin the {@link Spin} control instance using this skin
     */
    public CoffeeCupSpin(Spin spin) {
        this(spin, DEFAULT_SIZE, DEFAULT_STEAM_WIDTH, DEFAULT_STEAM_ANGLE);
    }

    /**
     * Constructs a new {@code CoffeeCupSpin} with custom size, steam line width, and steam angle.
     *
     * @param spin       the {@link Spin} control instance using this skin
     * @param size       the preferred size of the control
     * @param steamWidth the thickness of rising steam lines
     * @param steamAngle the tilt angle in degrees for the steam lines
     */
    public CoffeeCupSpin(Spin spin, double size, double steamWidth, double steamAngle) {
        this.spin = spin;
        this.size = size > 0 ? size : DEFAULT_SIZE;
        this.steamWidth = steamWidth > 0 ? steamWidth : DEFAULT_STEAM_WIDTH;
        this.steamAngle = steamAngle;

        getSkinnable().getStyleClass().add(STYLE_CLASS);
        construct();
    }

    /** Creates a new {@link Spin} with the default duration. */
    public static Spin create() {
        return create(null);
    }

    /** Creates a new {@link Spin} with the given duration. */
    public static Spin create(@Nullable Duration duration) {
        Spin spin = new Spin(Objects.requireNonNullElse(duration, Duration.seconds(DEFAULT_DURATION)));
        spin.setSkin(new CoffeeCupSpin(spin));
        return spin;
    }

    //*************************************************************************

    protected void construct() {
        double cupW = size * CUP_WIDTH_RATIO;
        double cupH = size * CUP_HEIGHT_RATIO;
        double steamH = size * STEAM_HEIGHT_RATIO;
        double travelY = size * STEAM_TRAVEL_Y_RATIO;

        double steamStartY = steamH + travelY;

        // steam lines
        steam1 = createSteamLine(steamWidth, steamH);
        steam2 = createSteamLine(steamWidth, steamH);
        steam3 = createSteamLine(steamWidth, steamH);

        double steamSpacing = cupW * STEAM_SPACING_RATIO;
        double startX = cupW * 0.20;

        positionSteam(steam1, startX, steamStartY);
        positionSteam(steam2, startX + steamSpacing, steamStartY);
        positionSteam(steam3, startX + (steamSpacing * 2), steamStartY);

        // cup body with asymmetric rounding
        cupBody = createCupPath(cupW, cupH, steamStartY);
        cupBody.setFill(spin.getPrimaryColor());
        cupBody.setStroke(null);

        // handle with inner cutout
        double handleW = size * HANDLE_WIDTH_RATIO;
        double handleH = size * HANDLE_HEIGHT_RATIO;
        double handleX = size * HANDLE_OFFSET_X_RATIO;
        double handleY = steamStartY + (size * HANDLE_OFFSET_Y_RATIO);
        double borderWidth = size * HANDLE_BORDER_RATIO;

        handle = createHandlePath(handleX, handleY, handleW, handleH, borderWidth);
        handle.setFill(spin.getPrimaryColor());
        handle.setStroke(null);

        root = new Pane(steam1, steam2, steam3, cupBody, handle);

        subscription = Subscription.combine(
            spin.primaryColorProperty().subscribe(_ -> updateColors()),
            spin.secondaryColorProperty().subscribe(_ -> updateColors()),
            spin.sceneProperty().subscribe(scene -> {
                if (scene != null) {
                    if (autostart) {
                        start();
                    }
                } else {
                    stop();
                }
            })
        );
    }

    @SuppressWarnings("SuspiciousNameCombination")
    protected Path createCupPath(double width, double height, double topY) {
        double rTop = size * TOP_CORNER_RADIUS_RATIO;
        double rBottom = size * BOTTOM_CORNER_RADIUS_RATIO;

        var path = new Path();
        path.getElements().add(new MoveTo(rTop, topY));
        path.getElements().add(new HLineTo(width - rTop));
        path.getElements().add(new ArcTo(rTop, rTop, 0, width, topY + rTop, false, true));
        path.getElements().add(new LineTo(width, topY + height - rBottom));
        path.getElements().add(new ArcTo(rBottom, rBottom, 0, width - rBottom, topY + height, false, true));
        path.getElements().add(new HLineTo(rBottom));
        path.getElements().add(new ArcTo(rBottom, rBottom, 0, 0, topY + height - rBottom, false, true));
        path.getElements().add(new LineTo(0, topY + rTop));
        path.getElements().add(new ArcTo(rTop, rTop, 0, rTop, topY, false, true));
        path.getElements().add(new ClosePath());

        return path;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected Path createHandlePath(double x, double y, double w, double h, double bw) {
        double rOuter = bw;
        double rInner = Math.max(1.0, bw / 2.0);

        var path = new Path();
        path.setFillRule(FillRule.EVEN_ODD);

        // outer handle boundary
        path.getElements().add(new MoveTo(x, y));
        path.getElements().add(new HLineTo(x + w - rOuter));
        path.getElements().add(new ArcTo(rOuter, rOuter, 0, x + w, y + rOuter, false, true));
        path.getElements().add(new LineTo(x + w, y + h - rOuter));
        path.getElements().add(new ArcTo(rOuter, rOuter, 0, x + w - rOuter, y + h, false, true));
        path.getElements().add(new HLineTo(x));

        // inner cutout boundary
        path.getElements().add(new LineTo(x, y + h - bw));
        path.getElements().add(new HLineTo(x + w - bw - rInner));
        path.getElements().add(new ArcTo(rInner, rInner, 0, x + w - bw, y + h - bw - rInner, false, false));
        path.getElements().add(new LineTo(x + w - bw, y + bw + rInner));
        path.getElements().add(new ArcTo(rInner, rInner, 0, x + w - bw - rInner, y + bw, false, false));
        path.getElements().add(new HLineTo(x));
        path.getElements().add(new ClosePath());

        return path;
    }

    protected Rectangle createSteamLine(double width, double height) {
        var line = new Rectangle(width, height);
        line.setArcWidth(width);
        line.setArcHeight(width);
        line.setStrokeType(StrokeType.INSIDE);
        line.setFill(spin.getPrimaryColor());
        line.setOpacity(0.0);

        // apply rotation via transform centered on the steam line center
        var rotate = new Rotate(steamAngle, width / 2.0, height / 2.0);
        line.getTransforms().add(rotate);

        return line;
    }

    protected void positionSteam(Rectangle steam, double x, double startY) {
        steam.setX(x);
        steam.setY(startY - steam.getHeight());
    }

    protected Timeline initTimeline() {
        updateColors();

        double travelY = size * STEAM_TRAVEL_Y_RATIO;
        Duration duration = spin.getDuration();

        var nextTimeline = new Timeline();

        // phase delays (0ms, 15% of duration, 30% of duration)
        Duration delay1 = Duration.ZERO;
        Duration delay2 = duration.multiply(0.15);
        Duration delay3 = duration.multiply(0.30);

        addSteamKeyFrames(nextTimeline, steam1, travelY, delay1, duration);
        addSteamKeyFrames(nextTimeline, steam2, travelY, delay2, duration);
        addSteamKeyFrames(nextTimeline, steam3, travelY, delay3, duration);

        nextTimeline.setCycleCount(Timeline.INDEFINITE);
        return nextTimeline;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    protected void addSteamKeyFrames(Timeline timeline, Rectangle steam,
                                     double travelY, Duration delay, Duration total) {
        Duration start = delay;
        Duration mid = delay.add(total.multiply(0.5));
        Duration end = delay.add(total);

        // start phase: bottom position, fully transparent
        timeline.getKeyFrames().add(new KeyFrame(start,
            new KeyValue(steam.translateYProperty(), 0.0, Interpolator.EASE_OUT),
            new KeyValue(steam.opacityProperty(), 0.0, Interpolator.EASE_OUT)
        ));

        // mid phase: half-rise position, semi-opaque
        timeline.getKeyFrames().add(new KeyFrame(mid,
            new KeyValue(steam.translateYProperty(), -travelY * 0.5, Interpolator.EASE_OUT),
            new KeyValue(steam.opacityProperty(), 0.6, Interpolator.EASE_OUT)
        ));

        // end phase: full height, fully transparent
        timeline.getKeyFrames().add(new KeyFrame(end,
            new KeyValue(steam.translateYProperty(), -travelY, Interpolator.EASE_OUT),
            new KeyValue(steam.opacityProperty(), 0.0, Interpolator.EASE_OUT)
        ));
    }

    @Override
    public Spin getSkinnable() {
        return spin;
    }

    @Override
    public Node getNode() {
        return root;
    }

    @Override
    @SuppressWarnings("all")
    public void dispose() {
        getSkinnable().getStyleClass().remove(STYLE_CLASS);
        subscription.unsubscribe();
        doStop();
        timeline.set(null);
        spin = null;
    }

    @Override
    public void autostart(boolean autostart) {
        this.autostart = autostart;
    }

    @Override
    public void start() {
        Platform.runLater(() -> timeline.set(doStart()));
    }

    @Override
    public void stop() {
        Platform.runLater(() -> {
            doStop();
            timeline.set(null);
        });
    }

    @Override
    public double computeMaxWidth(double height) {
        double handleX = size * HANDLE_OFFSET_X_RATIO;
        double handleW = size * HANDLE_WIDTH_RATIO;
        return handleX + handleW;
    }

    @Override
    public double computeMaxHeight(double width) {
        double cupH = size * CUP_HEIGHT_RATIO;
        double steamH = size * STEAM_HEIGHT_RATIO;
        double travelY = size * STEAM_TRAVEL_Y_RATIO;
        return cupH + steamH + travelY;
    }

    protected void updateColors() {
        Paint primaryColor = spin.getPrimaryColor();
        Paint secondaryColor = spin.getSecondaryColor();
        cupBody.setFill(primaryColor);
        handle.setFill(primaryColor);
        steam1.setFill(secondaryColor);
        steam2.setFill(secondaryColor);
        steam3.setFill(secondaryColor);
    }

    protected Timeline doStart() {
        Timeline activeTimeline = timeline.get();
        if (activeTimeline != null) {
            activeTimeline.stop();
        }
        Timeline nextTimeline = initTimeline();
        nextTimeline.playFromStart();
        return nextTimeline;
    }

    protected void doStop() {
        Timeline activeTimeline = timeline.get();
        if (activeTimeline != null) {
            activeTimeline.jumpTo(Duration.ZERO);
            activeTimeline.stop();
        }

        resetSteamState(steam1);
        resetSteamState(steam2);
        resetSteamState(steam3);
    }

    protected void resetSteamState(Rectangle steam) {
        steam.setTranslateY(0.0);
        steam.setOpacity(0.0);
    }
}