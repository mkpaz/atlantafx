/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.Animations;
import atlantafx.base.util.BBCodeParser;
import atlantafx.base.controls.Message;
import atlantafx.sampler.event.BrowseEvent;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
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
            The [i]Message[/i] is a component for displaying an important text or \
            alerts and is specifically designed to grab the user’s attention. It is \
            based on the [i]Tile[/i] layout and shares its structure, except it doesn't \
            provide the action slot."""
        );
        addSection("Usage", usageExample());
        addSection("Intent", intentExample());
        addSection("Incomplete Header", incompleteHeaderExample());
        addSection("Interactive", interactiveExample());
        addSection("Closeable", closeableExample());
    }

    private Node usageExample() {
        // won't work inside the snippet, because code
        // snippet use BBCode parse as well
        var url = "https://wikipedia.org/wiki/The_Elder_Scrolls_III:_Morrowind";
        var quote = FAKER.elderScrolls().quote()
            + " \n[url=" + url + "]Learn more[/url]";

        //snippet_1:start
        var msg = new Message(
            "Quote",
            quote,
            new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE)
        );
        msg.addEventFilter(ActionEvent.ACTION, e -> {
            if (e.getTarget() instanceof Hyperlink link) {
                BrowseEvent.fire((String) link.getUserData());
            }
            e.consume();
        });
        //snippet_1:end

        var box = new VBox(msg);
        var description = BBCodeParser.createFormattedText("""
            [i]Message[/i] does not have any mandatory properties. It supports text \
            wrapping and [i]BBCode[/i] formatted text, but only in the description field."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node intentExample() {
        //snippet_5:start
        var info = new Message(
            "Info",
            FAKER.lorem().sentence(10),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        info.getStyleClass().add(Styles.ACCENT);

        var success = new Message(
            "Success",
            FAKER.lorem().sentence(15),
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
        //snippet_5:end

        var box = new VBox(VGAP_10, info, success, warning, danger);
        var description = BBCodeParser.createFormattedText("""
            The [i]Message[/i] offers four severity levels that set a distinctive color. \
            To change the [i]Message[/i] intent, use the corresponding style class modifier."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 5), description);
    }

    private Node incompleteHeaderExample() {
        //snippet_2:start
        var info1 = new Message(
            FAKER.lorem().sentence(5),
            null,
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        info1.getStyleClass().add(Styles.ACCENT);

        var info2 = new Message(
            null,
            FAKER.lorem().sentence(50)
        );
        info2.getStyleClass().add(Styles.ACCENT);
        //snippet_2:end

        var box = new VBox(VGAP_10, info1, info2);
        var description = BBCodeParser.createFormattedText("""
            Both the title and description are completely optional, but one them has to be \
            specified in any case. Note that the styling changes depending on whether the [i]Message[/i] \
            has only a title, only a description, or both."""
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
        msg.setActionHandler(() -> Animations.flash(msg).playFromStart());
        //snippet_3:end

        var box = new VBox(msg);
        box.setPadding(new Insets(0, 0, 5, 0));
        var description = BBCodeParser.createFormattedText("""
            A [i]Message[/i] can be made interactive by setting an action handler. \
            This allows to call any arbitrary action when the user clicks inside \
            the message container. For example, you could show an extended dialog or \
            trigger a notification panel to appear.."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private Node closeableExample() {
        //snippet_4:start
        var regular = new Message(
            "Regular",
            FAKER.lorem().sentence(5),
            new FontIcon(Material2OutlinedAL.CHAT_BUBBLE_OUTLINE)
        );
        regular.setOnClose(e -> Animations.flash(regular).playFromStart());

        var info = new Message(
            "Info",
            FAKER.lorem().sentence(10),
            new FontIcon(Material2OutlinedAL.HELP_OUTLINE)
        );
        info.getStyleClass().add(Styles.ACCENT);
        info.setOnClose(e -> Animations.flash(info).playFromStart());

        var success = new Message(
            "Success",
            FAKER.lorem().sentence(15),
            new FontIcon(Material2OutlinedAL.CHECK_CIRCLE_OUTLINE)
        );
        success.getStyleClass().add(Styles.SUCCESS);
        success.setOnClose(e -> Animations.flash(success).playFromStart());

        var warning = new Message(
            "Warning",
            FAKER.lorem().sentence(20),
            new FontIcon(Material2OutlinedMZ.OUTLINED_FLAG)
        );
        warning.getStyleClass().add(Styles.WARNING);
        warning.setOnClose(e -> Animations.flash(warning).playFromStart());

        var danger = new Message(
            "Danger",
            FAKER.lorem().sentence(25),
            new FontIcon(Material2OutlinedAL.ERROR_OUTLINE)
        );
        danger.getStyleClass().add(Styles.DANGER);
        danger.setOnClose(e -> Animations.flash(danger).playFromStart());
        //snippet_4:end

        var box = new VBox(VGAP_10, regular, info, success, warning, danger);
        var description = BBCodeParser.createFormattedText("""
            You can make the [i]Message[/i] closeable by setting an appropriate message handler. \
            If the handler is set, the close button will appear in the top right corner of the \
            [i]Message[/i]. This handler should provide some logic for removing the [i]Message[/i] \
            from its parent container as no default implementation is provided."""
        );
        box.setPadding(new Insets(0, 0, 5, 0));

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
