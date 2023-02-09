/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.fake.domain;

import java.util.Objects;
import java.util.function.Function;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import net.datafaker.Faker;

public final class Product {

    private static final int MAX_STOCK_SIZE = 999;

    private int id;
    private final BooleanProperty state;
    private String brand;
    private String name;
    private String price;
    private Integer count;

    public Product(int id,
                   BooleanProperty state,
                   String brand,
                   String name,
                   String price,
                   Integer count
    ) {
        this.id = id;
        this.state = state;
        this.brand = brand;
        this.name = name;
        this.price = price;
        this.count = count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean getState() {
        return state.get();
    }

    public BooleanProperty stateProperty() {
        return state;
    }

    public void setState(boolean state) {
        this.state.set(state);
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public double getAvailability() {
        return count * 1.0 / MAX_STOCK_SIZE;
    }

    public static Product random(int id, Faker faker) {
        return new Product(
            id,
            new SimpleBooleanProperty(),
            faker.commerce().brand(),
            faker.commerce().productName(),
            faker.commerce().price(),
            faker.random().nextInt(0, MAX_STOCK_SIZE)
        );
    }

    public static Product random(int id, String brand, Faker faker) {
        var product = random(id, faker);
        product.setBrand(brand);
        return product;
    }

    public static Product empty(int id) {
        return new Product(id, new SimpleBooleanProperty(), "", "", "", 0);
    }

    public String toString(Function<Product, String> f) {
        Objects.requireNonNull(f);
        return f.apply(this);
    }
}
