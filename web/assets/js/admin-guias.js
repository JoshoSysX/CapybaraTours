CT.requireRole('ADMIN');
  document.getElementById('btnSalir').addEventListener('click', async (e) => { e.preventDefault(); await CT.logout(); window.location.href = '../login.html'; });

  async function cargar() {
    let guias = [];
    try { guias = await CT.call('GuiaController', { action: 'listar' }, 'GET'); } catch (e) {}
    if (!guias || guias.length === 0) {
      document.getElementById('sinGuias').classList.remove('d-none');
      document.getElementById('contTabla').classList.add('d-none');
      return;
    }
    document.getElementById('sinGuias').classList.add('d-none');
    document.getElementById('contTabla').classList.remove('d-none');
    const tbody = document.getElementById('tbody');
    tbody.innerHTML = '';
    guias.forEach(g => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><b>${g.nombre}</b></td><td>${g.telefono || '—'}</td>
        <td>
          <button class="btn btn-sm btn-warning me-1" onclick='editar(${JSON.stringify(g)})'><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-danger" onclick="eliminar(${g.idGuia}, '${(g.nombre||'').replace(/'/g, "")}')"><i class="bi bi-trash"></i></button>
        </td>`;
      tbody.appendChild(tr);
    });
  }

  function formularioHTML(g) {
    g = g || {};
    return `<div class="row g-2 text-start">
      <div class="col-12"><label class="form-label small">Nombre completo</label><input id="swNombre" class="form-control form-control-sm" value="${g.nombre || ''}"></div>
      <div class="col-12"><label class="form-label small">Teléfono</label><input id="swTelefono" class="form-control form-control-sm" value="${g.telefono || ''}" maxlength="15"></div>
    </div>`;
  }

  document.getElementById('btnNuevo').addEventListener('click', nuevoGuia);

  async function nuevoGuia() {
    const r = await Swal.fire({
      title: 'Registrar Guía', html: formularioHTML(), showCancelButton: true, confirmButtonText: 'Registrar', confirmButtonColor: '#A0522D',
      preConfirm: () => {
        const nombre = document.getElementById('swNombre').value.trim();
        if (!nombre) { Swal.showValidationMessage('El nombre es obligatorio'); return false; }
        return { nombre, telefono: document.getElementById('swTelefono').value.trim() };
      }
    });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('GuiaController', { action: 'guardar', nombre: r.value.nombre, telefono: r.value.telefono });
      if (ok) { CT.alertOk('Guía registrado'); cargar(); } else CT.alertErr('No se pudo registrar', '');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  }

  async function editar(g) {
    const r = await Swal.fire({
      title: 'Editar guía', html: formularioHTML(g), showCancelButton: true, confirmButtonText: 'Guardar cambios', confirmButtonColor: '#A0522D',
      preConfirm: () => {
        const nombre = document.getElementById('swNombre').value.trim();
        if (!nombre) { Swal.showValidationMessage('El nombre es obligatorio'); return false; }
        return { nombre, telefono: document.getElementById('swTelefono').value.trim() };
      }
    });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('GuiaController', { action: 'editar', id_guia: g.idGuia, nombre: r.value.nombre, telefono: r.value.telefono });
      if (ok) { CT.alertOk('Guía actualizado'); cargar(); } else CT.alertErr('No se pudo actualizar', '');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  }

  async function eliminar(id, nombre) {
    const r = await Swal.fire({ title: '¿Eliminar guía?', text: nombre, icon: 'warning', showCancelButton: true, confirmButtonText: 'Eliminar', cancelButtonText: 'Cancelar', confirmButtonColor: '#dc3545' });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('GuiaController', { action: 'eliminar', id });
      if (ok) { CT.alertOk('Guía eliminado'); cargar(); }
      else CT.alertErr('No se pudo eliminar', 'Es posible que tenga salidas asignadas.');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  }

  cargar();
