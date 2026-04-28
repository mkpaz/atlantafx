/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import java.time.LocalDate;
import java.time.chrono.Chronology;
import java.time.chrono.HijrahChronology;
import java.time.chrono.IsoChronology;
import java.time.chrono.JapaneseChronology;
import java.time.chrono.MinguoChronology;
import java.time.chrono.ThaiBuddhistChronology;
import javafx.scene.control.DateCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class CalendarTest {

    private Calendar calendar;

    @BeforeEach
    public void setUp() {
        calendar = new Calendar();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(calendar.getStyleClass()).contains("calendar");
    }

    @Test
    public void testDefaultValueIsNull() {
        assertThat(calendar.getValue()).isNull();
    }

    @Test
    public void testConstructorWithDate() {
        var date = LocalDate.of(2025, 6, 15);
        var cal = new Calendar(date);
        assertThat(cal.getValue()).isEqualTo(date);
    }

    @Test
    public void testConstructorWithNullDate() {
        var cal = new Calendar(null);
        assertThat(cal.getValue()).isNull();
    }

    @Test
    public void testValuePropertySetterGetter() {
        var date = LocalDate.of(2025, 3, 20);
        calendar.setValue(date);
        assertThat(calendar.getValue()).isEqualTo(date);

        calendar.setValue(null);
        assertThat(calendar.getValue()).isNull();
    }

    @Test
    public void testValuePropertyIsNotNull() {
        assertThat(calendar.valueProperty()).isNotNull();
    }

    @Test
    public void testDefaultChronologyIsIso() {
        assertThat(calendar.getChronology()).isEqualTo(IsoChronology.INSTANCE);
    }

    @Test
    public void testChronologyPropertySetterGetter() {
        calendar.setChronology(JapaneseChronology.INSTANCE);
        assertThat(calendar.getChronology()).isEqualTo(JapaneseChronology.INSTANCE);

        calendar.setChronology(HijrahChronology.INSTANCE);
        assertThat(calendar.getChronology()).isEqualTo(HijrahChronology.INSTANCE);
    }

    @Test
    public void testChronologyPropertyResetToDefault() {
        calendar.setChronology(MinguoChronology.INSTANCE);
        assertThat(calendar.getChronology()).isEqualTo(MinguoChronology.INSTANCE);

        calendar.setChronology(null);
        // null chronology falls back to locale default, which is typically ISO
        assertThat(calendar.getChronology()).isNotNull();
    }

    @Test
    public void testChronologyPropertyIsNotNull() {
        assertThat(calendar.chronologyProperty()).isNotNull();
    }

    @Test
    public void testShowWeekNumbersDefaultsToFalse() {
        assertThat(calendar.isShowWeekNumbers()).isFalse();
    }

    @Test
    public void testShowWeekNumbersPropertySetterGetter() {
        calendar.setShowWeekNumbers(true);
        assertThat(calendar.isShowWeekNumbers()).isTrue();

        calendar.setShowWeekNumbers(false);
        assertThat(calendar.isShowWeekNumbers()).isFalse();
    }

    @Test
    public void testShowWeekNumbersPropertyIsNotNull() {
        assertThat(calendar.showWeekNumbersProperty()).isNotNull();
    }

    @Test
    public void testDayCellFactoryDefaultsToNull() {
        assertThat(calendar.getDayCellFactory()).isNull();
    }

    @Test
    public void testDayCellFactoryPropertySetterGetter() {
        Callback<Calendar, DateCell> factory = c -> new DateCell();
        calendar.setDayCellFactory(factory);
        assertThat(calendar.getDayCellFactory()).isSameAs(factory);

        calendar.setDayCellFactory(null);
        assertThat(calendar.getDayCellFactory()).isNull();
    }

    @Test
    public void testDayCellFactoryPropertyIsNotNull() {
        assertThat(calendar.dayCellFactoryProperty()).isNotNull();
    }

    @Test
    public void testTopNodeDefaultsToNull() {
        assertThat(calendar.getTopNode()).isNull();
    }

    @Test
    public void testTopNodePropertySetterGetter() {
        var node = new VBox();
        calendar.setTopNode(node);
        assertThat(calendar.getTopNode()).isSameAs(node);

        calendar.setTopNode(null);
        assertThat(calendar.getTopNode()).isNull();
    }

    @Test
    public void testTopNodePropertyIsNotNull() {
        assertThat(calendar.topNodeProperty()).isNotNull();
    }

    @Test
    public void testBottomNodeDefaultsToNull() {
        assertThat(calendar.getBottomNode()).isNull();
    }

    @Test
    public void testBottomNodePropertySetterGetter() {
        var node = new VBox();
        calendar.setBottomNode(node);
        assertThat(calendar.getBottomNode()).isSameAs(node);

        calendar.setBottomNode(null);
        assertThat(calendar.getBottomNode()).isNull();
    }

    @Test
    public void testBottomNodePropertyIsNotNull() {
        assertThat(calendar.bottomNodeProperty()).isNotNull();
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = calendar.createDefaultSkin();
        assertThat(skin).isInstanceOf(CalendarSkin.class);
    }

    @Test
    public void testClassCssMetaDataIsNotEmpty() {
        assertThat(Calendar.getClassCssMetaData()).isNotEmpty();
    }

    @Test
    public void testControlCssMetaDataMatchesClassCssMetaData() {
        assertThat(calendar.getControlCssMetaData())
                .containsExactlyElementsOf(Calendar.getClassCssMetaData());
    }

    @Test
    public void testIsValidDateWithNullReturnsTrue() {
        assertThat(Calendar.isValidDate(IsoChronology.INSTANCE, null)).isTrue();
    }

    @Test
    public void testIsValidDateWithValidDateReturnsTrue() {
        var date = LocalDate.of(2025, 1, 1);
        assertThat(Calendar.isValidDate(IsoChronology.INSTANCE, date)).isTrue();
    }

    @Test
    public void testIsValidDateWithOffsetAndUnit() {
        var date = LocalDate.of(2025, 6, 15);
        assertThat(Calendar.isValidDate(IsoChronology.INSTANCE, date, 1, java.time.temporal.ChronoUnit.DAYS)).isTrue();
    }

    @Test
    public void testIsValidDateWithNullDateAndOffsetReturnsFalse() {
        assertThat(Calendar.isValidDate(IsoChronology.INSTANCE, null, 1, java.time.temporal.ChronoUnit.DAYS)).isFalse();
    }

    @Test
    public void testThaiBuddhistChronology() {
        calendar.setChronology(ThaiBuddhistChronology.INSTANCE);
        assertThat(calendar.getChronology()).isEqualTo(ThaiBuddhistChronology.INSTANCE);
    }
}
