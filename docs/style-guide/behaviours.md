# Behaviours

> Part of the [EnduranceTrio Style Guide](./index.md).

Behaviours are the JavaScript modules that make the static markup interactive. Each module is a
plain object with an `init()` method. `src/js/script.js` imports the modules and calls `init()`
once the DOM has loaded, so a page only needs the markup, not any script of its own.

This page covers the modules that are not documented in a component page. For tabs, tables, and
the navbar, see [Tabs](./tabs.md), [Tables](./tables.md), and [Navbar](./navbar.md).

| Module                  | Initialised by `script.js` | Documented here                         |
|-------------------------|----------------------------|-----------------------------------------|
| `bulma-modal.js`        | Yes                        | [Modal](#modal)                         |
| `bulma-navbar.js`       | Yes                        | [Navbar burger](#navbar-burger)         |
| `bulma-tab.js`          | Yes                        | [Tabs](./tabs.md)                       |
| `bulma-table-sort.js`   | Yes                        | [Tables](./tables.md#sortable-columns)  |
| `language-selector.js`  | Yes                        | [Language selector](#language-selector) |
| `bulma-panel.js`        | **No**                     | Not active; see the note at the end     |

## Modal

A modal is Bulma's [modal](https://bulma.io/documentation/components/modal/) component, opened by
a trigger button. The trigger carries `data-modal` with the `id` of the modal:

```html
<button type="button" class="button is-info is-light" data-modal="error-debug-modal">
  Show details
</button>

<div id="error-debug-modal" class="modal">
  <div class="modal-background"></div>
  <div class="modal-content">
    <div class="box">…</div>
  </div>
  <button type="button" class="modal-close is-large" aria-label="close"></button>
</div>
```

The modal is closed by:

- a click on `.modal-background` or `.modal-close` inside it,
- the **Escape** key.

Opening sets `is-active` on the modal. Keep the modal `id` unique on the page.

## Navbar burger

The burger button toggles the navigation menu on small screens. It carries `data-target` with the
`id` of the menu it controls:

```html
<a role="button" class="navbar-burger" aria-label="menu" aria-expanded="false"
   data-target="main-navbar">
  <span aria-hidden="true"></span>
  <span aria-hidden="true"></span>
  <span aria-hidden="true"></span>
  <span aria-hidden="true"></span>
</a>

<div id="main-navbar" class="navbar-menu">…</div>
```

A click toggles `is-active` on both the burger and the menu. The module does not update
`aria-expanded`, so keep it in step in your markup if you change this behaviour.

## Language selector

The language selector is the `.switch.is-inner-label` control in the navbar, with the id
`language-selector` (see [Switch Control](./switch-control.md#inner-label-variant)). It is part of
the site's bilingual routing, and it behaves as follows:

- The choice is saved in `localStorage` under `endurancetrioLanguage`.
- Toggling the switch moves the page to the same path under the other language prefix (`/en/…` to
  `/pt/…`).
- On each page load, a stored language that differs from the page's language redirects the
  visitor to the matching page. The error pages submit a form instead, because they cannot be
  reached by a plain redirect.

Do not reuse the `language-selector` id for another control. The script looks it up by id when
the page loads.

## Not active: `bulmaPanel`

`src/js/bulma-panel.js` filters the blocks of a Bulma panel by tab (`data-panel-tabs`,
`data-panel-target`, `data-panel-value`). It is not imported by `script.js`, and its styles,
`components/endurance-panel.scss`, are not forwarded in `style.scss`. Panel filtering therefore does
nothing at present.

To enable it, add `bulmaPanel.init()` to `src/js/script.js`, uncomment the panel `@forward`, and
document the component here before using it in a template.
