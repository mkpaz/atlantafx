/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;
import static atlantafx.sampler.util.Controls.hyperlink;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.Ikon;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.StackedFontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public class IconsPage extends AbstractPage {

    public static final String NAME = "Icons";

    @Override
    public String getName() {
        return NAME;
    }

    public IconsPage() {
        super();
        createView();
    }

    private void createView() {
        var headerText = new TextFlow(
            new Text("AtlantaFX supports "),
            hyperlink("Ikonli", URI.create("https://kordamp.org/ikonli")),
            new Text(" iconic fonts that can be used together with some JavaFX components.")
        );

        var browserText = new TextFlow(
            new Text("There's a variety of icon packs. Sampler app uses "),
            hyperlink("Material Icons", URI.create("https://kordamp.org/ikonli/cheat-sheet-material2.html")),
            new Text(" you can preview below.")
        );

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

        setUserContent(new VBox(
            PAGE_VGAP,
            headerText,
            expandingHBox(colorSample(), stackingSample()),
            browserText,
            filterBox,
            iconBrowser
        ));
    }

    private SampleBlock colorSample() {
        var accentIcon = new FontIcon(Material2MZ.THUMB_UP);
        accentIcon.getStyleClass().add(Styles.ACCENT);

        var successIcon = new FontIcon(Material2MZ.THUMB_UP);
        successIcon.getStyleClass().add(Styles.SUCCESS);

        var warningIcon = new FontIcon(Material2MZ.THUMB_UP);
        warningIcon.getStyleClass().add(Styles.WARNING);

        var dangerIcon = new FontIcon(Material2MZ.THUMB_UP);
        dangerIcon.getStyleClass().add(Styles.DANGER);

        var content = new VBox(
            BLOCK_VGAP,
            new Label("You can also use pseudo-classes to set icon color."),
            new HBox(BLOCK_HGAP, accentIcon, successIcon, warningIcon, dangerIcon)
        );

        return new SampleBlock("Colors", content);
    }

    private SampleBlock stackingSample() {
        var outerIcon1 = new FontIcon(Material2OutlinedAL.BLOCK);
        outerIcon1.getStyleClass().add("outer-icon");

        var innerIcon1 = new FontIcon(Material2MZ.PHOTO_CAMERA);
        innerIcon1.getStyleClass().add("inner-icon");

        var stackIcon1 = new StackedFontIcon();
        stackIcon1.getChildren().addAll(innerIcon1, outerIcon1);
        new CSSFragment("""
            .stacked-ikonli-font-icon > .outer-icon {
                -fx-icon-size: 48px;
                -fx-icon-color: -color-danger-emphasis;
            }
            .stacked-ikonli-font-icon > .inner-icon {
                -fx-icon-size: 24px;
            }
            """).addTo(stackIcon1);

        var outerIcon2 = new FontIcon(Material2OutlinedAL.CHECK_BOX_OUTLINE_BLANK);
        outerIcon2.getStyleClass().add("outer-icon");

        var innerIcon2 = new FontIcon(Material2AL.LOCK);
        innerIcon2.getStyleClass().add("inner-icon");

        var stackIcon2 = new StackedFontIcon();
        stackIcon2.getChildren().addAll(outerIcon2, innerIcon2);
        new CSSFragment("""
            .stacked-ikonli-font-icon > .outer-icon {
                -fx-icon-size: 48px;
            }
            .stacked-ikonli-font-icon > .inner-icon {
                -fx-icon-size: 24px;
            }
            """).addTo(stackIcon2);

        var content = new HBox(BLOCK_HGAP, stackIcon1, stackIcon2);

        return new SampleBlock("Stacking Icons", content);
    }
}
