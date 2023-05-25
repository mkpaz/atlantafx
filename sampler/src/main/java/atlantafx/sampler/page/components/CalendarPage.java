/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Calendar;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public final class CalendarPage extends OutlinePage {

    public static final String NAME = "Calendar";
    private static final LocalDate TODAY = LocalDate.now(ZoneId.systemDefault());

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public CalendarPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The date picker control that allows the user to select a date. Unlike standard JavaFX \
            [font=monospace]javafx.scene.control.DatePicker[/font] the [i]Calendar[/i] is not concealed \
            within a popup window."""
        );
        addSection("Usage", usageExample());
        addSection("No Past Dates", noPastDatesExample());
        addSection("User Slots", clockExample());
        addSection("Style", styleExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var cal = new Calendar(TODAY);
        cal.setShowWeekNumbers(true);
        //snippet_1:end

        var box = new HBox(cal);
        var description = BBCodeParser.createFormattedText("""
            In the default state, no date is selected. You can modify this behavior \
            either by using the constructor or by utilizing the [font=monospace]setValue()[/font] \
            method.""");

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox noPastDatesExample() {
        //snippet_2:start
        class FutureDateCell extends DateCell {

            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(TODAY));
            }
        }

        var cal = new Calendar(TODAY);
        cal.setDayCellFactory(c -> new FutureDateCell());
        //snippet_2:end

        var box = new HBox(cal);
        var description = BBCodeParser.createFormattedText("""
            This example demonstrates how you can disable past dates in the [i]Calendar[/i]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private ExampleBox clockExample() {
        final var style = """
            -fx-border-width: 0 0 0.5 0;
            -fx-border-color: -color-border-default;""";

        //snippet_3:start
        class Clock extends VBox {

            static final DateTimeFormatter DATE_FORMATTER =
                DateTimeFormatter.ofPattern("EEEE, LLLL dd, yyyy");
            static final DateTimeFormatter TIME_FORMATTER =
                DateTimeFormatter.ofPattern("HH:mm:ss");

            public Clock() {
                var clockLbl = new Label(TIME_FORMATTER.format(
                    LocalTime.now(ZoneId.systemDefault()))
                );
                clockLbl.getStyleClass().add(Styles.TITLE_2);

                var dateLbl = new Label(DATE_FORMATTER.format(
                    LocalDate.now(ZoneId.systemDefault()))
                );

                // -fx-border-width: 0 0 0.5 0;
                // -fx-border-color: -color-border-default;
                setStyle(style);
                setSpacing(10);
                getChildren().setAll(clockLbl, dateLbl);

                var t = new Timeline(new KeyFrame(
                    Duration.seconds(1),
                    e -> {
                        var time = LocalTime.now(ZoneId.systemDefault());
                        clockLbl.setText(TIME_FORMATTER.format(time));
                    }
                ));
                t.setCycleCount(Animation.INDEFINITE);
                t.playFromStart();
            }
        }

        var cal = new Calendar(TODAY);
        cal.setTopNode(new Clock());
        cal.setShowWeekNumbers(true);
        //snippet_3:end

        var box = new HBox(cal);
        var description = BBCodeParser.createFormattedText("""
            The [i]Calendar[/i] comes equipped with two slots (top and bottom) where \
            users can place their own content. For example, you can use these slots to \
            display a clock widget on top of the [i]Calendar[/i]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox styleExample() {
        var dataClass = """
            .date-picker-popup {
                -color-date-border: -color-accent-emphasis;
                -color-date-month-year-bg: -color-accent-emphasis;
                -color-date-month-year-fg: -color-fg-emphasis;
            }""";
        //snippet_4:start
        var cal = new Calendar(TODAY);
        cal.setShowWeekNumbers(true);

        // -color-date-border: -color-accent-emphasis;
        // -color-date-month-year-bg: -color-accent-emphasis;
        // -color-date-month-year-fg: -color-fg-emphasis;
        cal.getStylesheets().add(Styles.toDataURI(dataClass));
        //snippet_4:end

        var box = new HBox(cal);
        var description = BBCodeParser.createFormattedText("""
            You can alter the style of the [i]Calendar[/i] by using looked-up color variables."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
