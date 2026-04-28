/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.Toast;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class ToastPage extends OutlinePage {

    public static final String NAME = "Toast";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public ToastPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Toast[/i] is a lightweight, transient message that appears briefly \
            to provide feedback about an operation. Unlike [i]Notification[/i], Toast \
            is minimal — just a message and an optional close button. It auto-hides \
            after a configurable duration."""
        );
        addSection("Usage", usageExample());
        addSection("Duration", durationExample());
        addSection("Intent", intentExample());
        addSection("Closeable", closeableExample());
    }

    private Node usageExample() {
        //snippet_1:start
        var toast = new Toast("File saved successfully.");
        //snippet_1:end

        var box = new VBox(toast);
        var description = BBCodeParser.createFormattedText("""
            A [i]Toast[/i] only requires a message string. By default, it will \
            auto-hide after 5 seconds. The toast can be placed in any container, \
            such as a VBox for stacking multiple toasts."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node durationExample() {
        //snippet_2:start
        var shortToast = new Toast("This disappears quickly (2s).");
        shortToast.setDuration(Duration.seconds(2));

        var longToast = new Toast("This stays a bit longer (10s).");
        longToast.setDuration(Duration.seconds(10));

        var persistent = new Toast("This won't auto-hide.");
        persistent.setDuration(Duration.INDEFINITE);
        //snippet_2:end

        var toastContainer = new VBox(10);
        toastContainer.setPadding(new Insets(10, 0, 0, 0));

        var btn = new Button("Show Toasts");
        btn.setOnAction(e -> {
            toastContainer.getChildren().clear();

            var t1 = new Toast("Disappears in 2 seconds.");
            t1.setDuration(Duration.seconds(2));
            t1.setOnClose(ev -> toastContainer.getChildren().remove(t1));

            var t2 = new Toast("Stays for 10 seconds.");
            t2.setDuration(Duration.seconds(10));
            t2.setOnClose(ev -> toastContainer.getChildren().remove(t2));

            var t3 = new Toast("This won't auto-hide.");
            t3.setDuration(Duration.INDEFINITE);
            t3.setOnClose(ev -> toastContainer.getChildren().remove(t3));

            toastContainer.getChildren().setAll(t1, t2, t3);
        });

        var box = new VBox(VGAP_10, btn, toastContainer);
        var description = BBCodeParser.createFormattedText("""
            The auto-hide duration can be configured via the [code]duration[/code] property. \
            Set it to [code]Duration.INDEFINITE[/code] to disable auto-hide."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node intentExample() {
        //snippet_3:start
        var accent = new Toast("Accent toast message.");
        accent.getStyleClass().add(Styles.ACCENT);

        var success = new Toast("Operation completed successfully.");
        success.getStyleClass().add(Styles.SUCCESS);

        var warning = new Toast("Something might be wrong.");
        warning.getStyleClass().add(Styles.WARNING);

        var danger = new Toast("An error occurred.");
        danger.getStyleClass().add(Styles.DANGER);
        //snippet_3:end

        var box = new VBox(VGAP_10, accent, success, warning, danger);
        var description = BBCodeParser.createFormattedText("""
            The [i]Toast[/i] offers four color variants that can be set via \
            the corresponding style class modifier."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }

    private Node closeableExample() {
        //snippet_4:start
        var toast = new Toast("Click the close button to dismiss.");
        toast.setOnClose(e -> {
            if (toast.getParent() instanceof VBox parent) {
                parent.getChildren().remove(toast);
            }
        });
        toast.setDuration(Duration.INDEFINITE);
        //snippet_4:end

        var box = new VBox(toast);
        var description = BBCodeParser.createFormattedText("""
            You can make the [i]Toast[/i] closeable by setting the [code]onClose[/code] \
            handler. The close button only appears when the handler is set. The handler \
            is responsible for removing the toast from its parent container."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 4), description);
    }
}
