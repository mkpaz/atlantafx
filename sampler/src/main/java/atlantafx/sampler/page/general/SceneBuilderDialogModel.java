package atlantafx.sampler.page.general;

import atlantafx.base.theme.PrimerLight;
import atlantafx.sampler.theme.SamplerTheme;
import atlantafx.sampler.theme.SceneBuilderTheme;
import atlantafx.sampler.theme.ThemeManager;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;
import javafx.scene.control.ToggleGroup;
import org.jetbrains.annotations.Nullable;

class SceneBuilderDialogModel {

    public enum Screen {
        // order matters as it determines swipe direction when switching between screens
        START, ACTION, THEME, EXEC, REPORT
    }

    static final String ACTION_INSTALL = "INSTALL";
    static final String ACTION_ROLLBACK = "ROLLBACK";

    public SceneBuilderDialogModel() {
        // default constructor
    }

    private void changeScreen(@Nullable Transition transition) {
        // null value means that forward action was blocked on the previous screen, so no transition required
        if (transition == null) {
            return;
        }

        canGoBack.set(transition.canGoBack());
        canGoForward.set(transition.canGoForward());
        if (transition.action() != null) {
            transition.action().run();
        }
        activeScreen.set(transition.nextScreen());
    }

    private void updateThemeMapping() {
        themeMap.set(getThemeMapping().entrySet().stream().collect(
            Collectors.toMap(
                e -> e.getKey().name(),
                e -> e.getValue().getName(),
                (e1, e2) -> e1,
                LinkedHashMap::new
            )
        ));
    }

    private Map<SceneBuilderTheme, SamplerTheme> getThemeMapping() {
        var sbIdx = 0;
        var map = new LinkedHashMap<SceneBuilderTheme, SamplerTheme>();

        for (int idx = 0; idx < themes.size() && sbIdx < sceneBuilderThemes.size(); idx++) {
            var samplerTheme = themes.get(idx);
            if (samplerTheme.isSelected()) {
                var sbTheme = sceneBuilderThemes.get(sbIdx);
                map.put(sbTheme, samplerTheme.getTheme());
                sbIdx++;
            }
        }

        return map;
    }

    private void installSelectedThemes() {
        Objects.requireNonNull(installer, "SceneBuilder install directory must be selected first.");

        var task = new Task<Void>() {
            @Override
            protected Void call() {
                installer.install(getThemeMapping());
                return null;
            }
        };
        task.setOnSucceeded(x -> report.set(Report.info(
            "AtlantaFX themes successfully installed.\nRestart SceneBuilder to apply changes.",
            Screen.START
        )));
        task.setOnFailed(e -> report.set(Report.error(e.getSource().getException().getMessage(), Screen.EXEC)));

        new Thread(task).start();
    }

    private void uninstallAll() {
        Objects.requireNonNull(installer, "SceneBuilder install directory must be selected first.");

        var task = new Task<Void>() {
            @Override
            protected Void call() {
                installer.uninstall();
                return null;
            }
        };
        task.setOnSucceeded(x -> report.set(Report.info(
            "AtlantaFX themes successfully uninstalled.\nRestart SceneBuilder to apply changes.",
            Screen.START
        )));
        task.setOnFailed(e -> report.set(Report.error(e.getSource().getException().getMessage(), Screen.EXEC)));

        new Thread(task).start();
    }

    private void requireSupportedAction() {
        String action = (String) actionGroup.getSelectedToggle().getUserData();

        if (action == null) {
            throw new RuntimeException("Action must be selected.");
        }

        if (!ACTION_INSTALL.equals(action) && !ACTION_ROLLBACK.equals(action)) {
            throw new RuntimeException("Unknown action: \"" + action + "\".");
        }
    }

