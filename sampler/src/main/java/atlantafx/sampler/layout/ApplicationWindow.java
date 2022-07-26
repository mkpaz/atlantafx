/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.layout;

import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.components.OverviewPage;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;

import static javafx.scene.layout.Priority.ALWAYS;

public class ApplicationWindow extends BorderPane {

    public ApplicationWindow() {
        var sidebar = new Sidebar();
        sidebar.setMinWidth(200);

        final var pageContainer = new StackPane();
        HBox.setHgrow(pageContainer, ALWAYS);

        sidebar.setOnSelect(pageClass -> {
            try {
                // reset previous page, e.g. to free resources
                if (!pageContainer.getChildren().isEmpty() && pageContainer.getChildren().get(0) instanceof Page page) {
                    page.reset();
                }

                Page page = pageClass.getDeclaredConstructor().newInstance();
                pageContainer.getChildren().setAll(page.getView());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // ~
        setLeft(sidebar);
        setCenter(pageContainer);

        sidebar.select(OverviewPage.class);
        Platform.runLater(sidebar::requestFocus);
    }
}
