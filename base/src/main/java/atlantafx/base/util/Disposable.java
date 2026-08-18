package atlantafx.base.util;

/**
 * A component that holds resources which must be explicitly released.
 */
public interface Disposable {

    /**
     * Releases any resources held by this component.
     *
     * <p>After this method is called, the component should be considered
     * unusable; further interaction with it results in undefined behavior.
     */
    void dispose();
}
