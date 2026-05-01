// Theme management service

// Theme list
export const themes = [
  { value: 'default', label: 'Default Theme' },
  { value: 'dark', label: 'Dark Theme' },
  { value: 'light', label: 'Light Theme' }
]

// Get current theme
export function getCurrentTheme() {
  return localStorage.getItem('theme') || 'default'
}

// Set theme
export function setTheme(theme: string) {
  localStorage.setItem('theme', theme)
  document.documentElement.setAttribute('data-theme', theme)
}

// Initialize theme
export function initTheme() {
  const theme = getCurrentTheme()
  setTheme(theme)
}
