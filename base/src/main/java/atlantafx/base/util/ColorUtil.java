package atlantafx.base.util;

import javafx.scene.paint.Color;

/**
 * @author Nonoas
 * @datetime 2021/12/19 14:23
 */
public class ColorUtil {
    private ColorUtil() {
    }

    /**
     * 将 {@link Color} 实例转换为带 “#” 的十六进制字符串
     *
     * @param color {@link Color} 实例
     * @return 带 “#” 的十六进制字符串
     */
    public static String colorToHEX(Color color) {
        return color.toString().replace("0x", "#");
    }
}
