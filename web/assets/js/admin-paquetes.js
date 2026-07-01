CT.requireRole('ADMIN');

document.getElementById('btnSalir').addEventListener('click', async (e) => {
  e.preventDefault();
  await CT.logout();
  window.location.href = '../login.html';
});

let paquetes = [];

async function cargar() {
  try { paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET'); } catch (e) { paquetes = []; }
  if (!paquetes || paquetes.length === 0) {
    document.getElementById('sinPaquetes').classList.remove('d-none');
    document.getElementById('contTabla').classList.add('d-none');
    return;
  }
  document.getElementById('sinPaquetes').classList.add('d-none');
  document.getElementById('contTabla').classList.remove('d-none');
  const tbody = document.getElementById('tbody');
  tbody.innerHTML = '';
  paquetes.forEach(p => {
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td><b>${p.nombre}</b></td>
      <td class="small">${(p.descripcion || '').substring(0, 60)}${(p.descripcion || '').length > 60 ? '…' : ''}</td>
      <td><span class="tour-badge">${p.duracion || '—'}</span></td>
      <td><b style="color:var(--ct-accent-dark);">${Number(p.precio).toFixed(2)}</b></td>
      <td>
        <button class="btn btn-sm btn-warning me-1" onclick='editar(${JSON.stringify(p)})'><i class="bi bi-pencil"></i></button>
        <button class="btn btn-sm btn-danger" onclick="eliminar(${p.id_paquete}, '${(p.nombre || '').replace(/'/g, '')}')"><i class="bi bi-trash"></i></button>
      </td>`;
    tbody.appendChild(tr);
  });
}

function formularioHTML(p) {
  p = p || {};
  const imagenActual = p.imagen
    ? `<div class="col-12"><label class="form-label small">Imagen actual</label><input class="form-control form-control-sm" value="${p.imagen}" readonly><div class="form-text">Si no eliges una imagen nueva, se conservará esta ruta.</div></div>`
    : '';
  return `<div class="row g-2 text-start">
    <div class="col-12"><label class="form-label small">Nombre del paquete</label><input id="swNombre" class="form-control form-control-sm" value="${p.nombre || ''}" placeholder="Ej: Laguna Azul"></div>
    <div class="col-6"><label class="form-label small">Precio (S/)</label><input id="swPrecio" type="number" step="0.01" class="form-control form-control-sm" value="${p.precio || ''}"></div>
    <div class="col-6"><label class="form-label small">Duración</label><input id="swDuracion" class="form-control form-control-sm" value="${p.duracion || ''}" placeholder="Ej: 1 día"></div>
    <div class="col-12"><label class="form-label small">Descripción</label><textarea id="swDescripcion" class="form-control form-control-sm" rows="3">${p.descripcion || ''}</textarea></div>
    ${imagenActual}
    <div class="col-12"><label class="form-label small">Imagen del paquete</label><input id="swImagen" name="imagen" type="file" accept="image/*" class="form-control form-control-sm"></div>
  </div>`;
}

document.getElementById('btnNuevo').addEventListener('click', () => nuevoPaquete());

async function nuevoPaquete() {
  const r = await Swal.fire({
    title: 'Nuevo Paquete Turístico', html: formularioHTML(), showCancelButton: true,
    confirmButtonText: 'Registrar paquete', confirmButtonColor: '#A0522D',
    preConfirm: () => {
      const nombre = document.getElementById('swNombre').value.trim();
      const precio = document.getElementById('swPrecio').value;
      if (!nombre || !precio) { Swal.showValidationMessage('Nombre y precio son obligatorios'); return false; }
      return {
        nombre, precio,
        duracion: document.getElementById('swDuracion').value.trim(),
        descripcion: document.getElementById('swDescripcion').value.trim(),
        imagen: document.getElementById('swImagen').files[0]
      };
    }
  });
  if (!r.isConfirmed) return;
  const fd = new FormData();
  fd.append('action', 'guardar');
  fd.append('nombre', r.value.nombre);
  fd.append('descripcion', r.value.descripcion);
  fd.append('duracion', r.value.duracion);
  fd.append('precio', r.value.precio);
  if (r.value.imagen) fd.append('imagen', r.value.imagen);
  try {
    const ok = await CT.callForm('PaqueteController', fd);
    if (ok) { CT.alertOk('Paquete registrado'); cargar(); } else { CT.alertErr('No se pudo registrar', 'Revisa los datos ingresados.'); }
  } catch (e) { CT.alertErr('Error de conexión', ''); }
}

async function editar(p) {
  const r = await Swal.fire({
    title: 'Editar: ' + p.nombre, html: formularioHTML(p), showCancelButton: true,
    confirmButtonText: 'Guardar cambios', confirmButtonColor: '#A0522D',
    preConfirm: () => {
      const nombre = document.getElementById('swNombre').value.trim();
      const precio = document.getElementById('swPrecio').value;
      if (!nombre || !precio) { Swal.showValidationMessage('Nombre y precio son obligatorios'); return false; }
      return {
        nombre, precio,
        duracion: document.getElementById('swDuracion').value.trim(),
        descripcion: document.getElementById('swDescripcion').value.trim(),
        imagen: document.getElementById('swImagen').files[0]
      };
    }
  });
  if (!r.isConfirmed) return;
  const fd = new FormData();
  fd.append('action', 'editar');
  fd.append('id_paquete', p.id_paquete);
  fd.append('nombre', r.value.nombre);
  fd.append('descripcion', r.value.descripcion);
  fd.append('duracion', r.value.duracion);
  fd.append('precio', r.value.precio);
  fd.append('imagen_actual', p.imagen || '');
  if (r.value.imagen) fd.append('imagen', r.value.imagen);
  try {
    const ok = await CT.callForm('PaqueteController', fd);
    if (ok) { CT.alertOk('Paquete actualizado'); cargar(); } else { CT.alertErr('No se pudo actualizar', ''); }
  } catch (e) { CT.alertErr('Error de conexión', ''); }
}

async function eliminar(id, nombre) {
  const r = await Swal.fire({ title: '¿Eliminar paquete?', text: nombre, icon: 'warning', showCancelButton: true, confirmButtonText: 'Eliminar', confirmButtonColor: '#dc3545' });
  if (!r.isConfirmed) return;
  try {
    const ok = await CT.call('PaqueteController', { action: 'eliminar', id });
    if (ok) { CT.alertOk('Paquete eliminado'); cargar(); }
    else CT.alertErr('No se pudo eliminar', 'Es posible que tenga reservas asociadas.');
  } catch (e) { CT.alertErr('Error de conexión', ''); }
}

cargar();
