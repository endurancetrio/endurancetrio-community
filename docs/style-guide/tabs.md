# Tabs

> Part of the [EnduranceTrio Style Guide](./index.md).

Tabs switch between panels of content on the same page. The EnduranceTrio tabs use Bulma's
[tabs](https://bulma.io/documentation/components/tabs/) styling for the tab bar, and add `data-*`
hooks for switching the panels, plus a URL hash so a tab can be linked to.

Sources: `src/scss/components/endurance-tabs.scss` (panel visibility) and `src/js/bulma-tab.js`
(behaviour).

## Markup

The tab bar is a Bulma `.tabs` block with a `data-tabs` list. Each item has a `data-target` that
names a panel. Each panel has a matching `id` and the `data-tab-content` attribute:

```html
<div class="tabs">
  <ul data-tabs>
    <li class="is-active" data-target="overview"><a>Overview</a></li>
    <li data-target="results"><a>Results</a></li>
  </ul>
</div>

<div id="overview" data-tab-content class="is-active">
  <p>Overview content.</p>
</div>

<div id="results" data-tab-content>
  <p>Results content.</p>
</div>
```

The real usage in `templates/fragments/individual-results.html` follows this pattern. Each age
group is a tab, and each panel holds a sortable table.

## Data attributes

| Attribute                      | Applied to                      | Purpose                                                   |
|--------------------------------|---------------------------------|-----------------------------------------------------------|
| `data-tabs`                    | The list that holds the tabs    | Hooks the click listeners onto its direct children        |
| `data-target="id"`             | Each tab item                   | The `id` of the panel it shows                            |
| `data-tab-content`             | Each panel                      | Marks the panel as switchable; hidden unless `.is-active` |
| `data-button="tab-navigation"` | Any element outside the tab bar | Switches to the panel in its `data-target`                |

Panels are hidden by CSS (`[data-tab-content] { display: none }`) until they get `.is-active`.
A tabbed page therefore shows no panel at all if the script fails to load. Keep the JavaScript
initialisation in place, and pick a panel to show by default with `.is-active` in the markup.

## Choosing the active tab

On load, the active tab is chosen in this order:

1. The panel named in the URL hash, such as `#results`, if it matches a `data-target`.
2. The item that has `.is-active` in the markup.

A hash link such as `/en/events/2026/race-1/overview#results` opens the results tab directly.

## Switching tabs

Clicking a tab:

- moves `.is-active` to the clicked item and its panel,
- updates the URL hash with `history.replaceState`, so the browser history does not grow,
- **scrolls the page to the top.**

The scroll means tabs are best placed near the top of a page. If you need to keep the reader's
position, do not use this behaviour; use a plain Bulma `.tabs` with links instead.

A `hashchange` event (for example, a user editing the URL) also switches the tab.

### Switching from outside the tab bar

Any element with `data-button="tab-navigation"` and a `data-target` switches to that panel. Use it
for "see the results" links inside the page text:

```html
<a data-button="tab-navigation" data-target="results" class="is-clickable">See the results</a>
```

## Do not

- Do not reuse a `data-target` value for a panel that is not a `data-tab-content` element.
- Do not put tab panels inside a `.tabs` block; the `.tabs` class only styles the bar.
- Do not rely on tabs to hide sensitive content. Hidden panels are still in the HTML.
