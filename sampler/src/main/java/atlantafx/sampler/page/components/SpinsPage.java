/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Spin;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Colour;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.spins.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public final class SpinsPage extends AbstractPage {

    public static final String NAME = "Spins";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    public SpinsPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The Spin represents an indeterminate progress/loading indicator. \
            The control comes with a wide variety of skins you can find in a separate module."""
        );
        addNode(overview());
    }

    //*************************************************************************

    private Region overview() {
        var spinsGrid = new FlowPane(2, 2);
        for (int i = 0; i < getSkins().size(); i++) {
            spinsGrid.getChildren().add(new SpinCell(getSkins().get(i), i + 1));
        }
        VBox.setVgrow(spinsGrid, Priority.ALWAYS);

        var hintBox = new HBox(new Label("Click on a spinner to start it."));
        hintBox.setAlignment(Pos.CENTER);

        return new VBox(20, hintBox, new ColorChanger(spinsGrid), spinsGrid);
    }

    private List<Spin> getSkins() {
        return List.of(
            AccordionBallsSpin.create(),
            BarsEqualizerSpin.create(),
            BarsScaleSpin.create(),
            ChasingSquaresSpin.create(),
            ClockSpin.create(),
            CoffeeCupSpin.create(),
            CometSpin.create(),
            DiamondFillSpin.create(),
            DoubleArcSpin.create(),
            EclipseSpin.create(),
            FlipSquareSpin.create(),
            FourSquaresSpin.create(),
            GearsSpin.create(),
            HourglassSpin.create(),
            InnerArcSpin.create(),
            LoupeSearchSpin.create(),
            MergingBallsSpin.create(),
            PacmanSpin.create(),
            PerimeterBallSpin.create(),
            PieFillSpin.create(),
            PlanesSpin.create(),
            PulsatingDotsSpin.create(),
            PuzzleSpin.create(),
            RadiatingSpin.create(),
            RippleSpin.create(),
            RollingBallsSpin.create(),
            TextDotsSpin.create(),
            TextFillSpin.create(),
            TextProgressSpin.create(),
            ZebraSpin.create()
        );
    }

    private static class ColorChanger extends HBox {

        private static final Color DEFAULT_PRIMARY_COLOR = Color.DARKORCHID;
        private static final Color DEFAULT_SECONDARY_COLOR = Color.DARKSALMON;

        public ColorChanger(Pane cssRoot) {
            var primaryColor = new ColorPicker(DEFAULT_PRIMARY_COLOR);
            var secondaryColor = new ColorPicker(DEFAULT_SECONDARY_COLOR);

            setSpacing(10);
            setMinHeight(50);
            setAlignment(Pos.CENTER);

            getChildren().addAll(
                new Text("Primary"), primaryColor,
                new Text("Secondary"), secondaryColor
            );

            primaryColor.valueProperty().subscribe((old, val) ->
                updateStyles(cssRoot, val, secondaryColor.getValue())
            );
            secondaryColor.valueProperty().subscribe((old, val) ->
                updateStyles(cssRoot, primaryColor.getValue(), val)
            );
        }

        private void updateStyles(Pane pane, Color primaryColor, Color secondaryColor) {
            var declarations = new ArrayList<String>();

            if (!DEFAULT_PRIMARY_COLOR.equals(primaryColor)) {
                declarations.add("-spin-color-primary: " + Colour.color(primaryColor).toHex());
            }
            if (!DEFAULT_SECONDARY_COLOR.equals(secondaryColor)) {
                declarations.add("-spin-color-secondary: " + Colour.color(secondaryColor).toHex());
            }

            var css = ".spin { %s; }".formatted(String.join("; ", declarations));
            pane.getStylesheets().setAll(Styles.toDataURI(css));
        }
    }

    private static class SpinCell extends StackPane {

        private final BooleanProperty disabled = new SimpleBooleanProperty(true);

        public SpinCell(Spin spin, int index) {
            super();

            spin.setText("Loading");
            spin.autostart(false);
            Tooltip.install(this, new Tooltip(spin.getSkin().getClass().getSimpleName()));

            Label indexLabel = new Label(String.valueOf(index));
            StackPane.setAlignment(indexLabel, Pos.TOP_LEFT);
            StackPane.setMargin(indexLabel, new Insets(8, 0, 0, 8));

            setOnMouseClicked(evt -> {
                if (disabled.get()) {
                    spin.start();
                    disabled.set(false);
                    setStyle(
                        "-fx-background-insets: 0, 2; -fx-background-color: -color-accent-emphasis, -color-bg-default;"
                    );
                } else {
                    spin.stop();
                    disabled.set(true);
                    setStyle(
                        "-fx-background-insets: 0, 2; -fx-background-color: -color-bg-default, -color-bg-default;"
                    );
                }
            });

            setPrefSize(150, 150);
            setStyle(
                "-fx-background-insets: 0, 2; -fx-background-color: -color-bg-default, -color-bg-default;"
            );

            getChildren().addAll(indexLabel, spin);
        }
    }
}
