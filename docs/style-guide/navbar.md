# Navbar

> Part of the [EnduranceTrio Style Guide](./index.md).

`.endurance-navbar` extends Bulma's [navbar](https://bulma.io/documentation/components/navbar/).
It sets the navbar height as a CSS variable, makes the active item visible, and reserves space for
a fixed navbar on the page. Use it for the site-wide navigation bar.

Source: `src/scss/components/endurance-navbar.scss`. The burger menu is handled by
`src/js/bulma-navbar.js` (see [Behaviours](./behaviours.md#navbar-burger)).

## Basic markup

This is the site navbar from `templates/fragments/main-navbar.html`, simplified:

```html
<html class="has-endurance-navbar-fixed-top">
<body>
<nav class="endurance-navbar is-fixed-top has-shadow" role="navigation"
     aria-label="main navigation">
  <div class="container is-fluid">
    <div class="navbar-brand">
      <a href="/en/" class="navbar-item">
        <!-- logo -->
      </a>
      <a role="button" class="navbar-burger" aria-label="menu" aria-expanded="false"
         data-target="main-navbar">
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
        <span aria-hidden="true"></span>
      </a>
    </div>

    <div id="main-navbar" class="navbar-menu">
      <div class="navbar-end">
        <a href="/en/athletes" class="navbar-item is-active">Athletes</a>
        <a href="/en/events" class="navbar-item">Events</a>
        <a href="/en/insights" class="navbar-item">Insights</a>
      </div>
    </div>
  </div>
</nav>
</body>
</html>
```

## Height and page offset

The navbar height is `$endurance-navbar-height` (5.2rem by default). It overrides Bulma's own
`--bulma-navbar-height` on `:root`, so offsets and the navbar itself share one value:

| Variable                                       | Set on              | Use                                                |
|------------------------------------------------|---------------------|----------------------------------------------------|
| `--bulma-navbar-height`                        | `:root`             | Navbar height and offsets for any element below it |
| `--bulma-endurance-navbar-item-img-max-height` | `.endurance-navbar` | Maximum image or SVG height inside `.navbar-item`  |

For a fixed navbar, add the offset class to `<html>` (or `<body>`) so the page content starts below
the bar:

| Class                                | Effect                                           |
|--------------------------------------|--------------------------------------------------|
| `.has-endurance-navbar-fixed-top`    | Adds `padding-top` equal to the navbar height    |
| `.has-endurance-navbar-fixed-bottom` | Adds `padding-bottom` equal to the navbar height |

Full-height heroes must also subtract the navbar height. Use `.is-fullheight-with-navbar` on a
`.hero`, or the matching box preset (see [Boxes and Cards](./boxes-and-cards.md#height-presets)).

## Active state

Mark the current section with `.is-active` (or `.is-selected`) on the `<a class="navbar-item">`.
The active item is bold and uses the navbar's own background and text colours from Bulma's
variables, so it stays correct if the theme changes.

Do not use the active state for hover. Bulma already gives hover feedback. The active state is
only for "you are here".

## Items with images

Images and inline SVGs inside `.navbar-item` are capped at `$endurance-navbar-item-img-max-height`
(2.8rem by default), so a logo never grows the bar.

## Tab-style items

`.navbar-item.is-tab` is given the full navbar height, so the item fills the bar vertically, as a
tab would. Combine it with `.navbar-brand` or `.navbar-tabs` when a tab-style bar is needed.
