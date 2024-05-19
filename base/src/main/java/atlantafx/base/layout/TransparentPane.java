package atlantafx.base.layout;

import atlantafx.base.util.UIUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;

/**
 * 透明面板
 *
 * @author Nonoas
 * @datetime 2021/12/4 15:42
 */
public class TransparentPane extends AnchorPane {

    /**
     * 内容布局,实际显示节点的布局面板
     */
    private final AnchorPane contentPane = new AnchorPane();

    /**
     * 按钮布局
     */
    private final ObservableList<Node> sysButtons;


    public TransparentPane() {

        HBox sysBtnBox = new HBox();
        sysButtons = sysBtnBox.getChildren();
        sysBtnBox.getStyleClass().add("sys-btn-box");

        sysBtnBox.setAlignment(Pos.CENTER_RIGHT);

        this.setPadding(new Insets(15));

        setStyle("-fx-background-color: rgb(0,0,0,0)");

        contentPane.setStyle("-fx-background-color: white");
        contentPane.setEffect(getDropShadow());

        getChildren().setAll(contentPane, sysBtnBox);

        UIUtil.setAnchor(contentPane, 0.0);

        AnchorPane.setTopAnchor(sysBtnBox, 0.0);
        AnchorPane.setRightAnchor(sysBtnBox, 0.0);

    }

    public ObservableList<Node> getSysButtons() {
        return sysButtons;
    }

    public AnchorPane getContentPane() {
        return this.contentPane;
    }

    /**
     * 设置根布局
     *
     * @param content 根布局
     */
    public void setContent(Node content) {
        if (content instanceof Region region) {
            // 双向绑定宽高，使布局宽高随窗口变化
            UIUtil.setAnchor(region, 0.0);
            contentPane.getChildren().setAll(region);
        } else {
            contentPane.getChildren().setAll(content);
        }
    }

    private DropShadow getDropShadow() {
        // 阴影向外
        DropShadow dropshadow = new DropShadow();
        // 颜色蔓延的距离
        dropshadow.setRadius(15);
        // 颜色变淡的程度
        dropshadow.setSpread(0.15);
        // 设置颜色
        dropshadow.setColor(Color.rgb(0, 0, 0, 0.3));
        return dropshadow;
    }

}
