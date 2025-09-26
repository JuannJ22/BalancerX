const notification = document.querySelector('.notification');
const closeButton = document.querySelector('.notification__close');
const openButton = document.querySelector('.notification__open');

const openFile = () => {
  openButton.setAttribute('aria-pressed', 'true');
  openButton.classList.add('notification__open--active');
  setTimeout(() => {
    openButton.removeAttribute('aria-pressed');
    openButton.classList.remove('notification__open--active');
  }, 1200);
};

if (closeButton) {
  closeButton.addEventListener('click', () => {
    notification?.classList.add('notification--hidden');
  });
}

if (openButton) {
  openButton.addEventListener('click', (event) => {
    if (openButton.getAttribute('href') === '#') {
      event.preventDefault();
    }
    openFile();
  });
}
