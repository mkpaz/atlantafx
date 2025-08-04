/*
 * SPDX-License-Identifier: MIT
 *
 * Copyright (c) 2013, 2016, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package atlantafx.base.controls;

import static atlantafx.base.controls.Calendar.isValidDate;
import static java.time.temporal.ChronoField.DAY_OF_WEEK;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoUnit.DAYS;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.WEEKS;
import static java.time.temporal.ChronoUnit.YEARS;
import static javafx.scene.layout.Region.USE_PREF_SIZE;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DecimalStyle;
import java.time.temporal.ChronoUnit;
import java.time.temporal.ValueRange;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import atlantafx.base.util.NullSafetyHelper;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jspecify.annotations.Nullable;

/**
 * The default skin for the {@link Calendar} control.
 */
public class CalendarSkin extends BehaviorSkinBase<Calendar, CalendarBehavior> {

    // formatters
    final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("y");
    final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM");
    final DateTimeFormatter weekNumberFormatter = DateTimeFormatter.ofPattern("w");
    final DateTimeFormatter dayCellFormatter = DateTimeFormatter.ofPattern("d");
    final DateTimeFormatter monthFormatterSO = DateTimeFormatter.ofPattern("LLLL");    // standalone month name
    final DateTimeFormatter weekDayNameFormatter = DateTimeFormatter.ofPattern("ccc"); // standalone day name

    // UI
    protected final VBox rootPane = new VBox();
    protected CalendarGrid calendarGrid;

    protected Button forwardButton = NullSafetyHelper.lateNonNull();
    protected Button backButton = NullSafetyHelper.lateNonNull();
    protected Label monthLabel = NullSafetyHelper.lateNonNull();
    protected Label yearLabel = NullSafetyHelper.lateNonNull();

    // model
    protected final List<DateCell> dayNameCells = new ArrayList<>();
    protected final List<DateCell> weekNumberCells = new ArrayList<>();
    protected final List<DateCell> dayCells = new ArrayList<>();
    protected LocalDate[] dayCellDates = NullSafetyHelper.lateNonNull();
    protected @Nullable DateCell lastFocusedDayCell = null;
    protected final int daysPerWeek = getDaysPerWeek();

    private final ObjectProperty<YearMonth> displayedYearMonth
        = new SimpleObjectProperty<>(this, "displayedYearMonth");

    public ObjectProperty<YearMonth> displayedYearMonthProperty() {
        return displayedYearMonth;
    }

    private final ObjectBinding<LocalDate> firstDayOfMonth =
        Bindings.createObjectBinding(() -> displayedYearMonth.get().atDay(1), displayedYearMonth);

    public LocalDate getFirstDayOfMonth() {
        return firstDayOfMonth.get();
    }

    public CalendarSkin(Calendar control) {
        super(control);

        createUI();

        registerChangeListener(control.valueProperty(), e -> {
            LocalDate date = control.getValue();
            displayedYearMonthProperty().set(
                date != null ? YearMonth.from(date) : YearMonth.now(ZoneId.systemDefault())
            );
            updateValues();
            control.fireEvent(new ActionEvent());
        });

        registerChangeListener(control.showWeekNumbersProperty(), e -> {
            updateGrid();
            updateWeekNumberCells();
        });

        registerChangeListener(control.topNodeProperty(), e -> {
            Node node = control.getTopNode();
            if (node == null) {
                rootPane.getChildren().removeIf(c -> c.getStyleClass().contains("top-node"));
            } else {
                if (!node.getStyleClass().contains("top-node")) {
                    node.getStyleClass().add("top-node");
                }
                rootPane.getChildren().add(0, node);
            }
        });

        registerChangeListener(control.bottomNodeProperty(), e -> {
            Node node = control.getBottomNode();
            if (node == null) {
                rootPane.getChildren().removeIf(c -> c.getStyleClass().contains("bottom-node"));
            } else {
                if (!node.getStyleClass().contains("bottom-node")) {
                    node.getStyleClass().add("bottom-node");
                }
                rootPane.getChildren().add(node);
            }
        });
    }

