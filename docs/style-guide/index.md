# EnduranceTrio Style Guide

This is the reference for the visual and interaction building blocks of the EnduranceTrio
website. It documents the **custom** parts of the style guide: the EnduranceTrio
palette, the `endurance-*` components, and the JavaScript behaviours that extend Bulma.

Standard Bulma elements and components (buttons, notifications, columns, forms, and so on) are
not repeated here. Use the [Bulma documentation](https://bulma.io/documentation/) for those; this
guide only notes where the project overrides or extends them.

## Table of Contents

1. [Foundations](#foundations)
2. [Naming](./naming.md)
3. [Conventions](#conventions)
4. [Component Catalogue](#component-catalogue)
5. [Third-Party Code](#third-party-code)
6. [Adding a Component](#adding-a-component)
7. [Building and Verifying](#building-and-verifying)

## Foundations

The styling is built on [Bulma 1.0](https://bulma.io) and compiled with Webpack from
`endurancetrio-app/src/main/resources/webpack/`:

| Layer             | Location                                                 | Role                                                                 |
|-------------------|----------------------------------------------------------|----------------------------------------------------------------------|
| Sass entrypoint   | `src/scss/style.scss`                                    | Forwards Bulma modules and project partials, in order                |
| Palette tokens    | `src/scss/utilities/endurancetrio-variables.scss`        | `$et-{color}-{shade}` palette variables (see [Colors](./colors.md))  |
| Bulma customizing | `src/scss/utilities/endurance-bulma-utilities.scss`      | Maps palette tokens onto Bulma's semantic variables                  |
| Theme             | `src/scss/themes/endurance-light-theme.scss`             | Light theme setup and table hover overrides                          |
| Components        | `src/scss/{elements,components,form,layout,grid,klaro}/` | `endurance-*` partials, one per component                            |
| Behaviours        | `src/js/bulma-*.js`, `src/js/language-selector.js`       | Progressive-enhancement modules, initialised from `src/js/script.js` |
| Icons             | `icons.config.json` and `scripts/generate-icons.js`      | Generates `.et-{name}` mask icon classes                             |

The compiled output is written to `endurancetrio-app/src/main/resources/static/` and served by
Spring Boot. Templates in `endurancetrio-app/src/main/resources/templates/` consume the classes
directly; no build step is needed for template-only changes.

### Class namespaces

Bulma's class prefix is empty in this project (`$class-prefix: ""`), so Bulma classes keep their
standard names (`button`, `is-primary`, `table`). The project adds namespaces inside the class
name instead of changing that prefix:

| Namespace                       | Used for                              | Example                          |
|---------------------------------|---------------------------------------|----------------------------------|
| `endurance-{block}`             | Project blocks and their elements     | `.endurance-box`, `.endurance-media-left` |
| `is-*` / `has-*`                | Modifiers, states and helpers         | `.is-branded-thin`, `.has-shadow` |
| `et-*`                          | Icons                                 | `.et-sort`                       |
| `--bulma-endurance-*`           | Project CSS custom properties         | `--bulma-endurance-media-gap`    |
| `$endurance-{block}-{property}` | Project Sass variables                | `$endurance-media-gap`           |

Third-party namespaces (Klaro's `klaro`/`cm-*`/`cn-*` and bulma-switch-control's
`switch`/`check`/`control-label`) are kept intact. The full rules, the decision table and the
review checklist are on the [Naming](./naming.md) page.

## Conventions

- **Prefer Bulma first.** Use a Bulma class when one exists. Add an `endurance-*` class only when
  the design or behaviour is specific to EnduranceTrio.
- **Extend, do not fork.** Custom components `@extend` the Bulma class they build on (for example
  `.endurance-card` extends `.card`), so Bulma's markup and states keep working.
- **Colour through tokens.** Use Bulma's semantic classes (`is-primary`, `is-danger`, and so on)
  or the grey helpers. Do not hard-code hex values in templates. See [Colors](./colors.md) for
  the selection rules.
- **JavaScript hooks are `data-*` attributes**, not classes. Styling classes can change without
  breaking behaviour, and the hooks are visible in the markup.
- **Progressive enhancement.** Markup should read sensibly without JavaScript. Behaviours add
  state (`is-active`, `aria-expanded`, `is-hidden`) on top of it.
- **Accessibility.** Toggles are real `<button>` elements with `aria-expanded`, and interactive
  controls keep their native keyboard behaviour.
- **Naming.** Follow the [Naming](./naming.md) rules: blocks are `endurance-{block}`, elements are
  `endurance-{block}-{element}`, and modifiers/states are the global `is-*`/`has-*` classes written
  next to the block. Partial names match the component (`endurance-table-sort.scss` and
  `bulma-table-sort.js`).

## Component Catalogue

### Active components

| Component                                        | Kind           | Source                                                                            | Documentation                                          |
|--------------------------------------------------|----------------|-----------------------------------------------------------------------------------|--------------------------------------------------------|
| Palette, grey scale and grey background helpers  | Tokens/Utility | `utilities/endurancetrio-variables.scss`, `utilities/endurance-grey-helpers.scss` | [Colors](./colors.md)                                  |
| Icons (`.et-*`)                                  | Utility        | `utilities/endurance-icons.scss` (generated)                                      | [Icons](./icons.md)                                    |
| `.endurance-box`                                 | Element        | `elements/endurance-box.scss`                                                     | [Boxes and Cards](./boxes-and-cards.md)                |
| `.endurance-card`                                | Component      | `components/endurance-card.scss`                                                  | [Boxes and Cards](./boxes-and-cards.md)                |
| `.tag` variants (`is-swim`, `is-bike`, `is-run`) | Element        | `elements/endurance-tags.scss`                                                    | [Tags](./tags.md)                                      |
| `.endurance-media` (media object)                | Layout         | `layout/endurance-media.scss`                                                     | [Media](./media.md)                                    |
| `.endurance-navbar`                              | Component      | `components/endurance-navbar.scss`                                                | [Navbar](./navbar.md)                                  |
| Tabs (`data-tabs`, `data-tab-content`)           | Component      | `components/endurance-tabs.scss`, `js/bulma-tab.js`                               | [Tabs](./tabs.md)                                      |
| Sortable tables (`data-sortable`, `data-sort`)   | Element        | `elements/endurance-table-sort.scss`, `js/bulma-table-sort.js`                    | [Tables](./tables.md)                                  |
| Expandable table rows (`data-expandable`)        | Element        | `elements/endurance-table-expand.scss`, `js/bulma-table-expand.js`                | [Tables](./tables.md)                                  |
| `.switch` with inner labels (adapted, MIT)       | Form           | `form/endurance-switch-control*.scss`                                             | [Switch Control](./switch-control.md)                  |
| Modals (`data-modal`)                            | Component      | `js/bulma-modal.js` (Bulma markup)                                                | [Behaviours](./behaviours.md)                          |
| Navbar burger and language selector              | Behaviour      | `js/bulma-navbar.js`, `js/language-selector.js`                                   | [Behaviours](./behaviours.md)                          |
| Cookie consent theme (Klaro)                     | Theme          | `klaro/endurance-klaro*.scss`                                                     | Styles only; see the file headers                      |
| Light theme and Bulma variable overrides         | Theme          | `themes/endurance-light-theme.scss`, `utilities/endurance-bulma-utilities.scss`   | [Colors](./colors.md#bulma-color-system-customization) |

### Not currently enabled

These partials exist in the repository but are commented out of `style.scss`, so their styles are
not in the compiled CSS. Enable them with their `@forward` line before using them, and document
them when they are in use.

| Partial                           | Notes                                                                                         |
|-----------------------------------|-----------------------------------------------------------------------------------------------|
| `components/endurance-panel.scss` | Branded panel variants, paired with the `bulmaPanel` tab behaviour (not initialised).         |
| `grid/endurance-fixed-grid.scss`  | Fixed-grid breakpoint fixes for Bulma's container-query breakpoints inside padded containers. |

## Third-Party Code

Some components are adapted from open-source projects. Their notices are kept in the headers of the
source files, and they are summarised here:

| Project                                                                                                                                                   | Used for                                    | Licence      | Source files                                                                         |
|-----------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------|--------------|--------------------------------------------------------------------------------------|
| [Bulma](https://bulma.io)                                                                                                                                 | CSS framework that the whole system extends | MIT          | `node_modules/bulma`                                                                 |
| [bulma-switch-control](https://github.com/justboil/bulma-switch-control) by Viktor Kuzhelny, based on Buefy's switch by Walter Tommasi and Rafael Beraldo | Switch control                              | MIT          | `form/endurance-switch-control.scss`, `form/endurance-switch-control-variables.scss` |
| [Klaro](https://github.com/kiprotect/klaro) by KIProtect                                                                                                  | Cookie consent theme                        | BSD-3-Clause | `klaro/endurance-klaro*.scss`                                                        |
| [Material Design Icons](https://pictogrammers.com/library/mdi/) (`@mdi/svg`)                                                                              | Icon set                                    | Apache-2.0   | `icons.config.json`                                                                  |

See [Switch Control](./switch-control.md#attribution) for the full attribution of the switch
control.

## Adding a Component

1. **Name it.** Follow the [Naming](./naming.md) rules: `endurance-{block}` for the block,
   `endurance-{block}-{element}` for its elements, and `is-*`/`has-*` for modifiers, states and
   helpers.
2. **Write the stylesheet.** Create a partial in the matching folder (`elements/`, `components/`,
   `form/`, `layout/`) with the project licence header. Use the `endurance-` prefix and `@extend`
   the Bulma class it builds on.
3. **Forward it** from `src/scss/style.scss`, next to the Bulma module it extends.
4. **Add behaviour (if needed).** Create `src/js/bulma-<name>.js` as a default-exported object with
   an `init()` method, document it with JSDoc, and call `init()` from `src/js/script.js`.
5. **Add icons (if needed).** Add the Material Design icon name to `icons.config.json`, then run
   `npm run generate:icons`.
6. **Document it.** Add a section to the page for its family, or create a page and link it from
   the [Component Catalogue](#component-catalogue).
7. **Verify.** Run the dev build, lint, and format checks (see below), and check the component in
   a browser in both light and narrow widths.

## Building and Verifying

Run these from `endurancetrio-app/src/main/resources/webpack/`:

```shell
npm run build:dev      # development build with source maps
npm run build:watch    # rebuild on change (writes to target/classes/static when WEBPACK_OUTPUT_DIR is set)
npm run generate:icons # regenerate endurance-icons.scss after editing icons.config.json
npm run format         # Prettier and ESLint
```

The Maven build runs the frontend build automatically (`./mvnw clean install`).
