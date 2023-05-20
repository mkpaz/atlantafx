/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import atlantafx.sampler.Resources;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javafx.scene.image.Image;

final class FileIconRepository {

    public static final String IMAGE_DIRECTORY = "images/papirus/";
    public static final Image UNKNOWN_FILE = new Image(
        Resources.getResourceAsStream(IMAGE_DIRECTORY + "mimetypes/text-plain.png")
    );
    public static final Image FOLDER = new Image(
        Resources.getResourceAsStream(IMAGE_DIRECTORY + "places/folder-paleorange.png")
    );

    private final Map<String, Image> cache = new HashMap<>();
    private final Set<String> unknownMimeTypes = new HashSet<>();

    public Image getByMimeType(String mimeType) {
        if (mimeType == null || unknownMimeTypes.contains(mimeType)) {
            return UNKNOWN_FILE;
        }

        var cachedImage = cache.get(mimeType);
        if (cachedImage != null) {
            return cachedImage;
        }

        var fileName = mimeType.replaceAll("/", "-") + ".png";

        try {
            var image = new Image(Resources.getResourceAsStream(IMAGE_DIRECTORY + "mimetypes/" + fileName));
            cache.put(mimeType, image);
            return image;
        } catch (Exception e) {
            unknownMimeTypes.add(mimeType);
            return UNKNOWN_FILE;
        }
    }
}
