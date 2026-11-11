(() => {
  'use strict';

  const TOKEN_KEY = 'vuelos.token';
  const EMAIL_KEY = 'vuelos.email';
  const LOGIN_PATH = '/api/auth/login';

  const state = {
    token: sessionStorage.getItem(TOKEN_KEY) || '',
    email: sessionStorage.getItem(EMAIL_KEY) || '',
    role: '',
    view: 'vuelos',
    editingId: null,
    editingUserId: null,
    vuelos: [],
    users: []
  };

  const el = (id) => document.getElementById(id);

  /* ----------------------------- utilidades ----------------------------- */

  function decodeJwt(token) {
    try {
      const part = token.split('.')[1];
      const padded = part
        .replace(/-/g, '+')
        .replace(/_/g, '/')
        .padEnd(part.length + ((4 - (part.length % 4)) % 4), '=');
      return JSON.parse(atob(padded));
    } catch (error) {
      return {};
    }
  }

  function showView(view) {
    el('view-login').classList.toggle('hidden', view !== 'login');
    el('view-app').classList.toggle('hidden', view !== 'app');
  }

  function setAlert(node, message, type) {
    if (!message) {
      node.className = 'alert hidden';
      node.textContent = '';
      return;
    }
    node.className = 'alert alert-' + type;
    node.textContent = message;
  }

  let notifyTimer = null;

  function notify(message, type) {
    const box = el('app-alert');
    setAlert(box, message, type || 'info');
    clearTimeout(notifyTimer);
    notifyTimer = setTimeout(() => box.classList.add('hidden'), 4500);
  }

  function toIso(value) {
    if (!value) return value;
    return value.length === 16 ? value + ':00' : value;
  }

  function toInputValue(iso) {
    return iso ? String(iso).slice(0, 16) : '';
  }

  function formatDate(iso) {
    if (!iso) return '—';
    const date = new Date(iso);
    if (Number.isNaN(date.getTime())) return iso;
    const pad = (value) => String(value).padStart(2, '0');
    const day = pad(date.getDate());
    const month = pad(date.getMonth() + 1);
    const year = String(date.getFullYear()).slice(-2);
    return day + '/' + month + '/' + year + ' ' + pad(date.getHours()) + ':' + pad(date.getMinutes());
  }

  function formatMoney(value) {
    const number = Number(value);
    if (Number.isNaN(number)) return String(value);
    return number.toLocaleString('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  /* -------------------------------- API -------------------------------- */

  async function api(path, options) {
    const settings = options || {};
    const headers = Object.assign({ 'Content-Type': 'application/json' }, settings.headers || {});

    if (state.token) {
      headers.Authorization = 'Bearer ' + state.token;
    }

    const response = await fetch(path, Object.assign({}, settings, { headers }));

    if (response.status === 401 && path !== LOGIN_PATH) {
      signOut(true);
      throw new Error('Tu sesión expiró. Inicia sesión nuevamente.');
    }

    if (response.status === 204) return null;

    const text = await response.text();
    let data = null;
    if (text) {
      try {
        data = JSON.parse(text);
      } catch (error) {
        data = text;
      }
    }

    if (!response.ok) {
      const message = data && data.message ? data.message : 'Error ' + response.status;
      throw new Error(message);
    }

    return data;
  }

  /* --------------------------- autenticación --------------------------- */

  async function signIn(email, password) {
    const data = await api(LOGIN_PATH, {
      method: 'POST',
      body: JSON.stringify({ email: email, password: password })
    });

    state.token = data.accessToken;
    state.email = email;
    sessionStorage.setItem(TOKEN_KEY, state.token);
    sessionStorage.setItem(EMAIL_KEY, email);
    startApp();
  }

  function signOut(expired) {
    state.token = '';
    state.role = '';
    state.email = '';
    state.vuelos = [];
    state.users = [];
    state.editingId = null;
    state.editingUserId = null;
    sessionStorage.removeItem(TOKEN_KEY);
    sessionStorage.removeItem(EMAIL_KEY);
    el('vuelos-body').innerHTML = '';
    el('users-body').innerHTML = '';
    el('modal').classList.add('hidden');
    el('user-modal').classList.add('hidden');
    state.view = 'vuelos';
    el('tab-vuelos').classList.add('is-active');
    el('tab-usuarios').classList.remove('is-active');
    el('panel-vuelos').classList.remove('hidden');
    el('panel-usuarios').classList.add('hidden');
    showView('login');
    if (expired) {
      setAlert(el('login-error'), 'La sesión expiró. Inicia sesión nuevamente.', 'error');
    }
  }

  function switchView(view) {
    state.view = view;
    const isVuelos = view === 'vuelos';

    el('tab-vuelos').classList.toggle('is-active', isVuelos);
    el('tab-usuarios').classList.toggle('is-active', !isVuelos);
    el('tab-vuelos').setAttribute('aria-selected', String(isVuelos));
    el('tab-usuarios').setAttribute('aria-selected', String(!isVuelos));
    el('panel-vuelos').classList.toggle('hidden', !isVuelos);
    el('panel-usuarios').classList.toggle('hidden', isVuelos);
    setAlert(el('app-alert'), '');

    if (isVuelos) {
      loadVuelos();
    } else {
      loadUsers();
    }
  }

  function startApp() {
    const claims = decodeJwt(state.token);
    state.role = claims.role || 'MEMBER';

    const isAdmin = state.role === 'ADMIN';
    const canReadUsers = isAdmin || state.role === 'REVIEWER';

    el('user-chip').textContent = (state.email || claims.sub || '') + ' · ' + state.role;
    document.querySelectorAll('.admin-only').forEach((node) => {
      node.classList.toggle('hidden', !isAdmin);
    });
    document.querySelectorAll('.users-only').forEach((node) => {
      node.classList.toggle('hidden', !canReadUsers);
    });

    el('role-note').textContent = isAdmin
      ? 'Puedes crear, editar y eliminar vuelos.'
      : 'Tu rol es de solo lectura: puedes consultar el listado.';
    el('users-note').textContent = isAdmin
      ? 'Puedes crear, editar y eliminar usuarios.'
      : 'Tu rol es de solo lectura: puedes consultar el listado de usuarios.';

    showView('app');
    switchView('vuelos');
  }

  /* ------------------------------- vuelos ------------------------------- */

  async function loadVuelos() {
    try {
      const data = await api('/api/vuelos');
      state.vuelos = Array.isArray(data) ? data : [];
      renderVuelos();
    } catch (error) {
      if (state.token) notify(error.message, 'error');
    }
  }

  function cell(text, className) {
    const td = document.createElement('td');
    td.textContent = text;
    if (className) td.className = className;
    return td;
  }

  function cellWithSub(main, sub) {
    const td = document.createElement('td');
    const strong = document.createElement('span');
    strong.className = 'cell-main';
    strong.textContent = main;
    td.appendChild(strong);
    const small = document.createElement('span');
    small.className = 'cell-sub';
    small.textContent = sub;
    td.appendChild(small);
    return td;
  }

  function renderVuelos() {
    const body = el('vuelos-body');
    const table = el('vuelos-table');
    body.innerHTML = '';

    const isEmpty = state.vuelos.length === 0;
    table.classList.toggle('hidden', isEmpty);
    el('empty-state').classList.toggle('hidden', !isEmpty);

    const isAdmin = state.role === 'ADMIN';

    state.vuelos.forEach((vuelo) => {
      const row = document.createElement('tr');

      row.appendChild(cell(vuelo.numero, 'cell-main'));
      row.appendChild(cell(vuelo.aerolinea));
      row.appendChild(cellWithSub(formatDate(vuelo.fechaSalida), '→ ' + formatDate(vuelo.fechaLlegada)));
      row.appendChild(cellWithSub(vuelo.aeropuertoSalida, '→ ' + vuelo.aeropuertoLlegada));
      row.appendChild(cell(vuelo.cliente));
      row.appendChild(cell(vuelo.puesto));

      const valor = cell(formatMoney(vuelo.valor), 'right');
      row.appendChild(valor);

      const estadoCell = document.createElement('td');
      const badge = document.createElement('span');
      badge.className = 'badge badge-' + vuelo.estado;
      badge.textContent = String(vuelo.estado).replace('_', ' ');
      estadoCell.appendChild(badge);
      row.appendChild(estadoCell);

      const actions = document.createElement('td');
      actions.className = 'right';

      if (isAdmin) {
        const wrap = document.createElement('div');
        wrap.className = 'row-actions';

        const edit = document.createElement('button');
        edit.type = 'button';
        edit.className = 'link-btn';
        edit.textContent = '✏️';
        edit.title = 'Editar';
        edit.setAttribute('aria-label', 'Editar');
        edit.addEventListener('click', () => openModal(vuelo));

        const remove = document.createElement('button');
        remove.type = 'button';
        remove.className = 'link-btn danger';
        remove.textContent = '🗑️';
        remove.title = 'Eliminar';
        remove.setAttribute('aria-label', 'Eliminar');
        remove.addEventListener('click', () => removeVuelo(vuelo));

        wrap.appendChild(edit);
        wrap.appendChild(remove);
        actions.appendChild(wrap);
      } else {
        actions.textContent = '—';
      }

      row.appendChild(actions);
      body.appendChild(row);
    });
  }

  async function removeVuelo(vuelo) {
    const confirmed = window.confirm(
      '¿Eliminar el vuelo ' + vuelo.numero + ' (' + vuelo.puesto + ')?'
    );
    if (!confirmed) return;

    try {
      await api('/api/vuelos/' + encodeURIComponent(vuelo.id), { method: 'DELETE' });
      notify('Vuelo eliminado correctamente.', 'ok');
      await loadVuelos();
    } catch (error) {
      notify(error.message, 'error');
    }
  }

  /* ------------------------------- usuarios ------------------------------ */

  async function loadUsers() {
    try {
      const data = await api('/api/users');
      state.users = Array.isArray(data) ? data : [];
      renderUsers();
    } catch (error) {
      if (state.token) notify(error.message, 'error');
    }
  }

  function badgeCell(value) {
    const td = document.createElement('td');
    const badge = document.createElement('span');
    badge.className = 'badge badge-' + value;
    badge.textContent = String(value).replace('_', ' ');
    td.appendChild(badge);
    return td;
  }

  function renderUsers() {
    const body = el('users-body');
    const table = el('users-table');
    body.innerHTML = '';

    const isEmpty = state.users.length === 0;
    table.classList.toggle('hidden', isEmpty);
    el('users-empty').classList.toggle('hidden', !isEmpty);

    const isAdmin = state.role === 'ADMIN';

    state.users.forEach((user) => {
      const row = document.createElement('tr');

      row.appendChild(cell(user.name, 'cell-main'));
      row.appendChild(cell(user.email));
      row.appendChild(badgeCell(user.role));
      row.appendChild(badgeCell(user.status));

      const actions = document.createElement('td');
      actions.className = 'right';

      if (isAdmin) {
        const wrap = document.createElement('div');
        wrap.className = 'row-actions';

        const edit = document.createElement('button');
        edit.type = 'button';
        edit.className = 'link-btn';
        edit.textContent = '✏️';
        edit.title = 'Editar';
        edit.setAttribute('aria-label', 'Editar');
        edit.addEventListener('click', () => openUserModal(user));

        const remove = document.createElement('button');
        remove.type = 'button';
        remove.className = 'link-btn danger';
        remove.textContent = '🗑️';
        remove.title = 'Eliminar';
        remove.setAttribute('aria-label', 'Eliminar');
        remove.addEventListener('click', () => removeUser(user));

        wrap.appendChild(edit);
        wrap.appendChild(remove);
        actions.appendChild(wrap);
      } else {
        actions.textContent = '—';
      }

      row.appendChild(actions);
      body.appendChild(row);
    });
  }

  async function removeUser(user) {
    if (user.email.toLowerCase() === String(state.email).toLowerCase()) {
      notify('No puedes eliminar tu propio usuario desde la interfaz.', 'error');
      return;
    }

    const confirmed = window.confirm(
      '¿Eliminar el usuario ' + user.name + ' (' + user.email + ')?'
    );
    if (!confirmed) return;

    try {
      await api('/api/users/' + encodeURIComponent(user.id), { method: 'DELETE' });
      notify('Usuario eliminado correctamente.', 'ok');
      await loadUsers();
    } catch (error) {
      notify(error.message, 'error');
    }
  }

  /* -------------------------------- modal -------------------------------- */

  function newId() {
    if (window.crypto && typeof window.crypto.randomUUID === 'function') {
      return window.crypto.randomUUID();
    }
    return 'vuelo-' + Date.now();
  }

  function openModal(vuelo) {
    state.editingId = vuelo ? vuelo.id : null;

    el('modal-title').textContent = vuelo ? 'Editar vuelo' : 'Nuevo vuelo';
    el('field-id').classList.toggle('hidden', Boolean(vuelo));
    el('field-estado').classList.toggle('hidden', !vuelo);
    setAlert(el('form-error'), '');

    el('f-id').value = vuelo ? vuelo.id : newId();
    el('f-numero').value = vuelo ? vuelo.numero : '';
    el('f-aerolinea').value = vuelo ? vuelo.aerolinea : '';
    el('f-agencia').value = vuelo ? vuelo.agenciaViajes : '';
    el('f-fecha-compra').value = toInputValue(vuelo ? vuelo.fechaCompra : '');
    el('f-fecha-salida').value = toInputValue(vuelo ? vuelo.fechaSalida : '');
    el('f-fecha-llegada').value = toInputValue(vuelo ? vuelo.fechaLlegada : '');
    el('f-valor').value = vuelo ? vuelo.valor : '';
    el('f-cliente').value = vuelo ? vuelo.cliente : '';
    el('f-puesto').value = vuelo ? vuelo.puesto : '';
    el('f-avion').value = vuelo ? vuelo.avion : '';
    el('f-aeropuerto-salida').value = vuelo ? vuelo.aeropuertoSalida : '';
    el('f-aeropuerto-llegada').value = vuelo ? vuelo.aeropuertoLlegada : '';
    el('f-piloto').value = vuelo ? vuelo.piloto : '';
    el('f-estado').value = vuelo ? vuelo.estado : 'PROGRAMADO';

    el('modal').classList.remove('hidden');
    el('f-numero').focus();
  }

  function closeModal() {
    el('modal').classList.add('hidden');
    state.editingId = null;
  }

  async function saveVuelo(event) {
    event.preventDefault();

    const editing = Boolean(state.editingId);
    const payload = {
      fechaCompra: toIso(el('f-fecha-compra').value),
      fechaSalida: toIso(el('f-fecha-salida').value),
      fechaLlegada: toIso(el('f-fecha-llegada').value),
      agenciaViajes: el('f-agencia').value.trim(),
      aerolinea: el('f-aerolinea').value.trim(),
      numero: el('f-numero').value.trim().toUpperCase(),
      valor: Number(el('f-valor').value),
      cliente: el('f-cliente').value.trim(),
      puesto: el('f-puesto').value.trim().toUpperCase(),
      avion: el('f-avion').value.trim(),
      aeropuertoSalida: el('f-aeropuerto-salida').value.trim(),
      aeropuertoLlegada: el('f-aeropuerto-llegada').value.trim(),
      piloto: el('f-piloto').value.trim()
    };

    if (editing) {
      payload.estado = el('f-estado').value;
    } else {
      payload.id = el('f-id').value.trim();
    }

    const button = el('save-btn');
    button.disabled = true;
    setAlert(el('form-error'), '');

    try {
      if (editing) {
        await api('/api/vuelos/' + encodeURIComponent(state.editingId), {
          method: 'PUT',
          body: JSON.stringify(payload)
        });
      } else {
        await api('/api/vuelos', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
      }

      closeModal();
      notify(editing ? 'Vuelo actualizado correctamente.' : 'Vuelo creado correctamente.', 'ok');
      await loadVuelos();
    } catch (error) {
      setAlert(el('form-error'), error.message, 'error');
    } finally {
      button.disabled = false;
    }
  }

  /* ---------------------------- modal usuario ---------------------------- */

  function openUserModal(user) {
    state.editingUserId = user ? user.id : null;

    el('user-modal-title').textContent = user ? 'Editar usuario' : 'Nuevo usuario';
    el('user-field-id').classList.toggle('hidden', Boolean(user));
    el('user-field-status').classList.toggle('hidden', !user);
    el('u-password-hint').classList.toggle('hidden', !user);
    setAlert(el('user-form-error'), '');

    el('u-id').value = user ? user.id : newId();
    el('u-name').value = user ? user.name : '';
    el('u-email').value = user ? user.email : '';
    el('u-password').value = '';
    el('u-password').required = !user;
    el('u-role').value = user ? user.role : 'MEMBER';
    el('u-status').value = user ? user.status : 'ACTIVE';

    el('user-modal').classList.remove('hidden');
    el('u-name').focus();
  }

  function closeUserModal() {
    el('user-modal').classList.add('hidden');
    state.editingUserId = null;
  }

  async function saveUser(event) {
    event.preventDefault();

    const editing = Boolean(state.editingUserId);
    const password = el('u-password').value;

    const payload = {
      name: el('u-name').value.trim(),
      email: el('u-email').value.trim(),
      role: el('u-role').value
    };

    if (editing) {
      payload.status = el('u-status').value;
      if (password) {
        payload.password = password;
      }
    } else {
      payload.id = el('u-id').value.trim();
      payload.password = password;
    }

    const button = el('user-save-btn');
    button.disabled = true;
    setAlert(el('user-form-error'), '');

    try {
      if (editing) {
        await api('/api/users/' + encodeURIComponent(state.editingUserId), {
          method: 'PUT',
          body: JSON.stringify(payload)
        });
      } else {
        await api('/api/users', {
          method: 'POST',
          body: JSON.stringify(payload)
        });
      }

      closeUserModal();
      notify(editing ? 'Usuario actualizado correctamente.' : 'Usuario creado correctamente.', 'ok');
      await loadUsers();
    } catch (error) {
      setAlert(el('user-form-error'), error.message, 'error');
    } finally {
      button.disabled = false;
    }
  }

  /* -------------------------------- inicio -------------------------------- */

  document.addEventListener('DOMContentLoaded', () => {
    el('login-form').addEventListener('submit', async (event) => {
      event.preventDefault();

      const button = el('login-btn');
      button.disabled = true;
      setAlert(el('login-error'), '');

      try {
        await signIn(el('email').value.trim(), el('password').value);
        el('password').value = '';
      } catch (error) {
        setAlert(el('login-error'), error.message, 'error');
      } finally {
        button.disabled = false;
      }
    });

    el('logout-btn').addEventListener('click', () => signOut(false));

    el('tab-vuelos').addEventListener('click', () => switchView('vuelos'));
    el('tab-usuarios').addEventListener('click', () => switchView('usuarios'));

    el('refresh-btn').addEventListener('click', () => loadVuelos());
    el('new-btn').addEventListener('click', () => openModal(null));
    el('modal-close').addEventListener('click', closeModal);
    el('cancel-btn').addEventListener('click', closeModal);
    el('vuelo-form').addEventListener('submit', saveVuelo);

    el('users-refresh-btn').addEventListener('click', () => loadUsers());
    el('new-user-btn').addEventListener('click', () => openUserModal(null));
    el('user-modal-close').addEventListener('click', closeUserModal);
    el('user-cancel-btn').addEventListener('click', closeUserModal);
    el('user-form').addEventListener('submit', saveUser);

    el('modal').addEventListener('click', (event) => {
      if (event.target === el('modal')) closeModal();
    });

    el('user-modal').addEventListener('click', (event) => {
      if (event.target === el('user-modal')) closeUserModal();
    });

    document.addEventListener('keydown', (event) => {
      if (event.key !== 'Escape') return;
      if (!el('modal').classList.contains('hidden')) closeModal();
      if (!el('user-modal').classList.contains('hidden')) closeUserModal();
    });

    if (state.token) {
      startApp();
    } else {
      showView('login');
      el('email').focus();
    }
  });
})();
