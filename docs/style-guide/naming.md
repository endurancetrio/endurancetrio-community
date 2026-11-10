# Naming

> Part of the [EnduranceTrio Style Guide](./index.md).

EnduranceTrio does not invent a naming system. It adds a **namespace** to Bulma's, and reuses
every Bulma rule for everything else. A developer who knows Bulma can therefore predict the name
of a project class without looking it up.

This page is normative: new stylesheets and templates must follow it, and reviewers use the
[checklist](#review-checklist) at the end.

## Bulma's model

Bulma is a flat, hyphenated system. Its class names have no BEM `__` or `--` separators, and every
name follows one of the patterns below:

| Role                | Bulma pattern                                     | Bulma example                    |
|---------------------|---------------------------------------------------|----------------------------------|
| Block               | `$class-prefix` + name                            | `.card`, `.navbar`, `.table`     |
| Element             | block + `-` + element                             | `.card-content`, `.navbar-item`  |
| Modifier            | `is-` + modifier                                  | `.is-primary`, `.is-small`       |
| Feature modifier    | `has-` + feature                                  | `.has-shadow`, `.has-addons`     |
| State               | `is-` + state                                     | `.is-active`, `.is-hidden`       |
| Helper              | `is-` / `has-` + helper                           | `.is-flex`, `.has-text-centered` |
| CSS custom property | `--` + `$cssvars-prefix` + block + `-` + property | `--bulma-card-radius`            |
| Sass variable       | `$` + block + `-` + property                      | `$card-radius`                   |

Three properties of that model matter here:

1. **Modifiers are global and unscoped.** `.is-active` is not `.navbar-item-is-active`; it is
   written *next to* the block class, and the block class is always present.
2. **`is-` and `has-` have different meanings.** `is-` describes how the block *looks or is*
   (colour, size, selection, state); `has-` describes what the block *has* (a shadow, addons, a
   fixed position).
3. **Every class name is generated from the prefixes.** Bulma builds names as
   `.#{iv.$class-prefix}...`, helpers as `.#{iv.$class-prefix}#{iv.$helpers-prefix}...` and
   `.#{iv.$class-prefix}#{iv.$helpers-has-prefix}...`, and custom properties as
   `--#{iv.$cssvars-prefix}...`. The defaults are `$class-prefix: ""`, `$cssvars-prefix:
   "bulma-"`, `$helpers-prefix: "is-"` and `$helpers-has-prefix: "has-"`.

## EnduranceTrio's extension

The project adds one name namespace, `endurance-`, and one utility namespace, `et-`. Everything
else is Bulma's:

| Role                | EnduranceTrio pattern                  | Example                              |
|---------------------|----------------------------------------|--------------------------------------|
| Block               | `endurance-{block}`                    | `.endurance-box`, `.endurance-media` |
| Element             | `endurance-{block}-{element}`          | `.endurance-media-left`              |
| Modifier            | `is-{modifier}`                        | `.is-branded-thin`, `.is-swim`       |
| Feature modifier    | `has-{feature}`                        | `.has-plain-heading`                 |
| State               | `is-{state}`                           | `.is-active`, `.is-sorted-asc`       |
| Helper              | `has-{feature}` / `is-{feature}`       | `.has-endurance-navbar-fixed-top`    |
| CSS custom property | `--bulma-endurance-{block}-{property}` | `--bulma-endurance-media-gap`        |
| Sass variable       | `$endurance-{block}-{property}`        | `$endurance-media-gap`               |
| Icon                | `et-{name}` (plus the `et` marker)     | `.et-sort`                           |
| JavaScript hook     | `data-{hook}`                          | `data-sortable`                      |

`endurance-` is a **name namespace inside the class**, not Bulma's `$class-prefix`. Bulma's
`$class-prefix` stays empty, so Bulma classes keep their standard names (`.button`, `.card`). If
`$class-prefix` is ever set, both Bulma's classes and the project's classes change together,
because project classes interpolate it too (see [rule 10](#rules)).

## Namespace map

| Namespace                     | Owner                              | Used for                      | Example                           |
|-------------------------------|------------------------------------|-------------------------------|-----------------------------------|
| `endurance-`                  | EnduranceTrio                      | Blocks and their elements     | `.endurance-box`                  |
| `is-`                         | Bulma + EnduranceTrio              | Modifiers, states and helpers | `.is-branded-thin`                |
| `has-`                        | Bulma + EnduranceTrio              | Feature modifiers and helpers | `.has-endurance-navbar-fixed-top` |
| `et-`                         | EnduranceTrio                      | Icons                         | `.et-chevron-double-right`        |
| `--bulma-`                    | Bulma                              | CSS custom properties         | `--bulma-navbar-height`           |
| `--bulma-endurance-`          | EnduranceTrio                      | Project CSS custom properties | `--bulma-endurance-media-gap`     |
| `$endurance-`                 | EnduranceTrio                      | Project Sass variables        | `$endurance-media-gap`            |
| `$et-`                        | EnduranceTrio                      | Palette tokens                | `$et-blue-500`                    |
| `klaro`, `cm-`, `cn-`         | Klaro (third party)                | Cookie consent                | `.cm-btn`                         |
| `switch`, `check`, `control-` | bulma-switch-control (third party) | Switch control                | `.switch`, `.check`               |
| *(none)*                      | Bulma                              | Bulma blocks and elements     | `.button`, `.card-content`        |

## Rules

1. **Project blocks are `endurance-{block}`.** Use a lowercase, singular, hyphenated noun
   (`.endurance-box`, `.endurance-media`). A block is the thing a template composes with.
2. **Project elements are `endurance-{block}-{element}`.** Elements are flat, exactly like
   `.card-content` and `.navbar-item`. Never use `__`, and never drop the block name
   (`endurance-left` is wrong).
3. **Modifiers never take the `endurance-` prefix.** They are `is-{modifier}` or
   `has-{feature}`, and they are always written with the block class:
   `class="endurance-box is-branded-thin"`. `endurance-box-is-branded-thin` and
   `endurance-is-branded-thin` are both wrong.
4. **Use `is-` for appearance, size, selection and state; use `has-` for possession.** Follow
   Bulma's split: `.is-primary`, `.is-small`, `.is-selected`, `.is-active` versus `.has-shadow`,
   `.has-addons`, `.has-endurance-navbar-fixed-top`.
5. **Reuse Bulma's modifiers; do not clone them.** If Bulma already defines `.is-small`,
   `.is-medium`, `.is-large`, `.is-active`, `.is-selected`, `.is-hidden`, `.is-rounded`,
   `.is-outlined`, `.is-fullwidth` and so on, use them. A project block may support them by
   `@extend`ing the Bulma block, not by redefining them.
6. **A modifier belongs to the block that defines it.** `.is-branded-thin` is defined under
   `.endurance-box`, so it only works on `.endurance-box`. Do not put it on a plain `.box` and
   expect the stripe. If two project blocks need the same modifier, move the shared rule to the
   Bulma block they both extend, or give each block its own modifier.
7. **States are `is-{state}` and are for styling only.** JavaScript may add or remove them
   (`.is-active`, `.is-sorted-asc`), but it must find elements through `data-*` hooks, never
   through a styling class.
8. **CSS custom properties go through Bulma's helpers.** Register them with
   `cv.register-vars(...)` and read them with `cv.getVar(...)`, so a block named `endurance-media`
   exposes `--bulma-endurance-media-{property}`. Never declare a bare `--{property}` on a project
   block. When the goal is to customize a Bulma block, override Bulma's own variable
   (`--bulma-navbar-height`) instead of creating a parallel one.
9. **Project Sass variables are `$endurance-{block}-{property}` and use `!default`.** Palette tokens
   keep the `$et-` prefix (`$et-blue-500`), and third-party variables keep their upstream names
   (`$switch-width`). Private module helpers start with an underscore (`$_max-column-count`).
10. **Always interpolate the prefixes.** Build class names as
    `.#{iv.$class-prefix}...`, `.#{iv.$class-prefix}#{iv.$helpers-prefix}...` and
    `.#{iv.$class-prefix}#{iv.$helpers-has-prefix}...`, and custom properties through
    `cv.register-vars`. Never hard-code `endurance-`, `is-`, `has-` or `--bulma-` inside a
    selector when the prefix is available. `@use 'bulma/sass/utilities/initial-variables' as iv`
    (or the local `endurance-bulma-utilities` wrapper as `bu`) provides them.
11. **Name the partial after what it defines or extends.** A new block gets
    `endurance-{block}.scss`; an extension of a Bulma block gets `endurance-{bulma-block}.scss`
    or `endurance-{bulma-block}-{feature}.scss`. Put it in the folder that matches Bulma's own
    category (`elements/`, `components/`, `form/`, `layout/`, `grid/`, `utilities/`, `themes/`).
    Behaviours are `bulma-{behaviour}.js`. Generated files keep their generator's output name.
12. **Keep third-party namespaces intact.** Klaro (`klaro`, `cm-*`, `cn-*`) and
    bulma-switch-control (`switch`, `check`, `control-label`) keep their upstream class names, so
    upstream markup and documentation keep working. Project additions inside one of those
    namespaces follow the same block/element/modifier rules but stay inside the namespace: they
    do not take `endurance-`, and they do not rename upstream classes. For example, the switch
    inner-label variant is the `.is-inner-label` modifier on `.switch`, and its text is the
    `.switch-inner-label` element.
13. **Icons use the `et-` utility namespace.** `et-{name}` classes are generated from
    `icons.config.json`; the bare `et` class is the marker. `et-` is a utility namespace like
    Bulma's `is-`/`has-` helpers, not a block namespace.
14. **Do not introduce new prefixes.** There is no `app-`, `c-`, `et-component-` or
    `endurance-`-prefixed modifier. If a name does not fit the map above, the design is probably
    trying to create a second block; split it into a block and an element instead.

## Choosing a name

| You need…                               | Use                                    | Example                              |
|-----------------------------------------|----------------------------------------|--------------------------------------|
| A new standalone component or container | `endurance-{block}`                    | `.endurance-card`                    |
| A part inside a project block           | `endurance-{block}-{element}`          | `.endurance-media-footer`            |
| A colour, size, variant or selection    | `is-{modifier}` with the block         | `.endurance-box.is-branded-thin`     |
| Something the block has                 | `has-{feature}` with the block         | `.endurance-panel.has-plain-heading` |
| A global project utility                | `has-{feature}` / `is-{feature}`       | `.has-background-grey-300`           |
| An icon                                 | `et-{name}`                            | `.et-sort-ascending`                 |
| A behaviour hook                        | `data-{hook}`                          | `data-sortable`                      |
| A themeable value                       | `--bulma-endurance-{block}-{property}` | `--bulma-endurance-media-gap`        |
| A build-time knob                       | `$endurance-{block}-{property}`        | `$endurance-media-gap`               |

## Common mistakes

| Mistake                                   | Why it is wrong                                                         | Correct                                                                    |
|-------------------------------------------|-------------------------------------------------------------------------|----------------------------------------------------------------------------|
| `.box.is-branded-thin`                    | The modifier is defined under `.endurance-box`                          | `.endurance-box.is-branded-thin`                                           |
| `.endurance-box.is-branded`               | The default box is already branded; no such modifier exists             | `.endurance-box`                                                           |
| `.endurance-box.is is-branded-thick.is-b` | Unknown classes left in markup                                          | `.endurance-box.is-branded-thick`                                          |
| `.endurance-box-is-branded-thin`          | Modifiers are not block-prefixed                                        | `.endurance-box.is-branded-thin`                                           |
| `.endurance-is-branded-thin`              | The block name is missing from the modifier                             | `.endurance-box.is-branded-thin`                                           |
| `.endurance-media_left`                   | Elements use a hyphen, not an underscore                                | `.endurance-media-left`                                                    |
| `.endurance-media__left`                  | Bulma does not use BEM double underscores                               | `.endurance-media-left`                                                    |
| `--navbar-height` on `.endurance-navbar`  | Custom properties need the Bulma prefix; the height is Bulma's variable | `--bulma-navbar-height` (or `--bulma-endurance-*` for project-only values) |
| `.control-inner-label`                    | Reads as part of Bulma's `.control` block                               | `.switch-inner-label` (element of `.switch`)                               |
| `.switch-inner-label` as the label        | A variant is a modifier, not a block                                    | `.switch.is-inner-label`                                                   |
| `class="is-active"` as a JS selector      | Styling classes are not behaviour contracts                             | `data-active`                                                              |

## Review checklist

Use this list when adding or reviewing a component. It mirrors the rules above.

- [ ] The block is `endurance-{block}` and `@extend`s the Bulma block it builds on.
- [ ] Every element is `endurance-{block}-{element}`; no `__`, no missing block name.
- [ ] Every modifier is `is-*` or `has-*`, written with its block, and has no `endurance-` prefix.
- [ ] Bulma modifiers are reused, not redefined.
- [ ] Each modifier is scoped to the block that defines it, and the template uses the right block.
- [ ] Behaviour is hooked with `data-*`; only state (`is-active`, `is-hidden`, `is-sorted-*`) is
  toggled through classes.
- [ ] CSS custom properties are `--bulma-endurance-*`, registered with `cv.register-vars`.
- [ ] Bulma variables are overridden, not shadowed by a parallel property.
- [ ] Sass variables are `$endurance-{block}-{property}` with `!default`.
- [ ] All class names interpolate `$class-prefix` (and the helper prefixes).
- [ ] Third-party namespaces are unchanged, and project additions inside them stay in that
  namespace.
- [ ] The partial is named after the block it defines or extends, and is `@forward`ed in
  `style.scss`.
- [ ] The component is documented on its family page, and
  the [Component Catalogue](./index.md#component-catalogue)
  is up to date.
