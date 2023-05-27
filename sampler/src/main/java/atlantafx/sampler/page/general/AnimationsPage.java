/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.NEVER;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.Snippet;
import atlantafx.sampler.util.NodeUtils;
import java.net.URI;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public final class AnimationsPage extends StackPane implements Page {

    public static final String NAME = "Animations";

    private static final int OUTLINE_WIDTH = 250;
    private static final Duration DURATION = Duration.millis(2000);
    private static final ImageView ANIMATED_NODE = new ImageView(new Image(
        Resources.getResourceAsStream("images/fun/saitama.png")
    ));
    private static final ImageView ANIMATED_BG = new ImageView(new Image(
        Resources.getResourceAsStream("images/fun/saitama-bg.png")
    ));

    @Override
    public String getName() {
        return NAME;
    }

    public AnimationsPage() {
        super();

        var imageStack = new StackPane(ANIMATED_BG, ANIMATED_NODE);
        imageStack.setMinSize(450, 250);
        imageStack.setMaxSize(450, 250);
        ANIMATED_NODE.setViewOrder(-10);

        var imageStackWrapper = new StackPane(imageStack);

        var pageHeader = new PageHeader(this);

        var descriptionText = BBCodeParser.createFormattedText("""
            AtlantaFX provides a utility class with factory methods to create a predefined \
            animations for various effects, such as fade, slide, rotate, scale etc."""
        );

        var snippet = new Snippet(getClass(), 1);

        var userContent = new VBox(
            20,
            pageHeader,
            descriptionText,
            snippet.render(),
            imageStackWrapper
        );
        userContent.setPadding(new Insets(50, 0, 50, 0));

        StackPane.setMargin(userContent, new Insets(0, OUTLINE_WIDTH, 0, 0));
        userContent.setMinWidth(Page.MAX_WIDTH - OUTLINE_WIDTH - 100);
        userContent.setMaxWidth(Page.MAX_WIDTH - OUTLINE_WIDTH - 100);

        var outline = createOutline();
        var outlineWrapper = new StackPane(outline);
        StackPane.setAlignment(outline, Pos.TOP_RIGHT);

        var outlineScroll = new ScrollPane();
        outlineScroll.setContent(outlineWrapper);
        NodeUtils.setScrollConstraints(outlineScroll, AS_NEEDED, true, NEVER, true);
        outlineScroll.setMaxHeight(20_000);

        var pageBody = new StackPane();
        Styles.appendStyle(pageBody, "-fx-background-color", "-color-bg-default");
        pageBody.getChildren().setAll(outlineScroll, userContent);

        setMinWidth(Page.MAX_WIDTH);
        getChildren().setAll(pageBody);
    }

    private VBox createOutline() {
        var outline = new VBox();
        outline.setSpacing(10);
        outline.setPadding(new Insets(20));
        outline.setMinWidth(OUTLINE_WIDTH);
        outline.setMaxWidth(OUTLINE_WIDTH);
        Styles.appendStyle(outline, "-fx-background-color", "-color-bg-default");

        outline.getChildren().add(createMenuCaption("Fading"));
        outline.getChildren().addAll(fadeSubMenu());

        outline.getChildren().add(createMenuCaption("Rotating"));
        outline.getChildren().addAll(rotateSubMenu());

        outline.getChildren().add(createMenuCaption("Sliding"));
        outline.getChildren().addAll(slideSubMenu());

        outline.getChildren().add(createMenuCaption("Specials"));
        outline.getChildren().addAll(specialsSubMenu());

        outline.getChildren().add(createMenuCaption("Zooming"));
        outline.getChildren().addAll(zoomSubMenu());

        return outline;
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return true;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "util/" + getName()));
    }

    @Override
    public Node getSnapshotTarget() {
        return null;
    }

    @Override
    public void reset() {
    }

    ///////////////////////////////////////////////////////////////////////////

    // this method isn't used anywhere
    @SuppressWarnings("unused")
    private void snippetText() {
        //snippet_1:start
        var rectangle = new Rectangle(100, 100);
        rectangle.setFill(Color.RED);
        var animation = Animations.fadeIn(rectangle, Duration.seconds(1));
        animation.playFromStart();
        //snippet_1:end
    }

    private List<Node> fadeSubMenu() {
        var fadeIn = createMenuItem("Fade In");
        fadeIn.setOnAction(e -> {
            var t = Animations.fadeIn(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeOut = createMenuItem("Fade Out");
        fadeOut.setOnAction(e -> {
            var t = Animations.fadeOut(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeInDown = createMenuItem("Fade In Down");
        fadeInDown.setOnAction(e -> {
            var t = Animations.fadeInDown(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeOutDown = createMenuItem("Fade Out Down");
        fadeOutDown.setOnAction(e -> {
            var t = Animations.fadeOutDown(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeInLeft = createMenuItem("Fade In Left");
        fadeInLeft.setOnAction(e -> {
            var t = Animations.fadeInLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeOutLeft = createMenuItem("Fade Out Left");
        fadeOutLeft.setOnAction(e -> {
            var t = Animations.fadeOutLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeInRight = createMenuItem("Fade In Right");
        fadeInRight.setOnAction(e -> {
            var t = Animations.fadeInRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeOutRight = createMenuItem("Fade Out Right");
        fadeOutRight.setOnAction(e -> {
            var t = Animations.fadeOutRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeInUp = createMenuItem("Fade In Down");
        fadeInUp.setOnAction(e -> {
            var t = Animations.fadeInUp(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var fadeOutUp = createMenuItem("Fade Out Up");
        fadeOutUp.setOnAction(e -> {
            var t = Animations.fadeOutUp(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        return List.of(
            fadeIn, fadeInDown, fadeInLeft, fadeInRight, fadeInUp,
            fadeOut, fadeOutDown, fadeOutLeft, fadeOutRight, fadeOutUp
        );
    }

    private List<Node> rotateSubMenu() {
        var rotateIn = createMenuItem("Rotate In");
        rotateIn.setOnAction(e -> {
            var t = Animations.rotateIn(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateOut = createMenuItem("Rotate Out");
        rotateOut.setOnAction(e -> {
            var t = Animations.rotateOut(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateInDownLeft = createMenuItem("Rotate In Down Left");
        rotateInDownLeft.setOnAction(e -> {
            var t = Animations.rotateInDownLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateOutDownLeft = createMenuItem("Rotate Out Down Left");
        rotateOutDownLeft.setOnAction(e -> {
            var t = Animations.rotateOutDownLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateInDownRight = createMenuItem("Rotate In Down Right");
        rotateInDownRight.setOnAction(e -> {
            var t = Animations.rotateInDownRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateOutDownRight = createMenuItem("Rotate Out Down Right");
        rotateOutDownRight.setOnAction(e -> {
            var t = Animations.rotateOutDownRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateInUpLeft = createMenuItem("Rotate In Up Left");
        rotateInUpLeft.setOnAction(e -> {
            var t = Animations.rotateInUpLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateOutUpLeft = createMenuItem("Rotate Out Up Left");
        rotateOutUpLeft.setOnAction(e -> {
            var t = Animations.rotateOutUpLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateInUpRight = createMenuItem("Rotate In Up Right");
        rotateInUpRight.setOnAction(e -> {
            var t = Animations.rotateInUpRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rotateOutUpRight = createMenuItem("Rotate Out Up Right");
        rotateOutUpRight.setOnAction(e -> {
            var t = Animations.rotateOutUpRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        return List.of(
            rotateIn, rotateInDownLeft, rotateInDownRight, rotateInUpLeft, rotateInUpRight,
            rotateOut, rotateOutDownLeft, rotateOutDownRight, rotateOutUpLeft, rotateOutUpRight
        );
    }

    private List<Node> slideSubMenu() {
        var slideInDown = createMenuItem("Slide In Down");
        slideInDown.setOnAction(e -> {
            var t = Animations.slideInDown(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideOutDown = createMenuItem("Slide Out Down");
        slideOutDown.setOnAction(e -> {
            var t = Animations.slideOutDown(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideInLeft = createMenuItem("Slide In Left");
        slideInLeft.setOnAction(e -> {
            var t = Animations.slideInLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideOutLeft = createMenuItem("Slide Out Left");
        slideOutLeft.setOnAction(e -> {
            var t = Animations.slideOutLeft(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideInRight = createMenuItem("Slide In Right");
        slideInRight.setOnAction(e -> {
            var t = Animations.slideInRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideOutRight = createMenuItem("Slide Out Right");
        slideOutRight.setOnAction(e -> {
            var t = Animations.slideOutRight(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideInUp = createMenuItem("Slide In Up");
        slideInUp.setOnAction(e -> {
            var t = Animations.slideInUp(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var slideOutUp = createMenuItem("Slide Out Up");
        slideOutUp.setOnAction(e -> {
            var t = Animations.slideOutUp(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        return List.of(
            slideInDown, slideInLeft, slideInRight, slideInUp,
            slideOutDown, slideOutLeft, slideOutRight, slideOutUp
        );
    }

    private List<Node> specialsSubMenu() {
        var flash = createMenuItem("Flash");
        flash.setOnAction(e -> {
            var t = Animations.flash(ANIMATED_NODE);
            t.playFromStart();
        });

        var pulse = createMenuItem("Pulse");
        pulse.setOnAction(e -> {
            var t = Animations.pulse(ANIMATED_NODE);
            t.playFromStart();
        });

        var rollIn = createMenuItem("Roll In");
        rollIn.setOnAction(e -> {
            var t = Animations.rollIn(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var rollOut = createMenuItem("Roll Out");
        rollOut.setOnAction(e -> {
            var t = Animations.rollOut(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var shakeX = createMenuItem("Shake X");
        shakeX.setOnAction(e -> {
            var t = Animations.shakeX(ANIMATED_NODE);
            t.playFromStart();
        });

        var shakeY = createMenuItem("Shake Y");
        shakeY.setOnAction(e -> {
            var t = Animations.shakeY(ANIMATED_NODE);
            t.playFromStart();
        });

        var wobble = createMenuItem("Wobble");
        wobble.setOnAction(e -> {
            var t = Animations.wobble(ANIMATED_NODE);
            t.playFromStart();
        });

        return List.of(
            flash, pulse, rollIn, rollOut, shakeX, shakeY, wobble
        );
    }

    private List<Node> zoomSubMenu() {
        var zoomIn = createMenuItem("Zoom In");
        zoomIn.setOnAction(e -> {
            var t = Animations.zoomIn(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        var zoomOut = createMenuItem("Zoom Out");
        zoomOut.setOnAction(e -> {
            var t = Animations.zoomOut(ANIMATED_NODE, DURATION);
            t.playFromStart();
        });

        return List.of(
            zoomIn, zoomOut
        );
    }

    private Hyperlink createMenuItem(String text) {
        var item = new Hyperlink(text);
        Styles.appendStyle(item, "-color-link-fg", "-color-fg-default");
        Styles.appendStyle(item, "-color-link-fg-visited", "-color-fg-default");
        Styles.appendStyle(item, "-fx-underline", "false");
        return item;
    }

    private Label createMenuCaption(String text) {
        var label = new Label(text);
        label.getStyleClass().addAll(Styles.TEXT_CAPTION, Styles.TEXT_MUTED);
        label.setPadding(new Insets(20, 0, 0, 0));
        return label;
    }
}
