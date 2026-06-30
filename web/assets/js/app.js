

const CT = (function () {
  // Raiz del contexto de la app: funciona aunque la pagina este en
  // /cliente/.. o /admin/.. (sube un nivel si hace falta)
  function base() {
    const path = window.location.pathname;
    if (path.includes('/cliente/') || path.includes('/admin/')) {
      return '../';
    }
    return '';
  }

  async function call(controller, params, method) {
    method = method || 'POST';
    const url = base() + controller;
    let resp;
    if (method === 'GET') {
      const qs = new URLSearchParams(params || {}).toString();
      resp = await fetch(url + (qs ? '?' + qs : ''), { method: 'GET' });
    } else {
      const body = new URLSearchParams(params || {});
      resp = await fetch(url, { method: 'POST', body: body });
    }
    const text = await resp.text();
    try {
      return JSON.parse(text);
    } catch (e) {
      console.error('Respuesta no JSON de ' + controller + ':', text);
      throw new Error('El servidor respondio algo inesperado.');
    }
  }

  // Para envios con archivo (imagen de paquete) usamos FormData + multipart
  async function callForm(controller, formData) {
    const url = base() + controller;
    const resp = await fetch(url, { method: 'POST', body: formData });
    const text = await resp.text();
    try {
      return JSON.parse(text);
    } catch (e) {
      console.error('Respuesta no JSON de ' + controller + ':', text);
      throw new Error('El servidor respondio algo inesperado.');
    }
  }

  function setSesion(userData) {
    sessionStorage.setItem('ct_usuario', JSON.stringify(userData));
  }
  function getSesion() {
    const raw = sessionStorage.getItem('ct_usuario');
    return raw ? JSON.parse(raw) : null;
  }
  function clearSesion() {
    sessionStorage.removeItem('ct_usuario');
  }

  // Redirige si no hay sesion, o si el rol no coincide
  function requireRole(rol) {
    const u = getSesion();
    if (!u || (rol && u.rol !== rol)) {
      window.location.href = (rol === 'ADMIN') ? 'login.html' : 'login.html';
      return null;
    }
    return u;
  }

  async function logout() {
    try { await call('AuthController', { action: 'Salir' }); } catch (e) {}
    clearSesion();
  }

  function money(n) {
    n = Number(n) || 0;
    return 'S/ ' + n.toFixed(2);
  }

  function fechaCorta(iso) {
    if (!iso) return '—';
    const d = new Date(iso);
    if (isNaN(d.getTime())) return iso;
    return d.toLocaleDateString('es-PE');
  }

  function alertOk(title, text) {
    return Swal.fire({ icon: 'success', title: title, text: text, confirmButtonColor: '#A0522D' });
  }
  function alertErr(title, text) {
    return Swal.fire({ icon: 'error', title: title, text: text, confirmButtonColor: '#A0522D' });
  }
  function alertWarn(title, text) {
    return Swal.fire({ icon: 'warning', title: title, text: text, confirmButtonColor: '#A0522D' });
  }

  return { call, callForm, setSesion, getSesion, clearSesion, requireRole, logout, money, fechaCorta, alertOk, alertErr, alertWarn };
})();
