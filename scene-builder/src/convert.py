#!/usr/bin/env python3

from pathlib import Path
import cairosvg

INPUT_DIR = Path("./main/resources/atlantafx/scenebuilder/plugin/icons/svg/")
OUTPUT_DIR = Path("./main/resources/atlantafx/scenebuilder/plugin/icons/")
NEW_COLOR = "#1a7f37"
PNG_SIZE = (16, 16)

OUTPUT_DIR.mkdir(exist_ok=True)

def recolor_svg(svg_path: Path) -> str:
    content = svg_path.read_text(encoding="utf-8")
    return content.replace('<svg ', f'<svg fill="{NEW_COLOR}" ')

def main():
    svg_files = list(INPUT_DIR.glob("*.svg"))
    print(f"Found {len(svg_files)} SVG file(s). Processing...")

    for file_path in svg_files:
        png_path = OUTPUT_DIR / f"{file_path.stem}.png"
        modified_svg = recolor_svg(file_path)

        cairosvg.svg2png(
            bytestring=modified_svg.encode("utf-8"),
            write_to=str(png_path),
            output_width=PNG_SIZE[0],
            output_height=PNG_SIZE[1]
        )
        print(f"Converted: {file_path.name} -> {png_path.name}")

    print("Done.")

if __name__ == "__main__":
    main()
