const output = document.getElementById('authOutput');
const form = document.getElementById('loginForm');

const logResult = (value) => {
  output.textContent = typeof value === 'string' ? value : JSON.stringify(value, null, 2);
};

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const body = Object.fromEntries(new FormData(form).entries());
  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });
    const contentType = response.headers.get('content-type') || '';
    const result = contentType.includes('application/json') ? await response.json() : await response.text();
    if (!response.ok) throw new Error(typeof result === 'string' ? result : JSON.stringify(result));

    localStorage.setItem('bx_token', result.token);
    logResult('Acceso concedido. Abriendo panel...');
    window.location.href = '/app.html';
  } catch (error) {
    logResult(error.message);
  }
});
