/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.Nullable;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public final class IconsPage extends OutlinePage {

    public static final String NAME = "Icons";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public @Nullable URI getJavadocUri() {
        return null;
    }

    public IconsPage() {
        super();

        addPageHeader();
        addFormattedText("""
            AtlantaFX supports [url=https://kordamp.org/ikonli]Ikonli[/url] iconic fonts out \
            of the box, which can be used in conjunction with certain JavaFX components.""",
            true
        );
        addSection("Color", colorExample());
        addSection("Stacking", stackingExample());
        addSection("Icon Pack", iconBrowser());
    }

    private ExampleBox colorExample() {
        //snippet_1:start
        var accentIcon = new FontIcon(Material2MZ.THUMB_UP);
        accentIcon.getStyleClass().add(Styles.ACCENT);

        var successIcon = new FontIcon(Material2MZ.THUMB_UP);
        successIcon.getStyleClass().add(Styles.SUCCESS);

        var warningIcon = new FontIcon(Material2MZ.THUMB_UP);
        warningIcon.getStyleClass().add(Styles.WARNING);

        var dangerIcon = new FontIcon(Material2MZ.THUMB_UP);
        dangerIcon.getStyleClass().add(Styles.DANGER);
        //snippet_1:end

        var box = new HBox(HGAP_20, accentIcon, successIcon, warningIcon, dangerIcon);

        var description = BBCodeParser.createFormattedText("""
            You can use pseudo-classes to set the icon color.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 1), description);
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox stackingExample() {
        var dataClass1 = """
            .stacked-ikonli-font-icon > .outer-icon {
                -fx-icon-size: 48px;
                -fx-icon-color: -color-danger-emphasis;
            }
            .stacked-ikonli-font-icon > .inner-icon {
                -fx-icon-size: 24px;
            }
            """;

        var dataClass2 = """
            .stacked-ikonli-font-icon > .outer-icon {
                -fx-icon-size: 48px;
            }
            .stacked-ikonli-font-icon > .inner-icon {
                -fx-icon-size: 24px;
            }
            """;

        //snippet_2:start
        var outerIcon1 = new FontIcon(Material2OutlinedAL.BLOCK);
        outerIcon1.getStyleClass().add("outer-icon");

        var innerIcon1 = new FontIcon(Material2MZ.PHOTO_CAMERA);
        innerIcon1.getStyleClass().add("inner-icon");

        var stackIcon1 = new StackedFontIcon();
        stackIcon1.getChildren().addAll(innerIcon1, outerIcon1);
        // .stacked-ikonli-font-icon > .outer-icon {
        //   -fx-icon-size: 48px;
        //   -fx-icon-color: -color-danger-emphasis;
        // }
        // .stacked-ikonli-font-icon > .inner-icon {
        //   -fx-icon-size: 24px;
        // }
        stackIcon1.getStylesheets().add(Styles.toDataURI(dataClass1));

        var outerIcon2 = new FontIcon(
            Material2OutlinedAL.CHECK_BOX_OUTLINE_BLANK
        );
        outerIcon2.getStyleClass().add("outer-icon");

        var innerIcon2 = new FontIcon(Material2AL.LOCK);
        innerIcon2.getStyleClass().add("inner-icon");

        var stackIcon2 = new StackedFontIcon();
        stackIcon2.getChildren().addAll(outerIcon2, innerIcon2);
        // .stacked-ikonli-font-icon > .outer-icon {
        //   -fx-icon-size: 48px;
        // }
        // .stacked-ikonli-font-icon > .inner-icon {
        //   -fx-icon-size: 24px;
        // }
        stackIcon2.getStylesheets().add(Styles.toDataURI(dataClass2));
        //snippet_2:end

        var box = new HBox(HGAP_20, stackIcon1, stackIcon2);

        var description = BBCodeParser.createFormattedText("""
            Ikonli also supports icon stacking, although it is currently in an \
            initial state and requires some manual styling. However, it can be \
            useful in certain cases.""");

        var example = new ExampleBox(box, new Snippet(getClass(), 2), description);
        example.setAllowDisable(false);

        return example;
    }

    private Node iconBrowser() {
        var description = createFormattedText("""
            There are various icon packs available. The Sampler app uses \
            [url=https://kordamp.org/ikonli/cheat-sheet-material2.html]Material Icons[/url] \
            which you can preview below.""", true);

        var filterText = new CustomTextField();
        filterText.setLeft(new FontIcon(Material2MZ.SEARCH));
        filterText.setPrefWidth(300);

        var filterBox = new HBox(filterText);
        filterBox.setAlignment(Pos.CENTER);

        var icons = new ArrayList<Ikon>();
        icons.addAll(Arrays.asList(Material2AL.values()));
        icons.addAll(Arrays.asList(Material2MZ.values()));

        var iconBrowser = new IconBrowser(5, icons);
        VBox.setVgrow(iconBrowser, Priority.ALWAYS);
        iconBrowser.filterProperty().bind(filterText.textProperty());
        iconBrowser.setMinHeight(500);

        return new VBox(VGAP_10, description, filterBox, iconBrowser);
    }
}
