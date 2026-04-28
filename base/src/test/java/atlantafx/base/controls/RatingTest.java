/* SPDX-License-Identifier: MIT */

package atlantafx.base.controls;

import static org.assertj.core.api.Assertions.assertThat;

import atlantafx.base.util.JavaFXTest;
import org.jspecify.annotations.NullMarked;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({JavaFXTest.class})
@NullMarked
public class RatingTest {

    @Test
    public void testDefaultStyleClass() {
        var rating = new Rating();
        assertThat(rating.getStyleClass()).contains("rating");
    }

    @Test
    public void testDefaultPropertyValues() {
        var rating = new Rating();
        assertThat(rating.getRating()).isEqualTo(0);
        assertThat(rating.getMax()).isEqualTo(5);
        assertThat(rating.isEditable()).isTrue();
        assertThat(rating.isPartialRating()).isFalse();
    }

    @Test
    public void testCustomMax() {
        var rating = new Rating(10);
        assertThat(rating.getMax()).isEqualTo(10);

        rating.setMax(3);
        assertThat(rating.getMax()).isEqualTo(3);
    }

    @Test
    public void testRatingProperty() {
        var rating = new Rating();
        rating.setRating(3.5);
        assertThat(rating.getRating()).isEqualTo(3.5);

        rating.setRating(0);
        assertThat(rating.getRating()).isEqualTo(0);
    }

    @Test
    public void testEditableProperty() {
        var rating = new Rating();
        assertThat(rating.isEditable()).isTrue();

        rating.setEditable(false);
        assertThat(rating.isEditable()).isFalse();
    }

    @Test
    public void testPartialRatingProperty() {
        var rating = new Rating();
        assertThat(rating.isPartialRating()).isFalse();

        rating.setPartialRating(true);
        assertThat(rating.isPartialRating()).isTrue();
    }

    @Test
    public void testCreateDefaultSkin() {
        var rating = new Rating();
        var skin = rating.createDefaultSkin();
        assertThat(skin).isInstanceOf(RatingSkin.class);
    }
}
