/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import javafx.scene.Node;

/**
 * The Message is a component for displaying notifications or alerts
 * and is specifically designed to grab the user’s attention.
 * It is based on the Tile layout and shares its structure.
 */
public class Message extends Tile {

    /**
     * See {@link Tile#Tile()}.
     */
    public Message() {
        super(null, null, null);
    }

    /**
     * See {@link Tile#Tile(String, String)}.
     */
    public Message(String title, String subtitle) {
        this(title, subtitle, null);
    }

    /**
     * See {@link Tile#Tile(String, String, Node)}.
     */
    public Message(String title, String subtitle, Node graphic) {
        super(title, subtitle, graphic);
        getStyleClass().add("message");
    }
}
