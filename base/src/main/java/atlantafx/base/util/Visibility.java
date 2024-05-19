package atlantafx.base.util;

/**
 * UI组件的可见常量
 *
 * @author Nonoas
 * @datetime 2021/12/5 11:26
 */
public enum Visibility {

    /**
     * 设置组件可见
     */
    VISIBLE(true, true),

    /**
     * 设置组件不可见，但是会占用UI空间
     */
    INVISIBLE(false, true),

    /**
     * 设置组件不可见，且不占用UI空间
     */
    GONE(false, false);

    private final boolean visible;
    private final boolean managed;

    /**
     * @param visible 是否可见
     * @param managed 是否占用空间
     */
    Visibility(boolean visible, boolean managed) {
        this.visible = visible;
        this.managed = managed;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isManaged() {
        return managed;
    }
}
