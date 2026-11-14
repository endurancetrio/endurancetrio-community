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
 * Object containing Endurance Accordion functionality.
 *
 * @namespace enduranceAccordion
 */
const enduranceAccordion = {
  /**
   * Initializes the Endurance Accordion functionality.
   *
   * This function sets up event listeners on all elements with the class
   * 'endurance-accordion-trigger'. When clicked, it toggles the visibility of
   * the target element referenced by the 'data-accordion-target' attribute and
   * updates 'aria-expanded'.
   *
   * @function init
   * @memberof enduranceAccordion
   */
  init() {
    document.querySelectorAll('.endurance-accordion-trigger').forEach((trigger) => {
      trigger.addEventListener('click', () => {
        const target = document.getElementById(trigger.dataset.accordionTarget);
        const isExpanded = trigger.getAttribute('aria-expanded') === 'true';

        target.classList.toggle('is-hidden');
        trigger.setAttribute('aria-expanded', String(!isExpanded));
      });
    });
  },
};

export default enduranceAccordion;
