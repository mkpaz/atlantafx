/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.general;

import atlantafx.base.controls.SelectableTextFlow;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.net.URI;

public final class SelectableTextFlowPage extends OutlinePage {

    public static final String NAME = "SelectableTextFlow";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/SelectableTextFlow"));
    }

    public SelectableTextFlowPage() {
        super();

        addPageHeader();
        addFormattedText("""                       
            [i]SelectableTextFlow[/i] extends the functionality of the regular [i]TextFlow[/i] \
            by providing the ability to select a range of text. The selected range is \
            visually highlighted and can be retrieved as a string."""
        );
        addSection("Usage", usageExample());
        addSection("Programmatic Selection", programmaticSelectionExample());
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var textFlow = new SelectableTextFlow();
        textFlow.setText(
            new Text(FAKER.lorem().paragraph(1)),
            new Text(" "),
            new Text(FAKER.chuckNorris().fact())
        );
        textFlow.setTextSelectionOnMouseClick(true);

        var copyItem = new MenuItem("Copy");
        copyItem.setOnAction(e -> textFlow.copySelectedRangeToClipboard());

        var contextMenu = new ContextMenu(copyItem);
        textFlow.setContextMenu(contextMenu);
        //snippet_1:end

        var box = new VBox(HGAP_20, textFlow);

        var example = new ExampleBox(box, new Snippet(getClass(), 1));
        example.setAllowDisable(false);

        return example;
    }

    private ExampleBox programmaticSelectionExample() {
        //snippet_2:start
        var textFlow = new SelectableTextFlow();
        textFlow.setText(
            new Text(FAKER.lorem().paragraph(1)),
            new Text(" "),
            new Text(FAKER.chuckNorris().fact())
        );

        var selectAllBtn = new Button("Select All");
        selectAllBtn.setOnAction(e -> textFlow.selectAll());

        var selectRangeBtn = new Button("Select Range");
        selectRangeBtn.setOnAction(e -> textFlow.selectRange(1, 5));

        var clearBtn = new Button("Clear");
        clearBtn.setOnAction(e -> textFlow.clearSelection());
        //snippet_2:end

        var box = new VBox(
            VGAP_20,
            textFlow,
            new HBox(HGAP_20, selectAllBtn, selectRangeBtn, clearBtn)
        );

        var example = new ExampleBox(box, new Snippet(getClass(), 2));
        example.setAllowDisable(false);

        return example;
    }
}
