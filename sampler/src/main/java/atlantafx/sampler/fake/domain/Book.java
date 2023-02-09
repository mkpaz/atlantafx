/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.fake.domain;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.datafaker.Faker;

public final class Book {

    private UUID id;
    private final BooleanProperty state;
    private String author;
    private String title;
    private String isbn;

    public Book(UUID id,
                BooleanProperty state,
                String author,
                String title,
                String isbn) {
        this.id = id;
        this.state = state;
        this.author = author;
        this.title = title;
        this.isbn = isbn;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public boolean getState() {
        return state.get();
    }

    public void setState(boolean state) {
        this.state.set(state);
    }

    public BooleanProperty stateProperty() {
        return state;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public static Book random(Faker faker) {
        return new Book(
            UUID.randomUUID(),
            new SimpleBooleanProperty(),
            faker.book().author(),
            faker.book().title(),
            faker.code().isbn10()
        );
    }

    public String toString(Function<Book, String> f) {
        Objects.requireNonNull(f);
        return f.apply(this);
    }
}
