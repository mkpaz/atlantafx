package atlantafx.sampler.page.components;

import atlantafx.base.controls.Card;
import atlantafx.base.controls.Spacer;
import atlantafx.base.controls.Tile;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.util.function.Function;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2AL;
import org.kordamp.ikonli.material2.Material2MZ;

public class CardPage extends OutlinePage {

    public static final String NAME = "Card";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public CardPage() {
        super();

        addPageHeader();
        addFormattedText("""
            The [i]Card[/i] is a versatile container that can used in various contexts \
            such as headings, text, dialogs and more. It includes a header to provide a \
            brief overview or context of the information. The sub-header and body sections \
            provide more detailed content, while the footer may include additional actions \
            or information."""
        );
        addNode(skeleton());
        addSection("Usage", usageExample());
        addSection("Elevation", elevationExample());
    }

    private Node skeleton() {
        Function<String, VBox> cellBuilder = s -> {
            var lbl = new Label(s);
            lbl.getStyleClass().add(Styles.TEXT_SMALL);

            var cell = new VBox(lbl);
            cell.setPadding(new Insets(10));
            cell.setFillWidth(true);
            cell.setAlignment(Pos.CENTER);
            Styles.appendStyle(cell, "-fx-border-color", "-color-accent-muted");
            Styles.appendStyle(cell, "-fx-border-width", "5px");

            return cell;
        };

        var body = cellBuilder.apply("body");
        body.setPadding(new Insets(20, 10, 20, 10));

        var box = new VBox(10);
        box.setMaxWidth(500);
        box.getChildren().setAll(
            cellBuilder.apply("header"),
            cellBuilder.apply("sub-header"),
            body,
            cellBuilder.apply("footer")
        );

        return box;
    }

    private ExampleBox usageExample() {
        //snippet_1:start
        var tweetCard = new Card();
        tweetCard.setMinWidth(300);
        tweetCard.setMaxWidth(300);

        var title = new Label(FAKER.twitter().userName());
        title.getStyleClass().add(Styles.TITLE_4);
        tweetCard.setHeader(title);

        var text = new TextFlow(new Text(
            FAKER.lorem().sentence(20)
        ));
        text.setMaxWidth(260);
        tweetCard.setBody(text);

        var footer = new HBox(10,
            new FontIcon(Material2AL.FAVORITE),
            new Label("861"),
            new Spacer(20),
            new FontIcon(Material2MZ.SHARE),
            new Label("92")
        );
        footer.setAlignment(Pos.CENTER_LEFT);
        tweetCard.setFooter(footer);

        // ~
        var dialogCard = new Card();
        dialogCard.setHeader(new Tile(
            "Delete content",
            "Are you sure to remove this content? "
                + "You can access this file for 7 days in your trash."
        ));
        dialogCard.setBody(new CheckBox("Do not show it anymore"));

        var confirmBtn = new Button("Confirm");
        confirmBtn.setDefaultButton(true);
        confirmBtn.setPrefWidth(150);

        var cancelBtn = new Button("Cancel");
        cancelBtn.setPrefWidth(150);

        var dialogFooter = new HBox(20, confirmBtn, cancelBtn);
        dialogCard.setFooter(dialogFooter);
        //snippet_1:end

        var box = new HBox(HGAP_20, tweetCard, dialogCard);
        var description = BBCodeParser.createFormattedText("""
            The [i]Card[/i] pairs well with the [i]Tile[/i] component. \
            You can use the [i]Tile[/i] as either a header or body for the [i]Card[/i]. \
            It’s also suitable for building more complex dialogs as well."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private ExampleBox elevationExample() {
        //snippet_2:start
        var card1 = new Card();
        card1.getStyleClass().add(Styles.ELEVATED_2);
        card1.setMinWidth(250);
        card1.setMaxWidth(250);
        card1.setHeader(new Tile(
            "This is a title",
            "This is a subtitle"
        ));
        card1.setBody(new Label("This is content"));

        var card2 = new Card();
        card2.getStyleClass().add(Styles.INTERACTIVE);
        card2.setMinWidth(250);
        card2.setMaxWidth(250);
        card2.setHeader(new Tile(
            "This is a title",
            "This is a subtitle"
        ));
        card2.setBody(new Label("This is content"));
        //snippet_2:end

        var box = new HBox(HGAP_20, card1, card2);
        box.setPadding(new Insets(0, 0, 10, 0));

        var description = BBCodeParser.createFormattedText("""
            With [code]Styles.ELEVATED_N[/code] or [code]Styles.INTERACTIVE[/code] styles classes \
            you can add raised shadow effect to the [i]Card[/i]."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }
}
