/* SPDX-License-Identifier: MIT */
package atlantafx.sampler.page.showcase.filemanager;

import org.kordamp.ikonli.Ikon;

import java.nio.file.Path;

record Bookmark(String title, Path path, Ikon icon) { }
