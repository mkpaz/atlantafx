/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import java.nio.file.Path;
import java.util.function.Consumer;
import javafx.scene.layout.Pane;

interface DirectoryView {

    Pane getView();

    FileList getFileList();

    void setDirectory(Path path);

    void setOnAction(Consumer<Path> handler);
}
