/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.event.BrowseEvent;
import atlantafx.sampler.event.DefaultEventBus;
import atlantafx.sampler.layout.ApplicationWindow;
import java.net.URI;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import net.datafaker.Faker;
import org.kordamp.ikonli.feather.Feather;

public interface Page {

    int MAX_WIDTH = ApplicationWindow.MIN_WIDTH - ApplicationWindow.SIDEBAR_WIDTH;
    int HGAP_20 = 20;
    int HGAP_30 = 30;
    int VGAP_10 = 10;
    int VGAP_20 = 20;

    Faker FAKER = new Faker();
    Random RANDOM = new Random();
    int PAGE_HGAP = 30;
    int PAGE_VGAP = 30;

    String getName();

    Parent getView();

    boolean canDisplaySourceCode();

    boolean canChangeThemeSettings();

    void reset();

    default <T> List<T> generate(Supplier<T> supplier, int count) {
        return Stream.generate(supplier).limit(count).toList();
    }

    default Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }

    default Node createFormattedText(String text, boolean handleUrl) {
        var node = BBCodeParser.createFormattedText(text);

        if (handleUrl) {
            node.addEventFilter(ActionEvent.ACTION, e -> {
                if (e.getTarget() instanceof Hyperlink link && link.getUserData() != null) {
                    DefaultEventBus.getInstance().publish(
                        new BrowseEvent(URI.create((String) link.getUserData()))
                    );
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
}
