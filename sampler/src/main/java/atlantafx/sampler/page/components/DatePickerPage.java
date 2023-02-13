/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.base.theme.Styles.FLAT;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static javafx.scene.layout.GridPane.REMAINING;

import atlantafx.base.controls.InlineDatePicker;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.HijrahChronology;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class DatePickerPage extends AbstractPage {

    public static final String NAME = "DatePicker";
    private static final LocalDate TODAY = LocalDate.now(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE;
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final String DATE_FORMATTER_PROMPT = "yyyy-MM-dd";
    private static final int INLINE_DATE_PICKER_COL = 0;
    private static final int INLINE_DATE_PICKER_ROW = 4;
    private static final CSSFragment NO_YEAR_MONTH_STYLE = new CSSFragment("""
        .date-picker-popup >.month-year-pane {
          visibility: hidden;
          -fx-min-width: 0;
          -fx-pref-width: 0;
          -fx-max-width: 0;
          -fx-min-height: 0;
          -fx-pref-height: 0;
          -fx-max-height: 0;
        }
        """);

    @Override
    public String getName() {
        return NAME;
    }

    private final BooleanProperty weekNumProperty = new SimpleBooleanProperty();
    private final BooleanProperty showClockProperty = new SimpleBooleanProperty();
    private final BooleanProperty editableProperty = new SimpleBooleanProperty();
    private final BooleanProperty offPastDatesProperty = new SimpleBooleanProperty();
    private final BooleanProperty disableProperty = new SimpleBooleanProperty();
    private final Clock clock = new Clock();
    private DatePickerColorSelector colorSelector;

    public DatePickerPage() {
        super();
        setUserContent(new VBox(
            new SampleBlock("Playground", createPlayground())
        ));
    }

    private GridPane createPlayground() {
        var grid = new GridPane();
        grid.setHgap(40);
        grid.setVgap(BLOCK_VGAP);

        final var popupDatePicker = createPopupDatePicker();

        colorSelector = new DatePickerColorSelector(grid);
        final var inlineDatePicker = createInlineDatePicker(null);

        // == CONTROLS ==

        var weekNumToggle = new ToggleSwitch("Week numbers");
        weekNumProperty.bind(weekNumToggle.selectedProperty());
        weekNumToggle.setSelected(true);

        var showClockToggle = new ToggleSwitch("Show clock");
        showClockProperty.bind(showClockToggle.selectedProperty());
        showClockToggle.setSelected(true);

        var showYearMonthToggle = new ToggleSwitch("Show header");
        showYearMonthToggle.setSelected(true);
        showYearMonthToggle.selectedProperty().addListener((obs, old, val) -> {
            if (!val) {
                NO_YEAR_MONTH_STYLE.addTo(grid);
            } else {
                NO_YEAR_MONTH_STYLE.removeFrom(grid);
            }
        });

        var chronologyToggle = new ToggleSwitch("Second chronology");
        chronologyToggle.selectedProperty().addListener(
            (obs, old, val) -> popupDatePicker.setChronology(val ? HijrahChronology.INSTANCE : null)
        );

        var editableToggle = new ToggleSwitch("Editable");
        editableProperty.bind(editableToggle.selectedProperty());
        // clear selected value to demonstrate prompt text
        editableProperty.addListener(
            (obs, old, val) -> popupDatePicker.setValue(val ? null : TODAY)
        );

        var offPastDatesToggle = new ToggleSwitch("No past dates");
        offPastDatesProperty.bind(offPastDatesToggle.selectedProperty());
        offPastDatesProperty.addListener((obs, old, val) -> {
            popupDatePicker.setDayCellFactory(val ? dp -> new FutureDateCell() : null);
            popupDatePicker.setValue(TODAY);

            // we have to create new date picker, because changing cell factory won't update existing cells
            var datePicker = createInlineDatePicker(val ? dp -> new FutureDateCell() : null);
            grid.getChildren().removeIf(n -> n instanceof InlineDatePicker);
            grid.add(datePicker, INLINE_DATE_PICKER_COL, INLINE_DATE_PICKER_ROW);
        });

        var disablePickerToggle = new ToggleSwitch("Disable");
        disableProperty.bind(disablePickerToggle.selectedProperty());

        var controls = new VBox(
            BLOCK_VGAP,
            weekNumToggle,
            showClockToggle,
            showYearMonthToggle,
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

        grid.add(defaultLabel, 0, 0);
        grid.add(popupDatePicker, 0, 1);
        grid.add(new Spacer(20), 0, 2);
        grid.add(inlineLabel, 0, 3);
        grid.add(inlineDatePicker, INLINE_DATE_PICKER_COL, INLINE_DATE_PICKER_ROW);
        grid.add(controls, 1, 0, 1, REMAINING);

        return grid;
    }

    private DatePicker createPopupDatePicker() {
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

    private InlineDatePicker createInlineDatePicker(Callback<InlineDatePicker, DateCell> dayCellFactory) {
        var datePicker = new InlineDatePicker();
        datePicker.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        datePicker.setDayCellFactory(dayCellFactory);
        datePicker.setValue(TODAY);
        datePicker.showWeekNumbersProperty().bind(weekNumProperty);
        datePicker.disableProperty().bind(disableProperty);

        datePicker.setTopNode(clock);
        datePicker.setBottomNode(colorSelector);

        datePicker.topNodeProperty().bind(Bindings.createObjectBinding(
            () -> showClockProperty.get() ? clock : null, showClockProperty)
        );

        return datePicker;
    }

    ///////////////////////////////////////////////////////////////////////////

    private static final StringConverter<LocalDate> DATE_CONVERTER = new StringConverter<>() {

        @Override
        public String toString(LocalDate localDate) {
            if (localDate == null) {
                return "";
            }
            return DATE_FORMATTER.format(localDate);
        }

        @Override
        public LocalDate fromString(String dateString) {
            if (dateString == null || dateString.trim().isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(dateString, DATE_FORMATTER);
            } catch (Exception e) {
                return null;
            }
        }
    };

    private static class FutureDateCell extends DateCell {

        @Override
        public void updateItem(LocalDate date, boolean empty) {
            super.updateItem(date, empty);
            setDisable(empty || date.isBefore(TODAY));
        }
    }

    private static class Clock extends VBox {

        public Clock() {
            var clockLabel = new Label(
                TIME_FORMATTER.format(LocalTime.now(ZoneId.systemDefault()))
            );
            clockLabel.getStyleClass().add(Styles.TITLE_2);

            var dateLabel = new Label(
                DateTimeFormatter.ofPattern("EEEE, LLLL dd, yyyy").format(LocalDate.now(ZoneId.systemDefault()))
            );

            setStyle("""
                -fx-border-width: 0 0 0.5 0;
                -fx-border-color: -color-border-default;"""
            );
            setSpacing(BLOCK_VGAP);
            getChildren().setAll(clockLabel, dateLabel);

            var t = new Timeline(new KeyFrame(
                Duration.seconds(1),
                e -> clockLabel.setText(
                    TIME_FORMATTER.format(LocalTime.now(ZoneId.systemDefault()))
                )
            ));
            t.setCycleCount(Animation.INDEFINITE);
            t.playFromStart();
        }
    }

    // This class shares stylesheet with the AccentColorSelector
    private static class DatePickerColorSelector extends HBox {

        private final Pane parent;
        private final ObjectProperty<CSSFragment> style = new SimpleObjectProperty<>();

        public DatePickerColorSelector(Pane parent) {
            super();
            this.parent = Objects.requireNonNull(parent);
            createView();
        }

        private void createView() {
            var resetBtn = new Button("", new FontIcon(Material2AL.CLEAR));
            resetBtn.getStyleClass().addAll(BUTTON_ICON, FLAT);
            resetBtn.setOnAction(e -> style.set(null));

            style.addListener((obs, old, val) -> {
                if (old != null) {
                    old.removeFrom(parent);
                }
                if (val != null) {
                    val.addTo(parent);
                }
            });

            setAlignment(Pos.CENTER);
            getChildren().setAll(
                colorButton("-color-accent-emphasis", "-color-fg-emphasis"),
                colorButton("-color-success-emphasis", "-color-fg-emphasis"),
                colorButton("-color-danger-emphasis", "-color-fg-emphasis"),
                resetBtn
            );
            getStyleClass().add("color-selector");
        }

        private Button colorButton(String bgColorName, String fgColorName) {
            var icon = new Region();
            icon.getStyleClass().add("icon");
            icon.setStyle("-color-primary:" + bgColorName + ";");

            var btn = new Button("", icon);
            btn.getStyleClass().addAll(BUTTON_ICON, FLAT, "color-button");
            btn.setOnAction(e -> updateStyle(bgColorName, fgColorName));

            return btn;
        }

        private void updateStyle(String bgColorName, String fgColorName) {
            style.set(new CSSFragment(String.format("""
                    .date-picker-popup {
                      -color-date-border: %s;
                      -color-date-month-year-bg: %s;
                      -color-date-month-year-fg: %s;
                    }
                    .date-picker-popup >.top-node {
                        -fx-background-color: %s;
                        -color-fg-default: %s;
                        -color-border-default: %s;
                        -fx-border-color:  %s;
                    }""",
                bgColorName, bgColorName, fgColorName,
                bgColorName, fgColorName, fgColorName, fgColorName
            )));
        }
    }
}
