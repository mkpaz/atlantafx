/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ModalPane;
import atlantafx.base.controls.Tile;
import atlantafx.base.layout.ModalBox;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Cursor;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public final class ModalPanePage extends OutlinePage {

    public static final String NAME = "ModalPane";

    private final ModalPane modalPane = new ModalPane();
    private final ModalPane modalPaneTop = new ModalPane(-15);
    private final ModalPane modalPaneTopmost = new ModalPane(-20);

    @Override
    public String getName() {
        return NAME;
    }

    public ModalPanePage() {
        super();

        // add modal pane to the root container, which is StackPane
        getChildren().addAll(modalPane, modalPaneTop, modalPaneTopmost);
        modalPane.setId("modalPane");
        modalPaneTop.setId("modalPaneTop");
        modalPaneTopmost.setId("modalPaneTopmost");

        // reset side and transition to reuse a single modal pane between different examples
        modalPane.displayProperty().addListener((obs, old, val) -> {
            if (!val) {
                modalPane.setAlignment(Pos.CENTER);
                modalPane.usePredefinedTransitionFactories(null);
            }
        });

        addPageHeader();
        addFormattedText("""
            A container for displaying application dialogs ot top of the current scene \
            without opening a modal [code]javafx.stage.Stage[/code]. It's a translucent \
            (glass) pane that can hold arbitrary content as well as animate its appearance."""
        );
        addSection("Usage", usageExample());
        addSection("Content Position", contentPositionExample());
        addSection("Persistent", persistentExample());
        addSection("Nesting", nestingExample());
        addSection("Maximized", maximizedExample());
        addSection("Overflowed", overflowedExample());
        addSection("ModalBox", modalBoxExample());
        addSection("Lightbox", lightboxExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var dialog = new Dialog(450, 450);

        var openBtn = new Button("Open Dialog");
        openBtn.setOnAction(evt -> modalPane.show(dialog));

        var closeBtn = new Button("Close");
        closeBtn.setOnAction(evt -> modalPane.hide(true));
        dialog.getChildren().setAll(closeBtn);
        //snippet_1:end

        var box = new HBox(openBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            A [i]ModalPane[/i] can hold any content. By default, you just need to call the \
            [code]show()[/code] method, which is a convenience method for setting the content \
            of the modal pane and triggering its display state at the same time.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox contentPositionExample() {
        //snippet_2:start
        var topDialog = new Dialog(-1, 150);
        topDialog.getChildren().setAll(new Label(
            FAKER.country().name()
        ));

        var openTopBtn = new Button("Top");
        openTopBtn.setOnAction(evt -> {
            modalPane.setAlignment(Pos.TOP_CENTER);
            modalPane.usePredefinedTransitionFactories(Side.TOP);
            modalPane.show(topDialog);
        });

        // ~
        var rightDialog = new Dialog(250, -1);
        rightDialog.getChildren().setAll(new Label(
            FAKER.country().name()
        ));

        var openRightBtn = new Button("Right");
        openRightBtn.setOnAction(evt -> {
            modalPane.setAlignment(Pos.TOP_RIGHT);
            modalPane.usePredefinedTransitionFactories(Side.RIGHT);
            modalPane.show(rightDialog);
        });

        // ~
        var bottomDialog = new Dialog(-1, 150);
        bottomDialog.getChildren().setAll(new Label(
            FAKER.country().name()
        ));

        var openBottomBtn = new Button("Bottom");
        openBottomBtn.setOnAction(evt -> {
            modalPane.setAlignment(Pos.BOTTOM_CENTER);
            modalPane.usePredefinedTransitionFactories(Side.BOTTOM);
            modalPane.show(bottomDialog);
        });

        // ~
        var leftDialog = new Dialog(250, -1);
        leftDialog.getChildren().setAll(new Label(
            FAKER.country().name()
        ));

        var openLeftBtn = new Button("Left");
        openLeftBtn.setOnAction(evt -> {
            modalPane.setAlignment(Pos.TOP_LEFT);
            modalPane.usePredefinedTransitionFactories(Side.LEFT);
            modalPane.show(leftDialog);
        });
        //snippet_2:end

        openTopBtn.setPrefWidth(100);
        openRightBtn.setPrefWidth(100);
        openBottomBtn.setPrefWidth(100);
        openLeftBtn.setPrefWidth(100);

        var box = new HBox(HGAP_20, openTopBtn, openRightBtn, openBottomBtn, openLeftBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            The alignment and animated appearance of modal content can be changed \
            via corresponding properties.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox persistentExample() {
        //snippet_3:start
        var dialog = new Dialog(450, 450);

        var openBtn = new Button("Open Dialog");
        openBtn.setOnAction(evt -> {
            modalPane.setPersistent(true);
            modalPane.show(dialog);
        });

        var closeBtn = new Button("Close");
        closeBtn.setOnAction(evt -> {
            modalPane.hide(true);
            modalPane.setPersistent(false);
        });
        dialog.getChildren().setAll(closeBtn);
        //snippet_3:end

        var box = new HBox(openBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            By default, the [i]ModalPane[/i] exits when the ESC button is pressed \
            or when the mouse is clicked outside the content area. [code]setPersistent()[/code] \
            property prevents this behavior and instead plays a bouncing animation.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 3), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox nestingExample() {
        //snippet_4:start
        var dialog = new Dialog(600, 600);
        var topDialog = new Dialog(450, 450);
        var topmostDialog = new Dialog(300, 300);

        var openBtn = new Button("Open Dialog 1");
        // topViewOrder = -10 (default)
        openBtn.setOnAction(evt -> modalPane.show(dialog));

        var topDialogBtn = new Button("Open Dialog 2");
        topDialogBtn.setOnAction(// topViewOrder = -15
            evt -> modalPaneTop.show(topDialog)
        );
        dialog.getChildren().add(topDialogBtn);

        var topmostDialogBtn = new Button("Open Dialog 3");
        topmostDialogBtn.setOnAction(// topViewOrder = -20
            evt -> modalPaneTopmost.show(topmostDialog)
        );
        topDialog.getChildren().add(topmostDialogBtn);
        //snippet_4:end

        var box = new HBox(openBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            There is no a specific support for nested dialogs. But, you can achieve \
            the same behavior by stacking multiple modal panes and using the corresponding \
            [code]topViewOrder[/code] property value.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 4), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox maximizedExample() {
        //snippet_5:start
        var dialog = new Dialog(-1, -1);

        var openBtn = new Button("Open Dialog");
        openBtn.setOnAction(evt -> modalPane.show(dialog));

        var closeBtn = new Button("Close");
        closeBtn.setOnAction(evt -> modalPane.hide(true));
        dialog.getChildren().setAll(closeBtn);
        //snippet_5:end

        var box = new HBox(openBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            To create a maximized dialog, simply use a content node such as [i]VBox[/i] \
            that expands itself in both the horizontal and vertical directions.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 5), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox overflowedExample() {
        //snippet_6:start
        var dialog1 = new Dialog(450, -1);
        dialog1.setPadding(new Insets(20));

        var openBtn1 = new Button("Open Dialog 1");
        openBtn1.setOnAction(evt -> {
            StackPane.setMargin(dialog1, new Insets(20));
            modalPane.show(dialog1);
        });

        var textFlow1 = new TextFlow();
        dialog1.getChildren().setAll(textFlow1);
        for (int i = 0; i < 30; i++) {
            textFlow1.getChildren().add(
                new Text(FAKER.lorem().paragraph() + "\n\n")
            );
        }

        // ~
        var dialog2 = new Dialog(450, -1);
        dialog2.setPadding(new Insets(10, 0, 10, 0));

        var openBtn2 = new Button("Open Dialog 2");
        openBtn2.setOnAction(evt -> {
            StackPane.setMargin(dialog2, new Insets(20));
            modalPane.show(dialog2);
        });

        var textFlow2 = new TextFlow();
        textFlow2.setMaxWidth(430);
        textFlow2.setPadding(new Insets(10, 20, 10, 20));

        var scrollPane2 = new ScrollPane(textFlow2);
        scrollPane2.setMaxHeight(10_000);
        dialog2.getChildren().setAll(scrollPane2);

        for (int i = 0; i < 30; i++) {
            textFlow2.getChildren().add(
                new Text(FAKER.lorem().paragraph() + "\n\n")
            );
        }
        //snippet_6:end

        var box = new HBox(HGAP_20, openBtn1, openBtn2);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            The [i]ModalPane[/i] is already scrollable by default, but you can also use a \
            [i]ScrollPane[/i] for the content node if needed.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 6), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox modalBoxExample() {
        //snippet_8:start
        // you can use a selector
        var dialog = new ModalBox("#modalPane");

        // ... or your pass a ModalPane instance directly
        //var dialog = new ModalBox(modalPane);

        // ... or you can set your own close handler
        //dialog.setOnClose(/* whatever */);

        var ta = new TextArea();
        ta.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        VBox.setVgrow(ta, Priority.ALWAYS);

        var content = new VBox(
            16,
            new Tile("Example Dialog", FAKER.lorem().sentence(10)),
            ta
        );
        content.setPadding(new Insets(16));
        dialog.addContent(content);
        AnchorPane.setTopAnchor(content, 0d);
        AnchorPane.setRightAnchor(content, 0d);
        AnchorPane.setBottomAnchor(content, 0d);
        AnchorPane.setLeftAnchor(content, 0d);

        var openBtn = new Button("Open Dialog");
        openBtn.setOnAction(evt -> modalPane.show(dialog));
        //snippet_8:end

        dialog.setPrefSize(450, 450);
        dialog.setMaxSize(450, 450);

        var box = new HBox(openBtn);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            The [i]ModalBox[/i] is a specialized control (or layout) designed to hold the \
            [i]ModalPane[/i] dialog content. It includes the close button out-of-the-box \
            and allows for the addition of arbitrary children."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 8), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox lightboxExample() {
        //snippet_7:start
        var modalImage = new ImageView();

        var thumbnail1 = new ImageView(new Image(
            Resources.getResourceAsStream(
                "images/gallery/kush-dwivedi-unsplash.jpg"
            )));
        thumbnail1.setFitWidth(180);
        thumbnail1.setFitHeight(120);
        thumbnail1.setCursor(Cursor.HAND);
        thumbnail1.setOnMouseClicked(evt -> {
            modalImage.setImage(thumbnail1.getImage());
            modalPane.show(modalImage);
            modalImage.requestFocus();
        });

        var thumbnail2 = new ImageView(new Image(
            Resources.getResourceAsStream(
                "images/gallery/markus-spiske-unsplash.jpg"
            )));
        thumbnail2.setFitWidth(180);
        thumbnail2.setFitHeight(120);
        thumbnail2.setCursor(Cursor.HAND);
        thumbnail2.setOnMouseClicked(evt -> {
            modalImage.setImage(thumbnail2.getImage());
            modalPane.show(modalImage);
            modalImage.requestFocus();
        });

        var thumbnail3 = new ImageView(new Image(
            Resources.getResourceAsStream(
                "images/gallery/r0m0_4-unsplash.jpg"
            )));
        thumbnail3.setFitWidth(180);
        thumbnail3.setFitHeight(120);
        thumbnail3.setCursor(Cursor.HAND);
        thumbnail3.setOnMouseClicked(evt -> {
            modalImage.setImage(thumbnail3.getImage());
            modalPane.show(modalImage);
            modalImage.requestFocus();
        });
        //snippet_7:end

        var box = new HBox(5, thumbnail1, thumbnail2, thumbnail3);
        box.setAlignment(Pos.CENTER);

        var description = BBCodeParser.createFormattedText("""
            This simple example demonstrates how [i]ModalPane[/i] can be used to \
            implement the famous JS lightbox effect."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 7), description);
        example.setAllowDisable(false);

        return example;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class Dialog extends VBox {

        public Dialog(int width, int height) {
            super();

            setSpacing(10);
            setAlignment(Pos.CENTER);
            setMinSize(width, height);
            setMaxSize(width, height);
            setStyle("-fx-background-color: -color-bg-default;");
        }
    }
}
