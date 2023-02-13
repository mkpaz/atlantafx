/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.page.showcase.filemanager;

import java.nio.file.Path;
import org.kordamp.ikonli.Ikon;

record Bookmark(String title, Path path, Ikon icon) {
}
