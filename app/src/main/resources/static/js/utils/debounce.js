let debounceTimer;

export function debounce(callback, delay) {
  return function (...args) {
    clearTimeout(debounceTimer);
    debounceTimer = setTimeout(() => {
      callback.apply(this, args);
    }, delay);
  };
};
