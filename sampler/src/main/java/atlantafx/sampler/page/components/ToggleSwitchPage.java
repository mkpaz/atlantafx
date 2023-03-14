/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.geometry.HorizontalDirection;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

public class ToggleSwitchPage extends AbstractPage {

    public static final String NAME = "ToggleSwitch";

    @Override
    public String getName() {
        return NAME;
    }

    public ToggleSwitchPage() {
        super();
        setUserContent(new FlowPane(
            Page.PAGE_HGAP, Page.PAGE_VGAP,
            basicSample(),
            stateSample()
        ));
    }

    private SampleBlock basicSample() {
        var leftToggle = new ToggleSwitch("Enabled");
        leftToggle.selectedProperty().addListener(
            (obs, old, val) -> leftToggle.setText(val ? "Enabled" : "Disabled")
        );
        leftToggle.setSelected(true);

        var rightToggle = new ToggleSwitch("Disabled");
        rightToggle.selectedProperty().addListener(
            (obs, old, val) -> rightToggle.setText(val ? "Enabled" : "Disabled")
        );
        rightToggle.setLabelPosition(HorizontalDirection.RIGHT);
        rightToggle.setSelected(false);

        return new SampleBlock("Basic", new VBox(SampleBlock.BLOCK_VGAP, leftToggle, rightToggle));
    }

    private SampleBlock stateSample() {
        var successToggle = new ToggleSwitch("Enabled");
        successToggle.selectedProperty().addListener((obs, old, val) -> {
                successToggle.setText(val ? "Enabled" : "Disabled");
                successToggle.pseudoClassStateChanged(Styles.STATE_SUCCESS, val);
            }
        );
        successToggle.setSelected(true);
        successToggle.pseudoClassStateChanged(Styles.STATE_SUCCESS, true);

        var dangerToggle = new ToggleSwitch("Disabled");
        dangerToggle.selectedProperty().addListener((obs, old, val) -> {
                dangerToggle.setText(val ? "Enabled" : "Disabled");
                dangerToggle.pseudoClassStateChanged(Styles.STATE_DANGER, val);
            }
        );
        dangerToggle.setLabelPosition(HorizontalDirection.RIGHT);
        dangerToggle.setSelected(false);

        return new SampleBlock("State", new VBox(SampleBlock.BLOCK_VGAP, successToggle, dangerToggle));
    }
}
