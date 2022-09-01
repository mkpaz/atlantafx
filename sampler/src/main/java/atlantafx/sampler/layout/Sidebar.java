/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.layout;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.components.*;
import atlantafx.sampler.page.general.ThemePage;
import atlantafx.sampler.page.general.TypographyPage;
import atlantafx.sampler.page.showcase.filemanager.FileManagerPage;
import atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage;
import atlantafx.sampler.util.Containers;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import static javafx.scene.layout.Priority.ALWAYS;

class Sidebar extends VBox {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");

    private final FilteredList<Region> navigationMenu = navigationMenu();
    private final ReadOnlyObjectWrapper<NavLink> selectedLink = new ReadOnlyObjectWrapper<>();
    private Consumer<Class<? extends Page>> navigationHandler;

    public Sidebar() {
        super();
        setId("sidebar");

        createView();

        selectedLink.addListener((obs, old, val) -> {
            if (navigationHandler != null) {
                navigationHandler.accept(val != null ? val.getPageClass() : null);
            }
        });
    }

    public void select(Class<? extends Page> pageClass) {
        navigationMenu.stream()
                .filter(region -> region instanceof NavLink link && pageClass.equals(link.getPageClass()))
                .findFirst()
                .ifPresent(link -> navigate((NavLink) link));
    }

    public void setOnSelect(Consumer<Class<? extends Page>> c) {
        navigationHandler = c;
    }

