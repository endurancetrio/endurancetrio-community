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
 * Reusable expand/collapse behaviour for boxes and table rows.
 *
 * A trigger carrying the `data-expand` attribute (and the `expand`/`close` class for its
 * icon) toggles the visibility of the region marked `data-expand-content` that follows it
 * at the same nesting level. On a `tr[data-expand-row]` the whole row toggles the same way.
 *
 * No ids or content-derived names are required: the relationship is structural.
 *
 * @namespace enduranceExpand
 */
const enduranceExpand = {
  /** @type {number} */
  _nextId: 1,

  init() {
    document.querySelectorAll('[data-expand]').forEach((trigger) => {
      const row = trigger.closest('tr');
      // Target resolution uses the containing row when the trigger lives in a table,
      // so the detail row (the row's next sibling) is found even without data-expand-row.
      const scope = row || trigger;
      const wholeRow = !!(row && row.hasAttribute('data-expand-row'));

      const target = this._findTarget(scope);
      if (!target) {
        return;
      }

      // Auto-size the detail cell when the target is a table row.
      if (
        target instanceof window.HTMLTableRowElement &&
        scope instanceof window.HTMLTableRowElement &&
        target.cells[0]
      ) {
        target.cells[0].colSpan = scope.cells.length;
      }

      // Make sure the target has an id so aria-controls can reference it.
      if (!target.id) {
        target.id = `et-expand-${this._nextId++}`;
      }
      trigger.setAttribute('aria-controls', target.id);

      this._sync(trigger, target);

      if (wholeRow && row) {
        row.addEventListener('click', (event) => {
          const interactive = event.target.closest('a, button, input, select, textarea, label');
          if (interactive && interactive !== trigger) {
            return;
          }
          this.toggle(trigger, target);
        });
      } else {
        trigger.addEventListener('click', () => this.toggle(trigger, target));
      }
    });
  },

  /**
   * Finds the region that follows `scope` at the same level.
   *
   * @param {Element} scope
   * @returns {Element|null}
   */
  _findTarget(scope) {
    for (let el = scope.nextElementSibling; el; el = el.nextElementSibling) {
      if (el.hasAttribute('data-expand-content')) {
        return el;
      }
    }
    return null;
  },

  /**
   * Toggles a region's visibility and the trigger's state classes.
   *
   * @param {Element} trigger
   * @param {Element} target
   */
  toggle(trigger, target) {
    const expanded = !target.classList.contains('is-hidden');
    target.classList.toggle('is-hidden', expanded);
    trigger.classList.toggle('expand', expanded);
    trigger.classList.toggle('close', !expanded);
    trigger.setAttribute('aria-expanded', String(!expanded));
  },

  /**
   * Syncs aria-expanded with the current visibility of the target.
   *
   * @param {Element} trigger
   * @param {Element} target
   */
  _sync(trigger, target) {
    trigger.setAttribute('aria-expanded', String(!target.classList.contains('is-hidden')));
  },
};

export default enduranceExpand;
