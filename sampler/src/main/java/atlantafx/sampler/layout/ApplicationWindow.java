/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.layout;

import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.components.OverviewPage;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.Objects;

import static javafx.scene.layout.Priority.ALWAYS;

public class ApplicationWindow extends BorderPane {

    public ApplicationWindow() {
        var sidebar = new Sidebar();
        sidebar.setMinWidth(200);

        final var pageContainer = new StackPane();
        HBox.setHgrow(pageContainer, ALWAYS);

        sidebar.setOnSelect(pageClass -> {
            try {
                final Page prevPage = (!pageContainer.getChildren().isEmpty() && pageContainer.getChildren().get(0) instanceof Page page) ? page : null;
                final Page nextPage = pageClass.getDeclaredConstructor().newInstance();

                // startup, no animation
                if (getScene() == null) {
                    pageContainer.getChildren().add(nextPage.getView());
                    return;
                }

                Objects.requireNonNull(prevPage);

                // reset previous page, e.g. to free resources
                prevPage.reset();

                // animate switching between pages
                pageContainer.getChildren().add(nextPage.getView());
                FadeTransition transition = new FadeTransition(Duration.millis(300), nextPage.getView());
                transition.setFromValue(0.0);
                transition.setToValue(1.0);
                transition.setOnFinished(t -> pageContainer.getChildren().remove(prevPage.getView()));
                transition.play();
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
