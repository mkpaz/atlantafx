/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import org.jspecify.annotations.Nullable;

import java.net.URI;
import java.util.List;

public final class UtilsPage extends OutlinePage {

    public static final String NAME = "Utils";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    public UtilsPage() {
        super();

        addPageHeader();
        addFormattedText(" Utility classes provide ready-to-use CSS modifiers for quick styling.", true);
        addSection("Rounded Borders", roundedBordersExample());
    }

    private ExampleBox roundedBordersExample() {
        //snippet_1:start
        var classes = List.of(
            Styles.BORDER_RADIUS,
            Styles.BORDER_RADIUS_TOP,
            Styles.BORDER_RADIUS_RIGHT,
            Styles.BORDER_RADIUS_BOTTOM,
            Styles.BORDER_RADIUS_LEFT,
            Styles.BORDER_RADIUS_TOP_LEFT,
            Styles.BORDER_RADIUS_TOP_RIGHT,
            Styles.BORDER_RADIUS_BOTTOM_RIGHT,
            Styles.BORDER_RADIUS_BOTTOM_LEFT
        );

        var container = new FlowPane(10, 10);

        for (var styleClass : classes) {
            var tile = new StackPane(new Label(styleClass));
            tile.setPrefSize(200, 60);

            tile.getStyleClass().addAll(
                Styles.BORDER_DEFAULT,
                Styles.BG_ACCENT_SUBTLE,
                styleClass
            );

            container.getChildren().add(tile);
        }
        //snippet_1:end

        var description = BBCodeParser.createFormattedText("""
            Use custom border-radius utility classes to apply rounded corners to specific sides \
            or individual corners of a node, depending on the current theme settings."""
        );

        var example = new ExampleBox(container, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }
}
