const output = document.getElementById('output');
const sectionTitle = document.getElementById('sectionTitle');
const sectionHint = document.getElementById('sectionHint');
const sessionUser = document.getElementById('sessionUser');
let token = localStorage.getItem('bx_token') || '';

if (!token) window.location.href = '/login.html';

const panelMeta = {
  transferPanel: { title: 'Transferencias', hint: 'Crear, listar y operar transferencias de forma segura.' },
  usersPanel: { title: 'Usuarios', hint: 'Módulo solo para administradores.' },
  profilePanel: { title: 'Mi perfil', hint: 'Gestión de contraseña y firma electrónica personal.' }
};

const decodeJwtPayload = (jwt) => {
  try {
    const [, payload] = jwt.split('.');
    return JSON.parse(atob(payload.replace(/-/g, '+').replace(/_/g, '/')));
  } catch {
    return {};
  }
};

const claims = decodeJwtPayload(token);
const roleClaim = claims.role || claims['http://schemas.microsoft.com/ws/2008/06/identity/claims/role'] || '';
const role = String(roleClaim).toUpperCase();
const userName = claims.unique_name || claims.name || claims.sub || 'usuario';
const isAdmin = role === 'ADMIN';
const isTesoreria = role === 'TESORERIA';
const canPrint = isAdmin || isTesoreria;

sessionUser.textContent = `${userName} · ${role || 'ROL'}`;

const logResult = (value) => {
  output.textContent = typeof value === 'string' ? value : JSON.stringify(value, null, 2);
};

const api = async (url, opts = {}) => {
  const headers = opts.headers ? { ...opts.headers } : {};
  if (token) headers.Authorization = `Bearer ${token}`;
  const response = await fetch(url, { ...opts, headers });
  const contentType = response.headers.get('content-type') || '';
  const body = contentType.includes('application/json') ? await response.json() : await response.text();
  if (!response.ok) throw new Error(typeof body === 'string' ? body : JSON.stringify(body));
  return body;
};

const setVisiblePanel = (panelId) => {
  document.querySelectorAll('.panel').forEach((panel) => panel.classList.remove('visible'));
  document.getElementById(panelId)?.classList.add('visible');
  document.querySelectorAll('.nav-btn').forEach((btn) => btn.classList.toggle('active', btn.dataset.target === panelId));
  sectionTitle.textContent = panelMeta[panelId]?.title || 'BalancerX';
  sectionHint.textContent = panelMeta[panelId]?.hint || '';
};

document.querySelectorAll('.role-admin').forEach((node) => node.classList.toggle('hidden', !isAdmin));
if (!isAdmin) {
  const usersBtn = document.querySelector('[data-target="usersPanel"]');
  if (usersBtn?.classList.contains('active')) setVisiblePanel('transferPanel');
}

for (const navBtn of document.querySelectorAll('.nav-btn')) {
  navBtn.addEventListener('click', () => {
    if (navBtn.classList.contains('hidden')) return;
    setVisiblePanel(navBtn.dataset.target);
  });
}

document.getElementById('logoutBtn').addEventListener('click', () => {
  localStorage.removeItem('bx_token');
  token = '';
  window.location.href = '/login.html';
});

const asNumber = (value) => Number(value ?? 0);

const listTransfers = async () => {
  try {
    const result = await api('/api/transferencias');
    const items = Array.isArray(result) ? result : [];
    const tbody = document.getElementById('transferTableBody');
    tbody.innerHTML = '';
    items.forEach((item) => tbody.appendChild(renderTransferRow(item)));
    logResult({ total: items.length });
  } catch (error) {
    logResult(error.message);
  }
};

document.getElementById('createTransferForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
  ['monto', 'puntoVentaId', 'vendedorId', 'bancoId', 'cuentaContableId'].forEach((k) => payload[k] = asNumber(payload[k]));
  try {
    logResult(await api('/api/transferencias', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) }));
    await listTransfers();
  } catch (error) { logResult(error.message); }
});

