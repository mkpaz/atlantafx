/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.layout;

import javafx.scene.layout.StackPane;

class Sidebar extends StackPane {

    private final MainModel model;
    private final NavTree navTree;

    public Sidebar(MainModel model) {
        super();

        this.model = model;
        this.navTree = new NavTree(model);

        createView();
    }

    private void createView() {
        model.selectedPageProperty().addListener((obs, old, val) -> {
            if (val != null) {
                navTree.getSelectionModel().select(model.getTreeItemForPage(val));
            }
        });

        setId("sidebar");
        getChildren().addAll(navTree);
    }

    void begForFocus() {
        navTree.requestFocus();
    }
}
