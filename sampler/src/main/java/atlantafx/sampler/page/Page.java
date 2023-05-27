/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static javafx.scene.input.KeyCombination.ALT_DOWN;

import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.event.BrowseEvent;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.event.NavEvent;
import atlantafx.sampler.event.PageEvent;
import atlantafx.sampler.layout.ApplicationWindow;
import java.net.URI;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.layout.HBox;
import net.datafaker.Faker;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public interface Page {

    int MAX_WIDTH = ApplicationWindow.MIN_WIDTH - ApplicationWindow.SIDEBAR_WIDTH;
    int HGAP_20 = 20;
    int HGAP_30 = 30;
    int VGAP_10 = 10;
    int VGAP_20 = 20;

    Faker FAKER = new Faker();
    Random RANDOM = new Random();
    String JFX_JAVADOC_URI_TEMPLATE =
        "https://openjfx.io/javadoc/20/javafx.controls/javafx/scene/%s.html";
    String AFX_JAVADOC_URI_TEMPLATE =
        "https://mkpaz.github.io/atlantafx/apidocs/atlantafx.base/atlantafx/base/%s.html";

    String getName();

    Parent getView();

    boolean canDisplaySourceCode();

    @Nullable URI getJavadocUri();

    @Nullable Node getSnapshotTarget();

    void reset();

    default <T> List<T> generate(Supplier<T> supplier, int count) {
        return Stream.generate(supplier).limit(count).toList();
    }

    default Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }

    @SuppressWarnings("unchecked")
    default Node createFormattedText(String text, boolean handleUrl) {
        var node = BBCodeParser.createFormattedText(text);

        if (handleUrl) {
            node.addEventFilter(ActionEvent.ACTION, e -> {
                if (e.getTarget() instanceof Hyperlink link && link.getUserData() instanceof String url) {
                    if (url.startsWith("https://") || url.startsWith("http://")) {
                        DefaultEventBus.getInstance().publish(new BrowseEvent(URI.create(url)));
                    }

                    if (url.startsWith("local://")) {
                        try {
                            var rootPackage = "atlantafx.sampler.page.";
                            var c = Class.forName(rootPackage + url.substring(8));
                            if (Page.class.isAssignableFrom(c)) {
                                DefaultEventBus.getInstance().publish(new NavEvent((Class<? extends Page>) c));
                            } else {
                                throw new IllegalArgumentException();
                            }
                        } catch (Exception ignored) {
                            System.err.println("Invalid local URL: \"" + url + "\"");
                        }
                    }
                }
                e.consume();
            });
        }

        return node;
    }

    default Label captionLabel(String text) {
        var label = new Label(text);
        label.setStyle("-fx-font-family:monospace");
        return label;
    }

    ///////////////////////////////////////////////////////////////////////////

    class PageHeader extends HBox {

        public PageHeader(Page page) {
            super();

            Objects.requireNonNull(page, "page");

            var titleLbl = new Label(page.getName());
            titleLbl.getStyleClass().add(Styles.TITLE_2);

            var sourceCodeItem = new MenuItem("Source Code", new FontIcon(Feather.CODE));
            sourceCodeItem.setDisable(!page.canDisplaySourceCode());
            sourceCodeItem.setAccelerator(new KeyCodeCombination(KeyCode.C, ALT_DOWN));
            sourceCodeItem.setOnAction(e ->
                DefaultEventBus.getInstance().publish(new PageEvent(PageEvent.Action.SOURCE_CODE_ON))
            );

            final var uri = page.getJavadocUri();
            var javadocItem = new MenuItem("Javadoc", new FontIcon(Feather.COFFEE));
            javadocItem.setAccelerator(new KeyCodeCombination(KeyCode.J, ALT_DOWN));
            javadocItem.setDisable(uri == null);
            javadocItem.setOnAction(e -> {
                if (uri != null) {
                    DefaultEventBus.getInstance().publish(new BrowseEvent(uri));
                }
            });

            var menuBtn = new MenuButton(null, new FontIcon(Material2AL.EXPAND_MORE));
            menuBtn.getStyleClass().addAll(Styles.FLAT, Styles.BUTTON_ICON, Tweaks.NO_ARROW);
            menuBtn.getItems().setAll(sourceCodeItem, javadocItem);

            getStyleClass().add("header");
            setSpacing(20);
            getChildren().setAll(titleLbl, menuBtn);
        }
    }
}
