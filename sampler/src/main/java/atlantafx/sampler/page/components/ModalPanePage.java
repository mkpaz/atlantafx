/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ModalPane;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ModalPanePage extends AbstractPage {

    public static final String NAME = "Modal Pane";

    private final ModalPane modalPaneL1 = new ModalPane();
    private final ModalPane modalPaneL2 = new ModalPane(-15);
    private final ModalPane modalPaneL3 = new ModalPane(-20);
    private VBox centerDialog;
    private VBox topDialog;
    private VBox rightDialog;
    private VBox bottomDialog;
    private VBox leftDialog;

    @Override
    public String getName() {
        return NAME;
    }

    public ModalPanePage() {
        super();

        userContent.getChildren().setAll(
            modalPaneL1,
            modalPaneL2,
            modalPaneL3,
            new SampleBlock("Playground", createPlayground())
        );
    }

    private VBox createPlayground() {
        var controlPane = new GridPane();
        controlPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        controlPane.setMaxSize(300, 300);
        controlPane.setHgap(20);
        controlPane.setVgap(20);
        controlPane.getRowConstraints().addAll(
            new RowConstraints(50, 50, 50, Priority.NEVER, VPos.CENTER, false),
            new RowConstraints(50, 50, 50, Priority.NEVER, VPos.CENTER, false),
            new RowConstraints(50, 50, 50, Priority.NEVER, VPos.CENTER, false)
        );

        var topBtn = new Button("Top");
        topBtn.setOnAction(e -> {
            modalPaneL1.setAlignment(Pos.TOP_CENTER);
            modalPaneL1.usePredefinedTransitionFactories(Side.TOP);
            modalPaneL1.show(getOrCreateTopDialog());
        });
        controlPane.add(topBtn, 1, 0);

        var rightBtn = new Button("Right");
        rightBtn.setOnAction(e -> {
            modalPaneL1.setAlignment(Pos.TOP_RIGHT);
            modalPaneL1.usePredefinedTransitionFactories(Side.RIGHT);
            modalPaneL1.show(getOrCreateRightDialog());
        });
        controlPane.add(rightBtn, 2, 1);

        var centerBtn = new Button("Center");
        centerBtn.setOnAction(e -> {
            modalPaneL1.setAlignment(Pos.CENTER);
            modalPaneL1.usePredefinedTransitionFactories(null);
            modalPaneL1.show(getOrCreateCenterDialog());
        });
        controlPane.add(centerBtn, 1, 1);

        var bottomBtn = new Button("Bottom");
        bottomBtn.setOnAction(e -> {
            modalPaneL1.setAlignment(Pos.BOTTOM_CENTER);
            modalPaneL1.usePredefinedTransitionFactories(Side.BOTTOM);
            modalPaneL1.show(getOrCreateBottomDialog());
        });
        controlPane.add(bottomBtn, 1, 2);

        var leftBtn = new Button("Left");
        leftBtn.setOnAction(e -> {
            modalPaneL1.setAlignment(Pos.TOP_LEFT);
            modalPaneL1.usePredefinedTransitionFactories(Side.LEFT);
            modalPaneL1.show(getOrCreateLeftDialog());
        });
        controlPane.add(leftBtn, 0, 1);

        controlPane.getChildren().forEach(c -> ((Button) c).setPrefWidth(100));

        var root = new VBox(controlPane);
        root.setAlignment(Pos.CENTER);

        return root;
    }

    private Pane getOrCreateCenterDialog() {
        if (centerDialog != null) {
            return centerDialog;
        }

        centerDialog = createGenericDialog(450, 450, e -> modalPaneL1.hide(true));

        return centerDialog;
    }

    private Node getOrCreateTopDialog() {
        if (topDialog != null) {
            return topDialog;
        }

        topDialog = createGenericDialog(-1, 150, e -> modalPaneL1.hide(true));
        return topDialog;
    }

    private Node getOrCreateRightDialog() {
        if (rightDialog != null) {
            return rightDialog;
        }

        rightDialog = createGenericDialog(250, -1, e -> modalPaneL1.hide(true));
        return rightDialog;
    }

    private Node getOrCreateBottomDialog() {
        if (bottomDialog != null) {
            return bottomDialog;
        }

        bottomDialog = createGenericDialog(-1, 150, e -> modalPaneL1.hide(true));
        return bottomDialog;
    }

    private Node getOrCreateLeftDialog() {
        if (leftDialog != null) {
            return leftDialog;
        }

        leftDialog = createGenericDialog(250, -1, e -> modalPaneL1.hide(true));
        return leftDialog;
    }

    private VBox createOverflowDialog() {
        var dialog = createGenericDialog(400, 400, e1 -> modalPaneL1.hide(true));

        var content = new VBox();
        for (int i = 0; i < 10; i++) {
            var r = new Rectangle(600, 100);
            if (i % 2 == 0) {
                r.setFill(Color.AZURE);
            } else {
                r.setFill(Color.TOMATO);
            }
            content.getChildren().add(r);
        }

        var scrollPane = new ScrollPane();
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxHeight(20_000);
        scrollPane.setContent(content);

        dialog.getChildren().setAll(scrollPane);

        return dialog;
    }

    private VBox createFullScreenDialog() {
        return createGenericDialog(-1, -1, e1 -> modalPaneL1.hide(true));
    }

    private VBox createLevel1Dialog() {
        var dialog = createGenericDialog(600, 600, e1 -> modalPaneL1.hide(true));

        var nextDialogBtn = new Button("Dialog 2");
        nextDialogBtn.setOnAction(e -> {
            modalPaneL2.setAlignment(Pos.CENTER);
            modalPaneL2.usePredefinedTransitionFactories(null);
            modalPaneL2.show(createLevel2Dialog());
        });
        dialog.getChildren().add(nextDialogBtn);

        return dialog;
    }

    private VBox createLevel2Dialog() {
        var dialog = createGenericDialog(450, 450, e2 -> modalPaneL2.hide(true));

        var nextDialogBtn = new Button("Dialog 3");
        nextDialogBtn.setOnAction(e -> {
            modalPaneL3.setAlignment(Pos.CENTER);
            modalPaneL3.usePredefinedTransitionFactories(null);
            modalPaneL3.show(createLevel3Dialog());
        });
        dialog.getChildren().add(nextDialogBtn);

        return dialog;
    }

    private VBox createLevel3Dialog() {
        return createGenericDialog(300, 300, e2 -> modalPaneL3.hide(true));
    }

    private VBox createGenericDialog(double width, double height, EventHandler<ActionEvent> closeHandler) {
        var dialog = new VBox(10);
        dialog.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        dialog.setAlignment(Pos.CENTER);
        dialog.setMinSize(width, height);
        dialog.setMaxSize(width, height);

        var closeBtn = new Button("Close");
        closeBtn.setOnAction(closeHandler);
        dialog.getChildren().add(closeBtn);

        return dialog;
    }
}
