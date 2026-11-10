# Colors

> Part of the [EnduranceTrio Style Guide](./index.md).

The EnduranceTrio color system is built on four Material-Design-inspired palettes that extend
the three brand colors from the logo plus a supporting semantic green.

The system integrates directly with the [Bulma CSS framework](https://bulma.io) by mapping
its palette tokens onto Bulma's semantic color variables. This ensures that every Bulma
component - buttons, notifications, tags, messages, navbar, etc. - inherits the EnduranceTrio brand
consistently without custom CSS overrides.

**Goals of the EnduranceTrio Color System:**

- Strong brand recognition through consistent use of the logo colors
- Semantic clarity rooted in endurance-sport metaphors
- Readability and WCAG AA contrast wherever possible
- Clean Bulma integration using only its variable system

## Table of Contents

1. [Brand Foundations](#brand-foundations)
2. [Supporting Semantic Color - Green](#supporting-semantic-color---green)
3. [Material Design Palettes](#material-design-palettes)
4. [Grey Neutral Palette](#grey-neutral-palette)
5. [Grey Background Helpers](#grey-background-helpers)
6. [Bulma Color System Customization](#bulma-color-system-customization)
7. [Color Usage Recommendations](#color-usage-recommendations)
8. [Accessibility Guidance](#accessibility-guidance)
9. [Palette Swatch Reference](#palette-swatch-reference)
10. [GIMP and Inkscape Palette File](#gimp-and-inkscape-palette-file)

## Brand Foundations

The three logo colors define the EnduranceTrio visual identity. Each has a **primary** (the logo
color itself) and a **complementary** (a deeper variant used for text on light backgrounds, hover
states, and emphasis).

| Color  | Primary HEX | Primary HSL          | Complementary HEX | Complementary HSL     | Role                                          |
|--------|-------------|----------------------|-------------------|-----------------------|-----------------------------------------------|
| Blue   | `#41B6E6`   | `hsl(197, 77%, 58%)` | `#0077C8`         | `hsl(204, 100%, 39%)` | Trust, stability, navigation, primary actions |
| Yellow | `#FEDB00`   | `hsl(52, 100%, 50%)` | `#CC8A00`         | `hsl(41, 100%, 40%)`  | Energy, highlights, performance indicators    |
| Red    | `#F9423A`   | `hsl(3, 94%, 60%)`   | `#AF272F`         | `hsl(356, 64%, 42%)`  | Intensity, urgency, destructive actions       |

### Usage

**Blue** is the dominant interface color. It is used for the main brand, navigation, primary
buttons, links, and informational UI. The complementary blue provides sufficient contrast for text
and interactive states.

**Yellow** is an accent color. It should be used sparingly for badges, highlights, charts
and achievement indicators. Avoid large yellow surfaces — they reduce readability and create
accessibility issues.

**Red** is reserved for emphasis. It signals destructive actions, errors, alerts, and urgent
interactions. Red should not dominate the interface; its power comes from strategic, sparing use.

## Supporting Semantic Color - Green

Green is not part of the EnduranceTrio logo but is introduced as the semantic success color
for Bulma's `$success` variable.

| Color | Primary HEX | Primary HSL          | Complementary HEX | Complementary HSL     | Role                                   |
|-------|-------------|----------------------|-------------------|-----------------------|----------------------------------------|
| Green | `#3CDBC0`   | `hsl(170, 69%, 55%)` | `#00B388`         | `hsl(166, 100%, 35%)` | Success, completion, positive feedback |

### Usage

Green communicates successful outcomes and positive progress. It is used for success messages,
confirmation buttons, completion indicators, and healthy metrics. It should remain semantically
consistent and not be repurposed for other meanings.

## Material Design Palettes

Each color family follows a 10-step Material Design shade scale (`50`–`900`). Shade `500` is the
primary anchor (the logo primary or the green primary). Shade `700` is the complementary anchor.

Palette generation follows the tint/shade math used by
[`maketintsandshades.com`](https://maketintsandshades.com/):

- tints move each RGB channel toward white: `value + ((255 - value) * factor)`
- shades move each RGB channel toward black: `value * (1 - factor)`
- values are rounded to the nearest whole number

For each palette, the shade factor is fitted so that two repeated shade steps from `500` get as
close as possible to the supplied `700` anchor. That fitted factor is then used consistently
to derive `600`, all lighter tints (`400`→`050`), and all darker shades (`800`→`900`).

### HSL Palette Reference

| Shade   | Blue                  | Yellow               | Red                  | Green                 | Shade   |
|---------|-----------------------|----------------------|----------------------|-----------------------|---------|
| 050     | `hsl(197, 77%, 80%)`  | `hsl(52, 98%, 77%)`  | `hsl(2, 93%, 83%)`   | `hsl(170, 69%, 80%)`  | 050     |
| 100     | `hsl(198, 77%, 76%)`  | `hsl(52, 99%, 73%)`  | `hsl(2, 94%, 80%)`   | `hsl(170, 69%, 76%)`  | 100     |
| 200     | `hsl(197, 77%, 73%)`  | `hsl(52, 99%, 69%)`  | `hsl(2, 95%, 76%)`   | `hsl(170, 69%, 72%)`  | 200     |
| 300     | `hsl(197, 76%, 68%)`  | `hsl(52, 99%, 64%)`  | `hsl(2, 94%, 72%)`   | `hsl(170, 69%, 67%)`  | 300     |
| 400     | `hsl(197, 76%, 64%)`  | `hsl(52, 99%, 57%)`  | `hsl(2, 94%, 67%)`   | `hsl(170, 69%, 61%)`  | 400     |
| **500** | `hsl(197, 77%, 58%)`  | `hsl(52, 100%, 50%)` | `hsl(3, 94%, 60%)`   | `hsl(170, 69%, 55%)`  | **500** |
| 600     | `hsl(198, 56%, 50%)`  | `hsl(52, 100%, 42%)` | `hsl(2, 63%, 51%)`   | `hsl(170, 57%, 47%)`  | 600     |
| **700** | `hsl(204, 100%, 39%)` | `hsl(41, 100%, 40%)` | `hsl(356, 64%, 42%)` | `hsl(166, 100%, 35%)` | **700** |
| 800     | `hsl(204, 100%, 34%)` | `hsl(41, 100%, 34%)` | `hsl(357, 63%, 35%)` | `hsl(165, 100%, 30%)` | 800     |
| 900     | `hsl(204, 100%, 29%)` | `hsl(41, 100%, 29%)` | `hsl(357, 63%, 30%)` | `hsl(166, 100%, 25%)` | 900     |

### HEX Palette Reference

| Shade   | Blue                                           | HEX       | Yellow                                         | HEX       | Red                                            | HEX       | Green                                          | HEX       | Shade   |
|---------|------------------------------------------------|-----------|------------------------------------------------|-----------|------------------------------------------------|-----------|------------------------------------------------|-----------|---------|
| 050     | ![#A4DCF3](./assets/color-swatches/a4dcf3.svg) | `#A4DCF3` | ![#FEEF8C](./assets/color-swatches/feef8c.svg) | `#FEEF8C` | ![#FCAFAD](./assets/color-swatches/fcafad.svg) | `#FCAFAD` | ![#A7EFE3](./assets/color-swatches/a7efe3.svg) | `#A7EFE3` | 050     |
| 100     | ![#95D6F1](./assets/color-swatches/95d6f1.svg) | `#95D6F1` | ![#FEEC78](./assets/color-swatches/feec78.svg) | `#FEEC78` | ![#FCA09D](./assets/color-swatches/fca09d.svg) | `#FCA09D` | ![#98ECDE](./assets/color-swatches/98ecde.svg) | `#98ECDE` | 100     |
| 200     | ![#84D0EF](./assets/color-swatches/84d0ef.svg) | `#84D0EF` | ![#FEE961](./assets/color-swatches/fee961.svg) | `#FEE961` | ![#FC8E8A](./assets/color-swatches/fc8e8a.svg) | `#FC8E8A` | ![#86E9D8](./assets/color-swatches/86e9d8.svg) | `#86E9D8` | 200     |
| 300     | ![#71C9EC](./assets/color-swatches/71c9ec.svg) | `#71C9EC` | ![#FEE546](./assets/color-swatches/fee546.svg) | `#FEE546` | ![#FB7974](./assets/color-swatches/fb7974.svg) | `#FB7974` | ![#71E5D1](./assets/color-swatches/71e5d1.svg) | `#71E5D1` | 300     |
| 400     | ![#5BC0E9](./assets/color-swatches/5bc0e9.svg) | `#5BC0E9` | ![#FEE026](./assets/color-swatches/fee026.svg) | `#FEE026` | ![#FA605A](./assets/color-swatches/fa605a.svg) | `#FA605A` | ![#59E0C9](./assets/color-swatches/59e0c9.svg) | `#59E0C9` | 400     |
| **500** | ![#41B6E6](./assets/color-swatches/41b6e6.svg) | `#41B6E6` | ![#FEDB00](./assets/color-swatches/fedb00.svg) | `#FEDB00` | ![#F9423A](./assets/color-swatches/f9423a.svg) | `#F9423A` | ![#3CDBC0](./assets/color-swatches/3cdbc0.svg) | `#3CDBC0` | **500** |
| 600     | ![#389DC7](./assets/color-swatches/389dc7.svg) | `#389DC7` | ![#D8BA00](./assets/color-swatches/d8ba00.svg) | `#D8BA00` | ![#D13731](./assets/color-swatches/d13731.svg) | `#D13731` | ![#33BBA4](./assets/color-swatches/33bba4.svg) | `#33BBA4` | 600     |
| **700** | ![#0077C8](./assets/color-swatches/0077c8.svg) | `#0077C8` | ![#CC8A00](./assets/color-swatches/cc8a00.svg) | `#CC8A00` | ![#AF272F](./assets/color-swatches/af272f.svg) | `#AF272F` | ![#00B388](./assets/color-swatches/00b388.svg) | `#00B388` | **700** |
| 800     | ![#0067AD](./assets/color-swatches/0067ad.svg) | `#0067AD` | ![#AE7600](./assets/color-swatches/ae7600.svg) | `#AE7600` | ![#932127](./assets/color-swatches/932127.svg) | `#932127` | ![#009974](./assets/color-swatches/009974.svg) | `#009974` | 800     |
| 900     | ![#005995](./assets/color-swatches/005995.svg) | `#005995` | ![#946400](./assets/color-swatches/946400.svg) | `#946400` | ![#7B1C21](./assets/color-swatches/7b1c21.svg) | `#7B1C21` | ![#008263](./assets/color-swatches/008263.svg) | `#008263` | 900     |

## Grey Neutral Palette

A 10-step neutral grey palette following the same 050–900 scale as the brand colors. Unlike the
hue-based palettes above, all grey shades share a single hue and saturation — the same values
Bulma uses for its built-in grey scale — varying only in lightness.

| Shade | Grey                                           | HEX       | HSL                  | Lightness | Anchored To   |
|-------|------------------------------------------------|-----------|----------------------|-----------|---------------|
| 050   | ![#F6F7F8](./assets/color-swatches/f6f7f8.svg) | `#F6F7F8` | `hsl(221, 14%, 97%)` | 97%       | —             |
| 100   | ![#F0F2F4](./assets/color-swatches/f0f2f4.svg) | `#F0F2F4` | `hsl(221, 14%, 95%)` | 95%       | ~white-ter    |
| 200   | ![#D6D9E0](./assets/color-swatches/d6d9e0.svg) | `#D6D9E0` | `hsl(221, 14%, 86%)` | 86%       | =grey-lighter |
| 300   | ![#B3B9C6](./assets/color-swatches/b3b9c6.svg) | `#B3B9C6` | `hsl(221, 14%, 74%)` | 74%       | interpolated  |
| 400   | ![#9199AC](./assets/color-swatches/9199ac.svg) | `#9199AC` | `hsl(221, 14%, 62%)` | 62%       | interpolated  |
| 500   | ![#69748C](./assets/color-swatches/69748c.svg) | `#69748C` | `hsl(221, 14%, 48%)` | 48%       | =grey         |
| 600   | ![#4F5769](./assets/color-swatches/4f5769.svg) | `#4F5769` | `hsl(221, 14%, 36%)` | 36%       | interpolated  |
| 700   | ![#373C49](./assets/color-swatches/373c49.svg) | `#373C49` | `hsl(221, 14%, 25%)` | 25%       | interpolated  |
| 800   | ![#1F2229](./assets/color-swatches/1f2229.svg) | `#1F2229` | `hsl(221, 14%, 14%)` | 14%       | =black-ter    |
| 900   | ![#14161A](./assets/color-swatches/14161a.svg) | `#14161A` | `hsl(221, 14%,  9%)` | 9%        | =black-bis    |

The SCSS variables are declared as `$et-grey-{050…900}` in `endurancetrio-variables.scss`.

## Grey Background Helpers

Ten utility classes are generated from the grey palette, each setting both a background colour
and an automatically inverted text color so the content remains readable without extra markup.

| Class                          | Background                     | Text Colour                    | Links                          | Link Hover                     |
|--------------------------------|--------------------------------|--------------------------------|--------------------------------|--------------------------------|
| `.has-background-grey-050`     | `hsl(221, 14%, 97%)`           | `$et-grey-900`                 | `$et-blue-700`                 | `$et-blue-800`                 |
| `.has-background-grey-100`     | `hsl(221, 14%, 95%)`           | `$et-grey-900`                 | `$et-blue-700`                 | `$et-blue-800`                 |
| `.has-background-grey-200`     | `hsl(221, 14%, 86%)`           | `$et-grey-900`                 | `$et-blue-700`                 | `$et-blue-800`                 |
| `.has-background-grey-300`     | `hsl(221, 14%, 74%)`           | `$et-grey-900`                 | `$et-blue-700`                 | `$et-blue-800`                 |
| `.has-background-grey-400`     | `hsl(221, 14%, 62%)`           | `$et-grey-900`                 | `$et-blue-700`                 | `$et-blue-800`                 |
| `.has-background-grey-500`     | `hsl(221, 14%, 48%)`           | `$et-grey-050`                 | `$et-blue-300`                 | `$et-blue-200`                 |
| `.has-background-grey-600`     | `hsl(221, 14%, 36%)`           | `$et-grey-050`                 | `$et-blue-300`                 | `$et-blue-200`                 |
| `.has-background-grey-700`     | `hsl(221, 14%, 25%)`           | `$et-grey-050`                 | `$et-blue-300`                 | `$et-blue-200`                 |
| `.has-background-grey-800`     | `hsl(221, 14%, 14%)`           | `$et-grey-050`                 | `$et-blue-300`                 | `$et-blue-200`                 |
| `.has-background-grey-900`     | `hsl(221, 14%,  9%)`           | `$et-grey-050`                 | `$et-blue-300`                 | `$et-blue-200`                 |

The invert threshold follows Bulma's luminance-based logic (≈55 % lightness). Backgrounds lighter
than the threshold — 050 through 400 — get dark text and dark blue links (`$et-blue-700`).
Backgrounds darker than or equal to the threshold — 500 through 900 — get light text and light
blue links (`$et-blue-300`).

Link hover moves one step darker on light backgrounds (`$et-blue-800`) and one step lighter on
dark backgrounds (`$et-blue-200`). The `a:not(.button):not(.navbar-item)` selector prevents the
helpers from overriding Bulma's own button and navbar link styles.

The classes set CSS custom properties (`--bulma-text`, `--bulma-text-strong`, `--bulma-link-text`)
so that Bulma components such as `.title` and `.subtitle` inside these backgrounds automatically
inherit the correct color via Bulma's own variable cascade.

These are global helpers, so they keep Bulma's `has-` prefix and take no `endurance-` prefix. See
[Naming](./naming.md) for the helper rules.

## Bulma Color System Customization

Bulma's neutral shades are supplemented by the grey palette above. The four brand palettes replace
Bulma's built-in color variables as follows:

| Bulma Variable | Color Token   | HEX       | HSL                   |
|----------------|---------------|-----------|-----------------------|
| `$text`        | Bulma default | `#4a4a4a` | `hsl(0, 0%, 29%)`     |
| `$link`        | Blue 700      | `#0077C8` | `hsl(204, 100%, 39%)` |
| `$primary`     | Blue 500      | `#41B6E6` | `hsl(197, 77%, 58%)`  |
| `$info`        | Blue 500      | `#41B6E6` | `hsl(197, 77%, 58%)`  |
| `$success`     | Green 500     | `#3CDBC0` | `hsl(170, 69%, 55%)`  |
| `$warning`     | Yellow 500    | `#FEDB00` | `hsl(52, 100%, 50%)`  |
| `$danger`      | Red 500       | `#F9423A` | `hsl(3, 94%, 60%)`    |

The configuration above represents the target configuration. The SCSS implementation in `style.scss`
maps Bulma's variables onto the palette tokens declared in `endurancetrio-variables.scss`.

## Color Usage Recommendations

### Color Selection Rules

Apply these rules in order whenever a developer needs to choose a color for a new component
or feature.

1. Use a semantic Bulma token when the element communicates meaning.
2. Use Blue when the element communicates the default brand action or default interactive behavior.
3. Use Yellow only when the goal is to attract attention without implying failure or destruction.
4. Never substitute a different color family for aesthetic variation if the UI state already
   has a semantic meaning.

Typical decision order:

- Destructive or error state -> `$danger`
- Warning or caution state -> `$warning`
- Success or completion state -> `$success`
- Informational/default action state -> `$primary` or `$info`
- Decorative highlight with no semantic meaning -> Yellow accent shades

### Semantic Role Recommendations

| Role           | Bulma Token       | Preferred Color | Use For                                                                                           | Avoid Using For                                       |
|----------------|-------------------|-----------------|---------------------------------------------------------------------------------------------------|-------------------------------------------------------|
| Default action | `$primary`        | Blue 500        | Primary buttons, selected navigation, default CTA, active tabs, key action links                  | Destructive actions, warnings, completion states      |
| Informational  | `$info`           | Blue 500        | Info messages, passive status banners, help callouts, informational tags                          | Success confirmation, destructive feedback            |
| Link text      | `$link`           | Blue 700        | Inline links, text-level navigation, secondary actions in content                                 | Warnings, destructive buttons, decorative accents     |
| Success        | `$success`        | Green 500       | Success toasts, valid states, completion markers, positive metrics                                | Generic CTAs, warnings, informational notices         |
| Warning        | `$warning`        | Yellow 500      | Caution notices, warning surfaces, warning badges, attention markers, non-destructive risk states | Primary CTA fills, body text, navigation bars         |
| Danger         | `$danger`         | Red 500         | Destructive buttons, error banners, validation failures, irreversible operations                  | Decorative accents, default actions, neutral emphasis |
| Highlight      | No semantic token | Yellow 300–500  | Featured metrics, achievements, promotional callouts, non-semantic emphasis                       | Critical warnings, long text blocks, large surfaces   |

Priority rules:

- If an action is destructive, Danger wins even if the action is also primary.
- If a state is successful, Success wins over brand/default Blue.
- If a notice is cautionary but non-destructive, Warning wins over Info.
- If no semantic meaning exists, use Blue for interaction and Yellow for accent.

Warning shade guidance:

- Use Yellow 500 as the default warning surface and semantic warning color in Bulma.
- Use Yellow 700 when warning styling needs stronger edge definition or stronger emphasis without switching to Red.
- Prefer Yellow 700 for warning borders, warning icons, emphasized labels, and compact warning badges on light backgrounds.
- Prefer Yellow 500 for filled warning notifications, banners, overlays, and other larger warning surfaces.

### Component Recommendations

| Component           | Preferred Color Use                | Notes                                                                                                                    |
|---------------------|------------------------------------|--------------------------------------------------------------------------------------------------------------------------|
| Primary button      | Blue 500 fill                      | Use for the main action in a view. Hover/focus can move darker within the Blue palette.                                  |
| Secondary text link | Blue 700 text                      | Keeps links readable in content areas and distinguishes them from buttons.                                               |
| Destructive button  | Red 500 fill                       | Use for delete, revoke, reset, or irreversible actions. Do not style destructive actions as Blue.                        |
| Success button      | Green 500 fill                     | Reserve for confirm/save/complete actions when the success meaning is explicit.                                          |
| Warning notice      | Yellow 500 fill, Yellow 700 accent | Use Yellow 500 for the main warning surface. Use Yellow 700 for borders, icons, or emphasized labels inside the warning. |
| Error notice        | Red 500 or Red 700                 | Use Red 500 for emphasis and Red 700 when stronger contrast is needed.                                                   |
| Success notice      | Green 500 or Green 700             | Use Green 700 when white foreground text is required.                                                                    |
| Info notice         | Blue 500 or Blue 700               | Keep informational messaging aligned with the core brand family.                                                         |
| Tag or badge        | Semantic token first               | Use semantic color when the badge conveys status; use Yellow only for non-semantic highlights.                           |
| Navbar active state | Blue 500 / Blue 700                | Preserve Blue as the default navigation family. Do not use Red or Green for standard navigation states.                  |
| Form invalid state  | Red 500                            | Use on field borders, helper text, and error icons consistently.                                                         |
| Form valid state    | Green 500                          | Use for confirmation feedback only after validation succeeds.                                                            |
| Data visualization  | Blue, Yellow, Red, Green           | Use Blue as the baseline series; use Yellow for emphasis, Red for negative/critical values, Green for positive values.   |

### Interaction State Recommendations

These rules help developers choose consistent state colors even before all state tokens
are formalized in SCSS.

| State            | Recommendation                                                                                                    |
|------------------|-------------------------------------------------------------------------------------------------------------------|
| Default          | Use the semantic base color (`500` for filled elements, `700` for text-level links when needed).                  |
| Hover            | Move one step darker within the same palette when contrast allows.                                                |
| Focus            | Stay in the same palette family; use the semantic color plus a visible focus ring rather than switching families. |
| Active / pressed | Use a darker shade than hover within the same palette.                                                            |
| Selected         | Use the same family as the default action or semantic meaning; do not introduce a new color family.               |
| Disabled         | Do not use semantic colors to indicate disabled state; use neutral Bulma defaults and reduced emphasis.           |

### Developer Examples

| Scenario                             | Recommended Color        |
|--------------------------------------|--------------------------|
| "Create account" button              | Blue 500                 |
| "Delete activity" button             | Red 500                  |
| "Import finished successfully" toast | Green 500                |
| "Profile incomplete" warning badge   | Yellow 500 or Yellow 700 |
| "Learn more" inline content link     | Blue 700                 |
| Invalid email field border           | Red 500                  |
| Completed onboarding step            | Green 500                |
| Featured race statistic card accent  | Yellow 300–500           |

### Anti-Patterns

- Do not use Yellow as the fill color for the main page CTA.
- Do not use Red for non-destructive visual emphasis.
- Do not use Green for generic navigation or neutral actions.
- Do not use Blue for success or error messaging just to keep the UI on-brand.
- Do not encode disabled state with semantic colors.
- Do not mix multiple semantic meanings on the same element; choose the strongest
  semantic role and style the element consistently.

## Accessibility Guidance

Known-safe foreground–background combinations for text:

| Text Color                | Works On                                                          |
|---------------------------|-------------------------------------------------------------------|
| White                     | Blue 600–900, Red 600–900, Green 600–900, Grey 500–900            |
| `$et-grey-050` (near-white) | Grey 500–900                                                    |
| `$et-grey-900` (near-black) | Grey 050–400                                                    |
| Dark text (`$text`)       | Blue 50–400, Yellow 50–500, Red 50–200, Green 50–200             |

Risky combinations to avoid:

- White text on Yellow 500 (insufficient contrast)
- Yellow 500 text on white backgrounds
- Large body-copy blocks in Red 500 or Red 700

## Palette Swatch Reference

A visual reference of all four Material Design palettes with HEX and HSL notation is
available as a PDF in the `docs/style-guide/assets` directory:

[`docs/style-guide/assets/endurancetrio-color-palettes.pdf`](./assets/endurancetrio-color-palettes.pdf)

## GIMP and Inkscape Palette File

For image-editing work, the full color system is also available as a single GIMP Palette (`.gpl`)
file at [`docs/style-guide/assets/endurancetrio-color-palette.gpl`](./assets/endurancetrio-color-palette.gpl).
The format is natively supported by both [GIMP](https://www.gimp.org) and
[Inkscape](https://inkscape.org).

The file contains 52 swatches: `Black` and `White`, plus the ten-step (050–900) Grey, Blue, Yellow,
Red, and Green palettes listed above. The RGB values match the HEX reference exactly, and the
palette is laid out in four columns (`Columns: 4`).

### Installing in GIMP

Copy the file into your GIMP palettes directory, then open the Palettes dialog (*Windows → Dockable
Dialogs → Palettes*) and refresh it.

| Platform | Palettes directory                                     |
|----------|--------------------------------------------------------|
| Linux    | `~/.config/GIMP/2.10/palettes/` (use `3.0` for GIMP 3) |
| Windows  | `%APPDATA%\GIMP\2.10\palettes\`                        |
| macOS    | `~/Library/Application Support/GIMP/2.10/palettes/`    |

### Installing in Inkscape

Copy the file into your Inkscape palettes directory, restart Inkscape, then select the palette from
the *Swatches* dropdown.

| Platform | Palettes directory                                 |
|----------|----------------------------------------------------|
| Linux    | `~/.config/inkscape/palettes/`                     |
| Windows  | `%APPDATA%\inkscape\palettes\`                     |
| macOS    | `~/Library/Application Support/inkscape/palettes/` |