document.getElementById('updateTransferForm')?.addEventListener('submit', async (event) => {
  event.preventDefault();
  const f = new FormData(event.currentTarget);
  const id = asNumber(f.get('id'));
  const payload = {
    monto: asNumber(f.get('monto')),
    puntoVentaId: asNumber(f.get('puntoVentaId')),
    vendedorId: asNumber(f.get('vendedorId')),
    bancoId: asNumber(f.get('bancoId')),
    cuentaContableId: asNumber(f.get('cuentaContableId')),
    estado: String(f.get('estado') || ''),
    observacion: String(f.get('observacion') || '')
  };
  try {
    logResult(await api(`/api/transferencias/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) }));
    await listTransfers();
  } catch (error) { logResult(error.message); }
});

document.getElementById('uploadPdfForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const formData = new FormData(event.currentTarget);
  const id = formData.get('transferenciaId');
  try {
    logResult(await api(`/api/transferencias/${id}/archivo`, { method: 'POST', body: formData }));
    document.getElementById('pdfViewer').src = `/api/transferencias/${id}/archivo/visor`;
  } catch (error) { logResult(error.message); }
});

document.getElementById('viewerForm').addEventListener('submit', (event) => {
  event.preventDefault();
  const id = new FormData(event.currentTarget).get('transferenciaId');
  document.getElementById('pdfViewer').src = `/api/transferencias/${id}/archivo/visor`;
});

document.getElementById('passwordForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
  try { logResult(await api('/api/perfil/password', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) })); }
  catch (error) { logResult(error.message); }
});

document.getElementById('signatureForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  try { logResult(await api('/api/perfil/firma', { method: 'PUT', body: new FormData(event.currentTarget) })); }
  catch (error) { logResult(error.message); }
});

document.getElementById('listTransfersBtn').addEventListener('click', listTransfers);

const renderTransferRow = (item) => {
  const tr = document.createElement('tr');
  const created = item.creadoEnUtc ? new Date(item.creadoEnUtc).toLocaleString() : '-';
  tr.innerHTML = `<td>${item.id ?? '-'}</td><td>${Number(item.monto ?? 0).toFixed(2)}</td><td>${item.estado ?? '-'}</td><td>${item.bancoId ?? '-'}</td><td>${item.cuentaContableId ?? '-'}</td><td>${created}</td><td class="actions"></td>`;
  const actions = tr.querySelector('.actions');

  if (canPrint) {
    const printBtn = document.createElement('button');
    printBtn.textContent = 'Print';
    printBtn.onclick = async () => { try { logResult(await api(`/api/transferencias/${item.id}/print`, { method: 'POST' })); await listTransfers(); } catch (e) { logResult(e.message); } };
    actions.append(printBtn);
  }

  if (isAdmin) {
    const reprintBtn = document.createElement('button');
    reprintBtn.className = 'ghost';
    reprintBtn.textContent = 'Reprint';
    reprintBtn.onclick = async () => {
      const pinAdmin = window.prompt('PIN admin:');
      if (!pinAdmin) return;
      const razon = window.prompt('Razón:') || 'Sin observación';
      try { logResult(await api(`/api/transferencias/${item.id}/reprint`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ pinAdmin, razon }) })); } catch (e) { logResult(e.message); }
    };
    const delPdfBtn = document.createElement('button');
    delPdfBtn.className = 'danger';
    delPdfBtn.textContent = 'Borrar PDF';
    delPdfBtn.onclick = async () => { try { logResult(await api(`/api/transferencias/${item.id}/archivo`, { method: 'DELETE' })); } catch (e) { logResult(e.message); } };
    const delTxBtn = document.createElement('button');
    delTxBtn.className = 'danger';
    delTxBtn.textContent = 'Borrar';
    delTxBtn.onclick = async () => { try { logResult(await api(`/api/transferencias/${item.id}`, { method: 'DELETE' })); await listTransfers(); } catch (e) { logResult(e.message); } };
    actions.append(reprintBtn, delPdfBtn, delTxBtn);
  }

  const viewBtn = document.createElement('button');
  viewBtn.className = 'ghost';
  viewBtn.textContent = 'Ver PDF';
  viewBtn.onclick = () => document.getElementById('pdfViewer').src = `/api/transferencias/${item.id}/archivo/visor`;
  actions.append(viewBtn);

  return tr;
};

const listUsers = async () => {
  if (!isAdmin) return;
  try {
    const result = await api('/api/usuarios');
    const users = Array.isArray(result) ? result : [];
    const tbody = document.getElementById('usersTableBody');
    tbody.innerHTML = '';
    users.forEach((u) => {
      const tr = document.createElement('tr');
      tr.innerHTML = `<td>${u.id ?? '-'}</td><td>${u.usuario ?? '-'}</td><td>${u.rol ?? '-'}</td><td>${u.activo ? 'Sí' : 'No'}</td><td>${u.firmaElectronica ?? '-'}</td><td class='actions'></td>`;
      const delBtn = document.createElement('button');
      delBtn.className = 'danger';
      delBtn.textContent = 'Eliminar';
      delBtn.onclick = async () => { try { logResult(await api(`/api/usuarios/${u.id}`, { method: 'DELETE' })); await listUsers(); } catch (e) { logResult(e.message); } };
      tr.querySelector('.actions').append(delBtn);
      tbody.append(tr);
    });
  } catch (error) { logResult(error.message); }
};

document.getElementById('listUsersBtn')?.addEventListener('click', listUsers);

document.getElementById('createUserForm')?.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
  try {
    logResult(await api('/api/usuarios', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) }));
    await listUsers();
    event.currentTarget.reset();
  } catch (error) { logResult(error.message); }
});

listTransfers();
if (isAdmin) listUsers();
