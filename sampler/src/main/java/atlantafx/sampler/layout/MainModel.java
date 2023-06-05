/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import static atlantafx.sampler.layout.MainModel.SubLayer.PAGE;
import static atlantafx.sampler.layout.MainModel.SubLayer.SOURCE_CODE;

import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.NavEvent;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.components.AccordionPage;
import atlantafx.sampler.page.components.BreadcrumbsPage;
import atlantafx.sampler.page.components.ButtonPage;
import atlantafx.sampler.page.components.CalendarPage;
import atlantafx.sampler.page.components.CardPage;
import atlantafx.sampler.page.components.ChartPage;
import atlantafx.sampler.page.components.CheckBoxPage;
import atlantafx.sampler.page.components.ChoiceBoxPage;
import atlantafx.sampler.page.components.ColorPickerPage;
import atlantafx.sampler.page.components.ComboBoxPage;
import atlantafx.sampler.page.components.ContextMenuPage;
import atlantafx.sampler.page.components.CustomTextFieldPage;
import atlantafx.sampler.page.components.DatePickerPage;
import atlantafx.sampler.page.components.DeckPanePage;
import atlantafx.sampler.page.components.DialogPage;
import atlantafx.sampler.page.components.HtmlEditorPage;
import atlantafx.sampler.page.components.InputGroupPage;
import atlantafx.sampler.page.components.ListViewPage;
import atlantafx.sampler.page.components.MenuBarPage;
import atlantafx.sampler.page.components.MenuButtonPage;
import atlantafx.sampler.page.components.MessagePage;
import atlantafx.sampler.page.components.ModalPanePage;
import atlantafx.sampler.page.components.NotificationPage;
import atlantafx.sampler.page.components.PaginationPage;
import atlantafx.sampler.page.components.PopoverPage;
import atlantafx.sampler.page.components.ProgressIndicatorPage;
import atlantafx.sampler.page.components.RadioButtonPage;
import atlantafx.sampler.page.components.ScrollPanePage;
import atlantafx.sampler.page.components.SeparatorPage;
import atlantafx.sampler.page.components.SliderPage;
import atlantafx.sampler.page.components.SpinnerPage;
import atlantafx.sampler.page.components.SplitPanePage;
import atlantafx.sampler.page.components.TabPanePage;
import atlantafx.sampler.page.components.TableViewPage;
import atlantafx.sampler.page.components.TextAreaPage;
import atlantafx.sampler.page.components.TextFieldPage;
import atlantafx.sampler.page.components.TilePage;
import atlantafx.sampler.page.components.TitledPanePage;
import atlantafx.sampler.page.components.ToggleButtonPage;
import atlantafx.sampler.page.components.ToggleSwitchPage;
import atlantafx.sampler.page.components.ToolBarPage;
import atlantafx.sampler.page.components.TooltipPage;
import atlantafx.sampler.page.components.TreeTableViewPage;
import atlantafx.sampler.page.components.TreeViewPage;
import atlantafx.sampler.page.general.AnimationsPage;
import atlantafx.sampler.page.general.BBCodePage;
import atlantafx.sampler.page.general.IconsPage;
import atlantafx.sampler.page.general.ThemePage;
import atlantafx.sampler.page.general.TypographyPage;
import atlantafx.sampler.page.showcase.BlueprintsPage;
import atlantafx.sampler.page.showcase.OverviewPage;
import atlantafx.sampler.page.showcase.filemanager.FileManagerPage;
import atlantafx.sampler.page.showcase.musicplayer.MusicPlayerPage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public class MainModel {

    public static final Class<? extends Page> DEFAULT_PAGE = ThemePage.class;

    private static final Map<Class<? extends Page>, NavTree.Item> NAV_TREE = createNavItems();

    public enum SubLayer {
        PAGE,
        SOURCE_CODE
    }

    NavTree.Item getTreeItemForPage(Class<? extends Page> pageClass) {
        return NAV_TREE.getOrDefault(pageClass, NAV_TREE.get(DEFAULT_PAGE));
    }

    List<NavTree.Item> findPages(String filter) {
        return NAV_TREE.values().stream()
            .filter(item -> item.getValue() != null && item.getValue().matches(filter))
            .toList();
    }

    public MainModel() {
        DefaultEventBus.getInstance().subscribe(NavEvent.class, e -> navigate(e.getPage()));
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    // ~
    private final ReadOnlyObjectWrapper<Class<? extends Page>> selectedPage = new ReadOnlyObjectWrapper<>();

    public ReadOnlyObjectProperty<Class<? extends Page>> selectedPageProperty() {
        return selectedPage.getReadOnlyProperty();
    }

    // ~
    private final ReadOnlyObjectWrapper<SubLayer> currentSubLayer = new ReadOnlyObjectWrapper<>(PAGE);

    public ReadOnlyObjectProperty<SubLayer> currentSubLayerProperty() {
        return currentSubLayer.getReadOnlyProperty();
    }

    // ~
    private final ReadOnlyObjectWrapper<NavTree.Item> navTree = new ReadOnlyObjectWrapper<>(createTree());

    public ReadOnlyObjectProperty<NavTree.Item> navTreeProperty() {
        return navTree.getReadOnlyProperty();
    }

    private NavTree.Item createTree() {
        var general = NavTree.Item.group("General", new FontIcon(Material2OutlinedMZ.SPEED));
        general.getChildren().setAll(
            NAV_TREE.get(ThemePage.class),
            NAV_TREE.get(TypographyPage.class),
            NAV_TREE.get(BBCodePage.class),
            NAV_TREE.get(AnimationsPage.class),
            NAV_TREE.get(IconsPage.class)
        );
        general.setExpanded(true);

        var containers = NavTree.Item.group("Containers", new FontIcon(Material2OutlinedMZ.TABLE_CHART));
        containers.getChildren().setAll(
            NAV_TREE.get(AccordionPage.class),
            NAV_TREE.get(CardPage.class),
            NAV_TREE.get(ContextMenuPage.class),
            NAV_TREE.get(DeckPanePage.class),
            NAV_TREE.get(InputGroupPage.class),
            NAV_TREE.get(ModalPanePage.class),
            NAV_TREE.get(ScrollPanePage.class),
            NAV_TREE.get(SeparatorPage.class),
            NAV_TREE.get(SplitPanePage.class),
            NAV_TREE.get(PopoverPage.class),
            NAV_TREE.get(TilePage.class),
            NAV_TREE.get(TitledPanePage.class),
            NAV_TREE.get(ToolBarPage.class)
        );

        var dataDisplay = NavTree.Item.group("Data Display", new FontIcon(Material2OutlinedAL.LIST_ALT));
        dataDisplay.getChildren().setAll(
            NAV_TREE.get(ChartPage.class),
            NAV_TREE.get(ListViewPage.class),
            NAV_TREE.get(TableViewPage.class),
            NAV_TREE.get(TreeTableViewPage.class),
            NAV_TREE.get(TreeViewPage.class)
        );

        var feedback = NavTree.Item.group("Feedback", new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE));
        feedback.getChildren().setAll(
            NAV_TREE.get(DialogPage.class),
            NAV_TREE.get(MessagePage.class),
            NAV_TREE.get(NotificationPage.class),
            NAV_TREE.get(ProgressIndicatorPage.class),
            NAV_TREE.get(TooltipPage.class)
        );

        var inputs = NavTree.Item.group("Inputs & Controls", new FontIcon(Material2OutlinedAL.EDIT));
        inputs.getChildren().setAll(
            NAV_TREE.get(ButtonPage.class),
            NAV_TREE.get(CalendarPage.class),
            NAV_TREE.get(CheckBoxPage.class),
            NAV_TREE.get(ChoiceBoxPage.class),
            NAV_TREE.get(ColorPickerPage.class),
            NAV_TREE.get(ComboBoxPage.class),
            NAV_TREE.get(CustomTextFieldPage.class),
            NAV_TREE.get(DatePickerPage.class),
            NAV_TREE.get(HtmlEditorPage.class),
            NAV_TREE.get(MenuButtonPage.class),
            NAV_TREE.get(RadioButtonPage.class),
            NAV_TREE.get(SliderPage.class),
            NAV_TREE.get(SpinnerPage.class),
            NAV_TREE.get(TextAreaPage.class),
            NAV_TREE.get(TextFieldPage.class),
            NAV_TREE.get(ToggleButtonPage.class),
            NAV_TREE.get(ToggleSwitchPage.class)
        );

        var navigation = NavTree.Item.group("Navigation", new FontIcon(Material2OutlinedMZ.MENU_OPEN));
        navigation.getChildren().setAll(
            NAV_TREE.get(BreadcrumbsPage.class),
            NAV_TREE.get(MenuBarPage.class),
            NAV_TREE.get(PaginationPage.class),
            NAV_TREE.get(TabPanePage.class)
        );

        var showcases = NavTree.Item.group("Showcase", new FontIcon(Material2OutlinedMZ.VISIBILITY));
        showcases.getChildren().setAll(
            NAV_TREE.get(BlueprintsPage.class),
            NAV_TREE.get(FileManagerPage.class),
            NAV_TREE.get(MusicPlayerPage.class),
            NAV_TREE.get(OverviewPage.class)
        );

        var root = NavTree.Item.root();
        root.getChildren().setAll(
            general,
            containers,
            dataDisplay,
            feedback,
            inputs,
            navigation,
            showcases
        );

        return root;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Nav Tree                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public static Map<Class<? extends Page>, NavTree.Item> createNavItems() {
        var map = new HashMap<Class<? extends Page>, NavTree.Item>();

        // general
        map.put(ThemePage.class, NavTree.Item.page(ThemePage.NAME, ThemePage.class));
        map.put(AnimationsPage.class, NavTree.Item.page(AnimationsPage.NAME, AnimationsPage.class));
        map.put(BBCodePage.class, NavTree.Item.page(BBCodePage.NAME, BBCodePage.class));
        map.put(IconsPage.class, NavTree.Item.page(IconsPage.NAME, IconsPage.class));
        map.put(
            TypographyPage.class,
            NavTree.Item.page(TypographyPage.NAME, TypographyPage.class, "Label", "Hyperlink")
        );

        // components
        map.put(AccordionPage.class, NavTree.Item.page(AccordionPage.NAME, AccordionPage.class));
        map.put(BreadcrumbsPage.class, NavTree.Item.page(BreadcrumbsPage.NAME, BreadcrumbsPage.class));
        map.put(ButtonPage.class, NavTree.Item.page(ButtonPage.NAME, ButtonPage.class));
        map.put(CalendarPage.class, NavTree.Item.page(CalendarPage.NAME, CalendarPage.class));
        map.put(CardPage.class, NavTree.Item.page(CardPage.NAME, CardPage.class));
        map.put(ChartPage.class, NavTree.Item.page(ChartPage.NAME, ChartPage.class));
        map.put(ChoiceBoxPage.class, NavTree.Item.page(ChoiceBoxPage.NAME, ChoiceBoxPage.class));
        map.put(CheckBoxPage.class, NavTree.Item.page(CheckBoxPage.NAME, CheckBoxPage.class));
        map.put(ColorPickerPage.class, NavTree.Item.page(ColorPickerPage.NAME, ColorPickerPage.class));
        map.put(
            ComboBoxPage.class,
            NavTree.Item.page(ComboBoxPage.NAME, ComboBoxPage.class, "ChoiceBox")
        );
        map.put(ContextMenuPage.class, NavTree.Item.page(ContextMenuPage.NAME, ContextMenuPage.class));
        map.put(
            CustomTextFieldPage.class,
            NavTree.Item.page(
                CustomTextFieldPage.NAME, CustomTextFieldPage.class,
                "MaskTextField", "PasswordTextField"
            )
        );
        map.put(DatePickerPage.class, NavTree.Item.page(DatePickerPage.NAME, DatePickerPage.class));
        map.put(DeckPanePage.class, NavTree.Item.page(DeckPanePage.NAME, DeckPanePage.class));
        map.put(DialogPage.class, NavTree.Item.page(DialogPage.NAME, DialogPage.class));
        map.put(HtmlEditorPage.class, NavTree.Item.page(HtmlEditorPage.NAME, HtmlEditorPage.class));
        map.put(InputGroupPage.class, NavTree.Item.page(InputGroupPage.NAME, InputGroupPage.class));
        map.put(ListViewPage.class, NavTree.Item.page(ListViewPage.NAME, ListViewPage.class));
        map.put(MenuBarPage.class, NavTree.Item.page(MenuBarPage.NAME, MenuBarPage.class));
        map.put(MenuButtonPage.class, NavTree.Item.page(
            MenuButtonPage.NAME,
            MenuButtonPage.class, "SplitMenuButton")
        );
        map.put(MessagePage.class, NavTree.Item.page(MessagePage.NAME, MessagePage.class));
        map.put(ModalPanePage.class, NavTree.Item.page(ModalPanePage.NAME, ModalPanePage.class));
        map.put(NotificationPage.class, NavTree.Item.page(NotificationPage.NAME, NotificationPage.class));
        map.put(PaginationPage.class, NavTree.Item.page(PaginationPage.NAME, PaginationPage.class));
        map.put(PopoverPage.class, NavTree.Item.page(PopoverPage.NAME, PopoverPage.class));
        map.put(ProgressIndicatorPage.class, NavTree.Item.page(
            ProgressIndicatorPage.NAME,
            ProgressIndicatorPage.class, "ProgressBar")
        );
        map.put(RadioButtonPage.class, NavTree.Item.page(RadioButtonPage.NAME, RadioButtonPage.class));
        map.put(ScrollPanePage.class, NavTree.Item.page(ScrollPanePage.NAME, ScrollPanePage.class));
        map.put(SeparatorPage.class, NavTree.Item.page(SeparatorPage.NAME, SeparatorPage.class));
        map.put(SliderPage.class, NavTree.Item.page(SliderPage.NAME, SliderPage.class));
        map.put(SpinnerPage.class, NavTree.Item.page(SpinnerPage.NAME, SpinnerPage.class));
        map.put(SplitPanePage.class, NavTree.Item.page(SplitPanePage.NAME, SplitPanePage.class));
        map.put(TilePage.class, NavTree.Item.page(TilePage.NAME, TilePage.class));
        map.put(TableViewPage.class, NavTree.Item.page(TableViewPage.NAME, TableViewPage.class));
        map.put(TabPanePage.class, NavTree.Item.page(TabPanePage.NAME, TabPanePage.class));
        map.put(TextAreaPage.class, NavTree.Item.page(TextAreaPage.NAME, TextAreaPage.class));
        map.put(TextFieldPage.class, NavTree.Item.page(
            TextFieldPage.NAME,
            TextFieldPage.class, "PasswordField")
        );
        map.put(TitledPanePage.class, NavTree.Item.page(TitledPanePage.NAME, TitledPanePage.class));
        map.put(ToggleButtonPage.class, NavTree.Item.page(ToggleButtonPage.NAME, ToggleButtonPage.class));
        map.put(ToggleSwitchPage.class, NavTree.Item.page(ToggleSwitchPage.NAME, ToggleSwitchPage.class));
        map.put(ToolBarPage.class, NavTree.Item.page(ToolBarPage.NAME, ToolBarPage.class));
        map.put(TooltipPage.class, NavTree.Item.page(TooltipPage.NAME, TooltipPage.class));
        map.put(TreeTableViewPage.class, NavTree.Item.page(TreeTableViewPage.NAME, TreeTableViewPage.class));
        map.put(TreeViewPage.class, NavTree.Item.page(TreeViewPage.NAME, TreeViewPage.class));

        // showcases
        map.put(BlueprintsPage.class, NavTree.Item.page(BlueprintsPage.NAME, BlueprintsPage.class));
        map.put(FileManagerPage.class, NavTree.Item.page(FileManagerPage.NAME, FileManagerPage.class));
        map.put(MusicPlayerPage.class, NavTree.Item.page(MusicPlayerPage.NAME, MusicPlayerPage.class));
        map.put(OverviewPage.class, NavTree.Item.page(OverviewPage.NAME, OverviewPage.class));

        return map;
    }

    ///////////////////////////////////////////////////////////////////////////
    // Commands                                                              //
    ///////////////////////////////////////////////////////////////////////////

    public void navigate(Class<? extends Page> page) {
        selectedPage.set(Objects.requireNonNull(page));
        currentSubLayer.set(PAGE);
    }

    public void showSourceCode() {
        currentSubLayer.set(SOURCE_CODE);
    }

    public void hideSourceCode() {
        currentSubLayer.set(PAGE);
    }
}
