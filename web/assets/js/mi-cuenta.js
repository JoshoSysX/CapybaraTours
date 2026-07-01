const u = CT.requireRole();
if (u) {
  const p = u.persona || {};
  const dato = (label, value, icon) => `<div class="col-12 col-md-6"><div class="cuenta-dato"><div class="small text-muted"><i class="bi ${icon} me-1"></i>${label}</div><div class="fw-bold" style="color:var(--ct-accent-dark);">${value || '—'}</div></div></div>`;
  document.getElementById('datosCuenta').innerHTML =
    dato('Nombre', ((p.nombre || '') + ' ' + (p.apellido || '')).trim(), 'bi-person-fill') +
    dato('DNI / Documento', p.numeroDoc || p.documento, 'bi-card-text') +
    dato('Correo', p.email || u.usuario, 'bi-envelope-fill') +
    dato('Teléfono', p.telefono, 'bi-telephone-fill') +
    dato('Rol', u.rol, 'bi-shield-check') +
    `<div class="col-12 mt-3"><a class="btn btn-ct-outline" href="${u.rol === 'ADMIN' ? 'admin/dashboard.html' : 'cliente/mis-reservas.html'}"><i class="bi bi-arrow-left me-1"></i>Volver a mi panel</a></div>`;
}
