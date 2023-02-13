/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import static atlantafx.base.theme.Styles.BUTTON_ICON;
import static atlantafx.sampler.page.SampleBlock.BLOCK_HGAP;
import static atlantafx.sampler.page.SampleBlock.BLOCK_VGAP;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

public class InputGroupPage extends AbstractPage {

    public static final String NAME = "Input Group";

    @Override
    public String getName() {
        return NAME;
    }

    public InputGroupPage() {
        super();
        setUserContent(new VBox(
            Page.PAGE_VGAP,
            expandingHBox(httpMethodSample(), passwordSample()),
            expandingHBox(networkSample(), dropdownSample()),
            labelSample()
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

    private SampleBlock labelSample() {
        var leftLabel1 = new Label("", new CheckBox());
        leftLabel1.getStyleClass().add(Styles.LEFT_PILL);

        var rightText1 = new TextField();
        rightText1.setPromptText("Username");
        rightText1.getStyleClass().add(Styles.RIGHT_PILL);
        rightText1.setPrefWidth(100);

        var sample1 = new HBox(leftLabel1, rightText1);
        sample1.setAlignment(Pos.CENTER_LEFT);

        // ~

        var leftText2 = new TextField("johndoe");
        leftText2.getStyleClass().add(Styles.LEFT_PILL);
        leftText2.setPrefWidth(100);

        var centerLabel2 = new Label("@");
        centerLabel2.getStyleClass().add(Styles.CENTER_PILL);

        var rightText2 = new TextField("gmail.com");
        rightText2.getStyleClass().add(Styles.RIGHT_PILL);
        rightText2.setPrefWidth(100);

        var sample2 = new HBox(leftText2, centerLabel2, rightText2);
        sample2.setAlignment(Pos.CENTER_LEFT);

        // ~

        var leftText3 = new TextField("+123456");
        leftText3.getStyleClass().add(Styles.LEFT_PILL);
        leftText3.setPrefWidth(100);

        var rightLabel3 = new Label("", new FontIcon(Feather.DOLLAR_SIGN));
        rightLabel3.getStyleClass().add(Styles.RIGHT_PILL);

        var sample3 = new HBox(leftText3, rightLabel3);
        sample3.setAlignment(Pos.CENTER_LEFT);

        // ~

        var flowPane = new FlowPane(
            BLOCK_HGAP, BLOCK_VGAP,
            sample1,
            sample2,
            sample3
        );

        return new SampleBlock("Label & TextField", flowPane);
    }
}

