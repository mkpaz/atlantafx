/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.components.*;
import atlantafx.sampler.page.general.BBCodePage;
import atlantafx.sampler.page.general.DecorationsPage;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import atlantafx.sampler.page.general.SelectableTextFlowPage;
import javafx.scene.Node;
import org.jspecify.annotations.Nullable;

record Nav(String title,
           @Nullable Node graphic,
           @Nullable Class<? extends Page> pageClass,
           @Nullable List<String> searchKeywords) {

    public static final Nav ROOT = new Nav("ROOT", null, null, null);

    private static final Set<Class<? extends Page>> TAGGED_PAGES = Set.of(
        BBCodePage.class,
        BreadcrumbsPage.class,
        CalendarPage.class,
        CardPage.class,
        CustomTextFieldPage.class,
        DeckPanePage.class,
        DecorationsPage.class,
        InputGroupPage.class,
        MessagePage.class,
        ModalPanePage.class,
        NotificationPage.class,
        PopoverPage.class,
        SegmentedControlPage.class,
        SelectableTextFlowPage.class,
        TilePage.class,
        TabLinePage.class,
        ToggleSwitchPage.class
    );

    public Nav {
        Objects.requireNonNull(title, "title");
        searchKeywords = Objects.requireNonNullElse(searchKeywords, Collections.emptyList());
    }

    public boolean isGroup() {
        return pageClass == null;
    }

    public boolean matches(String filter) {
        Objects.requireNonNull(filter);
        return contains(title, filter)
            || (searchKeywords != null && searchKeywords.stream().anyMatch(keyword -> contains(keyword, filter)));
    }

    public boolean isTagged() {
        return pageClass != null && TAGGED_PAGES.contains(pageClass);
    }

    private boolean contains(String text, String filter) {
        return text.toLowerCase().contains(filter.toLowerCase());
    }
}