    @Override
    public CalendarBehavior createDefaultBehavior() {
        return new CalendarBehavior(getControl(), this);
    }

    public Locale getLocale() {
        return Locale.getDefault(Locale.Category.FORMAT);
    }

    public Scene getScene() {
        return getControl().getScene();
    }

    /**
     * The primary chronology for display.
     */
    public Chronology getPrimaryChronology() {
        return getControl().getChronology();
    }

    public int getMonthsPerYear() {
        ValueRange range = getPrimaryChronology().range(MONTH_OF_YEAR);
        return (int) (range.getMaximum() - range.getMinimum() + 1);
    }

    public int getDaysPerWeek() {
        ValueRange range = getPrimaryChronology().range(DAY_OF_WEEK);
        return (int) (range.getMaximum() - range.getMinimum() + 1);
    }

    ///////////////////////////////////////////////////////////////////////////
    // UI                                                                    //
    ///////////////////////////////////////////////////////////////////////////

    protected void createUI() {
        if (getControl().getTopNode() != null) {
            getControl().getTopNode().getStyleClass().add("top-node");
            rootPane.getChildren().add(getControl().getTopNode());
        }

        // YearMonth //

        LocalDate value = getControl().getValue();
        displayedYearMonth.set(
            value != null ? YearMonth.from(value) : YearMonth.now(ZoneId.systemDefault())
        );
        displayedYearMonth.addListener((observable, oldValue, newValue) -> updateValues());

        rootPane.getChildren().add(createMonthYearPane());

        // Calendar //

        calendarGrid = new CalendarGrid();
        calendarGrid.getStyleClass().add("calendar-grid");
        calendarGrid.setFocusTraversable(true);
        calendarGrid.setVgap(-1);
        calendarGrid.setHgap(-1);

        // get the weekday labels starting with the weekday that is the first-day-of-the-week
        // according to the locale in the displayed LocalDate
        for (int i = 0; i < daysPerWeek; i++) {
            DateCell cell = new DateCell();
            cell.getStyleClass().add("day-name-cell");
            dayNameCells.add(cell);
        }

        // week number column
        for (int i = 0; i < 6; i++) {
            DateCell cell = new DateCell();
            cell.getStyleClass().add("week-number-cell");
            weekNumberCells.add(cell);
        }

        createDayCells();
        updateGrid();

        // preserve default class name for compatibility reasons
        rootPane.getStyleClass().addAll("date-picker-popup", "calendar");
        rootPane.getChildren().add(calendarGrid);

        if (getControl().getBottomNode() != null) {
            getControl().getBottomNode().getStyleClass().add("bottom-node");
            rootPane.getChildren().add(getControl().getBottomNode());
        }

        getChildren().add(rootPane);

        getControl().setOnKeyPressed(e -> behavior.onKeyPressed(e));

        refresh();
    }

    protected HBox createMonthYearPane() {
        HBox monthYearPane = new HBox();
        monthYearPane.getStyleClass().add("month-year-pane");

        backButton = new Button();
        backButton.getStyleClass().addAll("back-button");
        backButton.setOnMouseClicked(behavior::moveBackward);

        StackPane leftArrow = new StackPane();
        leftArrow.getStyleClass().add("left-arrow");
        leftArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        backButton.setGraphic(leftArrow);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        monthLabel = new Label();
        monthLabel.getStyleClass().add("month-label");

        yearLabel = new Label();
        yearLabel.getStyleClass().add("year-label");

        forwardButton = new Button();
        forwardButton.getStyleClass().addAll("forward-button");
        forwardButton.setOnMouseClicked(behavior::moveForward);

        StackPane rightArrow = new StackPane();
        rightArrow.getStyleClass().add("right-arrow");
        rightArrow.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
        forwardButton.setGraphic(rightArrow);

        monthYearPane.getChildren().addAll(monthLabel, yearLabel, spacer, backButton, forwardButton);

        return monthYearPane;
    }

    protected class CalendarGrid extends GridPane {

