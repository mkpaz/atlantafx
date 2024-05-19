package atlantafx.base.layout;

import atlantafx.base.util.UIFactory;
import atlantafx.base.util.UIUtil;
import atlantafx.base.util.Visibility;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Collection;

/**
 * App窗口，通常作为唯一窗口
 */
public class AppStage {

    private double xOffset = 0;
    private double yOffset = 0;

    private final Stage stage = new Stage();

    private final Scene scene;


    /**
     * 窗口最大化属性
     */
    private final SimpleBooleanProperty maximized = new SimpleBooleanProperty(false);

    /**
     * 窗口根布局
     */
    private final TransparentPane stageRootPane;

    /**
     * 根布局阴影半径
     */
    private static final double ROOT_PANE_SHADOW_RADIUS = 15.0;

    public AppStage() {
        // 初始化数据
        stageRootPane = new TransparentPane();
        scene = new Scene(stageRootPane);

        initView();
    }

    /**
     * 初始化视图
     */
    private void initView() {

        scene.setFill(Color.TRANSPARENT);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);

        initSysButtons();
        initCursorListen();
        initResizedListener();
    }

    /**
     * 初始化窗口按钮，包括监听事件
     */
    private void initSysButtons() {
        Button minBtn = UIFactory.createMinimizeButton();
        Button maxBtn = UIFactory.createMaximizeButton(maximized);
        Button closeBtn = UIFactory.createCloseButton();

        minBtn.setOnAction(event -> this.stage.setIconified(true));
        maxBtn.setOnAction(event -> this.setMaximized(!isMaximized()));

        // 最大化按钮绑定 Stage 的 resizable 属性
        // resizable 为 false 的时候不显示最大化按钮
        stage.resizableProperty().addListener((observable, oldValue, resizable) -> {
            if (resizable) {
                UIUtil.setVisible(maxBtn, Visibility.VISIBLE);
            } else {
                UIUtil.setVisible(maxBtn, Visibility.GONE);
            }
        });

        closeBtn.setOnAction(event -> this.close());

        stageRootPane.getSysButtons().addAll(minBtn, maxBtn, closeBtn);
    }

    /**
     * 设置根布局
     *
     * @param parent 根布局
     */
    public void setContentView(Parent parent) {
        stageRootPane.setContent(parent);
    }

    public boolean isMaximized() {
        return maximized.get();
    }

    public SimpleBooleanProperty maximizedProperty() {
        return maximized;
    }


    // 最大化前的宽度，高度
    private double preMaximizedWith = 0.0, preMaximizedHeight = 0.0;

    /**
     * 最大化窗口
     *
     * @param isMax true：最大化
     */
    public void setMaximized(boolean isMax) {
        maximized.set(isMax);
        if (isMax) {
            preMaximizedWith = stage.getWidth();
            preMaximizedHeight = stage.getHeight();

            stageRootPane.setPadding(Insets.EMPTY);
            Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();

            stage.setX(visualBounds.getMinX());
            stage.setY(visualBounds.getMinY());
            stage.setWidth(visualBounds.getWidth());
            stage.setHeight(visualBounds.getHeight());
        } else {
            stageRootPane.setPadding(new Insets(15));
            stage.setWidth(Math.max(preMaximizedWith, stage.getMinWidth()));
            stage.setHeight(Math.max(preMaximizedHeight, stage.getMinHeight()));
            stage.centerOnScreen();
        }
    }

    public ObservableList<Node> getSystemButtons() {
        return stageRootPane.getSysButtons();
    }

    /**
     * 按下监听，用于记录点击时的位置，便于计算窗口拖动距离
     */
    private final EventHandler<MouseEvent> pressHandler = event -> {
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    };

    /**
     * 拖动监听，用于设置拖动后窗口的位置
     */
    private final EventHandler<MouseEvent> draggedHandler = event -> {
        if (!isMaximized()) {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        }
    };


    /**
     * 注册拖动节点到当前 AppStage:<br/>
     * 拖动注册节点时，窗口会随之移动
     *
     * @param parent 注册节点
     * @return 当前窗口
     */
    public AppStage registryDragger(Parent parent) {
        // 设置窗口拖动
        parent.setOnMousePressed(pressHandler);
        parent.setOnMouseDragged(draggedHandler);
        return this;
    }

    /**
     * 注销拖动节点
     *
     * @param parent 注销拖动节点
     * @return 当前窗口
     */
    public AppStage removeDragger(Parent parent) {
        // 取消组件的窗口拖动事件
        parent.setOnMousePressed(null);
        parent.setOnMouseDragged(null);
        return this;
    }


    /**
     * 显示窗口
     */
    public void show() {
        stage.show();
    }

    /**
     * 添加图标
     *
     * @param images 图标集合
     */
    public final void addIcons(Collection<Image> images) {
        stage.getIcons().addAll(images);
    }

    public void close() {
        stage.close();
    }

    public void setMinWidth(int i) {
        stage.setMinWidth(i);
    }

    public void setMinHeight(int i) {
        stage.setMinHeight(i);
    }

    public void setResizable(boolean b) {
        stage.setResizable(b);
    }

    public final void setAlwaysOnTop(boolean b) {
        stage.setAlwaysOnTop(b);
    }

    public final void setTitle(String title) {
        stage.setTitle(title);
    }

    public final void hide() {
        stage.hide();
    }

    public Stage getStage() {
        return stage;
    }


    /**
     * 由于 [Stage.show] 方法不能重写，显示窗口时可能会做一些其他的操作，所以提供此方法。
     * 调用时，如果窗口处于最小化状态，也会显示出来
     */
    public void display() {
        if (stage.isIconified()) {
            stage.setIconified(false);
        }
        stage.show();
    }

    /**
     * 判断窗口是否在显示在屏幕上，即没有最小化且没有隐藏
     */
    public boolean isInsight() {
        return stage.isShowing() && !stage.isIconified();
    }

    /**
     * 设置内容可缩放
     */
    private void initCursorListen() {

        scene.addEventFilter(MouseEvent.MOUSE_MOVED, event -> {
            // 消费此事件防止传递
//            event.consume();
            // 窗口大小不可改变时，直接退出
            if (!stage.isResizable() || this.isMaximized() || stage.isFullScreen()) {
                return;
            }
            Bounds layoutBounds = getResizeDealBounds();
            Cursor cursor = cursorResizeType(event, layoutBounds);
            scene.setCursor(cursor);
        });

    }


    /**
     * 设置窗口大小改变事件
     */
    private void initResizedListener() {

        scene.setOnMouseDragged(event -> {

            if (isMaximized()) {
                return;
            }

            double stageMinWidth = stage.getMinWidth();
            double stageMinHeight = stage.getMinHeight();

            // 保存窗口改变后的x、y坐标和宽度、高度，用于预判是否会小于最小宽度、最小高度
            double nextX = stage.getX();
            double nextY = stage.getY();


            double nextWidth = stage.getWidth();
            double nextHeight = stage.getHeight();

            double currW = nextWidth;
            double currH = nextHeight;

            double stageEndX = nextX + nextWidth;
            double stageEndY = nextY + nextHeight;

            // 所有左边调整
            if (isLeft || isTopLeft || isBottomLeft) {
                nextX = event.getScreenX() - ROOT_PANE_SHADOW_RADIUS;
                nextWidth = stageEndX - nextX;
            }
            // 所有右边调整
            if (isTopRight || isRight || isBottomRight) {
                nextWidth = event.getSceneX();
            }

            // 所有上边调整
            if (isTop || isTopLeft || isTopRight) {
                nextY = event.getScreenY() - ROOT_PANE_SHADOW_RADIUS;
                nextHeight = stageEndY - nextY;
            }
            // 所有下边调整
            if (isBottomLeft || isBottomRight || isBottom) {
                nextHeight = event.getSceneY();
            }

            // 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
            if (nextWidth <= stageMinWidth) {
                nextX = stage.getX();
                nextWidth = currW;
            }

            // 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
            if (nextHeight <= stageMinHeight) {
                nextHeight = currH;
                nextY = stage.getY();
            }

            // 最后统一改变窗口的x、y坐标和宽度、高度，可以防止刷新频繁出现的屏闪情况
            stage.setWidth(nextWidth);
            stage.setHeight(nextHeight);
            stage.setX(nextX);
            stage.setY(nextY);

            if (!(isBottom || isBottomRight || isBottomLeft
                    || isLeft || isRight
                    || isTop || isTopLeft || isTopRight)) {
                stage.setX(event.getScreenX() - xOffset);
                stage.setY(event.getScreenY() - yOffset);
            }

//            event.consume();
        });

        scene.setOnMousePressed(pressHandler);
    }

    /**
     * 窗口拉伸触发的误差半径
     */
    private static final double stageResizeBoundRadius = 5.0;

    private Bounds getResizeDealBounds() {
        // 偏移范围
        final double offsetWith = ROOT_PANE_SHADOW_RADIUS + stageResizeBoundRadius;

        Bounds bounds = stageRootPane.getLayoutBounds();

        double left = bounds.getMinX() + offsetWith;
        double width = bounds.getMaxX() - bounds.getMinX() - 2 * offsetWith;
        double top = bounds.getMinY() + offsetWith;
        double height = bounds.getMaxY() - bounds.getMinX() - 2 * offsetWith;

        return new BoundingBox(left, top, width, height);
    }


    //=================================================================
    //                           窗体拉伸属性
    //=================================================================

    private boolean isRight;
    private boolean isLeft;
    private boolean isBottomLeft;
    private boolean isBottomRight;
    private boolean isBottom;
    private boolean isTopLeft;
    private boolean isTopRight;
    private boolean isTop;

    // 设置鼠标悬停样式
    private Cursor cursorResizeType(MouseEvent e, Bounds bounds) {

        Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型

        double eX = e.getSceneX();
        double eY = e.getSceneY();

        // 先将所有调整窗口状态重置
        isRight = isLeft = false;
        isTop = isTopLeft = isTopRight = false;
        isBottomRight = isBottomLeft = isBottom = false;

        // 超出上边距
        if (eY < bounds.getMinY()) {
            // 左上
            if (eX < bounds.getMinX()) {
                isTopLeft = true;
                cursorType = Cursor.NW_RESIZE;
            }
            // 右上
            else if (eX > bounds.getMaxX()) {
                isTopRight = true;
                cursorType = Cursor.NE_RESIZE;
            }
            // 上
            else {
                isTop = true;
                cursorType = Cursor.N_RESIZE;
            }
        }
        // 超出下边距
        else if (eY > bounds.getMaxY()) {
            // 左下
            if (eX < bounds.getMinX()) {
                isBottomLeft = true;
                cursorType = Cursor.SW_RESIZE;
            }
            // 右下
            else if (eX > bounds.getMaxX()) {
                isBottomRight = true;
                cursorType = Cursor.SE_RESIZE;
            }
            // 下
            else {
                isBottom = true;
                cursorType = Cursor.S_RESIZE;
            }
        }
        // 左右边界
        else {
            // 左
            if (eX < bounds.getMinX()) {
                isLeft = true;
                cursorType = Cursor.W_RESIZE;
            }
            // 右
            else if (eX > bounds.getMaxX()) {
                isRight = true;
                cursorType = Cursor.E_RESIZE;
            }
        }

        return cursorType;
    }
}
