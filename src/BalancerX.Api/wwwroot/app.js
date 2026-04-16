const sectionTitle = document.getElementById('sectionTitle');
const sectionHint = document.getElementById('sectionHint');
const sessionUser = document.getElementById('sessionUser');
const resultMessage = document.getElementById('resultMessage');
const resultTechnical = document.getElementById('resultTechnical');

let token = localStorage.getItem('bx_token') || '';
if (!token) window.location.href = '/login.html';

const panelMeta = {
  transferPanel: { title: 'Operación de transferencias', hint: 'Crea y consulta transferencias con catálogos predefinidos.' },
  adminPanel: { title: 'Funciones de administrador', hint: 'Gestión de usuarios y controles de administración.' },
  profilePanel: { title: 'Mi perfil', hint: 'Gestión de contraseña y firma electrónica personal.' }
};

const catalogs = {
  puntosVenta: [],
  vendedores: [],
  bancos: [],
  cuentasPorBanco: new Map()
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

const normalizeRoleValue = (value) => String(value || '')
  .split(',')
  .map((item) => item.trim().toUpperCase())
  .filter(Boolean);

const resolveRoles = (jwtClaims) => {
  const candidateKeys = [
    'role',
    'roles',
    'http://schemas.microsoft.com/ws/2008/06/identity/claims/role'
  ];

  const roles = [];
  candidateKeys.forEach((key) => {
    const claimValue = jwtClaims?.[key];
    if (Array.isArray(claimValue)) {
      claimValue.forEach((item) => roles.push(...normalizeRoleValue(item)));
      return;
    }

    roles.push(...normalizeRoleValue(claimValue));
  });

  return Array.from(new Set(roles));
};

const roles = resolveRoles(claims);
const hasRole = (expectedRole) => roles.includes(String(expectedRole || '').trim().toUpperCase());
const role = roles[0] || '';
const userName = claims.unique_name || claims.name || claims.sub || 'usuario';
const isAdmin = hasRole('ADMIN');
const isTesoreria = hasRole('TESORERIA');
const isAuxiliar = hasRole('AUXILIAR');
const canPrint = isAdmin || isTesoreria || isAuxiliar;
const canUpdateTransfer = isAdmin || isTesoreria;

sessionUser.textContent = `${userName} · ${roles.join(', ') || 'ROL'}`;

document.querySelectorAll('.role-admin').forEach((node) => node.classList.toggle('hidden', !isAdmin));
document.querySelectorAll('.role-update-transfer').forEach((node) => node.classList.toggle('hidden', !canUpdateTransfer));
document.querySelectorAll('.role-manage-pdf').forEach((node) => node.classList.toggle('hidden', isAuxiliar));
document.querySelectorAll('.role-signature-management').forEach((node) => node.classList.toggle('hidden', isAuxiliar));
document.querySelectorAll('.role-pdf-viewer').forEach((node) => node.classList.toggle('hidden', isAuxiliar));

const setCreateTransferAvailability = () => {
  const createTransferForm = document.getElementById('createTransferForm');
  const createTransferRestriction = document.getElementById('createTransferRestriction');
  if (!createTransferForm) return;

  createTransferRestriction?.classList.toggle('hidden', !isAuxiliar);
  const controls = createTransferForm.querySelectorAll('input, select, button, textarea');
  controls.forEach((control) => {
    control.disabled = isAuxiliar;
  });
};

setCreateTransferAvailability();

const showResult = (kind, title, technical) => {
  resultMessage.className = `result ${kind}`;
  resultMessage.textContent = title;
  resultTechnical.textContent = typeof technical === 'string' ? technical : JSON.stringify(technical, null, 2);
};

const parseApiError = async (response) => {
  const contentType = response.headers.get('content-type') || '';
  const payload = contentType.includes('application/json') ? await response.json() : await response.text();
  const title = payload?.title || payload?.Title || 'No se pudo completar la operación';
  const detail = payload?.detail || payload?.Detail || (typeof payload === 'string' ? payload : 'Error de validación.');
  return { friendly: `${title}. ${detail}`.trim(), technical: payload, status: response.status };
};


let pdfViewerObjectUrl = '';

const mostrarPdfEnVisor = async (transferenciaId) => {
  const response = await fetch(`/api/transferencias/${transferenciaId}/archivo/visor`, {
    headers: token ? { Authorization: `Bearer ${token}` } : {}
  });

  if (!response.ok) {
    const parsed = await parseApiError(response);
    showResult('error', parsed.friendly, parsed.technical);
    throw new Error(parsed.friendly);
  }

  const blob = await response.blob();
  if (pdfViewerObjectUrl) URL.revokeObjectURL(pdfViewerObjectUrl);
  pdfViewerObjectUrl = URL.createObjectURL(blob);
  document.getElementById('pdfViewer').src = pdfViewerObjectUrl;
};

const api = async (url, opts = {}) => {
  const headers = opts.headers ? { ...opts.headers } : {};
  if (token) headers.Authorization = `Bearer ${token}`;
  const response = await fetch(url, { ...opts, headers });
  if (!response.ok) {
    const parsed = await parseApiError(response);
    if (response.status === 401) {
      localStorage.removeItem('bx_token');
      showResult('error', 'Sesión expirada. Inicie sesión nuevamente.', parsed.technical);
      setTimeout(() => { window.location.href = '/login.html'; }, 800);
      throw new Error('Sesión expirada');
    }

    showResult('error', parsed.friendly, parsed.technical);
    throw new Error(parsed.friendly);
  }

  const contentType = response.headers.get('content-type') || '';
  return contentType.includes('application/json') ? response.json() : response.text();
};

const solicitarDatosReimpresion = () => {
  const usuarioEncargado = String(window.prompt('Usuario del encargado:') || '').trim();
  if (!usuarioEncargado) return null;
  const pinEncargado = String(window.prompt('PIN del encargado:') || '').trim();
  if (!pinEncargado) return null;
  const razon = String(window.prompt('Razón de reimpresión:') || '').trim();
  if (!razon) return null;
  return { usuarioEncargado, pinEncargado, razon };
};

const setVisiblePanel = (panelId) => {
  document.querySelectorAll('.panel').forEach((panel) => panel.classList.remove('visible'));
  document.getElementById(panelId)?.classList.add('visible');
  document.querySelectorAll('.nav-btn').forEach((btn) => btn.classList.toggle('active', btn.dataset.target === panelId));
  sectionTitle.textContent = panelMeta[panelId]?.title || 'BalancerX';
  sectionHint.textContent = panelMeta[panelId]?.hint || '';
};

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

const bindDatalist = (listId, items) => {
  const list = document.getElementById(listId);
  list.innerHTML = '';
  items.forEach((x) => {
    const option = document.createElement('option');
    option.value = `${x.id} - ${x.nombre}`;
    list.append(option);
  });
};

const normalizeText = (value) => String(value || '').trim().toLowerCase();

const resolveIdFromText = (text, items = []) => {
  const raw = String(text || '').trim();
  if (!raw) return 0;

  const matched = /^(\d+)\s*-/.exec(raw);
  if (matched) return Number(matched[1]);

  if (/^\d+$/.test(raw)) return Number(raw);

  const normalized = normalizeText(raw);
  const byName = items.find((x) => normalizeText(x.nombre) === normalized);
  return byName ? Number(byName.id) : 0;
};


const resolveCatalogName = (items, id) => {
  const item = items.find((x) => Number(x.id) === Number(id));
  return item ? `${item.id} - ${item.nombre}` : (id ?? '-');
};

const fillBankSelect = (selectId, bancos) => {
  const select = document.getElementById(selectId);
  select.innerHTML = '<option value="">Seleccione banco...</option>';
  bancos.forEach((b) => {
    const option = document.createElement('option');
    option.value = String(b.id);
    option.textContent = b.nombre;
    select.append(option);
  });
};

const fillCuentaSelect = async (selectId, bancoId) => {
  const select = document.getElementById(selectId);
  select.innerHTML = '<option value="">Seleccione cuenta...</option>';
  if (!bancoId) return;

  let cuentas = catalogs.cuentasPorBanco.get(bancoId);
  if (!cuentas) {
    cuentas = await api(`/api/catalogos/bancos/${bancoId}/cuentas-contables`);
    catalogs.cuentasPorBanco.set(bancoId, cuentas);
  }

  cuentas.forEach((c) => {
    const option = document.createElement('option');
    option.value = String(c.id);
    option.textContent = `${c.numeroCuenta} · ${c.descripcion}`;
    select.append(option);
  });
};

const loadCatalogSafe = async (url, label) => {
  try {
    const data = await api(url);
    return { ok: true, data: Array.isArray(data) ? data : [], label };
  } catch (error) {
    return { ok: false, data: [], label, error: error?.message || 'Error cargando catálogo' };
  }
};

const loadCatalogs = async () => {
  const [puntosRes, vendedoresRes, bancosRes] = await Promise.all([
    loadCatalogSafe('/api/catalogos/puntos-venta', 'puntos de venta'),
    loadCatalogSafe('/api/catalogos/vendedores', 'vendedores'),
    loadCatalogSafe('/api/catalogos/bancos', 'bancos')
  ]);

  catalogs.puntosVenta = puntosRes.data;
  catalogs.vendedores = vendedoresRes.data;
  catalogs.bancos = bancosRes.data;

  bindDatalist('puntosVentaList', catalogs.puntosVenta);
  bindDatalist('vendedoresList', catalogs.vendedores);

  fillBankSelect('crearBancoId', catalogs.bancos);
  fillBankSelect('editModalBancoId', catalogs.bancos);

  const failed = [puntosRes, vendedoresRes, bancosRes].filter((x) => !x.ok);
  const details = {
    bancos: catalogs.bancos.length,
    puntosVenta: catalogs.puntosVenta.length,
    vendedores: catalogs.vendedores.length,
    errores: failed.map((x) => ({ catalogo: x.label, error: x.error }))
  };

  if (failed.length > 0 || catalogs.bancos.length === 0 || catalogs.vendedores.length === 0) {
    showResult('error', 'Catálogos cargados parcialmente. Revise permisos/SQL de catálogos.', details);
    return;
  }

  showResult('ok', 'Catálogos cargados correctamente.', details);
};

const buildTransferFiltersQuery = () => {
  const form = document.getElementById('transferFiltersForm');
  if (!form) return '';

  const data = new FormData(form);
  const puntoVentaId = resolveIdFromText(data.get('puntoVentaTexto'), catalogs.puntosVenta);
  const vendedorId = resolveIdFromText(data.get('vendedorTexto'), catalogs.vendedores);
  const fechaDesde = String(data.get('fechaDesde') || '').trim();
  const fechaHasta = String(data.get('fechaHasta') || '').trim();
  const estado = String(data.get('estado') || '').trim();
  const impresa = String(data.get('impresa') || '').trim();

  const params = new URLSearchParams();
  if (puntoVentaId > 0) params.set('puntoVentaId', String(puntoVentaId));
  if (vendedorId > 0) params.set('vendedorId', String(vendedorId));
  if (fechaDesde) params.set('fechaDesde', `${fechaDesde}T00:00:00`);
  if (fechaHasta) params.set('fechaHasta', `${fechaHasta}T23:59:59`);
  if (estado) params.set('estado', estado);
  if (impresa) params.set('impresa', impresa);

  const query = params.toString();
  return query ? `?${query}` : '';
};

const listTransfers = async (options = {}) => {
  const query = buildTransferFiltersQuery();
  const result = await api(`/api/transferencias${query}`);
  const items = Array.isArray(result) ? result : [];
  const tbody = document.getElementById('transferTableBody');
  tbody.innerHTML = '';
  items.forEach((item) => tbody.appendChild(renderTransferRow(item)));
  if (!options.suppressResult) {
    showResult('ok', `Transferencias cargadas: ${items.length}.`, { total: items.length, filtros: query || 'sin filtros', transferencias: items });
  }

  return items;
};

const ensureCreateTransferPdfField = () => {
  const createTransferForm = document.getElementById('createTransferForm');
  if (!createTransferForm) return;

  let pdfUploadField = createTransferForm.querySelector('.pdf-upload-field');
  let pdfInput = document.getElementById('createTransferPdfInput');
  let pdfName = document.getElementById('createTransferPdfName');
  let pdfClearButton = document.getElementById('createTransferPdfClearButton');

  if (!pdfUploadField || !pdfInput || !pdfName || !pdfClearButton) {
    pdfUploadField = document.createElement('div');
    pdfUploadField.className = 'pdf-upload-field';
    pdfUploadField.innerHTML = `
      <label for="createTransferPdfInput">PDF de soporte</label>
      <input id="createTransferPdfInput" name="archivoPdf" type="file" accept="application/pdf" />
      <div class="pdf-upload-actions">
        <span id="createTransferPdfName" class="file-name-placeholder">Sin archivo seleccionado</span>
        <button type="button" id="createTransferPdfClearButton" class="ghost">Quitar PDF</button>
      </div>
    `;

    const submitButton = createTransferForm.querySelector('button[type="submit"]');
    if (submitButton) {
      createTransferForm.insertBefore(pdfUploadField, submitButton);
    } else {
      createTransferForm.append(pdfUploadField);
    }

    pdfInput = document.getElementById('createTransferPdfInput');
    pdfName = document.getElementById('createTransferPdfName');
    pdfClearButton = document.getElementById('createTransferPdfClearButton');
  }

  pdfUploadField.classList.remove('hidden');
  pdfUploadField.style.display = 'grid';
};

ensureCreateTransferPdfField();

const createTransferPdfInput = document.getElementById('createTransferPdfInput');
const createTransferPdfName = document.getElementById('createTransferPdfName');
const createTransferPdfClearButton = document.getElementById('createTransferPdfClearButton');

const updateCreateTransferPdfName = () => {
  if (!createTransferPdfInput || !createTransferPdfName) return;
  const file = createTransferPdfInput.files?.[0];
  createTransferPdfName.textContent = file ? file.name : 'Sin archivo seleccionado';
};

createTransferPdfInput?.addEventListener('change', () => {
  updateCreateTransferPdfName();
});

createTransferPdfClearButton?.addEventListener('click', () => {
  if (!createTransferPdfInput) return;
  createTransferPdfInput.value = '';
  updateCreateTransferPdfName();
});

document.getElementById('crearBancoId').addEventListener('change', async (event) => {
  await fillCuentaSelect('crearCuentaContableId', asNumber(event.target.value));
});

document.getElementById('createTransferForm').addEventListener('submit', async (event) => {
  event.preventDefault();

  if (isAuxiliar) {
    showResult('error', 'El rol AUXILIAR no puede crear transferencias.', { rol: role });
    return;
  }

  const f = new FormData(event.currentTarget);
  const archivoPdf = f.get('archivoPdf');
  const payload = {
    monto: asNumber(f.get('monto')),
    puntoVentaId: resolveIdFromText(f.get('puntoVentaTexto'), catalogs.puntosVenta),
    vendedorId: resolveIdFromText(f.get('vendedorTexto'), catalogs.vendedores),
    bancoId: asNumber(f.get('bancoId')),
    cuentaContableId: asNumber(f.get('cuentaContableId')),
    observacion: String(f.get('observacion') || '')
  };

  try {
    const res = await api('/api/transferencias', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    const transferenciaId = Number(res?.id ?? 0);
    const tienePdf = archivoPdf instanceof File && archivoPdf.size > 0;

    if (tienePdf && transferenciaId > 0) {
      try {
        const pdfData = new FormData();
        pdfData.set('archivo', archivoPdf);
        const pdfRes = await api(`/api/transferencias/${transferenciaId}/archivo`, { method: 'POST', body: pdfData });
        showResult('ok', 'Transferencia creada y PDF adjuntado correctamente.', { transferencia: res, pdf: pdfRes });
      } catch (error) {
        showResult('warning', `Transferencia ${transferenciaId} creada, pero falló la carga del PDF. Puede adjuntarlo desde "Modificar".`, {
          transferencia: res,
          errorPdf: error?.message || 'No fue posible subir el archivo PDF en el alta.'
        });
      }
    } else {
      showResult('warning', 'Transferencia creada sin PDF adjunto.', res);
    }

    event.currentTarget.reset();
    updateCreateTransferPdfName();
    await fillCuentaSelect('crearCuentaContableId', 0);
    await listTransfers({ suppressResult: true });
  } catch { }
});


const estadosPermitidosTransferencia = ['SIN_IMPRIMIR', 'IMPRESA'];

const editTransferModal = document.getElementById('editTransferModal');
const editTransferModalForm = document.getElementById('editTransferModalForm');

const closeEditTransferModal = () => {
  if (!editTransferModal) return;
  editTransferModal.close();
  editTransferModalForm?.reset();
};

const cargarTransferenciaEnModalEdicion = async (id) => {
  if (!editTransferModalForm) return;

  const transferencia = await api(`/api/transferencias/${id}`);
  const estado = estadosPermitidosTransferencia.includes(String(transferencia.estado || '').toUpperCase())
    ? String(transferencia.estado).toUpperCase()
    : 'SIN_IMPRIMIR';

  editTransferModalForm.querySelector('[name="id"]').value = String(id);
  editTransferModalForm.querySelector('[name="idReadOnly"]').value = String(id);
  editTransferModalForm.querySelector('[name="monto"]').value = Number(transferencia.monto ?? 0);
  editTransferModalForm.querySelector('[name="puntoVentaTexto"]').value = resolveCatalogName(catalogs.puntosVenta, transferencia.puntoVentaId);
  editTransferModalForm.querySelector('[name="vendedorTexto"]').value = resolveCatalogName(catalogs.vendedores, transferencia.vendedorId);

  const bancoSelect = editTransferModalForm.querySelector('[name="bancoId"]');
  bancoSelect.value = String(transferencia.bancoId ?? '');
  await fillCuentaSelect('editModalCuentaContableId', asNumber(transferencia.bancoId));

  const cuentaSelect = editTransferModalForm.querySelector('[name="cuentaContableId"]');
  cuentaSelect.value = String(transferencia.cuentaContableId ?? '');

  editTransferModalForm.querySelector('[name="estado"]').value = estado;
  editTransferModalForm.querySelector('[name="observacion"]').value = transferencia.observacion || '';

  showResult('ok', `Transferencia ${id} cargada para edición.`, transferencia);
};

const abrirModalEdicionTransferencia = async (id) => {
  if (!editTransferModal || !editTransferModalForm) return;
  if (!canUpdateTransfer) {
    showResult('error', 'El rol actual no tiene permiso para modificar transferencias.', { rol: role });
    return;
  }

  await cargarTransferenciaEnModalEdicion(id);
  editTransferModal.showModal();
};

document.getElementById('editModalBancoId')?.addEventListener('change', async (event) => {
  await fillCuentaSelect('editModalCuentaContableId', asNumber(event.target.value));
});

document.getElementById('closeEditTransferModalBtn')?.addEventListener('click', () => {
  closeEditTransferModal();
});

editTransferModal?.addEventListener('cancel', (event) => {
  event.preventDefault();
  closeEditTransferModal();
});

editTransferModalForm?.addEventListener('submit', async (event) => {
  event.preventDefault();
  if (!canUpdateTransfer) {
    showResult('error', 'El rol actual no tiene permiso para modificar transferencias.', { rol: role });
    return;
  }

  const f = new FormData(event.currentTarget);
  const id = asNumber(f.get('id'));
  const payload = {
    monto: asNumber(f.get('monto')),
    puntoVentaId: resolveIdFromText(f.get('puntoVentaTexto'), catalogs.puntosVenta),
    vendedorId: resolveIdFromText(f.get('vendedorTexto'), catalogs.vendedores),
    bancoId: asNumber(f.get('bancoId')),
    cuentaContableId: asNumber(f.get('cuentaContableId')),
    estado: String(f.get('estado') || ''),
    observacion: String(f.get('observacion') || '')
  };

  try {
    const res = await api(`/api/transferencias/${id}`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });

    const archivo = f.get('archivo');
    if (archivo instanceof File && archivo.size > 0) {
      const pdfData = new FormData();
      pdfData.set('archivo', archivo);
      const pdfRes = await api(`/api/transferencias/${id}/archivo`, { method: 'POST', body: pdfData });
      showResult('ok', 'Transferencia y PDF actualizados correctamente.', { transferencia: res, pdf: pdfRes });
    } else {
      showResult('ok', 'Transferencia actualizada correctamente.', res);
    }

    await listTransfers();
    closeEditTransferModal();
  } catch { }
});

document.getElementById('viewerForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const id = new FormData(event.currentTarget).get('transferenciaId');
  try {
    await mostrarPdfEnVisor(id);
    showResult('ok', `Visor actualizado para transferencia ${id}.`, { transferenciaId: id });
  } catch { }
});

