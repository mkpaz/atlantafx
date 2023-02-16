/* SPDX-License-Identifier: MIT */

package atlantafx.sampler.theme;

public class HighlightJSTheme {

    private final String css;
    private final String background;
    private final String foreground;

    public HighlightJSTheme(String css, String background, String foreground) {
        this.css = css;
        this.background = background;
        this.foreground = foreground;
    }

    public String getCss() {
        return css;
    }

    public String getBackground() {
        return background;
    }

    public String getForeground() {
        return foreground;
    }

    public static HighlightJSTheme githubLight() {
        var css =
            ".hljs{color:#24292e;background:#fff}.hljs-doctag,.hljs-keyword,.hljs-meta .hljs-keyword,.hljs-template-tag,.hljs-template-variable,.hljs-type,.hljs-variable.language_{color:#d73a49}.hljs-title,.hljs-title.class_,.hljs-title.class_.inherited__,.hljs-title.function_{color:#6f42c1}.hljs-attr,.hljs-attribute,.hljs-literal,.hljs-meta,.hljs-number,.hljs-operator,.hljs-selector-attr,.hljs-selector-class,.hljs-selector-id,.hljs-variable{color:#005cc5}.hljs-meta .hljs-string,.hljs-regexp,.hljs-string{color:#032f62}.hljs-built_in,.hljs-symbol{color:#e36209}.hljs-code,.hljs-comment,.hljs-formula{color:#6a737d}.hljs-name,.hljs-quote,.hljs-selector-pseudo,.hljs-selector-tag{color:#22863a}.hljs-subst{color:#24292e}.hljs-section{color:#005cc5;font-weight:700}.hljs-bullet{color:#735c0f}.hljs-emphasis{color:#24292e;font-style:italic}.hljs-strong{color:#24292e;font-weight:700}.hljs-addition{color:#22863a;background-color:#f0fff4}.hljs-deletion{color:#b31d28;background-color:#ffeef0}";
        var bg = "#fff";
        var fg = "#24292f";
        return new HighlightJSTheme(css, bg, fg);
    }

    public static HighlightJSTheme githubDark() {
        var css =
            ".hljs{color:#c9d1d9;background:#0d1117}.hljs-doctag,.hljs-keyword,.hljs-meta .hljs-keyword,.hljs-template-tag,.hljs-template-variable,.hljs-type,.hljs-variable.language_{color:#ff7b72}.hljs-title,.hljs-title.class_,.hljs-title.class_.inherited__,.hljs-title.function_{color:#d2a8ff}.hljs-attr,.hljs-attribute,.hljs-literal,.hljs-meta,.hljs-number,.hljs-operator,.hljs-selector-attr,.hljs-selector-class,.hljs-selector-id,.hljs-variable{color:#79c0ff}.hljs-meta .hljs-string,.hljs-regexp,.hljs-string{color:#a5d6ff}.hljs-built_in,.hljs-symbol{color:#ffa657}.hljs-code,.hljs-comment,.hljs-formula{color:#8b949e}.hljs-name,.hljs-quote,.hljs-selector-pseudo,.hljs-selector-tag{color:#7ee787}.hljs-subst{color:#c9d1d9}.hljs-section{color:#1f6feb;font-weight:700}.hljs-bullet{color:#f2cc60}.hljs-emphasis{color:#c9d1d9;font-style:italic}.hljs-strong{color:#c9d1d9;font-weight:700}.hljs-addition{color:#aff5b4;background-color:#033a16}.hljs-deletion{color:#ffdcd7;background-color:#67060c}";
        var bg = "#0d1117";
        var fg = "#f0f6fc";
        return new HighlightJSTheme(css, bg, fg);
    }

    public static HighlightJSTheme nordLight() {
        // there's no Nord light theme,
        // below is "stackoverflow-light" theme with changed background
        var css =
            ".hljs{color:#2f3337;background:#f4f5f8}.hljs-subst{color:#2f3337}.hljs-comment{color:#656e77}.hljs-attr,.hljs-doctag,.hljs-keyword,.hljs-meta .hljs-keyword,.hljs-section,.hljs-selector-tag{color:#015692}.hljs-attribute{color:#803378}.hljs-name,.hljs-number,.hljs-quote,.hljs-selector-id,.hljs-template-tag,.hljs-type{color:#b75501}.hljs-selector-class{color:#015692}.hljs-link,.hljs-regexp,.hljs-selector-attr,.hljs-string,.hljs-symbol,.hljs-template-variable,.hljs-variable{color:#54790d}.hljs-meta,.hljs-selector-pseudo{color:#015692}.hljs-built_in,.hljs-literal,.hljs-title{color:#b75501}.hljs-bullet,.hljs-code{color:#535a60}.hljs-meta .hljs-string{color:#54790d}.hljs-deletion{color:#c02d2e}.hljs-addition{color:#2f6f44}.hljs-emphasis{font-style:italic}.hljs-strong{font-weight:700}";
        var bg = "#fafafc";
        var fg = "#2E3440";
        return new HighlightJSTheme(css, bg, fg);
    }

