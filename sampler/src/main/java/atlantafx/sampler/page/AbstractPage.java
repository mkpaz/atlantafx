/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page;

import static atlantafx.sampler.util.Containers.setScrollConstraints;
import static javafx.scene.control.ScrollPane.ScrollBarPolicy.AS_NEEDED;

import atlantafx.sampler.layout.Overlay;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import net.datafaker.Faker;
import org.kordamp.ikonli.feather.Feather;

public abstract class AbstractPage extends BorderPane implements Page {

    protected static final Faker FAKER = new Faker();
    protected static final Random RANDOM = new Random();

    protected final StackPane userContent = new StackPane();
    protected Overlay overlay;
    protected boolean isRendered = false;

    protected AbstractPage() {
        super();

        userContent.getStyleClass().add("user-content");
        getStyleClass().add("page");

        createPageLayout();
    }

    protected void createPageLayout() {
        var scrollPane = new ScrollPane(userContent);
        setScrollConstraints(scrollPane, AS_NEEDED, true, AS_NEEDED, true);
        scrollPane.setMaxHeight(10_000);

        setCenter(scrollPane);
    }

    protected void setUserContent(Node content) {
        userContent.getChildren().setAll(content);
    }

    @Override
    public Pane getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return true;
    }

    @Override
    public boolean canChangeThemeSettings() {
        return true;
    }

    @Override
    public void reset() {
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (isRendered) {
            return;
        }

        isRendered = true;
        onRendered();
    }

    // Some properties can only be obtained after node placed
    // to the scene graph and here is the place do this.
    protected void onRendered() {
        this.overlay = lookupOverlay();
    }

    protected Overlay lookupOverlay() {
        return getScene() != null && getScene().lookup("." + Overlay.STYLE_CLASS) instanceof Overlay ov ? ov : null;
    }

    ///////////////////////////////////////////////////////////////////////////

    protected HBox expandingHBox(Node... nodes) {
        var box = new HBox(PAGE_HGAP, nodes);
        Arrays.stream(nodes).forEach(n -> HBox.setHgrow(n, Priority.ALWAYS));
        return box;
    }

    protected <T> List<T> generate(Supplier<T> supplier, int count) {
        return Stream.generate(supplier).limit(count).toList();
    }

    protected Feather randomIcon() {
        return Feather.values()[RANDOM.nextInt(Feather.values().length)];
    }
}
