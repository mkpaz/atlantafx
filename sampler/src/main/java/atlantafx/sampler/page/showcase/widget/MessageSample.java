/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.widget;

import atlantafx.sampler.page.SampleBlock;
import atlantafx.sampler.theme.CSSFragment;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class MessageSample extends SampleBlock {

    public MessageSample() {
        super("Message", createContent());
    }

    private static Node createContent() {
        var content = new VBox(BLOCK_VGAP);
        content.setAlignment(Pos.TOP_CENTER);
        VBox.setVgrow(content, Priority.ALWAYS);
        new CSSFragment(Message.CSS).addTo(content);

        var closeHandler = new Consumer<Message>() {
            @Override
            public void accept(Message msg) {
                var newMsg = new Message(msg.getType(), msg.getHeader(), FAKER.chuckNorris().fact());
                newMsg.setCloseHandler(this);
                content.getChildren().add(newMsg);
            }
        };

        for (Message.Type type : Message.Type.values()) {
            var msg = new Message(type, type.name(), FAKER.chuckNorris().fact());
            msg.setCloseHandler(closeHandler);
            content.getChildren().add(msg);
        }

        return content;
    }
}
