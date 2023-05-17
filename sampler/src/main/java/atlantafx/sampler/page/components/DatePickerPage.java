/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.chrono.HijrahChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.util.StringConverter;

public final class DatePickerPage extends OutlinePage {

    public static final String NAME = "DatePicker";

    @Override
    public String getName() {
        return NAME;
    }

    public DatePickerPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A date picker control that allows the user to enter a date as text or to select \
            a date from a calendar popup. The calendar is based on either the standard \
            ISO-8601 chronology or any of the other chronology classes defined in the \
            [code]java.time.chrono[/code] package."""
        );
        addSection("Usage", usageExample());
        addSection("Editable", editableExample());
    }

    public ExampleBox usageExample() {
        //snippet_1:start
        var today = LocalDate.now(ZoneId.systemDefault());

        var dp1 = new DatePicker(today);
        dp1.setEditable(false);
        dp1.setPrefWidth(200);

        var dp2 = new DatePicker(today.plusDays(10));
        dp2.setShowWeekNumbers(true);
        dp2.setPrefWidth(200);

        var dp3 = new DatePicker(today.plusMonths(1));
        dp3.setChronology(HijrahChronology.INSTANCE);
        dp3.setPrefWidth(200);
        //snippet_1:end

        var grid = new GridPane();
        grid.setHgap(50);
        grid.setVgap(10);
        grid.addRow(0, captionLabel("Default"), dp1);
        grid.addRow(1, captionLabel("Week Numbers"), dp2);
        grid.addRow(2, captionLabel("Second Chronology"), dp3);

        var description = BBCodeParser.createFormattedText("""
            The [i]DatePicker[/i] control consists of a combo box with a date \
            field and a date chooser."""
        );

        return new ExampleBox(grid, new Snippet(getClass(), 1), description);
    }

    public ExampleBox editableExample() {
        //snippet_2:start
        final var today = LocalDate.now(ZoneId.systemDefault());
        final var formatter = DateTimeFormatter.ISO_DATE;

        var dp = new DatePicker(today);
        dp.setPromptText("yyyy-MM-dd");
        dp.setEditable(true);
        dp.setPrefWidth(200);
        dp.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate localDate) {
                if (localDate == null) {
                    return "";
                }
                return formatter.format(localDate);
            }

            @Override
            public LocalDate fromString(String dateString) {
                if (dateString == null || dateString.trim().isEmpty()) {
                    return today;
                }
                try {
                    return LocalDate.parse(dateString, formatter);
                } catch (DateTimeParseException e) {
                    return today;
                }
            }
        });
        //snippet_2:end

        var box = new HBox(dp);
        var description = BBCodeParser.createFormattedText("""
            The [code]editable[/code] property controls whether the [i]DatePicker[/i] \
            allows users to manually input a date."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }
}
