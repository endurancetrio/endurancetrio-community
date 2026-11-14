# Icons

> Part of the [EnduranceTrio Style Guide](./index.md).

Icons are SVG files turned into CSS classes. Each class uses the SVG as a `mask-image`, so the icon
takes the current text colour (`currentColor`). An icon therefore follows the colour of its
parent: a Bulma `has-text-primary` or a button's text colour applies to the icon with no extra
work.

Sources:

- `icons.config.json`: the list of icons to include
- `scripts/generate-icons.js`: the generator
- `src/scss/utilities/endurance-icons.scss`: the generated output. **Do not edit it by hand.**
- `src/icons/`: custom SVG files

## Using an icon

Add the `et` class with the icon's `et-{name}` class to an `<i>` element:

```html
<i class="et et-sort"></i>
```

The `et` class is a naming marker; only the `et-{name}` class has styles. Icons are sized to their
container, so place them in a Bulma `.icon` wrapper to get the standard icon size and alignment:

```html
<span class="icon">
  <i class="et et-chevron-double-left"></i>
</span>
```

Inside a `.icon-text` (an icon followed by text), the icon and the text stay aligned:

```html
<span class="icon-text">
  <span class="icon"><i class="et et-sort"></i></span>
  <span>Rank</span>
</span>
```

## Available icons

| Class                      | Source                        | Used in templates                                                      |
|----------------------------|-------------------------------|------------------------------------------------------------------------|
| `.et-arrow-up`             | Material Design Icons         | No                                                                     |
| `.et-arrow-down`           | Material Design Icons         | No                                                                     |
| `.et-chevron-double-left`  | Material Design Icons         | Yes                                                                    |
| `.et-chevron-double-right` | Material Design Icons         | Yes                                                                    |
| `.et-chevron-down`         | Material Design Icons         | Yes; expand toggle ([Boxes](./boxes-and-cards.md#expand-button), [Tables](./tables.md#expandable-rows)) |
| `.et-chevron-right`        | Material Design Icons         | No                                                                     |
| `.et-chevron-up`           | Material Design Icons         | Yes; expand toggle ([Boxes](./boxes-and-cards.md#expand-button), [Tables](./tables.md#expandable-rows)) |
| `.et-email-newsletter`     | Material Design Icons         | Yes                                                                    |
| `.et-face-man`             | Material Design Icons         | Yes                                                                    |
| `.et-face-woman`           | Material Design Icons         | Yes                                                                    |
| `.et-file-document`        | Material Design Icons         | No                                                                     |
| `.et-file-image`           | Material Design Icons         | No                                                                     |
| `.et-head-cog-outline`     | Material Design Icons         | Yes                                                                    |
| `.et-magnify`              | Material Design Icons         | Yes                                                                    |
| `.et-sort`                 | Material Design Icons         | Yes; unsorted state of [sortable tables](./tables.md#sortable-columns) |
| `.et-sort-ascending`       | Material Design Icons         | Yes; ascending state of sortable tables                                |
| `.et-sort-descending`      | Material Design Icons         | Set by the sort script; not in markup                                  |
| `.et-tally-mark-1`         | Material Design Icons         | Yes                                                                    |
| `.et-kofi`                 | Custom (`src/icons/kofi.svg`) | No                                                                     |

The list matches `icons.config.json`. "Used in templates" reflects the current Thymeleaf templates
and may change; check `endurancetrio-app/src/main/resources/templates/` before relying on it.

## Adding an icon

1. Find the icon on [Pictogrammers Material Design Icons](https://pictogrammers.com/library/mdi/)
   and note its name. Add the name to the `mdi` array in `icons.config.json`, in alphabetical
   order.
2. For a non-Material icon, save the SVG under `src/icons/` and add it to the `custom` object as
   `"name": "src/icons/file.svg"`. The class will be `.et-name`.
3. Regenerate the stylesheet:

   ```shell
   npm run generate:icons
   ```

   The `prebuild:*` npm hooks also run this, so a normal build picks up the change.

4. Include both `icons.config.json` and the regenerated `endurance-icons.scss` in the change.

Material icons are imported from the `@mdi/svg` package, so the names must match the package's
file names exactly.

## Accessibility

An icon on its own does not describe its meaning to a screen reader. Use one of these patterns:

- **With visible text**, mark the icon as decorative. Bulma's `icon` wrapper is not hidden by
  default, so add `aria-hidden="true"` to the `<i>`:

  ```html
  <span class="icon-text">
    <span class="icon"><i class="et et-file-document" aria-hidden="true"></i></span>
    <span>Results</span>
  </span>
  ```

- **Without visible text** (an icon-only button), give the button an accessible name with
  `aria-label` and hide the icon from assistive technology:

  ```html
  <button type="button" class="button is-ghost" aria-label="Search">
    <span class="icon"><i class="et et-magnify" aria-hidden="true"></i></span>
  </button>
  ```
