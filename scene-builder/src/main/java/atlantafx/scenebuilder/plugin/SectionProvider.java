/* SPDX-License-Identifier: MIT */

package atlantafx.scenebuilder.plugin;

import com.oracle.javafx.scenebuilder.kit.library.ExternalSectionProvider;
import org.jspecify.annotations.NullMarked;

import java.util.ArrayList;
import java.util.List;

@NullMarked
public final class SectionProvider implements ExternalSectionProvider {

    @Override
    public int getExternalSectionPosition() {
        return 2;
    }

    @Override
    public String getExternalSectionName() {
        return "AtlantaFX";
    }

    @Override
    public List<Class<?>> getExternalSectionItems() {
        List<Class<?>> items = new ArrayList<>();

        items.add(atlantafx.base.controls.Breadcrumbs.class);
        items.add(atlantafx.base.controls.Calendar.class);
        items.add(atlantafx.base.controls.Card.class);
        items.add(atlantafx.base.controls.CustomTextField.class);
        items.add(atlantafx.base.layout.DeckPane.class);
        items.add(atlantafx.base.layout.InputGroup.class);
        items.add(atlantafx.base.controls.MaskTextField.class);
        items.add(atlantafx.base.controls.Message.class);
        items.add(atlantafx.base.layout.ModalBox.class);
        items.add(atlantafx.base.controls.ModalPane.class);
        items.add(atlantafx.base.controls.Notification.class);
        items.add(atlantafx.base.controls.PasswordTextField.class);
        items.add(atlantafx.base.controls.Popover.class);
        items.add(atlantafx.base.controls.RingProgressIndicator.class);
        items.add(atlantafx.base.controls.SegmentedControl.class);
        items.add(atlantafx.base.controls.SelectableTextFlow.class);
        items.add(atlantafx.base.controls.Spacer.class);
        items.add(atlantafx.base.controls.Spin.class);
        items.add(atlantafx.base.controls.TabLine.class);
        items.add(atlantafx.base.controls.Tile.class);
        items.add(atlantafx.base.controls.ToggleLabel.class);
        items.add(atlantafx.base.controls.ToggleSwitch.class);

        return List.copyOf(items);
    }

    @Override
    public String getItemsFXMLPath() {
        return "library";
    }

    @Override
    public String getItemsIconPath() {
        return "icons";
    }
}