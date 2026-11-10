/*
 * Copyright (c) 2011-2026 Ricardo do Canto
 *
 * This file is part of the EnduranceTrio project.
 *
 * Licensed under the Functional Software License (FSL), Version 1.1, ALv2 Future License
 * (the "License");
 *
 * You may not use this file except in compliance with the License. You may obtain a copy
 * of the License at https://fsl.software/
 *
 * THE SOFTWARE IS PROVIDED "AS IS" AND WITHOUT WARRANTIES OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING WITHOUT LIMITATION WARRANTIES OF FITNESS FOR A PARTICULAR
 * PURPOSE, MERCHANTABILITY, TITLE OR NON-INFRINGEMENT.
 *
 * IN NO EVENT WILL WE HAVE ANY LIABILITY TO YOU ARISING OUT OF OR RELATED TO THE
 * SOFTWARE, INCLUDING INDIRECT, SPECIAL, INCIDENTAL OR CONSEQUENTIAL DAMAGES,
 * EVEN IF WE HAVE BEEN INFORMED OF THEIR POSSIBILITY IN ADVANCE.
 */

/**
 * Object providing expandable Bulma table rows, similar to the HTML `<details>` element.
 *
 * @namespace bulmaTableExpand
 */
const bulmaTableExpand = {
  /**
   * Initialises expandable rows on tables with a `data-expandable` attribute.
   *
   * Each expandable row is followed directly by a hidden detail row that spans the table.
   * The detail row `colspan` is set automatically to the number of cells in the row.
   *
   * The required markup structure is:
   *
   * ```html
   * <table data-expandable class="table is-fullwidth is-narrow">
   *   <tbody>
   *     <tr data-expandable-row>
   *       <td>
   *         <button type="button" data-expandable-toggle aria-expanded="false"
   *                 class="button is-ghost is-small px-0">
   *           <span class="icon"><i class="et et-chevron-right"></i></span>
   *         </button>
   *       </td>
   *       <td>…</td>
   *     </tr>
   *     <tr data-expandable-detail class="is-hidden">
   *       <td>…</td>
   *     </tr>
   *   </tbody>
   * </table>
   * ```
   *
   * **Data attributes used:**
   *
   * | Attribute               | Applied to                  | Purpose                                              |
   * |-------------------------|-----------------------------|------------------------------------------------------|
   * | `data-expandable`       | `<table>`                   | Hooks initialisation                                 |
   * | `data-expandable-row`   | Each expandable `<tr>`      | Row that toggles; clicking anywhere on it expands it |
   * | `data-expandable-toggle` | Button inside the row      | Visual and keyboard toggle, carries `aria-expanded`  |
   * | `data-expandable-detail`| Detail `<tr>` below the row | Hidden with `is-hidden` until the row is expanded    |
   *
   * Clicks on links, form controls and labels inside the row are not treated as toggles.
   * Rows without a toggle button or a following detail row are left untouched.
   *
   * @function init
   * @memberof bulmaTableExpand
   * @see {@link https://bulma.io/documentation/elements/table/}
   */
  init() {
    document.querySelectorAll('table[data-expandable]').forEach((table) => {
      table.querySelectorAll('tbody > tr[data-expandable-row]').forEach((row) => {
        const toggle = row.querySelector('[data-expandable-toggle]');
        const detail = row.nextElementSibling;
        if (!toggle || !detail?.hasAttribute('data-expandable-detail')) {
          return;
        }

        const detailCell = detail.cells[0];
        if (detailCell) {
          detailCell.colSpan = row.cells.length;
        }

        row.addEventListener('click', (event) => {
          const interactive = event.target.closest('a, button, input, select, textarea, label');
          if (interactive && interactive !== toggle) {
            return;
          }
          this.toggle(row);
        });
      });
    });
  },

  /**
   * Expands or collapses the detail row that follows an expandable row.
   *
   * Updates `aria-expanded` on the toggle button and the `is-hidden` class on the detail row.
   *
   * @function toggle
   * @memberof bulmaTableExpand
   * @param {HTMLTableRowElement} row - The expandable row whose detail row should be toggled.
   */
  toggle(row) {
    const toggle = row.querySelector('[data-expandable-toggle]');
    const detail = row.nextElementSibling;
    const expanded = toggle.getAttribute('aria-expanded') === 'true';

    toggle.setAttribute('aria-expanded', String(!expanded));
    detail.classList.toggle('is-hidden', expanded);
  },
};

export default bulmaTableExpand;
