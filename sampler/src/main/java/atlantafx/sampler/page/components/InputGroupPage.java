/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.BUTTON_ICON;

public class InputGroupPage extends AbstractPage {

    public static final String NAME = "Input Group";

    @Override
    public String getName() { return NAME; }

    public InputGroupPage() {
        super();
        setUserContent(new VBox(
                Page.PAGE_VGAP,
                expandingHBox(httpMethodSample(), passwordSample()),
                expandingHBox(networkSample(), dropdownSample())
        ));
    }

    private SampleBlock httpMethodSample() {
        var leftCombo = new ComboBox<>();
        leftCombo.getItems().addAll("POST", "GET", "PUT", "PATCH", "DELETE");
        leftCombo.getStyleClass().add(Styles.LEFT_PILL);
        leftCombo.getSelectionModel().selectFirst();

        var rightText = new TextField("https://example.org");
        rightText.getStyleClass().add(Styles.RIGHT_PILL);

        var box = new HBox(leftCombo, rightText);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("ComboBox & TextField", box);
    }

    private SampleBlock passwordSample() {
        var leftPassword = new TextField();
        leftPassword.setText(FAKER.internet().password());
        leftPassword.getStyleClass().add(Styles.LEFT_PILL);

        var rightBtn = new Button("", new FontIcon(Feather.REFRESH_CW));
        rightBtn.getStyleClass().addAll(BUTTON_ICON);
        rightBtn.setOnAction(e -> leftPassword.setText(FAKER.internet().password()));
        rightBtn.getStyleClass().add(Styles.RIGHT_PILL);

        var box = new HBox(leftPassword, rightBtn);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Text Field & Button", box);
    }

    private SampleBlock networkSample() {
        var leftText = new TextField("192.168.1.10");
        leftText.getStyleClass().add(Styles.LEFT_PILL);
        leftText.setPrefWidth(140);

        var centerText = new TextField("24");
        centerText.getStyleClass().add(Styles.CENTER_PILL);
        centerText.setPrefWidth(70);

        var rightText = new TextField("192.168.1.1");
        rightText.getStyleClass().add(Styles.RIGHT_PILL);
        rightText.setPrefWidth(140);

        var box = new HBox(leftText, centerText, rightText);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Text Fields", box);
    }

    private SampleBlock dropdownSample() {
        var rightText = new TextField(FAKER.harryPotter().spell());
        rightText.getStyleClass().add(Styles.RIGHT_PILL);

        var spellItem = new MenuItem("Spell");
        spellItem.setOnAction(e -> rightText.setText(FAKER.harryPotter().spell()));

        var characterItem = new MenuItem("Character");
        characterItem.setOnAction(e -> rightText.setText(FAKER.harryPotter().character()));

        var locationItem = new MenuItem("Location");
        locationItem.setOnAction(e -> rightText.setText(FAKER.harryPotter().location()));

        var leftMenu = new MenuButton("Generate");
        leftMenu.getItems().addAll(spellItem, characterItem, locationItem);
        leftMenu.getStyleClass().add(Styles.LEFT_PILL);

        var box = new HBox(leftMenu, rightText);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("MenuButton & TextField", box);
    }
}

