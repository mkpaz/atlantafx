/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * A versatile container that can be used in various contexts, such as headings,
 * text, dialogs and more. It includes a header to provide a brief overview
 * or context of the information. The sub-header and body sections provide
 * more detailed content, while the footer may include additional actions or
 * information.
 */
public class Card extends Control {

    /**
     * Creates an empty Card.
     */
    public Card() {
        super();
        getStyleClass().add("card");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Skin<?> createDefaultSkin() {
        return new CardSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Represents the card’s header node.
     */
    public ObjectProperty<Node> headerProperty() {
        return header;
    }

    private final ObjectProperty<Node> header = new SimpleObjectProperty<>(this, "header");

    public Node getHeader() {
        return header.get();
    }

    public void setHeader(Node header) {
        this.header.set(header);
    }

    /**
     * Represents the card’s sub-header node.
     */
    public final ObjectProperty<Node> subHeaderProperty() {
        return subHeader;
    }

    private final ObjectProperty<Node> subHeader = new SimpleObjectProperty<>(this, "subHeader");

    public Node getSubHeader() {
        return subHeader.get();
    }

    public void setSubHeader(Node subHeader) {
        this.subHeader.set(subHeader);
    }

    /**
     * Represents the card’s body node.
     */
    public ObjectProperty<Node> bodyProperty() {
        return body;
    }

    private final ObjectProperty<Node> body = new SimpleObjectProperty<>(this, "body");

    public Node getBody() {
        return body.get();
    }

    public void setBody(Node body) {
        this.body.set(body);
    }

    /**
     * Represents the card’s footer node.
     */
    public ObjectProperty<Node> footerProperty() {
        return footer;
    }

    private final ObjectProperty<Node> footer = new SimpleObjectProperty<>(this, "footer");

    public Node getFooter() {
        return footer.get();
    }

    public void setFooter(Node footer) {
        this.footer.set(footer);
    }
}
