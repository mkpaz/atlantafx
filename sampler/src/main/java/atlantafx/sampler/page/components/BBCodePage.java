/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.components;

import atlantafx.base.theme.Styles;
import atlantafx.base.util.BBCodeParser;
import atlantafx.sampler.page.AbstractPage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class BBCodePage extends AbstractPage {

    public static final String NAME = "BBCode Markup";

    @Override
    public String getName() {
        return NAME;
    }

    public BBCodePage() {
        super();

        setUserContent(new VBox(20,
            reference(),
            article()
        ));
    }

    private VBox reference() {
        var root = new VBox(20);
        root.setAlignment(Pos.TOP_LEFT);

        var header = BBCodeParser.createFormattedText("""
            [left][heading=1]Reference[/heading][/left]\
                        
            BBCode ("Bulletin Board Code") is a lightweight markup language used to format messages \
            in many Internet forum software. It was first introduced in 1998. The available tags of BBCode \
            are usually indicated by square brackets surrounding a keyword, and are parsed before \
            being translated into [s]HTML[/s] JavaFX layout.""");

        root.getChildren().add(header);

        ReferenceBlock block = new ReferenceBlock(
            "Bold, italics, underline and strikethrough",
            "Makes the wrapped text bold, italic, underlined, or strikethrough."
        );
        block.addFormattedText("This is [b]bold[/b] text.");
        block.addFormattedText("This is [i]italic[/i] text.");
        block.addFormattedText("This is [u]underlined[/u] text.");
        block.addFormattedText("This is [s]strikethrough[/s] text.");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Text color, font and size",
            "Changes the color, font, or size of the wrapped text."
        );
        block.addFormattedText("This is [color=red]red[/color] text.");
        block.addFormattedText("This is [color=-color-accent-emphasis]accent[/color] text.");
        block.addFormattedText("This is [label style='-fx-background-color: yellow']background[/label] color.");
        block.addFormattedText("This is [font=monospace]monospaced[/font] font.");
        block.addFormattedText("This is [small]small[/small] and [size=1.5em]big[/size] text.");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Subscript and superscript",
            "A text that is set slightly below or above the normal line of type, respectively."
        );
        block.addLayout("log[sub][small]2[/small][/sub](256) = 8");
        block.addLayout("10[sup][small]2[/small][/sup] = 100");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Headings levels 1 to 5",
            "Marks text as a structured heading."
        );
        block.addFormattedText("""
            [heading=1]H1 headline[/heading]
                            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non purus a nisi ornare facilisis.""");
        block.addFormattedText("""
            [heading=2]H2 headline[/heading]
                            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non purus a nisi ornare facilisis.""");
        block.addFormattedText("""
            [heading=3]H3 headline[/heading]
                            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non purus a nisi ornare facilisis.""");
        block.addFormattedText("""
            [heading=4]H4 headline[/heading]
                            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non purus a nisi ornare facilisis.""");
        block.addFormattedText("""
            [caption]Caption[/caption]
                            
            Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed non purus a nisi ornare facilisis.""");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Lists",
            "Displays a bulleted or numbered list."
        );
        block.addLayout("""
            [ul]
            [li]Entry 1[/li]
            [li]Entry 2[/li]
            [/ul]""");
        block.addLayout("""
            [ul=🗹]
            [li]Entry 1[/li]
            [li]Entry 2[/li]
            [/ul]""");
        block.addLayout("""
            [ol]
            [li]Entry 1[/li]
            [li]Entry 2[/li]
            [/ol]""");
        block.addLayout("""
            [ol=10]
            [li]Entry 1[/li]
            [li]Entry 2[/li]
            [/ol]""");
        block.addLayout("""
            [ol=a]
            [li]Entry 1[/li]
            [li]Entry 2[/li]
            [/ol]""");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Linking",
            "Links the wrapped text to the specified web page or email address."
        );
        block.addFormattedText("[url=https://www.example.com]Go to example.com[/url]");
        block.addFormattedText("[email=johndoe@example.com]Email me[/email]");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Alignment",
            "Changes the alignment of the wrapped text."
        );
        block.addLayout("[left]Left-aligned[/left]");
        block.addLayout("[center]Center-aligned[/center]");
        block.addLayout("[right]Right-aligned[/right]");
        block.addLayout("[align=center]Center-aligned[/align]");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Text indent",
            "Indents the wrapped text."
        );
        block.addLayout("[indent]Indented text[/indent]");
        block.addLayout("[indent=3]More indented[/indent]");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Horizontal line",
            "A horizontal separator line."
        );
        block.addLayout("Default line: [hr/]");
        block.addLayout("Thick line: [hr=5/]");
        root.getChildren().add(block);

        block = new ReferenceBlock(
            "Abbreviation",
            "An abbreviation, with mouse-over expansion."
        );
        block.addLayout("[abbr='on hover text']text[/abbr]");
        block.addLayout("[abbr]text[/abbr]");
        root.getChildren().add(block);

        return root;
    }

    private VBox article() {
        var article = """
            [left][heading=1]Example[/heading][/left]\

            [b]JavaFX[/b] is a Java library used to build Rich Internet Applications. \
            The applications written using this library can run consistently across multiple \
            platforms. The applications developed using JavaFX can run on various devices \
            such as Desktop Computers, Mobile Phones, TVs, Tablets, etc.

            To develop GUI Applications using [color=-color-accent-emphasis]Java programming language[/color], \
            the programmers rely on libraries such as [s]Advanced Windowing Toolkit[/s] and Swing. \
            After the advent of JavaFX, these Java programmers can now develop \
            [abbr='Graphical User Interface']GUI[/abbr] applications effectively with rich content.
             
            [heading=3]Key Features[/heading]

            Following are some of the [i]important features[/i] of JavaFX:\
            [ul]
            [li][b]Written in Java[/b] − The JavaFX library is written in Java and is [u]available for \
            the languages that can be executed on a JVM[/u], which include − Java, \
            [url="https://groovy-lang.org/"]Groovy[/url] and JRuby.These JavaFX applications are also \
            platform independent.[/li]\
            [li][b]FXML[/b] − JavaFX features a language known as FXML, which is a HTML like declarative \
            markup language. The sole purpose of this language is to define a user interface.[/li]\
            [li][b]Scene Builder[/b] − JavaFX provides an application named Scene Builder. On integrating \
            this application in IDE’s such [as Eclipse and NetBeans], the users can access a drag \
            and drop design interface, which is used to develop [code]FXML[/code] applications (just like \
            Swing Drag & Drop and DreamWeaver Applications).[/li]\
            [li][b]Built-in UI controls[/b] − JavaFX library caters UI controls using which we can develop a \
            full-featured application.[/li]\
            [li][b]CSS like Styling[/b] − JavaFX provides a CSS like styling. By using this, you can improve \
            the design of your application with a simple knowledge of CSS.[/li]\
            [li][b]Canvas and Printing[/b] − JavaFX provides [label style=-fx-background-color:yellow]Canvas[/label], \
            an immediate mode style of rendering API. Within the package [font=monospace]javafx.scene.canvas[/font] \
            it holds a set of classes for canvas, using which we can draw directly within an area of the \
            JavaFX scene. JavaFX also provides classes for Printing purposes in the package javafx.print.[/li]\
            [li][b]Graphics pipeline[/b] − JavaFX supports graphics based on the hardware-accelerated graphics \
            pipeline known as Prism. When used with a supported Graphic Card or GPU it offers smooth \
            graphics. In case the system does not support graphic card then prism defaults to the \
            software rendering stack.[/li]\
            [/ul]\
            [hr/]\
            [right]Source: [url=https://www.tutorialspoint.com/javafx/javafx_overview.htm]tutorialspoint.com\
            [/url][/right]""";

        return BBCodeParser.createLayout(article);
    }

    ///////////////////////////////////////////////////////////////////////////

    private static class ReferenceBlock extends VBox {

        private final VBox leftBox;
        private final VBox rightBox;

        public ReferenceBlock(String title, String description) {
            super();

            var titleLabel = new Label(title);
            titleLabel.getStyleClass().add(Styles.TITLE_4);

            leftBox = new VBox(15);
            leftBox.prefWidthProperty().bind(widthProperty().divide(2));
            leftBox.setPadding(new Insets(10));
            leftBox.setStyle("""
                -fx-font-family:monospace;\
                -fx-background-color:-color-bg-subtle;\
                -fx-border-width:1px;\
                -fx-border-color:-color-border-default"""
            );

            rightBox = new VBox(15);
            rightBox.prefWidthProperty().bind(widthProperty().divide(2));
            rightBox.setPadding(new Insets(10));
            rightBox.setStyle("""
                -fx-background-color:-color-bg-subtle;\
                -fx-border-width:1px;\
                -fx-border-color:-color-border-default"""
            );

            var splitBox = new HBox(leftBox, rightBox);
            splitBox.setSpacing(20);

            setSpacing(10);
            getChildren().addAll(titleLabel, new Label(description), splitBox);
        }

        public void addFormattedText(String markup) {
            leftBox.getChildren().add(new TextFlow(new Text(markup)));
            rightBox.getChildren().add(BBCodeParser.createFormattedText(markup));
        }

        public void addLayout(String markup) {
            leftBox.getChildren().add(new TextFlow(new Text(markup)));
            rightBox.getChildren().add(BBCodeParser.createLayout(markup));
        }
    }
}
