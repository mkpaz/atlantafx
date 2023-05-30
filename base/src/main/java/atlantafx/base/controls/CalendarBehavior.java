/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static atlantafx.base.util.PlatformUtils.isMac;
import static java.time.temporal.ChronoUnit.MONTHS;
import static java.time.temporal.ChronoUnit.YEARS;
import static javafx.scene.input.KeyCode.ESCAPE;

import java.time.LocalDate;
import java.time.ZoneId;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

/**
 * The default behavior for the {@link Calendar} control.
 */
public class CalendarBehavior extends BehaviorBase<Calendar, CalendarSkin> {

    public CalendarBehavior(Calendar control, CalendarSkin skin) {
        super(control, skin);
    }

    @SuppressWarnings("MissingCasesInEnumSwitch")
    public void onKeyPressed(KeyEvent e) {
        getSkin().rememberFocusedDayCell();

        if (e.getEventType() == KeyEvent.KEY_PRESSED) {
            switch (e.getCode()) {
                case HOME -> {
                    getSkin().goToDate(LocalDate.now(ZoneId.systemDefault()), true);
                    e.consume();
                }
                case PAGE_UP -> {
                    if ((isMac() && e.isMetaDown()) || (!isMac() && e.isControlDown())) {
                        if (getSkin().canGoYearForward()) {
                            getSkin().forward(1, YEARS, true);
                        }
                    } else {
                        if (getSkin().canGoMonthForward()) {
                            getSkin().forward(1, MONTHS, true);
                        }
                    }
                    e.consume();
                }
                case PAGE_DOWN -> {
                    if ((isMac() && e.isMetaDown()) || (!isMac() && e.isControlDown())) {
                        if (getSkin().canGoYearBack()) {
                            getSkin().forward(-1, YEARS, true);
                        }
                    } else {
                        if (getSkin().canGoMonthBack()) {
                            getSkin().forward(-1, MONTHS, true);
                        }
                    }
                    e.consume();
                }
            }
            getSkin().rememberFocusedDayCell();
        }

        // prevents any other key events but ESC from reaching the control owner
        if (e.getCode() != ESCAPE) {
            e.consume();
        }
    }

    public void moveForward(MouseEvent e) {
        if ((isMac() && e.isMetaDown()) || (!isMac() && e.isControlDown())) {
            if (getSkin().canGoYearForward()) {
                getSkin().forward(1, YEARS, true);
            }
        } else {
            if (getSkin().canGoMonthForward()) {
                getSkin().forward(1, MONTHS, true);
            }
        }
        e.consume();
    }

    public void moveBackward(MouseEvent e) {
        if ((isMac() && e.isMetaDown()) || (!isMac() && e.isControlDown())) {
            if (getSkin().canGoYearBack()) {
                getSkin().forward(-1, YEARS, true);
            }
        } else {
            if (getSkin().canGoMonthBack()) {
                getSkin().forward(-1, MONTHS, true);
            }
        }
        e.consume();
    }
}