document.getElementById('passwordForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
  try {
    const res = await api('/api/perfil/password', { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    showResult('ok', 'Contraseña actualizada correctamente.', res);
  } catch { }
});

document.getElementById('signatureForm').addEventListener('submit', async (event) => {
  event.preventDefault();
  try {
    const res = await api('/api/perfil/firma', { method: 'PUT', body: new FormData(event.currentTarget) });
    showResult('ok', 'Firma electrónica actualizada correctamente.', res);
  } catch { }
});

document.getElementById('listTransfersBtn').addEventListener('click', async () => {
  try { await listTransfers(); } catch { }
});

document.getElementById('transferFiltersForm')?.addEventListener('submit', async (event) => {
  event.preventDefault();
  try { await listTransfers(); } catch { }
});

document.getElementById('clearTransferFiltersBtn')?.addEventListener('click', async () => {
  const form = document.getElementById('transferFiltersForm');
  form?.reset();
  try { await listTransfers(); } catch { }
});

const renderTransferRow = (item) => {
  const tr = document.createElement('tr');
  const created = item.creadoEnUtc ? new Date(item.creadoEnUtc).toLocaleString() : '-';
  tr.innerHTML = `<td>${item.id ?? '-'}</td><td>${Number(item.monto ?? 0).toFixed(2)}</td><td>${item.estado ?? '-'}</td><td>${resolveCatalogName(catalogs.puntosVenta, item.puntoVentaId)}</td><td>${resolveCatalogName(catalogs.vendedores, item.vendedorId)}</td><td>${created}</td><td class="actions"></td>`;

  const actions = tr.querySelector('.actions');

  if (canPrint) {
    const printBtn = document.createElement('button');
    printBtn.textContent = 'Imprimir';
    printBtn.onclick = async () => {
      try {
        const r = await api(`/api/transferencias/${item.id}/print`, { method: 'POST' });
        showResult('ok', 'Impresión completada.', r);
        await listTransfers();
      } catch (error) {
        const mensaje = String(error?.message || '').toLowerCase();
        if (!mensaje.includes('ya fue impresa')) return;
        const datos = solicitarDatosReimpresion();
        if (!datos) return;
        try {
          const r = await api(`/api/transferencias/${item.id}/reprint`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(datos) });
          showResult('ok', 'Reimpresión completada.', r);
        } catch { }
      }
    };
    actions.append(printBtn);
  }

  if (canUpdateTransfer) {
    const editBtn = document.createElement('button');
    editBtn.className = 'ghost';
    editBtn.textContent = 'Modificar';
    editBtn.onclick = async () => {
      try { await abrirModalEdicionTransferencia(item.id); } catch { }
    };
    actions.append(editBtn);

    const viewBtn = document.createElement('button');
    viewBtn.className = 'ghost';
    viewBtn.textContent = 'Ver PDF';
    viewBtn.onclick = async () => { try { await mostrarPdfEnVisor(item.id); } catch { } };
    actions.append(viewBtn);
  }

  if (canPrint) {
    const reprintBtn = document.createElement('button');
    reprintBtn.className = 'ghost';
    reprintBtn.textContent = 'Reprint';
    reprintBtn.onclick = async () => {
      const datos = solicitarDatosReimpresion();
      if (!datos) return;
      try {
        const r = await api(`/api/transferencias/${item.id}/reprint`, { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(datos) });
        showResult('ok', 'Reimpresión completada.', r);
      } catch { }
    };
    actions.append(reprintBtn);
  }

  if (isAdmin) {
    const delPdfBtn = document.createElement('button');
    delPdfBtn.className = 'danger';
    delPdfBtn.textContent = 'Borrar PDF';
    delPdfBtn.onclick = async () => {
      if (!window.confirm(`¿Eliminar PDF de transferencia ${item.id}?`)) return;
      try { const r = await api(`/api/transferencias/${item.id}/archivo`, { method: 'DELETE' }); showResult('ok', 'PDF eliminado.', r); } catch { }
    };

    const delTxBtn = document.createElement('button');
    delTxBtn.className = 'danger';
    delTxBtn.textContent = 'Borrar';
    delTxBtn.onclick = async () => {
      if (!window.confirm(`¿Eliminar transferencia ${item.id}?`)) return;
      try { const r = await api(`/api/transferencias/${item.id}`, { method: 'DELETE' }); showResult('ok', 'Transferencia eliminada.', r); await listTransfers(); } catch { }
    };

    actions.append(delPdfBtn, delTxBtn);
  }

  return tr;
};

