package atlantafx.base.util;

import atlantafx.base.controls.SVGButton;
import atlantafx.base.controls.SVGImage;
import atlantafx.base.controls.SVGPath;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class UIFactory {

    public static Color COMMON_BTN_COLOR = Color.valueOf("#515151");

    public static Color HOVER_0 = Color.valueOf("#dedede");

    private UIFactory() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    public static Button createMinimizeButton() {
        SVGImage svgImage = new SVGImage(SVGPath.MINIMIZE_BUTTON.value(), COMMON_BTN_COLOR);
        svgImage.setSize(15, 1);
        return getBaseButton(svgImage);
    }

    public static Button createMaximizeButton(SimpleBooleanProperty maximizedProperty) {
        // 非最大化时图标
        SVGImage svgImage = new SVGImage(SVGPath.MAXIMIZE_BUTTON.value(), COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);
        // 最大化时图标
        SVGImage svgImage0 = new SVGImage(SVGPath.MAXIMIZE_BUTTON_0.value(), COMMON_BTN_COLOR);
        svgImage0.setSize(15, 15);

        SVGButton btn = (SVGButton) getBaseButton(svgImage);
        maximizedProperty.addListener((observableValue, aBoolean, newVal) -> {
            if (!newVal) {
                btn.setGraphic(svgImage);
            } else {
                btn.setGraphic(svgImage0);
            }
        });
        return btn;
    }

    public static Button createCloseButton() {
        SVGImage svgImage = new SVGImage(SVGPath.CLOSE_BUTTON.value(), COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);
        return new SVGButton.SvgButtonBuilder()
                .graphic(svgImage)
                .graphicColor(COMMON_BTN_COLOR)
                .graphicColorHover(Color.WHITE)
                .backgroundColor(Color.TRANSPARENT)
                .backgroundColorHover(Color.valueOf("#f55"))
                .build();
    }

    public static Button createPinButton(Stage stage) {
        SVGImage svgImage = new SVGImage(SVGPath.PIN_BUTTON.value(), COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);

        SVGImage svgImageTop = new SVGImage(SVGPath.PIN_BUTTON.value(), Color.valueOf("#4bbf73"));
        svgImageTop.setSize(15, 15);

        SVGImage graphic = stage.isAlwaysOnTop() ? svgImageTop : svgImage;
        SVGButton btn = new SVGButton(graphic);
        btn.setStyle("-fx-background-color: transparent");
        btn.setBackGroundColor(Color.TRANSPARENT, HOVER_0);

        btn.setOnAction(e -> {
            boolean onTop = !stage.isAlwaysOnTop();
            stage.setAlwaysOnTop(onTop);
        });

        stage.alwaysOnTopProperty().addListener((o, n, v) -> {
            btn.setGraphic(v ? svgImageTop : svgImage);
        });

        return btn;
    }

    public static Button createMenuButton() {
        SVGImage svgImage = new SVGImage(SVGPath.SETTING_BUTTON.value(), COMMON_BTN_COLOR);
        svgImage.setSize(15, 15);
        return getBaseButton(svgImage);
    }

    public static Button getBaseButton(SVGImage svgImage) {
        return new SVGButton.SvgButtonBuilder()
                .graphic(svgImage)
                .backgroundColor(Color.TRANSPARENT)
                .backgroundColorHover(HOVER_0)
                .build();
    }
}
