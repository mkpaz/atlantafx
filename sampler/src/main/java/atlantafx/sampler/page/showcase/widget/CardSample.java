/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.widget;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.Resources;
import atlantafx.sampler.theme.CSSFragment;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.datafaker.Faker;

import static atlantafx.sampler.page.Page.PAGE_HGAP;
import static atlantafx.sampler.page.Page.PAGE_VGAP;

public class CardSample extends HBox {

    private static final Faker FAKER = new Faker();
    private static final int CARD_WIDTH = 300;

    public CardSample() {
        new CSSFragment(Card.CSS).addTo(this);

        setSpacing(PAGE_HGAP);
        setAlignment(Pos.TOP_CENTER);
        setMinWidth(CARD_WIDTH * 2 + PAGE_HGAP);
        getChildren().setAll(
                // column 0
                new VBox(
                        PAGE_VGAP,
                        textFooterCard(),
                        titleTextCard(),
                        quoteCard()
                ),
                // column 1
                new VBox(
                        PAGE_VGAP,
                        imageTextCard(),
                        titleImageCard()
                )
        );
    }

    private Card textFooterCard() {
        var card = new Card();
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);

        var text = new Text(FAKER.chuckNorris().fact());
        card.setBody(new TextFlow(text));

        var btn = new Button("More!");
        btn.getStyleClass().addAll(Styles.ACCENT, Styles.BUTTON_OUTLINED);
        btn.setOnAction(e -> text.setText(FAKER.chuckNorris().fact()));

        card.setFooter(new HBox(btn));

        return card;
    }

    private Card imageTextCard() {
        var card = new Card();
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);

        var image = new ImageView(new Image(Resources.getResourceAsStream("images/20_min_adventure.jpg")));
        image.setFitWidth(300);
        image.setPreserveRatio(true);
        card.setImage(image);

        var text = new Text(FAKER.rickAndMorty().quote());
        card.setBody(new TextFlow(text));

        return card;
    }

    private Card titleTextCard() {
        var card = new Card();
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);

        card.setTitle("Title");
        card.setSubtitle("Subtitle");

        var text = new Text(FAKER.lorem().paragraph());
        card.setBody(new TextFlow(text));

        return card;
    }

    private Card titleImageCard() {
        var card = new Card();
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);

        var image = new Image(Resources.getResourceAsStream("images/pattern.jpg"));
        PixelReader pixelReader = image.getPixelReader();
        var cropImage = new WritableImage(pixelReader, 0, 0, 300, 100);

        card.setImage(new ImageView(cropImage));
        card.setTitle("Title");

        var text = new Text(FAKER.lorem().paragraph());
        card.setBody(new TextFlow(text));

        return card;
    }

    private Card quoteCard() {
        var card = new Card();
        card.setMinWidth(CARD_WIDTH);
        card.setMaxWidth(CARD_WIDTH);

        var quoteText = new Text(FAKER.bojackHorseman().quotes());
        quoteText.getStyleClass().add(Styles.TITLE_3);

        var authorText = new Text("Bojack Horseman");

        card.setBody(new VBox(
                10,
                new TextFlow(quoteText),
                authorText
        ));

        card.setFooter(new TextFlow(
                new Text("Share on "),
                new Hyperlink("Twitter")
        ));

        return card;
    }
}
