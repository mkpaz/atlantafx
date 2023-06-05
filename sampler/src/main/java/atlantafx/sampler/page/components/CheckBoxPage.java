/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

public final class CheckBoxPage extends OutlinePage {

    public static final String NAME = "CheckBox";

    @Override
    public String getName() {
        return NAME;
    }

    private CheckBox basicCheck;
    private CheckBox indeterminateCheck;

    public CheckBoxPage() {
        super();

        addPageHeader();
        addFormattedText("""
            A tri-state selection control is typically skinned as a box \
            with a checkmark or tick mark when checked."""
        );

        addSection("Usage", usageExample());
        addSection("Indeterminate", indeterminateExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var cb1 = new CheckBox("_Unchecked");
        cb1.setMnemonicParsing(true);

        var cb2 = new CheckBox("Checked");
        cb2.setSelected(true);
        //snippet_1:end

        var box = new HBox(40, cb1, cb2);
        basicCheck = cb1;

        var description = BBCodeParser.createFormattedText("""
            A [i]CheckBox[/i] can be in one of three states:
                        
            [ul]
            [li][b]checked[/b]: indeterminate == false, checked == true[/li]
            [li][b]unchecked[/b]: indeterminate == false, checked == false[/li]
            [li][b]undefined[/b]: indeterminate == true[/li][/ul]"""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox indeterminateExample() {
        //snippet_2:start
        var cb1 = new CheckBox("I_ndeterminate");
        cb1.setAllowIndeterminate(true);
        cb1.setIndeterminate(true);
        cb1.setMnemonicParsing(true);

        var cb2 = new CheckBox("Indeterminate + Checked");
        cb2.setAllowIndeterminate(true);
        cb2.setIndeterminate(false);
        cb2.setSelected(true);

        var cb3 = new CheckBox("Indeterminate + Unchecked");
        cb3.setAllowIndeterminate(true);
        //snippet_2:end

        var box = new HBox(40, cb1, cb2, cb3);
        indeterminateCheck = cb1;

        var description = BBCodeParser.createFormattedText("""
            The [code]allowIndeterminate[/code] variable, if true, allows the user to \
            cycle through the undefined state. A [i]CheckBox[/i] is undefined if \
            indeterminate is true, regardless of the state of selected."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    // visually compare normal and indeterminate checkboxes size
    @Override
    protected void onRendered() {
        var normalBox = basicCheck.lookup(".box");
        var indeterminateBox = indeterminateCheck.lookup(".box");

        if (normalBox == null || indeterminateBox == null) {
            return;
        }

        // force layout to obtain node bounds
        ((StackPane) normalBox).layout();
        ((StackPane) indeterminateBox).layout();

        System.out.printf("Basic: height = %.2f , width = %.2f\n",
            normalBox.getBoundsInParent().getHeight(),
            normalBox.getBoundsInParent().getWidth()
        );

        System.out.printf("Indeterminate: height = %.2f , width = %.2f\n",
            indeterminateBox.getBoundsInParent().getHeight(),
            indeterminateBox.getBoundsInParent().getWidth()
        );
    }
}