        @Override
        protected double computePrefWidth(double height) {
            final double width = super.computePrefWidth(height);

            // Make sure width snaps to pixel when divided by number of columns.
            // GridPane doesn't do this with percentage width constraints.
            // See GridPane.adjustColumnWidths().
            final int nCols = daysPerWeek + (getControl().isShowWeekNumbers() ? 1 : 0);
            final double snapHGap = snapSpaceX(getHgap());
            final double hGaps = snapHGap * (nCols - 1);
            final double left = snapSpaceX(getInsets().getLeft());
            final double right = snapSpaceX(getInsets().getRight());
            final double contentWidth = width - left - right - hGaps;
            return (snapSizeX(contentWidth / nCols) * nCols) + left + right + hGaps;
        }

        @Override
        protected void layoutChildren() {
            // prevent AssertionError in GridPane
            if (getWidth() > 0 && getHeight() > 0) {
                super.layoutChildren();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // API                                                                   //
    ///////////////////////////////////////////////////////////////////////////

    public void refresh() {
        updateDayNameCells();
        updateValues();
    }

    public void updateValues() {
        // preserve this order
        updateWeekNumberCells();
        updateDayCells();
        updateMonthYearPane();
    }

    public void updateGrid() {
        calendarGrid.getColumnConstraints().clear();
        calendarGrid.getChildren().clear();

        final int nCols = daysPerWeek + (getControl().isShowWeekNumbers() ? 1 : 0);

        // column constraints
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(100); // treated as weight
        for (int i = 0; i < nCols; i++) {
            calendarGrid.getColumnConstraints().add(columnConstraints);
        }

        // day names row
        for (int i = 0; i < daysPerWeek; i++) {
            calendarGrid.add(dayNameCells.get(i), i + nCols - daysPerWeek, 1);
        }

        // week number column
        if (getControl().isShowWeekNumbers()) {
            for (int i = 0; i < 6; i++) {
                calendarGrid.add(weekNumberCells.get(i), 0, i + 2);
            }
        }

        // setup 6 rows of daysPerWeek, which is the maximum number of cells
        // required in the worst case layout
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                calendarGrid.add(dayCells.get(row * daysPerWeek + col), col + nCols - daysPerWeek, row + 2);
            }
        }
    }

    public void updateDayNameCells() {
        // first day of week, 1 = monday, 7 = sunday
        final int firstDayOfWeek = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();

        // july 13th 2009 is a Monday, so a firstDayOfWeek = 1 must come out of the 13th
        final LocalDate date = LocalDate.of(2009, 7, 12 + firstDayOfWeek);
        for (int i = 0; i < daysPerWeek; i++) {
            String name = weekDayNameFormatter.withLocale(getLocale()).format(date.plus(i, DAYS));
            dayNameCells.get(i).setText(capitalize(name));
        }
    }

    public void updateWeekNumberCells() {
        if (getControl().isShowWeekNumbers()) {
            final Locale locale = getLocale();
            final int maxWeeksPerMonth = 6;

            final LocalDate firstOfMonth = displayedYearMonth.get().atDay(1);
            for (int i = 0; i < maxWeeksPerMonth; i++) {
                LocalDate date = firstOfMonth.plus(i, WEEKS);
                // use a formatter to ensure correct localization
                // such as when Thai numerals are required.
                String cellText = weekNumberFormatter
                    .withLocale(locale)
                    .withDecimalStyle(DecimalStyle.of(locale))
                    .format(date);
                weekNumberCells.get(i).setText(cellText);
            }
        }
    }

