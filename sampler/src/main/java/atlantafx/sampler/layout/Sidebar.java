/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.layout;

import atlantafx.base.controls.Spacer;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.components.*;
import atlantafx.sampler.page.general.IconsPage;
import atlantafx.sampler.page.general.ThemePage;
import atlantafx.sampler.page.general.TypographyPage;
import atlantafx.sampler.page.showcase.filemanager.FileManagerPage;
import atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage;
import atlantafx.sampler.page.showcase.widget.WidgetCollectionPage;
import atlantafx.sampler.util.Containers;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.css.PseudoClass;
import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.*;
import java.util.function.Predicate;

import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;
import static javafx.scene.layout.Priority.ALWAYS;

class Sidebar extends StackPane {

    private static final PseudoClass SELECTED = PseudoClass.getPseudoClass("selected");
    private static final PseudoClass FILTERED = PseudoClass.getPseudoClass("filtered");
    private static final Predicate<Region> PREDICATE_ANY = region -> true;

    private final MainModel model;
    private final NavMenu navMenu;
    private ScrollPane navScroll;

    public Sidebar(MainModel model) {
        super();

        this.model = model;
        this.navMenu = new NavMenu(model);

        createView();
    }

    private void createView() {
        var placeholder = new Label("No content");
        placeholder.getStyleClass().add(Styles.TITLE_4);

        var navContainer = new VBox();
        navContainer.getStyleClass().add("nav-menu");
        Bindings.bindContent(navContainer.getChildren(), navMenu.getContent());

        navScroll = new ScrollPane(navContainer);
        Containers.setScrollConstraints(navScroll, AS_NEEDED, true, AS_NEEDED, true);
        VBox.setVgrow(navScroll, ALWAYS);

        model.searchTextProperty().addListener((obs, old, val) -> {
            var empty = val == null || val.isBlank();
            pseudoClassStateChanged(FILTERED, !empty);
            navMenu.setPredicate(empty ? PREDICATE_ANY : region -> region instanceof NavLink link && link.matches(val));
        });

        model.selectedPageProperty().addListener((obs, old, val) -> {
            navMenu.findLink(old).ifPresent(link -> link.pseudoClassStateChanged(SELECTED, false));
            navMenu.findLink(val).ifPresent(link -> link.pseudoClassStateChanged(SELECTED, true));
        });

        navScroll.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            var offset = 1 / (navContainer.getHeight() - navScroll.getViewportBounds().getHeight());
            if (e.getCode() == KeyCode.UP) {
                navMenu.getPrevious().ifPresentOrElse(link -> {
                    navScroll.setVvalue(link.getLayoutY() * offset / 2);
                    model.navigate(link.getPageClass());
                }, () -> navScroll.setVvalue(0));
                e.consume();
            }
            if (e.getCode() == KeyCode.DOWN) {
                navMenu.getNext().ifPresentOrElse(link -> {
                    navScroll.setVvalue(link.getLayoutY() * offset / 2);
                    model.navigate(link.getPageClass());
                }, () -> navScroll.setVvalue(1.0));
                e.consume();
            }
        });

        navMenu.getContent().addListener((ListChangeListener<Region>) c -> {
            if (navMenu.getContent().isEmpty()) {
                placeholder.toFront();
            } else {
                placeholder.toBack();
            }
        });

        setId("sidebar");
        getChildren().addAll(placeholder, navScroll);
    }

    void begForFocus() {
        navScroll.requestFocus();
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class NavMenu {

        private final MainModel model;
        private final FilteredList<Region> content;
        private final Map<Class<? extends Page>, NavLink> registry = new HashMap<>();

        public NavMenu(MainModel model) {
            var links = create();

            this.model = model;
            this.content = new FilteredList<>(links);
            links.forEach(c -> {
                if (c instanceof NavLink link) {
                    registry.put(link.getPageClass(), link);
                }
            });
        }

        public FilteredList<Region> getContent() {
            return content;
        }

        public void setPredicate(Predicate<Region> predicate) {
            content.setPredicate(predicate);
        }

        public Optional<NavLink> findLink(Class<? extends Page> pageClass) {
            if (pageClass == null) { return Optional.empty(); }
            return Optional.ofNullable(registry.get(pageClass));
        }

        public Optional<NavLink> getPrevious() {
            var current = content.indexOf(registry.get(model.selectedPageProperty().get()));
            if (!(current > 0)) { return Optional.empty(); }

            for (int i = current - 1; i >= 0; i--) {
                var r = content.get(i);
                if (r instanceof NavLink link) { return Optional.of(link); }
            }

            return Optional.empty();
        }

        public Optional<NavLink> getNext() {
            var current = content.indexOf(registry.get(model.selectedPageProperty().get()));
            if (!(current >= 0 && current < content.size() - 1)) { return Optional.empty(); } // has next

            for (int i = current + 1; i < content.size(); i++) {
                var r = content.get(i);
                if (r instanceof NavLink link) { return Optional.of(link); }
            }

            return Optional.empty();
        }

        private ObservableList<Region> create() {
            return FXCollections.observableArrayList(
                    caption("GENERAL"),
                    navLink(ThemePage.NAME, ThemePage.class),
                    navLink(TypographyPage.NAME, TypographyPage.class),
                    navLink(IconsPage.NAME, IconsPage.class),
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
                    navLink(LabelPage.NAME, LabelPage.class),
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
                    navLink(MusicPlayerPage.NAME, MusicPlayerPage.class),
                    navLink(WidgetCollectionPage.NAME,
                            WidgetCollectionPage.class,
                            "Card", "Message", "Stepper", "Tag"
                    )
            );
        }

        private Label caption(String text) {
            var label = new Label(text);
            label.getStyleClass().add("caption");
            label.setMaxWidth(Double.MAX_VALUE);
            return label;
        }

        private NavLink navLink(String text, Class<? extends Page> pageClass, String... keywords) {
            var link = new NavLink(text, pageClass);

            if (keywords != null && keywords.length > 0) {
                link.getSearchKeywords().addAll(Arrays.asList(keywords));
            }

            link.setOnMouseClicked(e -> {
                if (e.getSource() instanceof NavLink target) {
                    model.navigate(target.getPageClass());
                }
            });

            return link;
        }
    }

    private static class NavLink extends Label {

        private final Class<? extends Page> pageClass;
        private final List<String> searchKeywords = new ArrayList<>();

        public NavLink(String text, Class<? extends Page> pageClass) {
            super(Objects.requireNonNull(text));
            this.pageClass = Objects.requireNonNull(pageClass);

            getStyleClass().add("nav-link");
            setMaxWidth(Double.MAX_VALUE);
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
