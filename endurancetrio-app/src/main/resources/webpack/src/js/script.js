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

import '../scss/style.scss';

import * as klaro from 'klaro/dist/klaro-no-css.js';
import klaroConfig from './klaro-config.js';

import bulmaModal from './bulma-modal.js';
import bulmaNavbar from './bulma-navbar.js';
import bulmaTab from './bulma-tab.js';
import bulmaTableExpand from './bulma-table-expand.js';
import bulmaTableSort from './bulma-table-sort.js';
import languageSelector from './language-selector.js';

document.addEventListener('DOMContentLoaded', () => {
  const currentLanguage = document.documentElement.lang === 'pt' ? 'pt' : 'en';

  klaro.setup({
    ...klaroConfig,
    privacyPolicy: `/${currentLanguage}/privacy-policy#cookies-policy`,
  });

  document.getElementById('cookies-preferences-link')?.addEventListener('click', (event) => {
    event.preventDefault();
    klaro.show(klaroConfig, true);
  });

  bulmaModal.init();
  bulmaNavbar.init();
  bulmaTab.init();
  languageSelector.init();
  bulmaTableSort.init();
  bulmaTableExpand.init();
});
