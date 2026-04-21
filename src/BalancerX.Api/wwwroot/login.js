const output = document.getElementById('authOutput');
const form = document.getElementById('loginForm');

const MESSAGE_BY_STATUS = {
  400: 'Solicitud inválida. Revisa los datos ingresados e inténtalo de nuevo.',
  401: 'Usuario o contraseña inválidos.',
  403: 'No tienes permisos para acceder al sistema.',
  404: 'Servicio de autenticación no disponible.',
  429: 'Demasiados intentos. Espera unos minutos antes de volver a intentar.',
  500: 'Ocurrió un error interno en el servidor. Inténtalo más tarde.',
  503: 'El servicio no está disponible temporalmente por un problema de conectividad.'
};

const setOutput = (message, type = 'info') => {
  output.textContent = message;
  output.dataset.state = type;
};

const toUserFacingMessage = ({ status, payload }) => {
  if (typeof payload === 'string' && payload.trim().length > 0) {
    return payload;
  }

  if (payload && typeof payload === 'object') {
    if (typeof payload.detail === 'string' && payload.detail.trim().length > 0) {
      return payload.detail;
    }

    if (typeof payload.title === 'string' && payload.title.trim().length > 0) {
      return payload.title;
    }

    if (typeof payload.message === 'string' && payload.message.trim().length > 0) {
      return payload.message;
    }
  }

  return MESSAGE_BY_STATUS[status] || 'No fue posible iniciar sesión. Inténtalo nuevamente.';
};

form.addEventListener('submit', async (event) => {
  event.preventDefault();
  const body = Object.fromEntries(new FormData(form).entries());

  setOutput('Validando credenciales...', 'info');

  try {
    const response = await fetch('/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(body)
    });

    const contentType = response.headers.get('content-type') || '';
    const payload = contentType.includes('application/json') ? await response.json() : await response.text();

    if (!response.ok) {
      const loginError = new Error(toUserFacingMessage({ status: response.status, payload }));
      loginError.status = response.status;
      throw loginError;
    }

    localStorage.setItem('bx_token', payload.token);
    setOutput('Acceso concedido. Abriendo panel...', 'success');
    window.location.href = '/app.html';
  } catch (error) {
    const message = error?.message || 'No fue posible iniciar sesión por un error de conexión.';
    setOutput(message, 'error');
  }
});
