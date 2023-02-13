/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import static atlantafx.sampler.page.Page.PAGE_HGAP;
import static atlantafx.sampler.page.Page.PAGE_VGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;

public class TagSample extends GridPane {

    private static final int PREF_WIDTH = 300;

    public TagSample() {
        new CSSFragment(Tag.CSS).addTo(this);

        setHgap(PAGE_HGAP);
        setVgap(PAGE_VGAP);

        add(filledTagSample(), 0, 0);
        add(iconTagSample(), 1, 0);
        add(outlinedTagSample(), 0, 1);
        add(closeableTagSample(), 1, 1);
        add(customColorTagSample(), 0, 2);
    }

    private SampleBlock filledTagSample() {
        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.setPrefWidth(PREF_WIDTH);

        var basicTag = new Tag("basic");
        content.getChildren().add(basicTag);

        var accentTag = new Tag("accent");
        accentTag.getStyleClass().add(Styles.ACCENT);
        content.getChildren().add(accentTag);

        var successTag = new Tag("success");
        successTag.getStyleClass().add(Styles.SUCCESS);
        content.getChildren().add(successTag);

        var dangerTag = new Tag("danger");
        dangerTag.getStyleClass().add(Styles.DANGER);
        content.getChildren().add(dangerTag);

        return new SampleBlock("Filled", content);
    }

    private SampleBlock iconTagSample() {
        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.setPrefWidth(PREF_WIDTH);

        var basicTag = new Tag("image", new FontIcon(Feather.IMAGE));
        content.getChildren().add(basicTag);

        var accentTag = new Tag("music", new FontIcon(Feather.MUSIC));
        content.getChildren().add(accentTag);

        var successTag = new Tag("video", new FontIcon(Feather.VIDEO));
        content.getChildren().add(successTag);

        return new SampleBlock("Icon", content);
    }

    private SampleBlock outlinedTagSample() {
        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.setPrefWidth(PREF_WIDTH);

        var accentTag = new Tag("accent");
        accentTag.getStyleClass().addAll(Styles.ACCENT, Styles.BUTTON_OUTLINED);
        content.getChildren().add(accentTag);

        var successTag = new Tag("success");
        successTag.getStyleClass().addAll(Styles.SUCCESS, Styles.BUTTON_OUTLINED);
        content.getChildren().add(successTag);

        var dangerTag = new Tag("danger");
        dangerTag.getStyleClass().addAll(Styles.DANGER, Styles.BUTTON_OUTLINED);
        content.getChildren().add(dangerTag);

        return new SampleBlock("Outlined", content);
    }

    private SampleBlock closeableTagSample() {
        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.setPrefWidth(PREF_WIDTH);

        var basicTag = new Tag("basic", new FontIcon(Material2AL.CLOSE));
        basicTag.setContentDisplay(ContentDisplay.RIGHT);
        content.getChildren().add(basicTag);

        var accentTag = new Tag("accent", new FontIcon(Material2AL.CANCEL));
        accentTag.setContentDisplay(ContentDisplay.RIGHT);
        accentTag.getStyleClass().add(Styles.ACCENT);
        content.getChildren().add(accentTag);

        var successTag = new Tag("success", new FontIcon(Material2AL.CANCEL));
        successTag.setContentDisplay(ContentDisplay.RIGHT);
        successTag.getStyleClass().add(Styles.SUCCESS);
        content.getChildren().add(successTag);

        var dangerTag = new Tag("danger", new FontIcon(Material2AL.CANCEL));
        dangerTag.setContentDisplay(ContentDisplay.RIGHT);
        dangerTag.getStyleClass().add(Styles.DANGER);
        content.getChildren().add(dangerTag);

        return new SampleBlock("Removable", content);
    }

    private SampleBlock customColorTagSample() {
        var content = new FlowPane(BLOCK_HGAP, BLOCK_VGAP);
        content.setPrefWidth(PREF_WIDTH);
        new CSSFragment("""
            .brand {
              -color-button-fg:         -color-fg-emphasis;
              -color-button-bg-hover:   -color-button-bg;
              -color-button-bg-pressed: -color-button-bg;
            }
            .twitter {
              -color-button-bg:     rgb(85, 172, 238);
              -color-button-border: rgb(85, 172, 238);
            }
            .youtube {
              -color-button-bg:     rgb(205, 32, 31);
              -color-button-border: rgb(205, 32, 31);
            }
            .facebook {
              -color-button-bg:     rgb(59, 89, 153);
              -color-button-border: rgb(59, 89, 153);
            }
            """).addTo(content);

        var twitterTag = new Tag("Twitter", new FontIcon(Feather.TWITTER));
        twitterTag.getStyleClass().addAll("brand", "twitter");
        content.getChildren().add(twitterTag);

        var youtubeTag = new Tag("YouTube", new FontIcon(Feather.YOUTUBE));
        youtubeTag.getStyleClass().addAll("brand", "youtube");
        content.getChildren().add(youtubeTag);

        var facebookTag = new Tag("Facebook", new FontIcon(Feather.FACEBOOK));
        facebookTag.getStyleClass().addAll("brand", "facebook");
        content.getChildren().add(facebookTag);

        return new SampleBlock("Custom Color", content);
    }
}
