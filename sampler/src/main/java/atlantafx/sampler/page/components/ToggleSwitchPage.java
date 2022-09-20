/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.components;

import atlantafx.base.controls.ToggleSwitch;
import atlantafx.sampler.page.AbstractPage;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.SampleBlock;
import javafx.scene.layout.FlowPane;

public class ToggleSwitchPage extends AbstractPage {

    public static final String NAME = "ToggleSwitch";

    @Override
    public String getName() { return NAME; }

    public ToggleSwitchPage() {
        super();
        setUserContent(new FlowPane(
                Page.PAGE_HGAP, Page.PAGE_VGAP,
                basicSample()
        ));
    }

    private SampleBlock basicSample() {
        var toggle = new ToggleSwitch();
        toggle.selectedProperty().addListener((obs, old, val) -> toggle.setText(val ? "Disable" : "Enable"));
        toggle.setSelected(true);
        return new SampleBlock("Basic", toggle);
    }
}
