package atlantafx.sampler.page.components;

import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.controls.ToggleSwitch;
import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.Resources;
import atlantafx.base.controls.Tile;
import atlantafx.sampler.page.ExampleBox;
import atlantafx.sampler.page.OutlinePage;
import atlantafx.sampler.page.Snippet;
import java.net.URI;
import java.util.function.BiFunction;
import javafx.collections.FXCollections;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.Spinner;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.material2.Material2OutlinedAL;

public class TilePage extends OutlinePage {

    public static final String NAME = "Tile";

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public URI getJavadocUri() {
        return URI.create(String.format(AFX_JAVADOC_URI_TEMPLATE, "controls/" + getName()));
    }

    public TilePage() {
        super();

        addPageHeader();
        addFormattedText("""
            The Tile is a versatile container that can used in various contexts \
            such as dialog headers, list items, and cards. It can contain a graphic, \
            a title, subtitle, and optional actions."""
        );
        addNode(skeleton());
        addSection("Usage", usageExample());
        addSection("Interactive", interactiveExample());
        addSection("Stacking", stackingExample());
    }

    private Node skeleton() {
        BiFunction<String, Pos, Node> cellBuilder = (s, pos) -> {
            var cell = new VBox(new Label(s));
            cell.setPadding(new Insets(10));
            cell.setFillWidth(true);
            cell.setAlignment(pos);
            Styles.appendStyle(cell, "-fx-border-color", "-color-accent-muted");
            Styles.appendStyle(cell, "-fx-border-width", "5px");
            return cell;
        };

        var grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setMaxWidth(600);
        grid.add(cellBuilder.apply("Graphic", Pos.CENTER), 0, 0, 1, GridPane.REMAINING);
        grid.add(cellBuilder.apply("Title", Pos.CENTER_LEFT), 1, 0, 1, 1);
        grid.add(cellBuilder.apply("Subtitle", Pos.CENTER_LEFT), 1, 1, 1, 1);
        grid.add(cellBuilder.apply("Action", Pos.CENTER), 2, 0, 1, GridPane.REMAINING);
        grid.getColumnConstraints().setAll(
            new ColumnConstraints(-1, -1, -1, Priority.NEVER, HPos.CENTER, true),
            new ColumnConstraints(-1, -1, -1, Priority.ALWAYS, HPos.CENTER, true),
            new ColumnConstraints(-1, -1, -1, Priority.NEVER, HPos.CENTER, true)
        );

        return grid;
    }

    private Node usageExample() {
        //snippet_1:start
        var tile1 = new Tile(
            "Title",
            FAKER.lorem().sentence(15)
        );

        var tile2 = new Tile(
            FAKER.name().fullName(),
            FAKER.elderScrolls().quote()
        );

        var img = new ImageView(new Image(
            Resources.getResourceAsStream(
                "assets/fxml/blueprints/resources/avatar1.png"
            )
        ));
        img.setFitWidth(64);
        img.setFitHeight(64);
        tile2.setGraphic(img);

        var tile3 = new Tile("Photos", "Last updated: Jun 9, 2019");
        var btn = new Button("", new FontIcon(Material2OutlinedAL.DELETE));
        btn.getStyleClass().addAll(Styles.BUTTON_CIRCLE, Styles.FLAT);
        tile3.setAction(btn);
        //snippet_1:end

        var box = new VBox(
            tile1, new Separator(),
            tile2, new Separator(),
            tile3
        );
        var description = BBCodeParser.createFormattedText("""
            [i]Tile[/i] does not have any mandatory properties, but you will not want \
            to use it without a title. Additionally, note that only the subtitle supports \
            text wrapping."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 1), description);
    }

    private Node interactiveExample() {
        //snippet_2:start
        var tile = new Tile(
            "Password",
            "Please enter your authentication password to unlock the content"
        );

        var tf = new PasswordTextField(null);
        tf.setPromptText("Click on the tile");
        tf.setPrefWidth(150);

        tile.setAction(tf);
        tile.setActionHandler(tf::requestFocus);
        //snippet_2:end

        var box = new VBox(tile);
        var description = BBCodeParser.createFormattedText("""
            A [i]Tile[/i] can be made interactive by setting an action handler that may \
            or may not be related to the action slot."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 2), description);
    }

    private Node stackingExample() {
        //snippet_3:start
        var tile1 = new Tile(
            "Content filtering",
            "Set the content filtering level to restrict downloaded apps"
        );
        var cmb = new ComboBox<>(FXCollections.observableArrayList(
            "Everyone", "Low", "Medium", "High"
        ));
        cmb.getSelectionModel().selectLast();
        cmb.setPrefWidth(150);
        tile1.setAction(cmb);

        var tile2 = new Tile(
            "Password",
            "Require password for purchase"
        );
        var tgl2 = new ToggleSwitch();
        tile2.setAction(tgl2);
        tile2.setActionHandler(() -> tgl2.setSelected(!tgl2.isSelected()));

        var tile3 = new Tile("Cache Size (Mb)", null);
        var spinner = new Spinner<>(10, 100, 50);
        spinner.setPrefWidth(150);
        tile3.setAction(spinner);

        var tile4 = new Tile(
            "Notifications",
            "Notify me about updates to apps that I downloaded"
        );
        var tgl3 = new ToggleSwitch();
        tile4.setAction(tgl3);
        tile4.setActionHandler(() -> tgl3.setSelected(!tgl3.isSelected()));

        var box = new VBox(tile1, tile2, tile3, new Separator(), tile4);
        //snippet_3:end

        var description = BBCodeParser.createFormattedText("""
            You can stack several [i]Tiles[/i] vertically. Optionally, use the [i]Separator[/i] \
            to split them into groups."""
        );

        return new ExampleBox(box, new Snippet(getClass(), 3), description);
    }
}
