# Tables

> Part of the [EnduranceTrio Style Guide](./index.md).

Tables use Bulma's [table](https://bulma.io/documentation/elements/table/) styling. On top of it,
the project adds a **sortable columns** behaviour. It is opt-in through a `data-*` attribute on the
`<table>`.

Sources:

| Feature          | Styles                                        | Behaviour                    |
|------------------|-----------------------------------------------|------------------------------|
| Sortable columns | `src/scss/elements/endurance-table-sort.scss` | `src/js/bulma-table-sort.js` |

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
