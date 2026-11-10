# Switch Control

> Part of the [EnduranceTrio Style Guide](./index.md).

The switch is an on/off toggle, the visual alternative to a checkbox. The EnduranceTrio version
replaces Bulma's switch styling, with the same class names, and adds an **inner-label** variant
that shows text ("EN" / "PT") inside the track. The language selector in the navbar uses it.

> **Attribution:** this component is adapted from
> [bulma-switch-control](https://github.com/justboil/bulma-switch-control) (MIT License). See
> [Attribution](#attribution) below.

Sources:

- `src/scss/form/endurance-switch-control.scss`: the switch and its colour, size and style modifiers
- `src/scss/form/endurance-switch-control-inner-label.scss`: the inner-label variant
- `src/scss/form/endurance-switch-control-variables.scss`: the Sass variables

## Plain switch

Use a `<label>` with the `switch` class. Put the checkbox first, then the track (`check`), then
the text (`control-label`):

```html
<label class="switch is-rounded">
  <input type="checkbox" checked>
  <span class="check is-link"></span>
  <span class="control-label">Receive the newsletter</span>
</label>
```

The checkbox is visually hidden. The label is the click target, so clicking the text toggles the
switch too. The checkbox still submits its value with a form, and it remains in the keyboard tab
order.

## Colours

The track colour when the switch is on comes from the `check` element's colour class. Use any Bulma
colour name (`is-primary`, `is-link`, `is-info`, `is-success`, `is-warning`, `is-danger`):

```html
<span class="check is-success"></span>
```

Pick the colour by meaning, as for buttons
(see [Colors](./colors.md#semantic-role-recommendations)):
a neutral setting uses the default `is-primary` or `is-link`, and a warning-type setting may use
`is-warning`.

## Modifiers

| Class          | Effect                                                              |
|----------------|---------------------------------------------------------------------|
| `.is-rounded`  | Fully rounded track and knob                                        |
| `.is-outlined` | Transparent track with a coloured border; the knob takes the colour |
| `.is-elastic`  | The knob stretches when pressed                                     |
| `.is-small`    | Smaller track and knob                                              |
| `.is-medium`   | Medium track and knob                                               |
| `.is-large`    | Larger track and knob                                               |

The track width is `$switch-width-number` ems (2.75 by default). Sizes are relative to the font
size.

## Inner-label variant

`.is-inner-label` is a modifier on `.switch` that makes the track wider and shows an "off" and an
"on" label inside it. The labels come from the `data-off-label` and `data-on-label` attributes on a
`.switch-inner-label` element. Use it when the two states are better described by words than by the
knob position:

```html
<label class="switch is-inner-label is-rounded is-outlined">
  <input id="language-selector" type="checkbox">
  <span class="check is-link">
    <span data-off-label="EN" data-on-label="PT" class="switch-inner-label"></span>
  </span>
</label>
```

`.switch-inner-label` is an element of the `switch` block, and `.is-inner-label` is a modifier, in
line with the [Naming](./naming.md) rules. The track is `3.5em` wide
(`$switch-inner-label-width-number`), so keep both labels short.

This is the navbar language selector (`templates/fragments/main-navbar.html`). The language
selector script (`src/js/language-selector.js`) listens for the `input` event on the
`#language-selector` checkbox and redirects to the other language. Keep that `id` if you reuse the
control for the same purpose.

## Accessibility notes

- Keep a visible text label for every switch. The inner-label text ("EN" / "PT") describes the
  states, not the setting, so when the setting itself is not obvious, add a visible label or an
  `aria-label`.
- **Known gap:** the checkbox sets `outline: none` on focus. The replacement focus indicator is a
  shadow, which is only applied when `$switch-has-shadows` is `1`. The default is `0`, so a
  keyboard user gets no visible focus ring. Set `$switch-has-shadows: 1` or add a
  `:focus-visible` outline before using switches in a form.

## Attribution

The switch control is adapted from
[bulma-switch-control](https://github.com/justboil/bulma-switch-control) by Viktor Kuzhelny,
released under the MIT License. That project is in turn based on the switch component of
[Buefy](https://buefy.org) by Walter Tommasi and Rafael Beraldo, also MIT-licensed.

The adapted code is in `endurance-switch-control.scss` and
`endurance-switch-control-variables.scss`.
Those files keep the original MIT notice in their headers, and the modifications are licensed under
the Functional Software License (FSL), Version 1.1, ALv2 Future License, like the rest of the
project.
`endurance-switch-control-inner-label.scss` is EnduranceTrio code that adds the inner-label variant
on top of the switch partial.
