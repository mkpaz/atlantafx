/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;

/**
 * Aa versatile container that can used in various contexts such as headings,
 * text, dialogs and more. It includes a header to provide a brief overview
 * or context of the information. The sub-header and body sections provide
 * more detailed content, while the footer may include additional actions or
 * information.
 */
public class Card extends Control {

    // Default constructor
    public Card() {
        super();
        getStyleClass().add("card");
    }

    @Override
    protected Skin<?> createDefaultSkin() {
        return new CardSkin(this);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Properties                                                            //
    ///////////////////////////////////////////////////////////////////////////

    /**
     * The property representing the card’s header node.
     */
    private final ObjectProperty<Node> header = new SimpleObjectProperty<>(this, "header");

    public Node getHeader() {
        return header.get();
    }

    public ObjectProperty<Node> headerProperty() {
        return header;
    }

    public void setHeader(Node header) {
        this.header.set(header);
    }

    /**
     * The property representing the card’s sub-header node.
     */
    private final ObjectProperty<Node> subHeader = new SimpleObjectProperty<>(this, "subHeader");

    public Node getSubHeader() {
        return subHeader.get();
    }

    public final ObjectProperty<Node> subHeaderProperty() {
        return subHeader;
    }

    public void setSubHeader(Node subHeader) {
        this.subHeader.set(subHeader);
    }

    /**
     * The property representing the card’s body node.
     */
    private final ObjectProperty<Node> body = new SimpleObjectProperty<>(this, "body");

    public Node getBody() {
        return body.get();
    }

    public ObjectProperty<Node> bodyProperty() {
        return body;
    }

    public void setBody(Node body) {
        this.body.set(body);
    }

    /**
     * The property representing the card’s footer node.
     */
    private final ObjectProperty<Node> footer = new SimpleObjectProperty<>(this, "footer");

    public Node getFooter() {
        return footer.get();
    }

    public ObjectProperty<Node> footerProperty() {
        return footer;
    }

    public void setFooter(Node footer) {
        this.footer.set(footer);
    }
}
