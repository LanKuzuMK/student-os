/* Design direction: an understated sidebar control that lets the workspace shift between calm light and focused dark modes. */
(() => {
  const storageKey = 'studentos-theme';
  const root = document.documentElement;

  const readThemeCookie = () => {
    const prefix = `${storageKey}=`;
    const entry = document.cookie.split('; ').find((value) => value.startsWith(prefix));
    const value = entry?.slice(prefix.length);
    return value === 'light' || value === 'dark' ? value : null;
  };

  const preferredTheme = () => {
    const cookieTheme = readThemeCookie();
    if (cookieTheme) return cookieTheme;
    try {
      const saved = window.localStorage.getItem(storageKey);
      if (saved === 'light' || saved === 'dark') return saved;
    } catch (_) { /* Storage can be unavailable in privacy-restricted browsers. */ }
    return 'light';
  };

  const updateTheme = (theme) => {
    root.dataset.theme = theme;
    const color = theme === 'dark' ? '#0d172a' : '#f5f7fb';
    document.querySelector('meta[name="theme-color"]')?.setAttribute('content', color);
    document.querySelectorAll('[data-theme-option]').forEach((option) => {
      const selected = option.dataset.themeOption === theme;
      option.classList.toggle('is-active', selected);
      option.setAttribute('aria-pressed', String(selected));
    });
  };

  const createControl = (rail) => {
    if (rail.querySelector('.theme-control')) return;
    const control = document.createElement('div');
    control.className = 'theme-control';
    control.setAttribute('role', 'group');
    control.setAttribute('aria-label', 'Appearance');
    control.innerHTML = '<span class="theme-control-eyebrow">Appearance</span><div class="theme-segmented-control"><button type="button" class="theme-segment" data-theme-option="light" aria-pressed="true"><span class="theme-segment-symbol theme-segment-sun" aria-hidden="true"></span>Light</button><button type="button" class="theme-segment" data-theme-option="dark" aria-pressed="false"><span class="theme-segment-symbol theme-segment-moon" aria-hidden="true"></span>Dark</button></div>';
    const footer = rail.querySelector('.sidebar-footer, .admin-rail-footer');
    if (footer) footer.insertBefore(control, footer.firstChild); else rail.append(control);
    control.querySelectorAll('[data-theme-option]').forEach((option) => option.addEventListener('click', () => {
      const next = option.dataset.themeOption;
      if (next === root.dataset.theme) return;
      try { window.localStorage.setItem(storageKey, next); } catch (_) { /* Keep the in-page preference if storage is unavailable. */ }
      document.cookie = `${storageKey}=${next}; Max-Age=31536000; Path=/; SameSite=Lax`;
      updateTheme(next);
    }));
  };

  document.querySelectorAll('.sidebar, .admin-rail').forEach(createControl);
  const initialTheme = preferredTheme();
  try { window.localStorage.setItem(storageKey, initialTheme); } catch (_) { /* The cookie still keeps the visual preference when storage is unavailable. */ }
  if (!readThemeCookie()) document.cookie = `${storageKey}=${initialTheme}; Max-Age=31536000; Path=/; SameSite=Lax`;
  updateTheme(initialTheme);
})();
