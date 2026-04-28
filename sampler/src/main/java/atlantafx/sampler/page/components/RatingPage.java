/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Rating;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RatingPage extends OutlinePage {

    public static final String NAME = "Rating";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public RatingPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Rating[/i] control allows users to provide a rating by displaying \
            a row of star-shaped indicators. Click a star to set the rating, or enable \
            partial rating for fractional values like 2.5."""
        );
        addSection("Usage", usageExample());
        addSection("Partial Rating", partialExample());
        addSection("Read Only", readOnlyExample());
        addSection("Custom Max", customMaxExample());
    }

    private static String formatRating(double v) {
        return v == Math.floor(v) ? String.format("%.0f", v) : String.format("%.1f", v);
    }

    private Node usageExample() {
        //snippet_1:start
        var rating = new Rating();
        rating.setRating(3);
        //snippet_1:end

        var valueLabel = new Label("Rating: 3");
        valueLabel.setStyle("-fx-text-fill: -color-fg-muted;");
        rating.ratingProperty().addListener((obs, old, val) ->
            valueLabel.setText("Rating: " + formatRating(val.doubleValue()))
        );

        var box = new VBox(10, rating, valueLabel);
        var description = BBCodeParser.createFormattedText("""
            A [i]Rating[/i] displays a row of 5 stars by default. Click any star \
            to set the rating value. The rating property can also be set programmatically."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node partialExample() {
        //snippet_2:start
        var rating = new Rating();
        rating.setPartialRating(true);
        rating.setRating(2.5);
        //snippet_2:end

        var valueLabel = new Label("Rating: 2.5");
        valueLabel.setStyle("-fx-text-fill: -color-fg-muted;");
        rating.ratingProperty().addListener((obs, old, val) ->
            valueLabel.setText("Rating: " + formatRating(val.doubleValue()))
        );

        var box = new VBox(10, rating, valueLabel);
        var description = BBCodeParser.createFormattedText("""
            Enable [code]partialRating[/code] to allow fractional values. \
            The filled area is clipped proportionally to the rating value."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node readOnlyExample() {
        //snippet_3:start
        var r1 = new Rating();
        r1.setRating(4);
        r1.setEditable(false);

        var r2 = new Rating();
        r2.setRating(1.5);
        r2.setPartialRating(true);
        r2.setEditable(false);

        var r3 = new Rating();
        r3.setRating(0);
        r3.setEditable(false);
        //snippet_3:end

        var l1 = new Label("Rating: 4");
        l1.setStyle("-fx-text-fill: -color-fg-muted;");
        var l2 = new Label("Rating: 1.5");
        l2.setStyle("-fx-text-fill: -color-fg-muted;");
        var l3 = new Label("Rating: 0");
        l3.setStyle("-fx-text-fill: -color-fg-muted;");

        var row1 = new HBox(10, r1, l1);
        row1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        var row2 = new HBox(10, r2, l2);
        row2.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        var row3 = new HBox(10, r3, l3);
        row3.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        var box = new VBox(VGAP_10, row1, row2, row3);
        var description = BBCodeParser.createFormattedText("""
            Set [code]editable[/code] to false to make the rating read-only. \
            This prevents user interaction and changes the cursor to default."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private Node customMaxExample() {
        //snippet_4:start
        var r3 = new Rating(3);
        r3.setRating(2);

        var r7 = new Rating(7);
        r7.setRating(5);

        var r10 = new Rating(10);
        r10.setRating(7);
        //snippet_4:end

        var l3 = new Label("2 / 3");
        var l7 = new Label("5 / 7");
        var l10 = new Label("7 / 10");

        r3.ratingProperty().addListener((obs, old, val) ->
            l3.setText((int) val.doubleValue() + " / 3"));
        r7.ratingProperty().addListener((obs, old, val) ->
            l7.setText((int) val.doubleValue() + " / 7"));
        r10.ratingProperty().addListener((obs, old, val) ->
            l10.setText((int) val.doubleValue() + " / 10"));

        var row1 = new HBox(10, r3, l3);
        row1.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        var row2 = new HBox(10, r7, l7);
        row2.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
        var row3 = new HBox(10, r10, l10);
        row3.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

        var box = new VBox(VGAP_10, row1, row2, row3);
        var description = BBCodeParser.createFormattedText("""
            The maximum number of stars can be changed via the constructor or \
            the [code]max[/code] property."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
