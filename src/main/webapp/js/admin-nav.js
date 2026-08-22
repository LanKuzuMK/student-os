/* Design direction: compact-screen control room navigation with the same calm, intentional behavior as the student workspace. */
(() => {
  const rail = document.querySelector('.admin-rail');
  if (!rail || document.querySelector('.admin-menu-toggle')) return;

  const themeScript = document.createElement('script');
  themeScript.src = '/js/theme-toggle.js?v=theme-control-2';
  document.head.append(themeScript);

  const accessibilityScript = document.createElement('script');
  accessibilityScript.src = '/js/accessibility.js?v=accessibility-1';
  document.head.append(accessibilityScript);

  const navigation = rail.querySelector('.admin-nav');
  if (navigation && !navigation.querySelector('a[href="/admin/health"]')) {
    const healthLink = document.createElement('a');
    healthLink.href = '/admin/health';
    healthLink.textContent = 'System health';
    const backToApp = navigation.querySelector('a[href="/dashboard"]');
    navigation.insertBefore(healthLink, backToApp || null);
  }

  const toggle = document.createElement('button');
  toggle.type = 'button';
  toggle.className = 'admin-menu-toggle';
  toggle.setAttribute('aria-label', 'Open administrator navigation');
  toggle.setAttribute('aria-expanded', 'false');
  toggle.innerHTML = '<span aria-hidden="true"></span>';

  const backdrop = document.createElement('button');
  backdrop.type = 'button';
  backdrop.className = 'admin-menu-backdrop';
  backdrop.setAttribute('aria-label', 'Close administrator navigation');

  const close = () => {
    document.body.classList.remove('admin-nav-open');
    toggle.setAttribute('aria-label', 'Open administrator navigation');
    toggle.setAttribute('aria-expanded', 'false');
  };
  const open = () => {
    document.body.classList.add('admin-nav-open');
    toggle.setAttribute('aria-label', 'Close administrator navigation');
    toggle.setAttribute('aria-expanded', 'true');
    rail.querySelector('a')?.focus();
  };

  toggle.addEventListener('click', () => document.body.classList.contains('admin-nav-open') ? close() : open());
  backdrop.addEventListener('click', close);
  rail.querySelectorAll('a').forEach((link) => link.addEventListener('click', close));
  document.addEventListener('keydown', (event) => { if (event.key === 'Escape') close(); });
  window.addEventListener('resize', () => { if (window.innerWidth > 900) close(); });
  document.body.append(toggle, backdrop);
})();
