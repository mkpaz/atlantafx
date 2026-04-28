/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import javafx.scene.control.Label;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class CardTest {

    private Card card;

    @BeforeEach
    public void setUp() {
        card = new Card();
    }

    @Test
    public void testDefaultStyleClass() {
        assertThat(card.getStyleClass()).contains("card");
    }

    @Test
    public void testDefaultPropertyValues() {
        assertThat(card.getHeader()).isNull();
        assertThat(card.getSubHeader()).isNull();
        assertThat(card.getBody()).isNull();
        assertThat(card.getFooter()).isNull();
    }

    @Test
    public void testHeaderProperty() {
        var header = new Label("Header");
        card.setHeader(header);
        assertThat(card.getHeader()).isSameAs(header);

        card.setHeader(null);
        assertThat(card.getHeader()).isNull();
    }

    @Test
    public void testSubHeaderProperty() {
        var subHeader = new Label("SubHeader");
        card.setSubHeader(subHeader);
        assertThat(card.getSubHeader()).isSameAs(subHeader);

        card.setSubHeader(null);
        assertThat(card.getSubHeader()).isNull();
    }

    @Test
    public void testBodyProperty() {
        var body = new Label("Body");
        card.setBody(body);
        assertThat(card.getBody()).isSameAs(body);

        card.setBody(null);
        assertThat(card.getBody()).isNull();
    }

    @Test
    public void testFooterProperty() {
        var footer = new Label("Footer");
        card.setFooter(footer);
        assertThat(card.getFooter()).isSameAs(footer);

        card.setFooter(null);
        assertThat(card.getFooter()).isNull();
    }

    @Test
    public void testCreateDefaultSkin() {
        var skin = card.createDefaultSkin();
        assertThat(skin).isInstanceOf(CardSkin.class);
    }
}
