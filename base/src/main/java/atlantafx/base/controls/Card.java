/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Skin;
import org.jspecify.annotations.Nullable;

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
    public ObjectProperty<@Nullable Node> headerProperty() {
        return header;
    }

    private final ObjectProperty<@Nullable Node> header = new SimpleObjectProperty<>(this, "header");

    public @Nullable Node getHeader() {
        return header.get();
    }

    public void setHeader(@Nullable Node header) {
        this.header.set(header);
    }

    /**
     * Represents the card’s sub-header node.
     */
    public final ObjectProperty<@Nullable Node> subHeaderProperty() {
        return subHeader;
    }

    private final ObjectProperty<@Nullable Node> subHeader = new SimpleObjectProperty<>(this, "subHeader");

    public @Nullable Node getSubHeader() {
        return subHeader.get();
    }

    public void setSubHeader(@Nullable Node subHeader) {
        this.subHeader.set(subHeader);
    }

    /**
     * Represents the card’s body node.
     */
    public ObjectProperty<@Nullable Node> bodyProperty() {
        return body;
    }

    private final ObjectProperty<@Nullable Node> body = new SimpleObjectProperty<>(this, "body");

    public @Nullable Node getBody() {
        return body.get();
    }

    public void setBody(@Nullable Node body) {
        this.body.set(body);
    }

    /**
     * Represents the card’s footer node.
     */
    public ObjectProperty<@Nullable Node> footerProperty() {
        return footer;
    }

    private final ObjectProperty<@Nullable Node> footer = new SimpleObjectProperty<>(this, "footer");

    public @Nullable Node getFooter() {
        return footer.get();
    }

    public void setFooter(@Nullable Node footer) {
        this.footer.set(footer);
    }
}
