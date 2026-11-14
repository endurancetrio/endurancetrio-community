# Tables

> Part of the [EnduranceTrio Style Guide](./index.md).

Tables use Bulma's [table](https://bulma.io/documentation/elements/table/) styling. On top of it,
the project adds two behaviours: **sortable columns** and **expandable rows**. Both are opt-in
through a `data-*` attribute on the `<table>`, and they work together.

Sources:

| Feature          | Styles                                                                                        | Behaviour                    |
|------------------|-----------------------------------------------------------------------------------------------|------------------------------|
| Sortable columns | `src/scss/elements/endurance-table-sort.scss`                                                 | `src/js/bulma-table-sort.js` |
| Expandable rows  | `src/scss/elements/endurance-table-expand.scss` (+ `src/scss/elements/endurance-expand.scss`) | `src/js/endurance-expand.js` |

## Basic table

Wrap the table in `.table-container` so that wide tables scroll horizontally on small screens:

```html
<div class="table-container">
  <table class="table is-fullwidth is-hoverable is-narrow is-striped">
    <thead>
    <tr>
      <th>Rank</th>
      <th>Athlete</th>
    </tr>
    </thead>
    <tbody>
    <tr>
      <td class="has-text-centered">1</td>
      <td>Athlete name</td>
    </tr>
    </tbody>
  </table>
</div>
```

## Sortable columns

Add `data-sortable` to the table and `data-sort` to each header cell that can be sorted. The
attribute value identifies the column; the script only checks that it is present.

```html
<table data-sortable class="table is-fullwidth is-hoverable is-narrow is-striped">
  <thead>
  <tr>
    <th data-sort="rank" class="is-narrow is-sorted-asc has-text-centered">
      <span class="icon-text">
        <span class="icon"><i class="et et-sort-ascending"></i></span>
        <span>Rank</span>
      </span>
    </th>
    <th data-sort="athlete">
      <span class="icon-text">
        <span class="icon"><i class="et et-sort"></i></span>
        <span>Athlete</span>
      </span>
    </th>
  </tr>
  </thead>
  <tbody>…</tbody>
</table>
```

### Behaviour

- Clicking a header sorts the rows by that column. The first click sorts ascending; clicking the
  same header again sorts descending.
- The header's icon changes to match: `et-sort` when not sorted, `et-sort-ascending` or
  `et-sort-descending` when sorted. The icon must be inside `.icon-text .icon i` for this to work.
- The sorted header gets `.is-sorted-asc` or `.is-sorted-desc`. The rows are not sorted on load,
  so if the markup is already in order, put `is-sorted-asc` on that header and set its icon to
  `et-sort-ascending` yourself, as in the example above.
- Values are compared as follows, in order:
    1. Numbers, compared as numbers.
    2. Durations in `HH:mm:ss` format, compared as text (which is correct for that format).
    3. Anything else, compared with the browser's locale, with numeric segments in text sorted
       naturally (`2` before `10`).
- Empty cells always sort to the bottom, in either direction.
- The sort reads the visible text of the cell (`textContent`), so format values as you want them
  shown. Each row must have one `<td>` per column, in the same order as the headers.

### Styling

`th[data-sort]` gets a pointer cursor, `user-select: none`, and `nowrap`. Do not add your own
cursor styling to sortable headers.

## Expandable rows

Expandable rows behave like the HTML `<details>` element. A row can be expanded to show a
detail row below it, which is hidden until the user expands the row. This suits longer data that
would make the table too wide, such as a command's output or a list of splits.

Add `data-expandable` to the table. Each expandable row has `data-expandable-row`, and it is
directly followed by a detail row with `data-expandable-detail` and the `is-hidden` class.

```html
<table class="table is-fullwidth is-hoverable is-narrow">
  <thead>
  <tr>
    <th></th>
    <th>Command</th>
    <th>Status</th>
  </tr>
  </thead>
  <tbody>
  <tr data-expand-row>
    <td>
      <button type="button" data-expand aria-expanded="false" class="expand"></button>
    </td>
    <td><code>prepare</code></td>
    <td><span class="tag is-small is-success">Succeeded</span></td>
  </tr>
  <tr data-expand-content class="is-hidden">
    <td>
      <pre class="box has-background-dark has-text-light p-3">Command output…</pre>
    </td>
  </tr>
  </tbody>
</table>
```

### Behaviour

- The trigger (`data-expand`) always toggles on click. Add `data-expand-row` to the row to make
  the whole row a toggle as well (a convenience for wide rows); clicks on links, buttons, form
  controls and labels inside the row then do their own job and do not toggle the row. Omitting
  `data-expand-row` leaves only the button as the trigger.
- The trigger carries `data-expand` and the `expand`/`close` class (chevron-down / chevron-up);
  its `aria-expanded` is kept in sync. Use a real `<button>`, not a link or an icon, so it can
  be reached with the keyboard.
- The `data-expand-content` row must sit directly below its row. Its `colspan` is set
  automatically to the number of cells in the row; you do not need to write it.
- Rows without a `data-expand` trigger, or without a `data-expand-content` row directly below
  them, are left alone.

### Attributes

| Attribute             | Applied to                          | Required? | Purpose                                              |
|-----------------------|-------------------------------------|-----------|------------------------------------------------------|
| `data-expand`         | Trigger button (box or table)       | Yes       | Behaviour hook; carries `aria-expanded`              |
| `data-expand-content` | Region revealed on expand           | Yes       | Content shown when expanded; starts with `is-hidden` |
| `data-expand-row`     | `<tr>` wrapping the trigger         | No        | Makes the whole row clickable as a toggle            |

### Tips

- Do not combine `is-striped` with expandable rows. The striping counts detail rows too, so the
  stripes end up uneven.
- Keep the trigger in the first cell so the chevron lines up in every row.

The shared expand stylesheet (`src/scss/elements/endurance-expand.scss`) must be loaded for the
chevron icons to render; it is forwarded from `src/scss/style.scss`.

## Sorting and expanding together

`data-sortable` and `data-expandable` can be used on the same table. When the rows are sorted,
each detail row moves with its parent row, so expanded content stays with the correct item.

```html
<table data-sortable data-expandable class="table is-fullwidth is-narrow">
  …
</table>
```

Put the sort headers on the data columns, and leave the toggle column without `data-sort`.