    public static HighlightJSTheme nordDark() {
        var css =
            ".hljs{display:block;overflow-x:auto;padding:1em}code.hljs{padding:3px 5px}.hljs{background:#2e3440}.hljs,.hljs-subst{color:#d8dee9}.hljs-selector-tag{color:#81a1c1}.hljs-selector-id{color:#8fbcbb;font-weight:700}.hljs-selector-attr,.hljs-selector-class{color:#8fbcbb}.hljs-property,.hljs-selector-pseudo{color:#88c0d0}.hljs-addition{background-color:rgba(163,190,140,.5)}.hljs-deletion{background-color:rgba(191,97,106,.5)}.hljs-built_in,.hljs-class,.hljs-type{color:#8fbcbb}.hljs-function,.hljs-function>.hljs-title,.hljs-title.hljs-function{color:#88c0d0}.hljs-keyword,.hljs-literal,.hljs-symbol{color:#81a1c1}.hljs-number{color:#b48ead}.hljs-regexp{color:#ebcb8b}.hljs-string{color:#a3be8c}.hljs-title{color:#8fbcbb}.hljs-params{color:#d8dee9}.hljs-bullet{color:#81a1c1}.hljs-code{color:#8fbcbb}.hljs-emphasis{font-style:italic}.hljs-formula{color:#8fbcbb}.hljs-strong{font-weight:700}.hljs-link:hover{text-decoration:underline}.hljs-comment,.hljs-quote{color:#4c566a}.hljs-doctag{color:#8fbcbb}.hljs-meta,.hljs-meta .hljs-keyword{color:#5e81ac}.hljs-meta .hljs-string{color:#a3be8c}.hljs-attr{color:#8fbcbb}.hljs-attribute{color:#d8dee9}.hljs-name{color:#81a1c1}.hljs-section{color:#88c0d0}.hljs-tag{color:#81a1c1}.hljs-template-variable,.hljs-variable{color:#d8dee9}.hljs-template-tag{color:#5e81ac}.language-abnf .hljs-attribute{color:#88c0d0}.language-abnf .hljs-symbol{color:#ebcb8b}.language-apache .hljs-attribute{color:#88c0d0}.language-apache .hljs-section{color:#81a1c1}.language-arduino .hljs-built_in{color:#88c0d0}.language-aspectj .hljs-meta{color:#d08770}.language-aspectj>.hljs-title{color:#88c0d0}.language-bnf .hljs-attribute{color:#8fbcbb}.language-clojure .hljs-name{color:#88c0d0}.language-clojure .hljs-symbol{color:#ebcb8b}.language-coq .hljs-built_in{color:#88c0d0}.language-cpp .hljs-meta .hljs-string{color:#8fbcbb}.language-css .hljs-built_in{color:#88c0d0}.language-css .hljs-keyword{color:#d08770}.language-diff .hljs-meta,.language-ebnf .hljs-attribute{color:#8fbcbb}.language-glsl .hljs-built_in{color:#88c0d0}.language-groovy .hljs-meta:not(:first-child),.language-haxe .hljs-meta,.language-java .hljs-meta{color:#d08770}.language-ldif .hljs-attribute{color:#8fbcbb}.language-lisp .hljs-name,.language-lua .hljs-built_in,.language-moonscript .hljs-built_in,.language-nginx .hljs-attribute{color:#88c0d0}.language-nginx .hljs-section{color:#5e81ac}.language-pf .hljs-built_in,.language-processing .hljs-built_in{color:#88c0d0}.language-scss .hljs-keyword,.language-stylus .hljs-keyword{color:#81a1c1}.language-swift .hljs-meta{color:#d08770}.language-vim .hljs-built_in{color:#88c0d0;font-style:italic}.language-yaml .hljs-meta{color:#d08770}";
        var bg = "#2E3440";
        var fg = "#ECEFF4";
        return new HighlightJSTheme(css, bg, fg);
    }

    public static HighlightJSTheme dracula() {
        var css =
            "pre code.hljs{display:block;overflow-x:auto;padding:1em}code.hljs{padding:3px 5px}.hljs{color:#e9e9f4;background:#282936}.hljs ::selection,.hljs::selection{background-color:#4d4f68;color:#e9e9f4}.hljs-comment{color:#626483}.hljs-tag{color:#62d6e8}.hljs-operator,.hljs-punctuation,.hljs-subst{color:#e9e9f4}.hljs-operator{opacity:.7}.hljs-bullet,.hljs-deletion,.hljs-name,.hljs-selector-tag,.hljs-template-variable,.hljs-variable{color:#ea51b2}.hljs-attr,.hljs-link,.hljs-literal,.hljs-number,.hljs-symbol,.hljs-variable.constant_{color:#b45bcf}.hljs-class .hljs-title,.hljs-title,.hljs-title.class_{color:#00f769}.hljs-strong{font-weight:700;color:#00f769}.hljs-addition,.hljs-code,.hljs-string,.hljs-title.class_.inherited__{color:#ebff87}.hljs-built_in,.hljs-doctag,.hljs-keyword.hljs-atrule,.hljs-quote,.hljs-regexp{color:#a1efe4}.hljs-attribute,.hljs-function .hljs-title,.hljs-section,.hljs-title.function_,.ruby .hljs-property{color:#62d6e8}.diff .hljs-meta,.hljs-keyword,.hljs-template-tag,.hljs-type{color:#b45bcf}.hljs-emphasis{color:#b45bcf;font-style:italic}.hljs-meta,.hljs-meta .hljs-keyword,.hljs-meta .hljs-string{color:#00f769}.hljs-meta .hljs-keyword,.hljs-meta-keyword{font-weight:700}";
        var bg = "#282a36";
        var fg = "#f8f8f2";
        return new HighlightJSTheme(css, bg, fg);
    }
}
