
const CT = (function () {
  function base() {
    const path = window.location.pathname;
    if (path.includes('/cliente/') || path.includes('/admin/')) return '../';
    return '';
  }
  function isAdminPage(){ return window.location.pathname.includes('/admin/'); }
  function isClientPage(){ return window.location.pathname.includes('/cliente/'); }
  function page(path){ return base()+path; }

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
    try { return JSON.parse(text); }
    catch (e) { console.error('Respuesta no JSON de ' + controller + ':', text); throw new Error('El servidor respondio algo inesperado.'); }
  }
  async function callForm(controller, formData) {
    const resp = await fetch(base() + controller, { method: 'POST', body: formData });
    const text = await resp.text();
    try { return JSON.parse(text); }
    catch (e) { console.error('Respuesta no JSON de ' + controller + ':', text); throw new Error('El servidor respondio algo inesperado.'); }
  }
  function setSesion(userData) {
    sessionStorage.setItem('ct_usuario', JSON.stringify(userData));
    sessionStorage.removeItem('usuario');
    sessionStorage.removeItem('admin');
    sessionStorage.removeItem('cliente');
  }
  function getSesion() {
    const raw = sessionStorage.getItem('ct_usuario');
    if (!raw) return null;
    try { return JSON.parse(raw); }
    catch (e) { clearSesion(); return null; }
  }
  function clearSesion() {
    sessionStorage.removeItem('ct_usuario');
    sessionStorage.removeItem('usuario');
    sessionStorage.removeItem('admin');
    sessionStorage.removeItem('cliente');
  }

  function requireRole(rol) {
    const u = getSesion();
    if (!u) { window.location.href = page('login.html'); return null; }
    if (rol && u.rol !== rol) { window.location.href = page('index.html'); return null; }
    return u;
  }
  async function logout() { try { await call('AuthController', { action: 'Salir' }); } catch (e) {} clearSesion(); }
  function money(n) { n = Number(n) || 0; return 'S/ ' + n.toFixed(2); }
  function fechaCorta(iso) { if (!iso) return '—'; const d = new Date(iso); return isNaN(d.getTime()) ? iso : d.toLocaleDateString('es-PE'); }
  function alertOk(title, text) { return Swal.fire({ icon:'success', title, text, confirmButtonColor:'#A0522D' }); }
  function alertErr(title, text) { return Swal.fire({ icon:'error', title, text, confirmButtonColor:'#A0522D' }); }
  function alertWarn(title, text) { return Swal.fire({ icon:'warning', title, text, confirmButtonColor:'#A0522D' }); }

  function nombreUsuario(u){ return (u && u.persona && u.persona.nombre) ? u.persona.nombre : (u ? u.usuario : ''); }

  function pintarHeaderPublico() {
    const ul = document.querySelector('.navbar-ct .navbar-nav');
    if (!ul) return;
    const u = getSesion();
    const prefix = base();
    const cuenta = isAdminPage() ? 'mi-cuenta.html' : (isClientPage() ? 'mi-cuenta.html' : 'mi-cuenta.html');
    if (u) {
      ul.innerHTML = `
        <li class="nav-item"><a class="nav-link" href="${prefix}index.html"><i class="bi bi-house-fill me-1"></i>Inicio</a></li>
        <li class="nav-item"><a class="nav-link" href="${prefix}cliente/catalogo.html"><i class="bi bi-map-fill me-1"></i>Paquetes</a></li>
        <li class="nav-item"><a class="nav-link" href="${prefix}cliente/reservar.html"><i class="bi bi-calendar-check-fill me-1"></i>Reservar</a></li>
        <li class="nav-item"><a class="nav-link" href="${prefix}cliente/mis-reservas.html"><i class="bi bi-journal-text me-1"></i>Mis Reservas</a></li>
        ${u.rol === 'ADMIN' ? `<li class="nav-item"><a class="nav-link" href="${prefix}admin/dashboard.html"><i class="bi bi-speedometer2 me-1"></i>Panel Admin</a></li>` : ''}
        <li class="nav-item ms-lg-2"><a class="btn btn-ct-outline px-3" href="${prefix}mi-cuenta.html"><i class="bi bi-person-circle me-1"></i>Mi cuenta</a></li>
        <li class="nav-item ms-1"><a class="btn btn-ct-primary px-3" href="#" id="ctBtnLogout"><i class="bi bi-box-arrow-right me-1"></i>Cerrar sesión</a></li>`;
    } else {
      ul.innerHTML = `
        <li class="nav-item"><a class="nav-link" href="${prefix}index.html"><i class="bi bi-house-fill me-1"></i>Inicio</a></li>
        <li class="nav-item"><a class="nav-link" href="${prefix}cliente/catalogo.html"><i class="bi bi-map-fill me-1"></i>Paquetes</a></li>
        <li class="nav-item"><a class="nav-link" href="${prefix}cliente/reservar.html"><i class="bi bi-calendar-check-fill me-1"></i>Reservar</a></li>
        <li class="nav-item"><a class="nav-link" href="${prefix}cliente/mis-reservas.html"><i class="bi bi-journal-text me-1"></i>Mis Reservas</a></li>
        <li class="nav-item ms-lg-2"><a class="btn btn-ct-primary px-3" href="${prefix}login.html"><i class="bi bi-person-fill me-1"></i>Ingresar</a></li>
        <li class="nav-item ms-1"><a class="btn btn-ct-outline px-3" href="${prefix}cliente/registro.html"><i class="bi bi-person-plus-fill me-1"></i>Registrarse</a></li>`;
    }
    const out = document.getElementById('ctBtnLogout');
    if (out) out.addEventListener('click', async (e)=>{ e.preventDefault(); await logout(); window.location.href = prefix + 'index.html'; });
  }

  function mejorarSidebarAdmin(){
    const nav = document.querySelector('.sidebar .nav.flex-column');
    if (!nav) return;
    const links = [
      ['../index.html','bi-house-fill','Inicio'],
      ['dashboard.html','bi-speedometer2','Dashboard'],
      ['reservas.html','bi-calendar-week','Reservas'],
      ['reservar-cliente.html','bi-person-plus-fill','Reservar Cliente'],
      ['guias.html','bi-person-badge-fill','Guías'],
      ['transportes.html','bi-truck','Transportes'],
      ['paquetes.html','bi-map-fill','Paquetes'],
      ['cobros.html','bi-cash-stack','Cobros'],
      ['consultas.html','bi-bar-chart-fill','Consultas'],
      ['logs.html','bi-clock-history','Logs'],
      ['../mi-cuenta.html','bi-person-circle','Mi cuenta']
    ];
    const current = window.location.pathname.split('/').pop();
    nav.innerHTML = links.map(([href, icon, txt])=>{
      const active = href.endsWith(current) ? ' active' : '';
      return `<li class="nav-item"><a class="nav-link${active}" href="${href}"><i class="bi ${icon}"></i>${txt}</a></li>`;
    }).join('');
  }

  function initUI(){
    pintarHeaderPublico();
    mejorarSidebarAdmin();
    document.querySelectorAll('input[type="date"]#fechaTour, input[type="date"]#fecha_programada, input[type="date"].fecha-futura').forEach(fecha => {
      fecha.min = new Date().toISOString().slice(0,10);
    });
  }
  document.addEventListener('DOMContentLoaded', initUI);

  return { call, callForm, setSesion, getSesion, clearSesion, requireRole, logout, money, fechaCorta, alertOk, alertErr, alertWarn, page, initUI, nombreUsuario };
})();
