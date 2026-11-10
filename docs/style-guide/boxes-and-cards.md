# Boxes and Cards

> Part of the [EnduranceTrio Style Guide](./index.md).

`.endurance-box` and `.endurance-card` are EnduranceTrio versions of Bulma's
[box](https://bulma.io/documentation/elements/box/) and
[card](https://bulma.io/documentation/components/card/). Both extend the Bulma class, so every
Bulma modifier and child element still works. What they add is the branded colour stripe, height
presets for hero-like blocks, and column layouts that fill their height.

Source: `src/scss/elements/endurance-box.scss` and `src/scss/components/endurance-card.scss`.

## Endurance Box

`.endurance-box` extends `.box`. Every endurance box has a tri-colour stripe along its bottom
edge: blue, yellow, and red in equal thirds. The stripe is drawn as a background gradient, so it
does not affect the box's content or padding. The default is already branded, so there is no
`is-branded` modifier; use a stripe variant to change the height.

```html
<div class="endurance-box">
  <h3 class="title is-5">Default box</h3>
  <p>Blue, yellow, and red stripe along the bottom edge.</p>
</div>
```

### Stripe variants

| Class                     | Stripe                                                                        | Height |
|---------------------------|-------------------------------------------------------------------------------|--------|
| (default)                 | Blue / yellow / red thirds                                                    | 3px    |
| `.is-branded-thin`        | Blue / yellow / red thirds                                                    | 1px    |
| `.is-branded-thick`       | Blue / yellow / red thirds                                                    | 6px    |
| `.is-branded-extra-thick` | Blue / yellow / red thirds                                                    | 9px    |
| `.is-branded-faded`       | Continuous gradient: blue at the left, yellow at the centre, red at the right | 3px    |

```html
<div class="endurance-box is-branded-thin">…</div>
<div class="endurance-box is-branded-faded">…</div>
```

Stripe heights are set by the Sass variables `$endurance-box-stripe-height`,
`$endurance-box-stripe-height-thin`, `$endurance-box-stripe-height-thick`, and
`$endurance-box-stripe-height-extra-thick`.

### Height presets

Height modifiers set a minimum height and centre the content vertically, which suits call-to-action
and hero-style blocks:

| Class                        | Minimum height                             |
|------------------------------|--------------------------------------------|
| `.is-small`                  | `6rem`                                     |
| `.is-medium`                 | `12rem`                                    |
| `.is-large`                  | `18rem`                                    |
| `.is-halfheight`             | `50vh`                                     |
| `.is-halfheight-with-navbar` | `calc(50vh - var(--bulma-navbar-height))`  |
| `.is-fullheight`             | `100vh`                                    |
| `.is-fullheight-with-navbar` | `calc(100vh - var(--bulma-navbar-height))` |

```html
<div class="endurance-box is-medium is-branded-thin has-text-centered">
  <p class="title is-4">Centred call to action</p>
</div>
```

Use `.is-content-top` to keep the content at the top instead of centring it. It is also a flex
column, so it accepts the same height presets.

```html
<div class="endurance-box is-branded-thin is-content-top">…</div>
```

### Inside columns

A box placed directly in a Bulma `.column`, or in an `<a>` that is a direct child of a column,
stretches to the full height of the column. Use this to line up a row of boxes:

```html
<div class="columns">
  <div class="column">
    <a href="/events" class="is-clickable">
      <div class="endurance-box is-branded-thin">…</div>
    </a>
  </div>
  <div class="column">
    <div class="endurance-box is-branded-thin">…</div>
  </div>
</div>
```

The `is-clickable` class on the link is Bulma's helper (it sets a pointer cursor). The box styles
do not depend on it.

## Endurance Card

`.endurance-card` extends `.card`. Use it for an image-and-text tile, such as an article or event
teaser. It keeps all Bulma card parts (`.card-image`, `.card-content`, `.card-footer`).

```html
<div class="endurance-card">
  <div class="card-image">
    <figure class="image">
      <img src="/img/endurancetrio-open-graph.png" alt="Article title">
    </figure>
  </div>
  <div class="card-content">
    <p class="title is-3">Article title</p>
    <p class="subtitle is-5">Author name</p>
  </div>
</div>
```

Inside a Bulma `.column`, a card becomes a flex column that fills the column height, and
`.card-content` grows to take the spare space. Cards in a row therefore finish at the same height
even when their text lengths differ:

```html
<div class="columns">
  <div class="column">
    <a href="/insights/slug" class="is-clickable">
      <div class="endurance-card">…</div>
    </a>
  </div>
</div>
```

## Choosing between them

| Need                                                  | Use               |
|-------------------------------------------------------|-------------------|
| A branded container for text, lists or a single block | `.endurance-box`  |
| An image on top with title and text, as a teaser      | `.endurance-card` |
| A plain Bulma box without the brand stripe            | `.box`            |
