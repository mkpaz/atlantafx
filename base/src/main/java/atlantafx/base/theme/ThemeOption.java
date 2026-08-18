package atlantafx.base.theme;

import org.jspecify.annotations.Nullable;

import java.util.function.Consumer;

/**
 * Defines a dynamic theme option.
 *
 * @param <T> the value type of the option
 */
public interface ThemeOption<T> {

    /**
     * Returns the unique key of this option.
     */
    Key<T> key();

    /**
     * Returns the default value for this option.
     */
    @Nullable T defaultValue();

    /**
     * Applies this option change to a scene.
     */
    void apply(ThemeManager.Change<T> change);

    /**
     * Creates a theme option instance.
     *
     * @param key          the option key
     * @param defaultValue default value of the option
     * @param handler      action that applies the option
     * @param <T>          the value type
     */
    static <T> ThemeOption<T> of(Key<T> key, @Nullable T defaultValue, Consumer<ThemeManager.Change<T>> handler) {

        return new ThemeOption<>() {
            @Override
            public Key<T> key() {
                return key;
            }

            @Override
            public @Nullable T defaultValue() {
                return defaultValue;
            }

            @Override
            public void apply(ThemeManager.Change<T> change) {
                handler.accept(change);
            }
        };
    }

    /**
     * Represents a typed key for identifying a theme option.
     *
     * @param id   unique string identifier
     * @param type class type of the option value
     * @param <T>  the value type
     */
    record Key<T>(String id, Class<T> type) {

        @Override
        public String toString() {
            return "Key[" + id + ": " + type.getSimpleName() + "]";
        }
    }
}
