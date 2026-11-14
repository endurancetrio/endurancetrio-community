# Tags

> Part of the [EnduranceTrio Style Guide](./index.md).

The EnduranceTrio tag variants extend Bulma's [tag](https://bulma.io/documentation/elements/tag/)
with the three triathlon disciplines. Use them to label a discipline (swim, bike, run) wherever a
result, split, or event shows one. For status tags, use Bulma's semantic colours instead (see the
[colour selection rules](./colors.md#color-selection-rules)).

Source: `src/scss/elements/endurance-tags.scss`.

## Discipline variants

| Class      | Background             | Text                       | Source colour |
|------------|------------------------|----------------------------|---------------|
| `.is-swim` | Blue 500 (`#41B6E6`)   | White                      | Blue          |
| `.is-bike` | Yellow 500 (`#FEDB00`) | Near-black (15% lightness) | Yellow        |
| `.is-run`  | Red 500 (`#F9423A`)    | White                      | Red           |

```html
<span class="tag is-swim">Swim</span>
<span class="tag is-bike">Bike</span>
<span class="tag is-run">Run</span>
```

Yellow uses dark text because white on Yellow 500 does not meet contrast requirements (see
[Accessibility Guidance](./colors.md#accessibility-guidance)).

## Light variants

Add `.is-light` for a pale background with coloured text. This suits dense tables and lists, where
a solid tag would compete with the content:

| Class               | Background | Text       |
|---------------------|------------|------------|
| `.is-swim.is-light` | Blue 050   | Blue 700   |
| `.is-bike.is-light` | Yellow 050 | Yellow 700 |
| `.is-run.is-light`  | Red 050    | Red 700    |

```html
<span class="tag is-swim is-light">Swim</span>
<span class="tag is-bike is-light">Bike</span>
<span class="tag is-run is-light">Run</span>
```

## Outlined variant

Add `.is-outlined` for a transparent tag with a coloured border and text, the tag counterpart of
Bulma's [outlined button](https://bulma.io/documentation/elements/button/#outlined):

| Class                  | Border and text | Fill        |
|------------------------|-----------------|-------------|
| `.is-swim.is-outlined` | Blue 700        | transparent |
| `.is-bike.is-outlined` | Yellow 700      | transparent |
| `.is-run.is-outlined`  | Red 700         | transparent |

```html
<span class="tag is-swim is-outlined">Swim</span>
<span class="tag is-bike is-outlined">Bike</span>
<span class="tag is-run is-outlined">Run</span>
```

The border and the text use the same shade as the [light variants](#light-variants) use for their
text, not the solid 500 colour. The 500 shades do not read as text on the scheme background, and
Yellow 500 in particular is unreadable (see
[Accessibility Guidance](./colors.md#accessibility-guidance)). A plain `.tag.is-outlined` with no
colour modifier renders as a neutral outline in the tag's text colour.

Outlined tags follow Bulma's semantic colours too, taking each colour's `light-invert` shade:

```html
<span class="tag is-success is-outlined">Succeeded</span>
<span class="tag is-danger is-outlined">Failed</span>
```

Because `.is-outlined` is a shape modifier, it wins over the pale fill of `.is-light`: a tag with
both classes renders outlined. It combines with the size modifiers, and in a `.tags.has-addons`
group the touching borders overlap so they read as a single line.

The border is `max(1px, 0.0625em)`. Interactive outlined tags (`a.tag`, `button.tag` and
`.is-hoverable`) thicken it to `max(2px, 0.125em)` on hover and focus, like an outlined button. Both
widths are set by `$endurance-tag-border-width-outlined` and
`$endurance-tag-border-width-outlined-hover`.

## Size and placement

Discipline tags support Bulma's size modifiers (`.is-medium`, `.is-large`) and the tag group
wrapper (`.tags`), with no extra markup. `.is-small` is added here, because Bulma does not provide
a small tag:

```html
<div class="tags">
  <span class="tag is-swim is-small">Swim 1.5 km</span>
  <span class="tag is-bike is-small">Bike 40 km</span>
  <span class="tag is-run is-small">Run 10 km</span>
</div>
```

### Small tag metrics

A standard tag is `2em` tall with a `1.5` line-height, so its text fills 75% of its height. The
small tag keeps that proportion while shrinking:

| Property           | Standard tag     | Small tag    |
|--------------------|------------------|--------------|
| Height             | `2em` (`1.5rem`) | `1.125rem`   |
| Font size          | `0.75rem`        | `0.5625rem`  |
| Text height        | `1.125rem`       | `0.84375rem` |
| Vertical padding   | none             | none         |
| Horizontal padding | `0.75em`         | `0.75em`     |

Text height is 75% of the tag height in both cases, and horizontal padding is 37.5% of the tag
height in both cases. The height is the line box of `.is-size-7` text (`0.75rem` × the body
line-height), so a small tag is exactly as tall as the small copy it sits next to, such as the
subtitle in a result row. Horizontal padding stays at `0.75em` because the font and the height
shrink by the same 0.75 scale.

The three values are set by `$endurance-tag-small-height`, `$endurance-tag-small-font-scale` and
`$endurance-tag-small-padding-x` in `src/scss/elements/endurance-tags.scss`.

`.tags` is a block and carries a bottom margin when it is not the last child. When two groups sit
side by side in a flex row (for example inside a `.level-item`), that margin pushes the first
group up by half the margin and leaves the second group lower. Add `mb-0` to both groups so they
centre on the same line:

```html
<div class="level-item">
  <span class="tags has-addons mb-0">…</span>
  <span class="tags has-addons mb-0">…</span>
</div>
```

## Choosing a tag colour
| Meaning                                          | Use                               |
|--------------------------------------------------|-----------------------------------|
| A swim, bike, or run segment                     | `.is-swim`, `.is-bike`, `.is-run` |
| A penalty, warning, or caution status            | Bulma `.is-warning` (see Colors)  |
| A success, completion, or positive status        | Bulma `.is-success`               |
| A destructive or error status                    | Bulma `.is-danger`                |
| Neutral metadata such as a count or an age group | Bulma `.is-light` or `.is-dark`   |

Do not use the discipline colours to mean a status. A red `.is-run` tag and a red error tag look
the same, which would mislead users.
