/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.layout.DeckPane;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.Resources;
import atlantafx.sampler.page.AbstractPage;
import java.net.URI;
import java.util.function.Supplier;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

public final class DeckPanePage extends AbstractPage {

    public static final String NAME = "DeckPane";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "layout/" + getName()));
    }

    public DeckPanePage() {
        super();

        addPageHeader();
        addFormattedText("""
            [i]DeckPane[/i] represents a pane that displays all of its child nodes in a deck, \
            where only one node can be visible at a time. It does not maintain any sequence \
            (model), but only cares about the top node, which can be changed by various \
            transition effects.
                        
            Using the control is as simple as calling the [code]swipeX(Node)[/code] and \
            [code]slideX(Node)[/code] methods, where [code]X[/code] represents the direction \
            of the transition that you want to achieve."""
        );
        addNode(createGallery());
    }

    private Node createGallery() {
        var galleryPane = new BorderPane();
        galleryPane.setMinSize(570, 400);
        galleryPane.setMaxSize(570, 400);

        var image1 = new ImageView(new Image(
            Resources.getResourceAsStream("images/gallery/kush-dwivedi-unsplash.jpg"))
        );
        image1.setFitWidth(450);
        image1.setFitHeight(300);

        var image2 = new ImageView(new Image(
            Resources.getResourceAsStream("images/gallery/markus-spiske-unsplash.jpg"))
        );
        image2.setFitWidth(450);
        image2.setFitHeight(300);

        var image3 = new ImageView(new Image(
            Resources.getResourceAsStream("images/gallery/r0m0_4-unsplash.jpg"))
        );
        image3.setFitWidth(450);
        image3.setFitHeight(300);

        // ~

        var transitionTypeBox = new VBox(VGAP_10);
        transitionTypeBox.setAlignment(Pos.CENTER);

        var label = new Label("Transition Type");
        label.getStyleClass().addAll(Styles.TEXT_CAPTION, Styles.TEXT_MUTED);

        var swipeRadio = new RadioButton("Swipe");
        swipeRadio.setSelected(true);
        swipeRadio.setUserData(-1);

        var slideRadio = new RadioButton("Slide");
        slideRadio.setUserData(1);

        var transitionType = new ToggleGroup();
        transitionType.getToggles().setAll(swipeRadio, slideRadio);

        var radioBox = new HBox(HGAP_20, swipeRadio, slideRadio);
        radioBox.setAlignment(Pos.CENTER);

        transitionTypeBox.getChildren().setAll(label, radioBox);

        // ~

        var deck = new DeckPane(image1, image2, image3);
        deck.setMinSize(450, 300);
        deck.setMaxSize(450, 300);
        deck.setAnimationDuration(Duration.millis(350));
        galleryPane.setCenter(deck);

        // circularly returns the next item from the deck
        Supplier<Node> nextItem = () -> {
            var next = (deck.getChildren().indexOf(deck.getTopNode()) + 1)
                % deck.getChildren().size();
            return deck.getChildren().get(next);
        };

        var topBtn = new Button(null, new FontIcon(Material2MZ.NORTH));
        topBtn.setOnAction(e -> {
            if ((int) transitionType.getSelectedToggle().getUserData() < 0) {
                deck.swipeUp(nextItem.get());
            } else {
                deck.slideUp(nextItem.get());
            }
        });
        galleryPane.setTop(topBtn);
        BorderPane.setAlignment(topBtn, Pos.CENTER);

        var rightBtn = new Button(null, new FontIcon(Material2AL.EAST));
        rightBtn.setOnAction(e -> {
            if ((int) transitionType.getSelectedToggle().getUserData() < 0) {
                deck.swipeRight(nextItem.get());
            } else {
                deck.slideRight(nextItem.get());
            }
        });
        galleryPane.setRight(rightBtn);
        BorderPane.setAlignment(rightBtn, Pos.CENTER);

        var bottomBtn = new Button(null, new FontIcon(Material2MZ.SOUTH));
        bottomBtn.setOnAction(e -> {
            if ((int) transitionType.getSelectedToggle().getUserData() < 0) {
                deck.swipeDown(nextItem.get());
            } else {
                deck.slideDown(nextItem.get());
            }
        });
        galleryPane.setBottom(bottomBtn);
        BorderPane.setAlignment(bottomBtn, Pos.CENTER);

        var leftBtn = new Button(null, new FontIcon(Material2MZ.WEST));
        leftBtn.setOnAction(e -> {
            if ((int) transitionType.getSelectedToggle().getUserData() < 0) {
                deck.swipeLeft(nextItem.get());
            } else {
                deck.slideLeft(nextItem.get());
            }
        });
        galleryPane.setLeft(leftBtn);
        BorderPane.setAlignment(leftBtn, Pos.CENTER);

        // ~
        var root = new VBox(VGAP_20, galleryPane, transitionTypeBox);
        root.setAlignment(Pos.CENTER);

        return root;
    }
}
