/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class SampleBlock {

    protected final VBox root;
    protected final Label titleLabel;
    protected final Node content;

    public SampleBlock(String title, Node content) {
        this.titleLabel = new Label(title);
        this.titleLabel.getStyleClass().add("title");

        this.content = content;
        VBox.setVgrow(content, Priority.ALWAYS);

        this.root = new VBox(titleLabel, content);
        this.root.getStyleClass().add("sample-block");
    }

    public Pane getRoot() {
        return root;
    }

    public String getText() {
        return titleLabel.getText();
    }

    public void setText(String text) {
        titleLabel.setText(text);
    }

    public Node getContent() {
        return content;
    }
}
