package atlantafx.base.controls;

import javafx.beans.NamedArg;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.css.CssMetaData;
import javafx.css.SimpleStyleableDoubleProperty;
import javafx.css.Styleable;
import javafx.css.StyleableDoubleProperty;
import javafx.css.converter.SizeConverter;
import javafx.scene.Parent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 用于显示 svg 图像的节点
 *
 * @author Shadi Shaheen
 * @version 1.0
 * @since 2016-03-09
 */
public class SVGImage extends Region {
    private static final String DEFAULT_STYLE_CLASS = "jfx-svg-image";

    private final int glyphId;
    private final String name;
    private static final int DEFAULT_PREF_SIZE = 64;
    private double widthHeightRatio = 1;
    private final ObjectProperty<Paint> fill = new SimpleObjectProperty<>();

    public SVGImage() {
        this(null);
    }

    public SVGImage(@NamedArg("svgPathContent") String svgPathContent) {
        this(-1, "UNNAMED", svgPathContent, Color.BLACK);
    }

    public SVGImage(@NamedArg("svgPathContent") String svgPathContent, @NamedArg("fill") Paint fill) {
        this(-1, "UNNAMED", svgPathContent, fill);
    }

    /**
     * 为指定的 svg 内容和颜色构造 SVGGlyph 节点 注意：创建单个 SVG 图像时不需要 name 和 glyphId，
     * 它们已在 SVGGlyphLoader 中用于加载 icomoon svg 字体。
     *
     * @param glyphId        整数表示字形ID
     * @param name           glyph name
     * @param svgPathContent path
     * @param fill           填充颜色
     */
    public SVGImage(int glyphId, String name, String svgPathContent, Paint fill) {
        this.glyphId = glyphId;
        this.name = name;
        getStyleClass().add(DEFAULT_STYLE_CLASS);
        this.fill.addListener((observable) -> setBackground(new Background(
                new BackgroundFill(getFill() == null ? Color.BLACK : getFill(), null, null))));

        shapeProperty().addListener(observable -> {
            Shape shape = getShape();
            if (getShape() != null) {
                widthHeightRatio = shape.prefWidth(-1) / shape.prefHeight(-1);
                if (getSize() != Region.USE_COMPUTED_SIZE) {
                    setSizeRatio(getSize());
                }
            }
        });

        if (svgPathContent != null && !svgPathContent.isEmpty()) {
            SVGPath shape = new SVGPath();
            shape.setContent(svgPathContent);
            setShape(shape);
            setFill(fill);
        }

        setPrefSize(DEFAULT_PREF_SIZE, DEFAULT_PREF_SIZE);
    }

    /**
     * @return current svg id
     */
    public int getGlyphId() {
        return glyphId;
    }

    /**
     * @return current svg name
     */
    public String getName() {
        return name;
    }

    /**
     * svg color property
     */
    public void setFill(Paint fill) {
        this.fill.setValue(fill);
    }

    public ObjectProperty<Paint> fillProperty() {
        return fill;
    }

    public Paint getFill() {
        return fill.getValue();
    }

    /**
     * resize the svg to a certain width and height
     */
    public void setSize(double width, double height) {
        this.setMinSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
        this.setPrefSize(width, height);
        this.setMaxSize(StackPane.USE_PREF_SIZE, StackPane.USE_PREF_SIZE);
    }

    /**
     * 将 svg 调整到此大小，同时保持宽高比
     *
     * @param size 以像素为单位
     */
    private void setSizeRatio(double size) {
        double width = widthHeightRatio * size;
        double height = size / widthHeightRatio;
        if (width <= size) {
            setSize(width, size);
        } else {
            setSize(size, Math.min(height, size));
        }
    }

    /**
     * resize the svg to certain width while keeping the width/height ratio
     *
     * @param width in pixel
     */
    public void setSizeForWidth(double width) {
        double height = width / widthHeightRatio;
        setSize(width, height);
    }

    /**
     * resize the svg to certain width while keeping the width/height ratio
     *
     * @param height in pixel
     */
    public void setSizeForHeight(double height) {
        double width = height * widthHeightRatio;
        setSize(width, height);
    }

    /**
     * specifies the radius of the spinner node, by default it's set to -1 (USE_COMPUTED_SIZE)
     */
    private final StyleableDoubleProperty size = new SimpleStyleableDoubleProperty(StyleableProperties.SIZE,
            SVGImage.this,
            "size",
            Region.USE_COMPUTED_SIZE) {
        @Override
        public void invalidated() {
            setSizeRatio(getSize());
        }
    };

    public double getSize() {
        return size.get();
    }

    public DoubleProperty sizeProperty() {
        return size;
    }

    public void setSize(double size) {
        this.size.set(size);
    }

    private static class StyleableProperties {
        private static final CssMetaData<SVGImage, Number> SIZE =
                new CssMetaData<>("-jfx-size",
                        SizeConverter.getInstance(), Region.USE_COMPUTED_SIZE) {
                    @Override
                    public boolean isSettable(SVGImage control) {
                        return control.size == null || !control.size.isBound();
                    }

                    @Override
                    public StyleableDoubleProperty getStyleableProperty(SVGImage control) {
                        return control.size;
                    }
                };

        private static final List<CssMetaData<? extends Styleable, ?>> CHILD_STYLEABLES;

        static {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(Parent.getClassCssMetaData());
            Collections.addAll(styleables,
                    SIZE
            );
            CHILD_STYLEABLES = Collections.unmodifiableList(styleables);
        }
    }

    // inherit the styleable properties from parent
    private List<CssMetaData<? extends Styleable, ?>> STYLEABLES;

    @Override
    public List<CssMetaData<? extends Styleable, ?>> getCssMetaData() {
        if (STYLEABLES == null) {
            final List<CssMetaData<? extends Styleable, ?>> styleables =
                    new ArrayList<>(Pane.getClassCssMetaData());
            styleables.addAll(getClassCssMetaData());
            STYLEABLES = Collections.unmodifiableList(styleables);
        }
        return STYLEABLES;
    }

    public static List<CssMetaData<? extends Styleable, ?>> getClassCssMetaData() {
        return StyleableProperties.CHILD_STYLEABLES;
    }
}
