package atlantafx.base.util;

import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

/**
 * UI 处理工具
 *
 * @author Nonoas
 * @datetime 2021/12/5 11:25
 */
public class UIUtil {

    private UIUtil() {
    }

    /**
     * 设置组件可见度
     *
     * @param node 指定组件
     * @param v    可见枚举
     */
    public static void setVisible(Node node, Visibility v) {
        node.setVisible(v.isVisible());
        node.setManaged(v.isManaged());
    }

    /**
     * 设置ImageView的宽高
     *
     * @param widthHeight 宽和高
     */
    public static void setImageViewSize(ImageView iv, Double widthHeight) {
        iv.setFitWidth(widthHeight);
        iv.setFitHeight(widthHeight);
    }

    /**
     * 设置ImageView的宽高
     *
     * @param width  宽
     * @param height 高
     */
    public static void setImageViewSize(ImageView iv, Double width, Double height) {
        iv.setFitWidth(width);
        iv.setFitHeight(height);
    }

    /**
     * 设置 AnchorPane 锚点
     *
     * @param child  需要设置的子节点
     * @param top    上
     * @param right  右
     * @param bottom 下
     * @param left   左
     */
    public static void setAnchor(Node child, double top, double right, double bottom, double left) {
        AnchorPane.setTopAnchor(child, top);
        AnchorPane.setRightAnchor(child, right);
        AnchorPane.setBottomAnchor(child, bottom);
        AnchorPane.setLeftAnchor(child, left);
    }

    /**
     * 设置 AnchorPane 锚点
     *
     * @param child              需要设置的子节点
     * @param topRightBottomLeft 上下左右使用一样的锚点值
     */
    public static void setAnchor(Node child, double topRightBottomLeft) {
        AnchorPane.setTopAnchor(child, topRightBottomLeft);
        AnchorPane.setRightAnchor(child, topRightBottomLeft);
        AnchorPane.setBottomAnchor(child, topRightBottomLeft);
        AnchorPane.setLeftAnchor(child, topRightBottomLeft);
    }

    public static Bounds getScreeBounds(Node node) {
        Bounds bounds = node.getBoundsInLocal();
        return node.localToScreen(bounds);
    }
}
