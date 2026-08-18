/* SPDX-License-Identifier: MIT */

import org.jspecify.annotations.NullMarked;

@NullMarked
module atlantafx.sampler {
    requires static org.jspecify;

    requires atlantafx.base;
    requires atlantafx.decorations;
    requires atlantafx.spins;

    requires javafx.graphics;
    requires javafx.controls;
    requires jfx.incubator.richtext;
    requires jfx.incubator.input;

    requires java.desktop;
    requires java.prefs;
    requires java.net.http;
    requires javafx.swing;
    requires javafx.media;
    requires javafx.web;
    requires javafx.fxml;
    requires jdk.zipfs;

    requires org.kordamp.ikonli.core;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.feather;
    requires org.kordamp.ikonli.material2;

    requires fr.brouillard.oss.cssfx;
    requires devtoolsfx.gui;
    requires datafaker;
    requires javafx.base;

    // note: CSSFX uses com.sun.nio.file.SensitivityWatchEventModifier
    //       which is marked for removal since JDK 25
    requires jdk.unsupported;

    exports atlantafx.sampler;
    exports atlantafx.sampler.fake.domain;
    exports atlantafx.sampler.event;
    exports atlantafx.sampler.layout;
    exports atlantafx.sampler.page;
    exports atlantafx.sampler.page.general;
    exports atlantafx.sampler.page.components;
    exports atlantafx.sampler.page.showcase;
    exports atlantafx.sampler.theme;
    exports atlantafx.sampler.util;

    opens atlantafx.sampler.fake.domain;

    // resources
    opens atlantafx.sampler;
    opens atlantafx.sampler.assets.highlightjs;
    opens atlantafx.sampler.assets.styles;
    opens atlantafx.sampler.images;
    opens atlantafx.sampler.images.modena;
    opens atlantafx.sampler.media;
    opens atlantafx.sampler.page.general;
    opens atlantafx.sampler.page.showcase;
    opens atlantafx.sampler.page.components;
}
