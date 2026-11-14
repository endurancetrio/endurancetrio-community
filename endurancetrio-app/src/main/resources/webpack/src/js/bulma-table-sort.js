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
 * Client-side table sorting for Bulma-styled tables with `data-sortable` attribute.
 *
 * Supports numeric, duration (HH:mm:ss), and locale-aware string comparison.
 * Empty/null cells are always sorted to the end regardless of direction.
 */
const bulmaTableSort = {
  /** Initializes sort listeners on all tables with `data-sortable`. */
  init() {
    document.querySelectorAll('table[data-sortable]').forEach((table) => {
      const tbody = table.querySelector('tbody');
      const headers = table.querySelectorAll('thead th[data-sort]');

      headers.forEach((th) => {
        th.addEventListener('click', () => this.sort(table, tbody, th));
      });
    });
  },

  /** Updates the sort icon on a header element. */
  setIcon(th, iconName) {
    const icon = th.querySelector('.icon-text .icon i');
    if (icon) {
      icon.className = `et et-${iconName}`;
    }
  },

  /** Sorts the table body rows by the clicked column, toggling ascending/descending. */
  sort(table, tbody, clickedTh) {
    const isAsc = clickedTh.classList.contains('is-sorted-asc');

    // Reset all headers to neutral sort icon and remove state classes
    table.querySelectorAll('thead th[data-sort]').forEach((th) => {
      th.classList.remove('is-sorted-asc', 'is-sorted-desc');
      this.setIcon(th, 'sort');
    });

    // Set the clicked header's sort state and icon
    if (isAsc) {
      clickedTh.classList.add('is-sorted-desc');
      this.setIcon(clickedTh, 'sort-descending');
    } else {
      clickedTh.classList.add('is-sorted-asc');
      this.setIcon(clickedTh, 'sort-ascending');
    }

    const index = this.getCellIndex(clickedTh);
    const rows = Array.from(tbody.querySelectorAll(':scope > tr:not([data-expand-content])'));

    rows.sort((a, b) => {
      const aVal = (a.querySelector(`td:nth-child(${index})`)?.textContent ?? '').trim();
      const bVal = (b.querySelector(`td:nth-child(${index})`)?.textContent ?? '').trim();

      // Empty/null values always go last regardless of sort direction
      if (aVal === '' && bVal === '') return 0;
      if (aVal === '') return 1;
      if (bVal === '') return -1;

      const result = this.compare(aVal, bVal);
      return isAsc ? -result : result;
    });

    rows.forEach((row) => {
      const detail = row.nextElementSibling;
      tbody.appendChild(row);
      if (detail?.hasAttribute('data-expand-content')) {
        tbody.appendChild(detail);
      }
    });
  },

  /** Returns the 1-based column index of a header cell. */
  getCellIndex(th) {
    return Array.from(th.parentElement.children).indexOf(th) + 1;
  },

  /** Compares two cell values: numeric, then duration (HH:mm:ss), then locale-aware. */
  compare(a, b) {
    const na = Number(a);
    const nb = Number(b);

    if (!Number.isNaN(na) && !Number.isNaN(nb)) {
      return na - nb;
    }

    if (/^\d{2}:\d{2}:\d{2}$/.test(a) && /^\d{2}:\d{2}:\d{2}$/.test(b)) {
      return a.localeCompare(b);
    }

    return a.localeCompare(b, undefined, { numeric: true });
  },
};

export default bulmaTableSort;
