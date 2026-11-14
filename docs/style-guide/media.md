# Media

> Part of the [EnduranceTrio Style Guide](./index.md).

`.endurance-media` is a CSS Grid version of Bulma's
[media object](https://bulma.io/documentation/layout/media-object/). It places an image or a
badge beside a block of content. Unlike Bulma's flexbox media object, the grid lets the left and
right columns align on their own tracks, which keeps badges, dates and titles lined up across a
list of items.

Source: `src/scss/layout/endurance-media.scss`. Its Sass variables have SassDoc comments.

## Structure

| Class                      | Role                                                           |
|----------------------------|----------------------------------------------------------------|
| `.endurance-media`         | Grid container                                                 |
| `.endurance-media-left`    | Left column: an image or a text badge                          |
| `.endurance-media-content` | Content column: titles, text, and other blocks                 |
| `.endurance-media-footer`  | Optional row at the bottom of the content column (tags, links) |

```html
<div class="endurance-media">
  <div class="endurance-media-left is-64x64">
    <img src="/img/avatar.png" alt="Athlete photo">
  </div>
  <div class="endurance-media-content">
    <p class="title is-5">Athlete name</p>
    <p class="subtitle is-6">Club, country</p>
    <p class="endurance-media-footer">
      <span class="tag is-swim is-light">Swim</span>
    </p>
  </div>
</div>
```

## Responsive behaviour

- **Mobile:** the two columns stack. The left column takes the full width, with a bottom margin.
- **Mobile with `.is-mobile`:** the two columns stay side by side. The left column keeps its
  content width and loses the bottom margin, so a badge stays next to the content at every
  viewport. The modifier mirrors `.columns.is-mobile` and `.level.is-mobile`.
- **Tablet and up:** the left column is sized by its content (`auto`), and the content column fills
  the remaining space (`1fr`).
- **Footer:** the footer is pushed to the bottom of the content column. On mobile it gains a thin
  top border and centred text, so it reads as a separate row.
- **Long text:** the content column has `min-width: 0`, so long unbroken strings wrap instead of
  pushing the layout wider.

```html
<div class="endurance-media is-mobile">
  <div class="endurance-media-left p-0">
    <div class="box is-outlined is-64x64">3</div>
  </div>
  <div class="endurance-media-content">
    <p class="title is-5">Race title</p>
  </div>
</div>
```

The `p-0` helper removes the left column's padding so the badge sits flush; the grid `column-gap`
provides the spacing.

## Left column sizing

### Square pixel sizes

`.is-{N}x{N}` fixes a square badge at N pixels. The available sizes are `16`, `24`, `32`, `48`,
`64`, `96` and `128`. On tablet and up the height is fixed to N pixels and the aspect ratio is 1:

```html
<div class="endurance-media-left is-64x64"><img src="/img/icon.png" alt=""></div>
<div class="endurance-media-left is-128x128"><img src="/img/logo.png" alt=""></div>
```

The list is set by the `$endurance-media-dimensions` Sass variable.

### Aspect ratios

| Class          | Result                                                       |
|----------------|--------------------------------------------------------------|
| `.is-square`   | `aspect-ratio: 1`                                            |
| `.is-{W}by{H}` | Any ratio from Bulma's `$aspect-ratios`, such as `.is-16by9` |

Ratio badges take the full column width, and their image fills the box with `object-fit: cover`.

### Open Graph sizes

`.is-og-1200x628`, `.is-og-600x314`, `.is-og-480x251` and `.is-og-320x167` give the standard
social-share image proportions. Use them for article and event thumbnails:

```html
<div class="endurance-media-left is-og-320x167">
  <img src="/img/article.jpg" alt="Article thumbnail">
</div>
```

### Minimum width

`.is-min-width-4rem`, `.is-min-width-6rem` and `.is-min-width-8rem` keep a text badge at least that
wide. Use them so badges with short labels line up in a list:

```html
<div class="endurance-media-left is-min-width-6rem has-background-grey-300 has-text-centered">
  <p class="title is-4">12-05</p>
  <p class="subtitle is-6">2026</p>
</div>
```

## Text badges

A text badge is an `.endurance-media-left` without an image. Combine it with Bulma's colour
helpers or the grey background helpers (see [Colors](./colors.md#grey-background-helpers)):

```html
<div class="endurance-media-left has-background-grey-600 has-text-white">
  <p class="title is-4">Label</p>
</div>
```

The badge has `border-radius` from Bulma's `radius-large` and its padding comes from
`$endurance-media-left-padding`.

## Choosing between Bulma's media object and this component

| Need                                                                    | Use                          |
|-------------------------------------------------------------------------|------------------------------|
| A simple image-and-text row with no alignment across items              | Bulma `.media`               |
| Aligned badges or dates in a list, a footer row, or responsive stacking | `.endurance-media`           |
| A badge that must stay beside the content on mobile too                 | `.endurance-media.is-mobile` |
