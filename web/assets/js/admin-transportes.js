CT.requireRole('ADMIN');
  document.getElementById('btnSalir').addEventListener('click', async (e) => { e.preventDefault(); await CT.logout(); window.location.href = '../login.html'; });

  async function cargar() {
    let transportes = [];
    try { transportes = await CT.call('TransporteController', { action: 'listar' }, 'GET'); } catch (e) {}
    if (!transportes || transportes.length === 0) {
      document.getElementById('sinTransportes').classList.remove('d-none');
      document.getElementById('contTabla').classList.add('d-none');
      return;
    }
    document.getElementById('sinTransportes').classList.add('d-none');
    document.getElementById('contTabla').classList.remove('d-none');
    const tbody = document.getElementById('tbody');
    tbody.innerHTML = '';
    transportes.forEach(g => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td><b>${g.vehiculo}</b></td><td>${g.capacidad}</td><td>${g.placa || '—'}</td>
        <td>
          <button class="btn btn-sm btn-warning me-1" onclick='editar(${JSON.stringify(g)})'><i class="bi bi-pencil"></i></button>
          <button class="btn btn-sm btn-danger" onclick="eliminar(${g.idTransporte}, '${(g.placa||'').replace(/'/g, "")}')"><i class="bi bi-trash"></i></button>
        </td>`;
      tbody.appendChild(tr);
    });
  }

  function formularioHTML(g) {
    g = g || {};
    return `<div class="row g-2 text-start">
      <div class="col-12"><label class="form-label small">Tipo de vehículo (código, 3 letras)</label><input id="swVehiculo" class="form-control form-control-sm" value="${g.vehiculo || ''}" placeholder="VAN / BUS / AUT" maxlength="3"></div>
      <div class="col-6"><label class="form-label small">Capacidad (pasajeros)</label><input id="swCapacidad" type="number" class="form-control form-control-sm" value="${g.capacidad || ''}"></div>
      <div class="col-6"><label class="form-label small">Placa</label><input id="swPlaca" class="form-control form-control-sm" value="${g.placa || ''}" maxlength="10"></div>
    </div>`;
  }

  document.getElementById('btnNuevo').addEventListener('click', nuevoTransporte);

  function leerForm() {
    const vehiculo = document.getElementById('swVehiculo').value.trim().toUpperCase();
    const capacidad = document.getElementById('swCapacidad').value;
    const placa = document.getElementById('swPlaca').value.trim();
    if (!vehiculo || !capacidad) { Swal.showValidationMessage('Tipo de vehículo y capacidad son obligatorios'); return false; }
    return { vehiculo, capacidad, placa };
  }

  async function nuevoTransporte() {
    const r = await Swal.fire({
      title: 'Registrar Transporte', html: formularioHTML(), showCancelButton: true, confirmButtonText: 'Registrar', confirmButtonColor: '#A0522D',
      preConfirm: leerForm
    });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('TransporteController', { action: 'guardar', vehiculo: r.value.vehiculo, capacidad: r.value.capacidad, placa: r.value.placa });
      if (ok) { CT.alertOk('Transporte registrado'); cargar(); } else CT.alertErr('No se pudo registrar', '');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  }

  async function editar(g) {
    const r = await Swal.fire({
      title: 'Editar transporte', html: formularioHTML(g), showCancelButton: true, confirmButtonText: 'Guardar cambios', confirmButtonColor: '#A0522D',
      preConfirm: leerForm
    });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('TransporteController', { action: 'editar', id_transporte: g.idTransporte, vehiculo: r.value.vehiculo, capacidad: r.value.capacidad, placa: r.value.placa });
      if (ok) { CT.alertOk('Transporte actualizado'); cargar(); } else CT.alertErr('No se pudo actualizar', '');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  }

  async function eliminar(id, placa) {
    const r = await Swal.fire({ title: '¿Eliminar transporte?', text: placa, icon: 'warning', showCancelButton: true, confirmButtonText: 'Eliminar', cancelButtonText: 'Cancelar', confirmButtonColor: '#dc3545' });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('TransporteController', { action: 'eliminar', id });
      if (ok) { CT.alertOk('Transporte eliminado'); cargar(); }
      else CT.alertErr('No se pudo eliminar', 'Es posible que tenga salidas asignadas.');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  }

  cargar();
