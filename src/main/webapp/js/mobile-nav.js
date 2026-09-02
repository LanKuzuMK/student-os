(() => {
  const sidebar = document.querySelector('.sidebar');
  if (!sidebar || document.querySelector('.mobile-nav-toggle')) return;

  const themeScript = document.createElement('script');
  themeScript.src = '/js/theme-toggle.js?v=theme-control-4';
  document.head.append(themeScript);

  const accessibilityScript = document.createElement('script');
  accessibilityScript.src = '/js/accessibility.js?v=accessibility-1';
  document.head.append(accessibilityScript);

  const toggle = document.createElement('button');
  toggle.type = 'button';
  toggle.className = 'mobile-nav-toggle';
  toggle.setAttribute('aria-label', 'Open navigation');
  toggle.setAttribute('aria-expanded', 'false');
  toggle.innerHTML = '<span class="mobile-nav-dots" aria-hidden="true"><span></span><span></span><span></span></span>';

  const backdrop = document.createElement('button');
  backdrop.type = 'button';
  backdrop.className = 'mobile-nav-backdrop';
  backdrop.setAttribute('aria-label', 'Close navigation');

  const closeMenu = () => {
    document.body.classList.remove('mobile-nav-open');
    toggle.setAttribute('aria-expanded', 'false');
    toggle.setAttribute('aria-label', 'Open navigation');
  };

  const openMenu = () => {
    document.body.classList.add('mobile-nav-open');
    toggle.setAttribute('aria-expanded', 'true');
    toggle.setAttribute('aria-label', 'Close navigation');
    sidebar.querySelector('a')?.focus();
  };

  toggle.addEventListener('click', () => {
    document.body.classList.contains('mobile-nav-open') ? closeMenu() : openMenu();
  });
  backdrop.addEventListener('click', closeMenu);
  sidebar.querySelectorAll('a').forEach((link) => link.addEventListener('click', closeMenu));
  document.addEventListener('keydown', (event) => {
    if (event.key === 'Escape') {
      closeMenu();
    }
  });
  window.addEventListener('resize', () => {
    if (window.innerWidth > 860) {
      closeMenu();
    }
  });

  document.body.append(toggle, backdrop);
})();

  // Global custom modal logic to replace native confirm()
  const createConfirmModal = (message, onConfirm) => {
    const overlay = document.createElement('div');
    overlay.className = 'custom-confirm-overlay';
    const modal = document.createElement('div');
    modal.className = 'custom-confirm-modal';
    const title = document.createElement('h3');
    title.textContent = 'Please confirm';
    const text = document.createElement('p');
    text.textContent = message;
    const actions = document.createElement('div');
    actions.className = 'custom-confirm-actions';
    const cancelBtn = document.createElement('button');
    cancelBtn.type = 'button';
    cancelBtn.className = 'btn btn-secondary';
    cancelBtn.textContent = 'Cancel';
    const confirmBtn = document.createElement('button');
    confirmBtn.type = 'button';
    confirmBtn.className = 'btn btn-primary';
    confirmBtn.textContent = 'OK';
    actions.append(cancelBtn, confirmBtn);
    modal.append(title, text, actions);
    overlay.append(modal);
    document.body.append(overlay);

    requestAnimationFrame(() => overlay.classList.add('show'));

    const close = () => {
      overlay.classList.remove('show');
      setTimeout(() => overlay.remove(), 200);
    };
    cancelBtn.addEventListener('click', close);
    overlay.addEventListener('click', (e) => { if (e.target === overlay) close(); });
    confirmBtn.addEventListener('click', () => { close(); onConfirm(); });
  };

  document.addEventListener('submit', (e) => {
    const form = e.target;
    if (form.hasAttribute('onsubmit')) {
      const match = form.getAttribute('onsubmit').match(/return\s+confirm\(['"](.*?)['"]\)/);
      if (match) {
        e.preventDefault();
        createConfirmModal(match[1], () => {
          form.removeAttribute('onsubmit');
          form.submit();
        });
      }
    }
  });

  document.addEventListener('click', (e) => {
    const el = e.target.closest('[onclick*="confirm("]');
    if (el) {
      const match = el.getAttribute('onclick').match(/return\s+confirm\(['"](.*?)['"]\)/);
      if (match) {
        e.preventDefault();
        e.stopPropagation();
        createConfirmModal(match[1], () => {
          el.removeAttribute('onclick');
          el.click();
        });
      }
    }
  }, true);