    private void createView() {
        var navContainer = new VBox();
        navContainer.getStyleClass().add("nav-menu");
        Bindings.bindContent(navContainer.getChildren(), navigationMenu);

        var navScroll = new ScrollPane(navContainer);
        Containers.setScrollConstraints(navScroll,
                ScrollPane.ScrollBarPolicy.AS_NEEDED, true,
                ScrollPane.ScrollBarPolicy.AS_NEEDED, true
        );
        VBox.setVgrow(navScroll, ALWAYS);

        // == SEARCH FORM ==

        var searchField = new TextField();
        searchField.setPromptText("Search");
        HBox.setHgrow(searchField, ALWAYS);
        searchField.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.isBlank()) {
                navigationMenu.setPredicate(c -> true);
                return;
            }
            navigationMenu.setPredicate(c -> c instanceof NavLink link && link.matches(val));
        });

        var searchForm = new HBox(searchField);
        searchForm.setId("search-form");
        searchForm.setAlignment(Pos.CENTER_LEFT);

        // ~

        getChildren().addAll(searchForm, navScroll);
    }

    private Label caption(String text) {
        var label = new Label(text);
        label.getStyleClass().add("caption");
        label.setMaxWidth(Double.MAX_VALUE);
        return label;
    }

    private FilteredList<Region> navigationMenu() {
        return new FilteredList<>(FXCollections.observableArrayList(
                caption("GENERAL"),
                navLink(ThemePage.NAME, ThemePage.class),
                navLink(TypographyPage.NAME, TypographyPage.class),
                new Separator(),
                caption("COMPONENTS"),
                navLink(OverviewPage.NAME, OverviewPage.class),
                navLink(InputGroupPage.NAME, InputGroupPage.class),
                new Spacer(10, Orientation.VERTICAL),
                navLink(AccordionPage.NAME, AccordionPage.class),
                navLink(BreadcrumbsPage.NAME, BreadcrumbsPage.class),
                navLink(ButtonPage.NAME, ButtonPage.class),
                navLink(ChartPage.NAME, ChartPage.class),
                navLink(CheckBoxPage.NAME, CheckBoxPage.class),
                navLink(ColorPickerPage.NAME, ColorPickerPage.class),
                navLink(ComboBoxPage.NAME, ComboBoxPage.class, "ChoiceBox"),
                navLink(CustomTextFieldPage.NAME, CustomTextFieldPage.class),
                navLink(DatePickerPage.NAME, DatePickerPage.class),
                navLink(DialogPage.NAME, DialogPage.class),
                navLink(HTMLEditorPage.NAME, HTMLEditorPage.class),
                navLink(ListPage.NAME, ListPage.class),
                navLink(MenuPage.NAME, MenuPage.class),
                navLink(MenuButtonPage.NAME, MenuButtonPage.class, "SplitMenuButton"),
                navLink(PaginationPage.NAME, PaginationPage.class),
                navLink(PopoverPage.NAME, PopoverPage.class),
                navLink(ProgressPage.NAME, ProgressPage.class),
                navLink(RadioButtonPage.NAME, RadioButtonPage.class),
                navLink(ScrollPanePage.NAME, ScrollPanePage.class),
                navLink(SeparatorPage.NAME, SeparatorPage.class),
                navLink(SliderPage.NAME, SliderPage.class),
                navLink(SpinnerPage.NAME, SpinnerPage.class),
                navLink(SplitPanePage.NAME, SplitPanePage.class),
                navLink(TablePage.NAME, TablePage.class),
                navLink(TabPanePage.NAME, TabPanePage.class),
                navLink(TextAreaPage.NAME, TextAreaPage.class),
                navLink(TextFieldPage.NAME, TextFieldPage.class, "PasswordField"),
                navLink(TitledPanePage.NAME, TitledPanePage.class),
                navLink(ToggleButtonPage.NAME, ToggleButtonPage.class),
                navLink(ToggleSwitchPage.NAME, ToggleSwitchPage.class),
                navLink(ToolBarPage.NAME, ToolBarPage.class),
                navLink(TooltipPage.NAME, TooltipPage.class),
                navLink(TreePage.NAME, TreePage.class),
                navLink(TreeTablePage.NAME, TreeTablePage.class),
                caption("SHOWCASE"),
                navLink(FileManagerPage.NAME, FileManagerPage.class),
                navLink(MusicPlayerPage.NAME, MusicPlayerPage.class)
        ));
    }

    private NavLink navLink(String text, Class<? extends Page> pageClass, String... keywords) {
        return navLink(text, pageClass, false, keywords);
    }

    @SuppressWarnings("SameParameterValue")
    private NavLink navLink(String text, Class<? extends Page> pageClass, boolean isNew, String... keywords) {
        var link = new NavLink(text, pageClass, isNew);

        if (keywords != null && keywords.length > 0) {
            link.getSearchKeywords().addAll(Arrays.asList(keywords));
        }

        link.setOnMouseClicked(e -> {
            if (e.getSource() instanceof NavLink dest) { navigate(dest); }
        });

        return link;
    }

    private void navigate(NavLink link) {
        if (selectedLink.get() != null) { selectedLink.get().pseudoClassStateChanged(SELECTED, false); }
        link.pseudoClassStateChanged(SELECTED, true);
        selectedLink.set(link);
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class NavLink extends Label {

        private final Class<? extends Page> pageClass;
        private final List<String> searchKeywords = new ArrayList<>();

        public NavLink(String text, Class<? extends Page> pageClass, boolean isNew) {
            super(Objects.requireNonNull(text));
            this.pageClass = Objects.requireNonNull(pageClass);

            getStyleClass().add("nav-link");
            setMaxWidth(Double.MAX_VALUE);
            setContentDisplay(ContentDisplay.RIGHT);

            if (isNew) {
                var tag = new Label("new");
                tag.getStyleClass().addAll("tag", Styles.TEXT_SMALL);
                setGraphic(tag);
            }
        }

        public Class<? extends Page> getPageClass() {
            return pageClass;
        }

        public List<String> getSearchKeywords() {
            return searchKeywords;
        }

        public boolean matches(String filter) {
            Objects.requireNonNull(filter);
            return contains(getText(), filter) || searchKeywords.stream().anyMatch(keyword -> contains(keyword, filter));
        }

        private boolean contains(String text, String filter) {
            return text.toLowerCase().contains(filter.toLowerCase());
        }
    }
}