    public void updateDayCells() {
        final Locale locale = getLocale();
        final Chronology chrono = getPrimaryChronology();
        final YearMonth curMonth = displayedYearMonth.get();
        final int firstOfMonthIdx = determineFirstOfMonthDayOfWeek();

        YearMonth prevMonth = null;
        YearMonth nextMonth = null;
        int daysInCurMonth = -1;
        int daysInPrevMonth = -1;

        for (int i = 0; i < 6 * daysPerWeek; i++) {
            final DateCell dayCell = dayCells.get(i);
            dayCell.getStyleClass().setAll("cell", "date-cell", "day-cell");
            dayCell.setDisable(false);
            dayCell.setStyle(null);
            dayCell.setGraphic(null);
            dayCell.setTooltip(null);

            try {
                daysInCurMonth = daysInCurMonth == -1 ? curMonth.lengthOfMonth() : daysInCurMonth;
                YearMonth month = curMonth;
                int day = i - firstOfMonthIdx + 1;

                if (i < firstOfMonthIdx) {
                    if (prevMonth == null) {
                        prevMonth = curMonth.minusMonths(1);
                        daysInPrevMonth = prevMonth.lengthOfMonth();
                    }
                    month = prevMonth;
                    day = i + daysInPrevMonth - firstOfMonthIdx + 1;
                    dayCell.getStyleClass().add("previous-month");
                } else if (i >= firstOfMonthIdx + daysInCurMonth) {
                    if (nextMonth == null) {
                        nextMonth = curMonth.plusMonths(1);
                    }
                    month = nextMonth;
                    day = i - daysInCurMonth - firstOfMonthIdx + 1;
                    dayCell.getStyleClass().add("next-month");
                }

                LocalDate date = month.atDay(day);
                dayCellDates[i] = date;
                ChronoLocalDate cDate = chrono.date(date);

                dayCell.setDisable(false);

                if (isToday(date)) {
                    dayCell.getStyleClass().add("today");
                }

                if (date.equals(getControl().getValue())) {
                    dayCell.getStyleClass().add("selected");
                }

                String cellText = dayCellFormatter.withLocale(locale)
                    .withChronology(chrono)
                    .withDecimalStyle(DecimalStyle.of(locale))
                    .format(cDate);

                dayCell.setText(cellText);
                dayCell.updateItem(date, false);
            } catch (DateTimeException ex) {
                // date is out of range
                dayCell.setText(" ");
                dayCell.setDisable(true);
            }
        }
    }

    // determine on which day of week idx the first of the months is
    private int determineFirstOfMonthDayOfWeek() {
        // determine with which cell to start
        int firstDayOfWeek = WeekFields.of(getLocale()).getFirstDayOfWeek().getValue();
        int firstOfMonthIdx = displayedYearMonth.get().atDay(1).getDayOfWeek().getValue() - firstDayOfWeek;
        return firstOfMonthIdx < 0 ? firstOfMonthIdx + daysPerWeek : firstOfMonthIdx;
    }

    public void updateMonthYearPane() {
        YearMonth yearMonth = displayedYearMonth.get();
        monthLabel.setText(formatMonth(yearMonth));
        yearLabel.setText(formatYear(yearMonth));
        backButton.setDisable(!canGoMonthBack());
        forwardButton.setDisable(!canGoMonthForward());
    }

    protected String formatMonth(YearMonth yearMonth) {
        Chronology chrono = getPrimaryChronology();
        try {
            ChronoLocalDate chronoDate = chrono.date(yearMonth.atDay(1));
            String str = monthFormatterSO.withLocale(getLocale())
                .withChronology(chrono)
                .format(chronoDate);
            if (Character.isDigit(str.charAt(0))) {
                // fallback: if standalone format returned a number, use standard format instead
                str = monthFormatter.withLocale(getLocale())
                    .withChronology(chrono)
                    .format(chronoDate);
            }
            return capitalize(str);
        } catch (DateTimeException ex) {
            // date is out of range
            return "";
        }
    }

    protected String formatYear(YearMonth yearMonth) {
        Chronology chrono = getPrimaryChronology();
        try {
            ChronoLocalDate chronoDate = chrono.date(yearMonth.atDay(1));
            return yearFormatter.withLocale(getLocale())
                .withChronology(chrono)
                .withDecimalStyle(DecimalStyle.of(getLocale()))
                .format(chronoDate);
        } catch (DateTimeException ex) {
            // date is out of range
            return "";
        }
    }

