(() => {
  const sidebar = document.querySelector('.sidebar');
  if (!sidebar || document.querySelector('.mobile-nav-toggle')) return;

  const themeScript = document.createElement('script');
  themeScript.src = '/js/theme-toggle.js?v=theme-control-1';
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