    private boolean isSelectedAction(String action) {
        return action.equals(actionGroup.getSelectedToggle().getUserData());
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                              //
    ///////////////////////////////////////////////////////////////////////////

    private final List<SceneBuilderTheme> sceneBuilderThemes = SceneBuilderTheme.CASPIAN_THEMES;

    private final List<ThemeToggle> themes = ThemeManager.getInstance().getRepository().getAll().stream()
        .map(t -> new ThemeToggle(t, t.unwrap() instanceof PrimerLight))
        .toList();

    private final ToggleGroup actionGroup = new ToggleGroup();
    private @Nullable SceneBuilderInstaller installer;

    private final ReadOnlyObjectWrapper<Map<String, String>> themeMap = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Path> installDir = new ReadOnlyObjectWrapper<>();
    private final ReadOnlyObjectWrapper<Screen> activeScreen = new ReadOnlyObjectWrapper<>(Screen.START);
    private final ReadOnlyObjectWrapper<Report> report = new ReadOnlyObjectWrapper<>(null);
    private final ReadOnlyBooleanWrapper canGoBack = new ReadOnlyBooleanWrapper(false);
    // block initially until user selects installation directory
    private final ReadOnlyBooleanWrapper canGoForward = new ReadOnlyBooleanWrapper(false);

    public List<SceneBuilderTheme> getSceneBuilderThemes() {
        return sceneBuilderThemes;
    }

    public List<ThemeToggle> getThemes() {
        return themes;
    }

    public ToggleGroup getActionGroup() {
        return actionGroup;
    }

    public ReadOnlyObjectProperty<Map<String, String>> themeMapProperty() {
        return themeMap.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Path> installDirProperty() {
        return installDir.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Screen> activeScreenProperty() {
        return activeScreen.getReadOnlyProperty();
    }

    public ReadOnlyObjectProperty<Report> reportProperty() {
        return report.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty canGoBackProperty() {
        return canGoBack.getReadOnlyProperty();
    }

    public ReadOnlyBooleanProperty canGoForwardProperty() {
        return canGoForward.getReadOnlyProperty();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void setInstallDir(Path path) {
        Objects.requireNonNull(path);
        installDir.set(path);
        installer = new SceneBuilderInstaller(path);
        canGoForward.set(true);
    }

    public void notifyThemeToggleStateChanged() {
        var selectedCount = themes.stream()
            .filter(ThemeToggle::isSelected)
            .count();
        canGoForward.set(selectedCount > 0 && selectedCount <= sceneBuilderThemes.size());
    }

    public void reset() {
        // go to the start screen, but keep install dir and form fill
        activeScreen.set(Screen.START);
        canGoBack.set(false);
        canGoForward.set(true);
    }

    public void back() {
        var transition = switch (activeScreen.get()) {
            case START -> null;
            case ACTION -> new Transition(Screen.START, false, true);
            case THEME -> {
                Objects.requireNonNull(installer, "SceneBuilder install directory must be selected first.");

                yield installer.isThemePackInstalled()
                    ? new Transition(Screen.ACTION, true, true)
                    : new Transition(Screen.START, false, true);
            }
            case EXEC -> isSelectedAction(ACTION_INSTALL)
                ? new Transition(Screen.THEME, true, true)
                : new Transition(Screen.ACTION, true, true);
            case REPORT -> {
                var returnScreen = report.get().returnScreen();
                yield new Transition(returnScreen, returnScreen != Screen.START, true, () -> report.set(null));
            }
        };

        changeScreen(transition);
    }

    public void forward() {
        var transition = switch (activeScreen.get()) {
            case START -> {
                Objects.requireNonNull(installer, "SceneBuilder install directory must be selected first.");

                if (!installer.isValidDir()) {
                    yield new Transition(Screen.REPORT, true, false, () -> report.set(
                        Report.error("Selected directory doesn't look like SceneBuilder installation directory.")
                    ));
                }

                if (!installer.hasUserWritePermission()) {
                    yield new Transition(Screen.REPORT, true, false, () -> report.set(
                        Report.error("You don't have permission to write into installation directory.")
                    ));
                }

                yield new Transition(
                    installer.isThemePackInstalled() ? Screen.ACTION : Screen.THEME, true, true,
                    () -> actionGroup.selectToggle(actionGroup.getToggles().get(0)) // reset action
                );
            }
            case ACTION -> {
                // action must be selected before leaving this screen (fail first)
                requireSupportedAction();

                if (isSelectedAction(ACTION_ROLLBACK)) {
                    yield new Transition(Screen.REPORT, true, false, this::uninstallAll);
                }

                yield new Transition(Screen.THEME, true, true);
            }
            case THEME -> new Transition(Screen.EXEC, true, true, this::updateThemeMapping);
            case EXEC -> {
                requireSupportedAction();

                if (isSelectedAction(ACTION_INSTALL)) {
                    yield new Transition(Screen.REPORT, true, false, this::installSelectedThemes);
                }

                yield null;
            }
            case REPORT -> null;
        };

        changeScreen(transition);
    }

    ///////////////////////////////////////////////////////////////////////////

    public record Report(String message, Screen returnScreen, boolean error) {

        public Report {
            Objects.requireNonNull(message);
            Objects.requireNonNull(returnScreen);
        }

        public static Report info(String message) {
            return info(message, Screen.EXEC);
        }

        public static Report info(String message, Screen returnScreen) {
            return new Report(message, returnScreen, false);
        }

        public static Report error(String message) {
            return error(message, Screen.START);
        }

        public static Report error(String message, Screen returnScreen) {
            return new Report(message, returnScreen, true);
        }
    }

    public static class ThemeToggle {

        private final SamplerTheme theme;
        private final BooleanProperty selected = new SimpleBooleanProperty();

        public ThemeToggle(SamplerTheme theme, boolean selected) {
            this.theme = Objects.requireNonNull(theme);
            this.selected.set(selected);
        }

        public SamplerTheme getTheme() {
            return theme;
        }

        public boolean isSelected() {
            return selected.get();
        }

        public BooleanProperty selectedProperty() {
            return selected;
        }
    }

    public record Transition(Screen nextScreen, boolean canGoBack, boolean canGoForward, @Nullable Runnable action) {

        public Transition(Screen nextScreen, boolean canGoBack, boolean canGoForward) {
            this(nextScreen, canGoBack, canGoForward, null);
        }
    }
}
