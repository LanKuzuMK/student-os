/* Accessibility foundation: provide a consistent keyboard route past persistent navigation. */
(() => {
  const main = document.querySelector('main');
  if (!main) return;
  if (!main.id) main.id = 'main-content';
  if (document.querySelector('.skip-link')) return;
  const skip = document.createElement('a');
  skip.className = 'skip-link';
  skip.href = '#main-content';
  skip.textContent = 'Skip to main content';
  document.body.prepend(skip);
})();
