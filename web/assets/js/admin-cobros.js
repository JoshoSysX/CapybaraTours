CT.requireRole('ADMIN');
  document.getElementById('btnSalir').addEventListener('click', async (e) => { e.preventDefault(); await CT.logout(); window.location.href = '../login.html'; });

  const METODOS = { YAP: 'Yape', PLI: 'Plin', TAR: 'Tarjeta', EFE: 'Efectivo' };

  async function cargar() {
    let pagos = [];
    try { pagos = await CT.call('PagoController', { action: 'listar' }, 'GET'); } catch (e) {}
    if (!pagos || pagos.length === 0) {
      document.getElementById('sinPagos').classList.remove('d-none');
      document.getElementById('contTabla').classList.add('d-none');
      return;
    }
    document.getElementById('sinPagos').classList.add('d-none');
    document.getElementById('contTabla').classList.remove('d-none');
    const tbody = document.getElementById('tbody');
    tbody.innerHTML = '';
    pagos.forEach(p => {
      const tr = document.createElement('tr');
      tr.innerHTML = `
        <td>#${p.id_pago}</td>
        <td><b style="color:var(--ct-accent);">#${p.reserva ? p.reserva.id_reserva : '—'}</b></td>
        <td>${CT.money(p.monto)}</td>
        <td>${CT.fechaCorta(p.fecha_pago)}</td>
        <td><span class="badge bg-secondary">${METODOS[p.metodo_pago] || p.metodo_pago}</span></td>`;
      tbody.appendChild(tr);
    });
  }

  document.getElementById('btnNuevo').addEventListener('click', async () => {
    let reservas = [];
    try { reservas = await CT.call('ReservaController', { action: 'listar' }, 'GET'); } catch (e) {}
    if (!reservas.length) { CT.alertWarn('No hay reservas', 'Aún no hay reservas a las cuales registrar un pago.'); return; }

    const opts = reservas.map(r => `<option value="${r.id_reserva}">#${r.id_reserva} — ${r.persona ? r.persona.nombre : ''} — ${r.paquete ? r.paquete.nombre : ''}</option>`).join('');

    const r = await Swal.fire({
      title: 'Registrar pago', showCancelButton: true, confirmButtonText: 'Registrar', confirmButtonColor: '#A0522D',
      html: `<div class="row g-2 text-start">
        <div class="col-12"><label class="form-label small">Reserva</label><select id="swReserva" class="form-select form-select-sm">${opts}</select></div>
        <div class="col-6"><label class="form-label small">Monto (S/)</label><input id="swMonto" type="number" step="0.01" class="form-control form-control-sm"></div>
        <div class="col-6"><label class="form-label small">Fecha de pago</label><input id="swFecha" type="date" class="form-control form-control-sm" value="${new Date().toISOString().slice(0,10)}"></div>
        <div class="col-12"><label class="form-label small">Método</label>
          <select id="swMetodo" class="form-select form-select-sm">
            <option value="YAP">Yape</option><option value="PLI">Plin</option><option value="TAR">Tarjeta</option><option value="EFE">Efectivo</option>
          </select>
        </div>
      </div>`,
      preConfirm: () => {
        const monto = document.getElementById('swMonto').value;
        if (!monto || monto <= 0) { Swal.showValidationMessage('Ingresa un monto válido'); return false; }
        return {
          id_reserva: document.getElementById('swReserva').value,
          monto, fecha_pago: document.getElementById('swFecha').value,
          metodo_pago: document.getElementById('swMetodo').value
        };
      }
    });
    if (!r.isConfirmed) return;
    try {
      const ok = await CT.call('PagoController', { action: 'guardar', ...r.value });
      if (ok) { CT.alertOk('Pago registrado', 'El estado de la reserva se actualiza automáticamente.'); cargar(); }
      else CT.alertErr('No se pudo registrar el pago', '');
    } catch (e) { CT.alertErr('Error de conexión', ''); }
  });

  cargar();