    public void forward(int offset, ChronoUnit unit, boolean focusDayCell) {
        YearMonth yearMonth = displayedYearMonth.get();
        DateCell dateCell = lastFocusedDayCell;
        if (dateCell == null || !getDayCellDate(dateCell).getMonth().equals(yearMonth.getMonth())) {
            dateCell = findDayCellForDate(yearMonth.atDay(1));
        }
        goToDayCell(dateCell, offset, unit, focusDayCell);
    }

    public void goToDayCell(DateCell dateCell, int offset, ChronoUnit unit, boolean focusDayCell) {
        goToDate(getDayCellDate(dateCell).plus(offset, unit), focusDayCell);
    }

    public void goToDate(LocalDate date, boolean focusDayCell) {
        if (isValidDate(getPrimaryChronology(), date)) {
            displayedYearMonth.set(YearMonth.from(date));
            if (focusDayCell) {
                findDayCellForDate(date).requestFocus();
            }
        }
    }

    private DateCell findDayCellForDate(LocalDate date) {
        for (int i = 0; i < dayCellDates.length; i++) {
            if (date.equals(dayCellDates[i])) {
                return dayCells.get(i);
            }
        }
        return dayCells.get(dayCells.size() / 2 + 1);
    }

    public void selectDayCell(DateCell dateCell) {
        getControl().setValue(getDayCellDate(dateCell));
    }

    private LocalDate getDayCellDate(DateCell dateCell) {
        return dayCellDates[dayCells.indexOf(dateCell)];
    }

    protected void createDayCells() {
        EventHandler<MouseEvent> dayCellActionHandler = e -> {
            if (e.getButton() != MouseButton.PRIMARY) {
                return;
            }
            DateCell dayCell = (DateCell) e.getSource();
            selectDayCell(dayCell);
            lastFocusedDayCell = dayCell;
        };

        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < daysPerWeek; col++) {
                DateCell dayCell = createDayCell();
                dayCell.addEventHandler(MouseEvent.MOUSE_CLICKED, dayCellActionHandler);
                dayCells.add(dayCell);
            }
        }

        dayCellDates = new LocalDate[6 * daysPerWeek];
    }

    protected DateCell createDayCell() {
        Callback<Calendar, DateCell> factory = getControl().getDayCellFactory();
        return Objects.requireNonNullElseGet(
            factory != null ? factory.call(getControl()) : null,
            DateCell::new
        );
    }

    public void rememberFocusedDayCell() {
        Node node = getControl().getScene().getFocusOwner();
        if (node instanceof DateCell dc) {
            lastFocusedDayCell = dc;
        }
    }

    public boolean canGoMonthBack() {
        return isValidDate(getPrimaryChronology(), getFirstDayOfMonth(), -1, DAYS);
    }

    public boolean canGoMonthForward() {
        return isValidDate(getPrimaryChronology(), getFirstDayOfMonth(), +1, MONTHS);
    }

    public boolean canGoYearBack() {
        return isValidDate(getPrimaryChronology(), getFirstDayOfMonth(), -1, YEARS);
    }

    public boolean canGoYearForward() {
        return isValidDate(getPrimaryChronology(), getFirstDayOfMonth(), +1, YEARS);
    }

    public void clearFocus() {
        LocalDate focusDate = Objects.requireNonNullElseGet(getControl().getValue(), LocalDate::now);
        if (YearMonth.from(focusDate).equals(displayedYearMonth.get())) {
            goToDate(focusDate, true); // focus date
        } else {
            backButton.requestFocus(); // should not happen
        }
    }

    private static String capitalize(String word) {
        if (word.length() > 0) {
            int firstChar = word.codePointAt(0);
            if (!Character.isTitleCase(firstChar)) {
                word = new String(new int[] {Character.toTitleCase(firstChar)}, 0, 1)
                    + word.substring(Character.offsetByCodePoints(word, 0, 1));
            }
        }
        return word;
    }

    private static boolean isToday(@Nullable LocalDate date) {
        return date != null && date.equals(today());
    }

    private static LocalDate today() {
        return LocalDate.now(ZoneId.systemDefault());
    }
}
