package atlantafx.sampler.page.components;

import atlantafx.base.layout.AppStage;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.Page;
import atlantafx.sampler.page.Snippet;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

/**
 * AppStage is a Stage decorator for creating a window without an operating system border
 *
 * @author Nonoas
 * @date 2024/5/19
 */
public class AppStagePage extends StackPane implements Page {

    public static final String NAME = "AppStage";

    private AppStage appStage;

    public AppStagePage() {
        super();

        var pageHeader = new PageHeader(this);

        var descriptionText = BBCodeParser.createFormattedText("""
                AppStage is a Stage decorator for creating a window without an operating system border
                """
        );

        var userContent = getUserContent(pageHeader, descriptionText);

        StackPane.setMargin(userContent, new Insets(0, 0, 0, 0));
        userContent.setPadding(new Insets(50, 0, 50, 0));
        userContent.setMinWidth(Page.MAX_WIDTH - 100);
        userContent.setMaxWidth(Page.MAX_WIDTH - 100);

        var pageBody = new StackPane();
        Styles.appendStyle(pageBody, "-fx-background-color", "-color-bg-default");
        pageBody.getChildren().setAll(userContent);

        setMinWidth(Page.MAX_WIDTH);
        getChildren().setAll(pageBody);

    }

    @NotNull
    private VBox getUserContent(PageHeader pageHeader, TextFlow descriptionText) {
        var button = new Button("Show AppStage");
        button.getStyleClass().add(Styles.ACCENT);

        button.setOnAction(actionEvent -> {
            if (appStage != null) {
                appStage.display();
            }else {
                createAppStage();
                appStage.display();
            }
        });

        var snippet = new Snippet(getClass(), 1);
        return new VBox(
                20,
                pageHeader,
                descriptionText,
                snippet.render(),
                button
        );
    }

    private void createAppStage() {
        appStage = new AppStage();
        BorderPane borderPane = new BorderPane();
        borderPane.setMinWidth(600);
        borderPane.setMinHeight(500);

        appStage.setMinWidth(600);
        appStage.setMinHeight(500);
        appStage.setContentView(borderPane);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Parent getView() {
        return this;
    }

    @Override
    public boolean canDisplaySourceCode() {
        return true;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "util/" + getName()));
    }

    @Override
    public @Nullable Node getSnapshotTarget() {
        return null;
    }

    @Override
    public void reset() {

    }

    // this method isn't used anywhere
    @SuppressWarnings("unused")
    private void snippetText() {
        //snippet_1:start
        AppStage appStage = new AppStage();
        BorderPane borderPane = new BorderPane();
        borderPane.setMinWidth(600);
        borderPane.setMinHeight(500);

        appStage.setMinWidth(600);
        appStage.setMinHeight(500);
        appStage.setContentView(borderPane);
        appStage.show();
        //snippet_1:end
    }

}
