/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Notification;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public class NotificationPage extends OutlinePage {

    public static final String NAME = "Notification";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public NotificationPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Notification[/i] control is intended for displaying alerts and messages \
            to users as pop-ups. It's customizable with different colors and icons, can contain \
            a graphic or image, along with the message and additional actions for users to take."""
        );
        addSection("Usage", usageExample());
        addSection("Actions", actionsExample());
        addSection("Intent", intentExample());
        addSection("Popup", popupExample());
        addSection("Elevation", elevationExample());
    }

    private Node usageExample() {
        //snippet_1:start
        var ntf1 = new Notification(
            FAKER.lorem().sentence(15)
        );
        ntf1.getStyleClass().add(Styles.ELEVATED_1);
        var icon1 = new ImageView(new Image(
            Resources.getResourceAsStream("images/warning_32.png")
        ));
        ntf1.setGraphic(icon1);

        var ntf2 = new Notification(FAKER.lorem().sentence(15));
        ntf2.getStyleClass().add(Styles.ELEVATED_1);
        ntf2.setOnClose(e -> Animations.flash(ntf2).playFromStart());
        //snippet_1:end

        var box = new VBox(VGAP_20, ntf1, ntf2);
        var description = BBCodeParser.createFormattedText("""
            The [i]Notification[/i] has no mandatory properties, but it wouldn't make \
            sense without a message. Optionally, you can use a graphic and also make the \
            [i]Notification[/i] closeable by providing an appropriate dismiss handler."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node actionsExample() {
        //snippet_2:start
        var ntf = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        ntf.getStyleClass().add(Styles.ELEVATED_1);
        ntf.setOnClose(e -> Animations.flash(ntf).playFromStart());

        var yesBtn = new Button("Yes");
        yesBtn.setDefaultButton(true);

        var noBtn = new Button("No");

        ntf.setPrimaryActions(yesBtn, noBtn);
        ntf.setSecondaryActions(
            new MenuItem("Item 1"),
            new MenuItem("Item 2")
        );
        //snippet_2:end

        var box = new VBox(ntf);
        var description = BBCodeParser.createFormattedText("""
            The notification has two slots for setting custom actions. The primary action \
            slot appears like a [i]ButtonBar[/i] (check the Javadoc for its features, by the way) \
            at the bottom of the notification. The secondary actions slot is the dropdown menu \
            at the top right corner. Both slots are completely optional."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node intentExample() {
        //snippet_3:start
        var info = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        info.getStyleClass().add(Styles.ELEVATED_1);
        info.getStyleClass().add(Styles.ACCENT);
        info.setOnClose(e -> Animations.flash(info).playFromStart());

        var success = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        success.getStyleClass().add(Styles.ELEVATED_1);
        success.getStyleClass().add(Styles.SUCCESS);
        success.setOnClose(e -> Animations.flash(success).playFromStart());

        var warning = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        warning.getStyleClass().add(Styles.ELEVATED_1);
        warning.getStyleClass().add(Styles.WARNING);
        warning.setOnClose(e -> Animations.flash(warning).playFromStart());

        var danger = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        danger.getStyleClass().add(Styles.ELEVATED_1);
        danger.getStyleClass().add(Styles.DANGER);
        danger.setOnClose(e -> Animations.flash(danger).playFromStart());
        //snippet_3:end

        var box = new VBox(VGAP_20, info, success, warning, danger);
        var description = BBCodeParser.createFormattedText("""
            The [i]Notification[/i] supports colors (or intents). To set them, \
            use the corresponding style class modifier."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private ExampleBox popupExample() {
        //snippet_4:start
        final var msg = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        msg.getStyleClass().addAll(
            Styles.ACCENT, Styles.ELEVATED_1
        );
        msg.setPrefHeight(Region.USE_PREF_SIZE);
        msg.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(msg, Pos.TOP_RIGHT);
        StackPane.setMargin(msg, new Insets(10, 10, 0, 0));

        var btn = new Button("Show");

        msg.setOnClose(e -> {
            var out = Animations.slideOutUp(msg, Duration.millis(250));
            out.setOnFinished(f -> getChildren().remove(msg));
            out.playFromStart();
        });
        btn.setOnAction(e -> {
            var in = Animations.slideInDown(msg, Duration.millis(250));
            if (!getChildren().contains(msg)) {
                getChildren().add(msg);
            }
            in.playFromStart();
        });
        //snippet_4:end

        var box = new VBox(VGAP_20, btn);
        box.setPadding(new Insets(0, 0, 10, 0));
        var description = BBCodeParser.createFormattedText("""
            There isn't any special support for popup notifications, but \
            you can easily implement it by using the [i]StackPane[/i] layout."""
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 4), description);
        example.setAllowDisable(false);

        return example;
    }

    private Node elevationExample() {
        //snippet_5:start
        var ntf1 = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        ntf1.getStyleClass().add(Styles.ELEVATED_2);
        ntf1.setOnClose(e -> Animations.flash(ntf1).playFromStart());

        var ntf2 = new Notification(
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        ntf2.getStyleClass().add(Styles.INTERACTIVE);
        ntf2.setOnClose(e -> Animations.flash(ntf2).playFromStart());

        //snippet_5:end

        var box = new VBox(VGAP_20, ntf1, ntf2);
        box.setPadding(new Insets(0, 0, 10, 0));
        var description = BBCodeParser.createFormattedText("""
            To add the raised effect to the [i]Notification[/i], use the [code]Styles.ELEVATED_N[/code] \
            or [code]Styles.INTERACTIVE[/code] style classes."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }
}
