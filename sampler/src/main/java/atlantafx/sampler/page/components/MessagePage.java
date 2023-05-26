package atlantafx.sampler.page.components;

import atlantafx.base.controls.Message;
import atlantafx.base.theme.Styles;
import atlantafx.base.theme.Tweaks;
import atlantafx.base.util.Animations;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;
import org.kordamp.ikonli.material2.Material2OutlinedMZ;

public class MessagePage extends OutlinePage {

    public static final String NAME = "Message";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public MessagePage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Message[/i] is a component for displaying notifications or alerts \
            and is specifically designed to grab the user’s attention. It is \
            based on the [i]Tile[/i] layout and shares its structure."""
        );
        addSection("Usage", usageExample());
        addSection("No Title", noTitleExample());
        addSection("Interactive", interactiveExample());
        addSection("Banner", bannerExample());
    }

    private Node usageExample() {
        //snippet_1:start
        var info = new Message(
            "Info",
            FAKER.lorem().sentence(5),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );

        var success = new Message(
            "Success",
            FAKER.lorem().sentence(10),
            new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE)
        );
        success.getStyleClass().add(Styles.SUCCESS);

        var warning = new Message(
            "Warning",
            FAKER.lorem().sentence(20),
            new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG)
        );
        warning.getStyleClass().add(Styles.WARNING);

        var danger = new Message(
            "Danger",
            FAKER.lorem().sentence(25),
            new FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        );
        danger.getStyleClass().add(Styles.DANGER);
        //snippet_1:end

        var box = new VBox(VGAP_10, info, success, warning, danger);
        var description = BBCodeParser.createFormattedText("""
            The default [i]Message[/i] type is "info", which corresponds to the \
            accent color. Success, warning, and danger colors are also supported."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node noTitleExample() {
        //snippet_2:start
        var info1 = new Message(
            null,
            FAKER.lorem().sentence(5),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );

        var info2 = new Message(
            null,
            FAKER.lorem().sentence(15)
        );

        var info3 = new Message(
            null,
            FAKER.lorem().sentence(50)
        );
        var btn = new Button("Done");
        btn.getStyleClass().add(Styles.ACCENT);
        info3.setAction(btn);
        //snippet_2:end

        var box = new VBox(VGAP_10, info1, info2, info3);
        var description = BBCodeParser.createFormattedText("""
            Unlike the [i]Tile[/i], the message title is optional. This example \
            demonstrates various messages without a title."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node interactiveExample() {
        //snippet_3:start
        var msg = new Message(
            "Success",
            FAKER.lorem().sentence(25)
        );
        msg.getStyleClass().add(Styles.SUCCESS);

        var btn = new Button("Undo");
        btn.getStyleClass().addAll(Styles.SUCCESS);

        msg.setAction(btn);
        msg.setActionHandler(() -> Animations.flash(msg).playFromStart());
        //snippet_3:end

        var box = new VBox(msg);
        box.setPadding(new Insets(0, 0, 5, 0));
        var description = BBCodeParser.createFormattedText("""
            A [i]Message[/i] can be made interactive by setting an action handler that may \
            or may not be related to the action slot."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private Node bannerExample() {
        //snippet_4:start
        final var msg = new Message(
            null,
            FAKER.lorem().sentence(10)
        );
        msg.getStyleClass().addAll(
            Styles.DANGER, Tweaks.EDGE_TO_EDGE
        );

        var closeBtn = new Button("Close");
        closeBtn.getStyleClass().addAll(Styles.DANGER);
        msg.setAction(closeBtn);

        var showBannerBtn = new Button("Show banner");
        showBannerBtn.setOnAction(e1 -> {
            var parent = (BorderPane) getScene().lookup("#main");

            parent.setTop(new VBox(msg));
            closeBtn.setOnAction(e2 -> parent.setTop(null));

            msg.setOpacity(0);
            Animations.fadeInDown(msg, Duration.millis(350))
                .playFromStart();
        });
        //snippet_4:end

        var box = new VBox(showBannerBtn);
        var description = BBCodeParser.createFormattedText("""
            The [i]Message[/i] supports the [code]Tweaks.EDGE_TO_EDGE[/code]  style class modifier, \
            which can be used to create a fancy banner, for example."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 4), description);
        example.setAllowDisable(false);

        return example;
    }
}
