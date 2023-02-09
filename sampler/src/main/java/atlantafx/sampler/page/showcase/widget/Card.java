/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class Card extends VBox {

    public static final String CSS = """
        .card {
           -fx-background-color: -color-bg-default;
         }
         .card > .subtitle {
            -fx-text-fill: -color-fg-muted;
            -fx-padding: 0px 15px 10px 15px;
         }
         .card > .title,
         .card > .body,
         .card > .footer {
           -fx-padding: 10px 15px 10px 15px;
         }
         """;

    private final StringProperty title = new SimpleStringProperty();
    private final StringProperty subtitle = new SimpleStringProperty();
    private final ObjectProperty<ImageView> image = new SimpleObjectProperty<>();
    private final ObjectProperty<Parent> body = new SimpleObjectProperty<>();
    private final ObjectProperty<Parent> footer = new SimpleObjectProperty<>();

    public Card() {
        super();
        createView();
    }

    private void createView() {
        var footerSep = new Separator();
        footerSep.getStyleClass().add(Styles.SMALL);
        footerSep.managedProperty().bind(Bindings.createObjectBinding(
            () -> footer.get() != null && footer.get().isManaged(), footer
        ));

        getChildren().setAll(
            createPlaceholder(), // title
            createPlaceholder(), // subtitle
            createPlaceholder(), // image
            createPlaceholder(), // body
            footerSep,
            createPlaceholder()  // footer
        );

        image.addListener(
            (obs, old, val) -> setChild(0, val, "image")
        );
        title.addListener(
            (obs, old, val) -> setChild(1, val != null ? new Label(val) : null, "title", Styles.TITLE_4)
        );
        subtitle.addListener(
            (obs, old, val) -> setChild(2, val != null ? new Label(val) : null, "subtitle")
        );
        body.addListener(
            (obs, old, val) -> setChild(3, val, "body")
        );
        footer.addListener(
            (obs, old, val) -> setChild(5, val, "footer")
        );

        getStyleClass().addAll("card", Styles.BORDERED);
    }

    private void setChild(int index, Node node, String... styleClass) {
        if (node != null) {
            for (var s : styleClass) {
                if (!node.getStyleClass().contains(s)) {
                    node.getStyleClass().add(s);
                }
            }
            getChildren().set(index, node);
        } else {
            getChildren().set(index, createPlaceholder());
        }
    }

    public String getTitle() {
        return title.get();
    }

    public StringProperty titleProperty() {
        return title;
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getSubtitle() {
        return subtitle.get();
    }

    public StringProperty subtitleProperty() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle.set(subtitle);
    }

    public ImageView getImage() {
        return image.get();
    }

    public ObjectProperty<ImageView> imageProperty() {
        return image;
    }

    public void setImage(ImageView image) {
        this.image.set(image);
    }

    public Parent getBody() {
        return body.get();
    }

    public ObjectProperty<Parent> bodyProperty() {
        return body;
    }

    public void setBody(Parent body) {
        this.body.set(body);
    }

    public Parent getFooter() {
        return footer.get();
    }

    public ObjectProperty<Parent> footerProperty() {
        return footer;
    }

    public void setFooter(Parent footer) {
        this.footer.set(footer);
    }

    private Parent createPlaceholder() {
        var g = new Group();
        g.setManaged(false);
        return g;
    }
}
