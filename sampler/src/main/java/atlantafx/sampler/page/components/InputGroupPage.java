/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import static atlantafx.base.theme.Styles.BUTTON_ICON;

public class InputGroupPage extends AbstractPage {

    public static final String NAME = "Input Group";

    @Override
    public String getName() { return NAME; }

    public InputGroupPage() {
        super();
        createView();
    }

    private void createView() {
        userContent.getChildren().addAll(new FlowPane(
                20, 20,
                httpMethodSample().getRoot(),
                passwordSample().getRoot(),
                networkSample().getRoot(),
                dropdownSample().getRoot()
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

        return new SampleBlock("ComboBox + TextField", box);
    }

    private SampleBlock passwordSample() {
        var leftPassword = new PasswordField();
        leftPassword.setText(FAKER.internet().password());
        leftPassword.getStyleClass().add(Styles.LEFT_PILL);

        var rightBtn = new Button("", new FontIcon(Feather.REFRESH_CW));
        rightBtn.getStyleClass().addAll(BUTTON_ICON);
        rightBtn.setOnAction(e -> leftPassword.setText(FAKER.internet().password()));
        rightBtn.getStyleClass().add(Styles.RIGHT_PILL);

        var box = new HBox(leftPassword, rightBtn);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Password Field + Button", box);
    }

    private SampleBlock networkSample() {
        var leftText = new TextField("192.168.1.10");
        leftText.getStyleClass().add(Styles.LEFT_PILL);

        var centerText = new TextField("24");
        centerText.getStyleClass().add(Styles.CENTER_PILL);
        centerText.setPrefWidth(50);

        var rightText = new TextField("192.168.1.10");
        rightText.getStyleClass().add(Styles.RIGHT_PILL);

        var box = new HBox(leftText, centerText, rightText);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("Text Fields", box);
    }

    private SampleBlock dropdownSample() {
        var leftMenu = new MenuButton(FAKER.harryPotter().character());
        leftMenu.getItems().addAll(
                new MenuItem(FAKER.harryPotter().spell()),
                new MenuItem(FAKER.harryPotter().spell()),
                new MenuItem(FAKER.harryPotter().spell())
        );
        leftMenu.getStyleClass().add(Styles.LEFT_PILL);

        var rightText = new TextField();
        rightText.getStyleClass().add(Styles.RIGHT_PILL);

        var box = new HBox(leftMenu, rightText);
        box.setAlignment(Pos.CENTER_LEFT);

        return new SampleBlock("MenuButton + TextField", box);
    }
}

