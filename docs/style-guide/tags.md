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

## Size and placement

Discipline tags support Bulma's size modifiers (`.is-small`, `.is-medium`, `.is-large`) and the
tag group wrapper (`.tags`), with no extra markup:

```html
<div class="tags">
  <span class="tag is-swim is-small">Swim 1.5 km</span>
  <span class="tag is-bike is-small">Bike 40 km</span>
  <span class="tag is-run is-small">Run 10 km</span>
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
