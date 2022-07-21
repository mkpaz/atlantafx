/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.InlineDatePicker;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.StringConverter;

import java.time.LocalDate;
import java.time.chrono.HijrahChronology;
import java.time.format.DateTimeFormatter;

import static javafx.scene.layout.GridPane.REMAINING;

public class DatePickerPage extends AbstractPage {

    public static final String NAME = "DatePicker";
    private static final LocalDate TODAY = LocalDate.now();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final String DATE_FORMATTER_PROMPT = "yyyy-MM-dd";
    private static final int INLINE_DATE_PICKER_COL = 0;
    private static final int INLINE_DATE_PICKER_ROW = 4;

    @Override
    public String getName() { return NAME; }

    private final BooleanProperty weekNumProperty = new SimpleBooleanProperty();
    private final BooleanProperty editableProperty = new SimpleBooleanProperty();
    private final BooleanProperty offPastDatesProperty = new SimpleBooleanProperty();
    private final BooleanProperty disableProperty = new SimpleBooleanProperty();

    public DatePickerPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(playground());
    }

    private GridPane playground() {
        var playground = new GridPane();
        playground.setHgap(40);
        playground.setVgap(10);

        final var popupDatePicker = popupDatePicker();
        final var inlineDatePicker = inlineDatePicker(null);

        var inlineValue = new Label();
        inlineValue.setAlignment(Pos.CENTER);
        inlineValue.setMaxWidth(Double.MAX_VALUE);
        inlineValue.textProperty().bind(inlineDatePicker.valueProperty().asString());

        // == CONTROLS ==

        var weekNumToggle = new ToggleSwitch("Week numbers");
        weekNumProperty.bind(weekNumToggle.selectedProperty());
        weekNumToggle.setSelected(true);

        var chronologyToggle = new ToggleSwitch("Second chronology");
        chronologyToggle.selectedProperty().addListener((obs, old, val) ->
                popupDatePicker.setChronology(val ? HijrahChronology.INSTANCE : null)
        );

        var editableToggle = new ToggleSwitch("Editable");
        editableProperty.bind(editableToggle.selectedProperty());
        // clear selected value to demonstrate prompt text
        editableProperty.addListener((obs, old, val) -> popupDatePicker.setValue(val ? null : TODAY));

        var offPastDatesToggle = new ToggleSwitch("No past dates");
        offPastDatesProperty.bind(offPastDatesToggle.selectedProperty());
        offPastDatesProperty.addListener((obs, old, val) -> {
            popupDatePicker.setDayCellFactory(val ? dp -> new CustomDateCell() : null);
            popupDatePicker.setValue(TODAY);

            // we have to create new date picker, because changing cell factory won't update existing cells
            var datePicker = inlineDatePicker(val ? dp -> new CustomDateCell() : null);
            playground.getChildren().removeIf(n -> n instanceof InlineDatePicker);
            playground.add(datePicker, INLINE_DATE_PICKER_COL, INLINE_DATE_PICKER_ROW);
            inlineValue.textProperty().unbind();
            inlineValue.textProperty().bind(datePicker.valueProperty().asString());
        });

        var disablePickerToggle = new ToggleSwitch("Disable");
        disableProperty.bind(disablePickerToggle.selectedProperty());

        var controls = new VBox(10,
                weekNumToggle,
                chronologyToggle,
                editableToggle,
                offPastDatesToggle,
                disablePickerToggle
        );
        controls.setAlignment(Pos.CENTER_RIGHT);

        // == GRID ==
        var defaultLabel = new Label("Default");
        defaultLabel.getStyleClass().add(Styles.TEXT_BOLD);

        var inlineLabel = new Label("Inline");
        inlineLabel.getStyleClass().add(Styles.TEXT_BOLD);

        playground.add(defaultLabel, 0, 0);
        playground.add(popupDatePicker, 0, 1);
        playground.add(new Spacer(20), 0, 2);
        playground.add(inlineLabel, 0, 3);
        playground.add(inlineDatePicker, INLINE_DATE_PICKER_COL, INLINE_DATE_PICKER_ROW);
        playground.add(inlineValue, 0, 5);
        playground.add(controls, 1, 0, 1, REMAINING);

        return playground;
    }

    private DatePicker popupDatePicker() {
        var datePicker = new DatePicker();
        datePicker.setConverter(DATE_CONVERTER);
        datePicker.setPromptText(DATE_FORMATTER_PROMPT);
        datePicker.setMaxWidth(Double.MAX_VALUE);
        datePicker.setValue(TODAY);
        datePicker.showWeekNumbersProperty().bind(weekNumProperty);
        datePicker.editableProperty().bind(editableProperty);
        datePicker.disableProperty().bind(disableProperty);

        return datePicker;
    }

    private InlineDatePicker inlineDatePicker(Callback<InlineDatePicker, DateCell> dayCellFactory) {
        var datePicker = new InlineDatePicker();
        datePicker.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        datePicker.setDayCellFactory(dayCellFactory);
        datePicker.setValue(TODAY);
        datePicker.showWeekNumbersProperty().bind(weekNumProperty);
        datePicker.disableProperty().bind(disableProperty);

        return datePicker;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static final StringConverter<LocalDate> DATE_CONVERTER = new StringConverter<>() {

        @Override
        public String toString(LocalDate localDate) {
            if (localDate == null) { return ""; }
            return DATE_FORMATTER.format(localDate);
        }

        @Override
        public LocalDate fromString(String dateString) {
            if (dateString == null || dateString.trim().isEmpty()) { return null; }
            try {
                return LocalDate.parse(dateString, DATE_FORMATTER);
            } catch (Exception e) { return null; }
        }
    };

    private static class CustomDateCell extends DateCell {

        public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.compareTo(TODAY) < 0);
        }
    }
}