const listUsers = async () => {
  if (!isAdmin) return;
  const result = await api('/api/usuarios');
  const users = Array.isArray(result) ? result : [];
  const tbody = document.getElementById('usersTableBody');
  tbody.innerHTML = '';
  users.forEach((u) => {
    const tr = document.createElement('tr');
    tr.innerHTML = `<td>${u.id ?? '-'}</td><td>${u.usuario ?? '-'}</td><td>${u.rolId ?? '-'} - ${u.rol ?? '-'}</td><td>${u.puntoVentaId ?? '-'}</td><td>${u.activo ? 'Sí' : 'No'}</td><td>${u.firmaElectronica ?? '-'}</td><td class='actions'></td>`;
    const changeRoleBtn = document.createElement('button');
    changeRoleBtn.className = 'ghost';
    changeRoleBtn.textContent = 'Cambiar rol';
    changeRoleBtn.onclick = async () => {
      const nuevoRolId = Number(window.prompt(`Nuevo rolId para ${u.usuario} (1=ADMIN, 2=TESORERIA, 3=AUXILIAR):`, String(u.rolId ?? '')));
      if (!Number.isInteger(nuevoRolId) || nuevoRolId <= 0) return;
      try {
        const r = await api(`/api/usuarios/${u.id}/rol`, { method: 'PUT', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ rolId: nuevoRolId }) });
        showResult('ok', 'Rol actualizado correctamente.', r);
        await listUsers();
      } catch { }
    };

    const delBtn = document.createElement('button');
    delBtn.className = 'danger';
    delBtn.textContent = 'Eliminar';
    delBtn.onclick = async () => {
      if (!window.confirm(`¿Eliminar usuario ${u.usuario}?`)) return;
      try {
        const r = await api(`/api/usuarios/${u.id}`, { method: 'DELETE' });
        showResult('ok', 'Usuario eliminado correctamente.', r);
        await listUsers();
      } catch { }
    };
    tr.querySelector('.actions').append(changeRoleBtn, delBtn);
    tbody.append(tr);
  });

  showResult('ok', `Usuarios cargados: ${users.length}.`, { total: users.length, usuarios: users });
};

document.getElementById('listUsersBtn')?.addEventListener('click', async () => { try { await listUsers(); } catch { } });

document.getElementById('createUserForm')?.addEventListener('submit', async (event) => {
  event.preventDefault();
  const payload = Object.fromEntries(new FormData(event.currentTarget).entries());
  payload.rolId = payload.rolId ? Number(payload.rolId) : 0;
  payload.puntoVentaId = payload.puntoVentaId ? Number(payload.puntoVentaId) : null;
  try {
    const r = await api('/api/usuarios', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(payload) });
    showResult('ok', 'Usuario creado correctamente.', r);
    await listUsers();
    event.currentTarget.reset();
  } catch { }
});

(async () => {
  try {
    await loadCatalogs();
    await listTransfers();
    if (isAdmin) await listUsers();
  } catch {
    // handled by api helper
  }
})();
