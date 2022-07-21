/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.page.AbstractPage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class ToggleSwitchPage extends AbstractPage {

    public static final String NAME = "ToggleSwitch";

    @Override
    public String getName() { return NAME; }

    public ToggleSwitchPage() {
        super();
        createView();
    }

    private void createView() {
        var toggle = new ToggleSwitch();
        toggle.selectedProperty().addListener((obs, old, val) -> toggle.setText(val ? "Disable" : "Enable"));
        toggle.setSelected(true);

        var box = new VBox(20, new Label("Nothing fancy here."), toggle);
        box.setAlignment(Pos.CENTER);

        userContent.getChildren().setAll(box);
    }
}
