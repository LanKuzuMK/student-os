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
    document.querySelectorAll('[data-theme-toggle]').forEach((toggle) => {
      const dark = theme === 'dark';
      toggle.setAttribute('aria-pressed', String(dark));
      toggle.setAttribute('aria-label', dark ? 'Switch to light mode' : 'Switch to dark mode');
      const label = toggle.closest('.theme-control')?.querySelector('[data-theme-label]');
      if (label) label.textContent = dark ? 'Dark mode' : 'Light mode';
    });
  };

  const createControl = (rail) => {
    if (rail.querySelector('.theme-control')) return;
    const control = document.createElement('div');
    control.className = 'theme-control';
    control.innerHTML = '<div class="theme-control-copy"><span class="theme-control-eyebrow">Appearance</span><strong data-theme-label>Light mode</strong></div><button type="button" class="theme-toggle" data-theme-toggle aria-pressed="false" aria-label="Switch to dark mode"><span class="theme-toggle-track" aria-hidden="true"><span class="theme-toggle-knob"></span></span></button>';
    const footer = rail.querySelector('.sidebar-footer, .admin-rail-footer');
    if (footer) footer.insertBefore(control, footer.firstChild); else rail.append(control);
    control.querySelector('[data-theme-toggle]').addEventListener('click', () => {
      const next = root.dataset.theme === 'dark' ? 'light' : 'dark';
      try { window.localStorage.setItem(storageKey, next); } catch (_) { /* Keep the in-page preference if storage is unavailable. */ }
      document.cookie = `${storageKey}=${next}; Max-Age=31536000; Path=/; SameSite=Lax`;
      updateTheme(next);
    });
  };

  document.querySelectorAll('.sidebar, .admin-rail').forEach(createControl);
  const initialTheme = preferredTheme();
  try { window.localStorage.setItem(storageKey, initialTheme); } catch (_) { /* The cookie still keeps the visual preference when storage is unavailable. */ }
  if (!readThemeCookie()) document.cookie = `${storageKey}=${initialTheme}; Max-Age=31536000; Path=/; SameSite=Lax`;
  updateTheme(initialTheme);
})();
