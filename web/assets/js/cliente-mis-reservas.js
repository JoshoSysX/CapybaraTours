const usuario = CT.requireRole('CLIENTE');

    document.getElementById('btnSalir').addEventListener('click', async (e) => {
      e.preventDefault();
      await CT.logout();
      window.location.href = '../login.html';
    });

    const ESTADOS = {
      PENDIENTE:  { txt: 'Pendiente',  clase: 'badge-pendiente' },
      CONFIRMADO: { txt: 'Anticipo pagado', clase: 'badge-guia text-white' },
      PAGADA:     { txt: 'Pagado completo', clase: 'badge-confirmada text-white' },
      EN_CURSO:   { txt: 'En curso',   clase: 'badge-ejecutado text-white' },
      FINALIZADA: { txt: 'Finalizada', clase: 'badge-ejecutado text-white' },
      CANCELADA:  { txt: 'Cancelada',  clase: 'bg-danger text-white' }
    };

    function fechaInput(valor) {
      if (!valor) return '';
      const texto = String(valor);
      if (/^\d{4}-\d{2}-\d{2}/.test(texto)) return texto.slice(0, 10);
      const d = new Date(texto);
      if (isNaN(d.getTime())) return '';
      return d.toISOString().slice(0, 10);
    }

    function estadoCodigo(e) {
      return ({ PENDIENTE:'PEN', CONFIRMADO:'CNF', PAGADA:'PAG', EN_CURSO:'CUR', FINALIZADA:'FIN', CANCELADA:'CAN' }[e] || e || 'PEN');
    }

    function tarjeta(valor, color, label) {
      return `<div class="col-6 col-md-3"><div class="card card-ct text-center py-3">
        <div style="font-size:1.8rem;font-weight:800;color:${color};">${valor}</div>
        <div class="small text-muted">${label}</div></div></div>`;
    }

    async function cargar() {
      let reservas = [];
      try { reservas = await CT.call('ReservaController', { action: 'misReservas' }, 'GET'); } catch (e) { reservas = []; }
      document.getElementById('cargando').classList.add('d-none');

      if (!Array.isArray(reservas) || reservas.length === 0) {
        document.getElementById('sinReservas').classList.remove('d-none');
        document.getElementById('tarjetasEstado').innerHTML = tarjeta(0, 'var(--ct-accent-dark)', 'Total reservas');
        return;
      }

      window.reservasClienteCache = reservas;
      const total = reservas.length;
      const pagadas = reservas.filter(r => r.estadoReserva === 'PAGADA').length;
      const finalizadas = reservas.filter(r => r.estadoReserva === 'FINALIZADA').length;
      const pendientes = reservas.filter(r => r.estadoReserva === 'PENDIENTE' || r.estadoReserva === 'CONFIRMADO').length;

      document.getElementById('tarjetasEstado').innerHTML =
        tarjeta(total, 'var(--ct-accent-dark)', 'Total reservas') +
        tarjeta(pagadas, '#28a745', 'Pagadas') +
        tarjeta(finalizadas, '#17a2b8', 'Finalizadas') +
        tarjeta(pendientes, '#ffc107', 'Pendientes / saldo');

      document.getElementById('contTabla').classList.remove('d-none');
      const tbody = document.getElementById('tbodyReservas');
      tbody.innerHTML = '';
      reservas.forEach(r => {
        const est = ESTADOS[r.estadoReserva] || { txt: r.estadoReserva, clase: 'bg-secondary text-white' };
        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td><span class="fw-bold" style="color:var(--ct-accent);">#${r.id_reserva}</span></td>
          <td>${r.paquete ? r.paquete.nombre : '—'}</td>
          <td>${CT.fechaCorta(r.fecha_programada)}</td>
          <td>${r.cantidad}</td>
          <td><span class="badge ${est.clase}">${est.txt}</span></td>
          <td>${estadoCodigo(r.estadoReserva) === 'CAN'
            ? '<span class="text-muted small"><i class="bi bi-lock-fill me-1"></i>No editable</span>'
            : `<button class="btn btn-sm btn-ct-outline" onclick="editarReservaCliente(${r.id_reserva})"><i class="bi bi-pencil me-1"></i>Editar</button>`}
          </td>`;
        tbody.appendChild(tr);
      });
    }

    cargar();

async function editarReservaCliente(idReserva) {
  const reserva = (window.reservasClienteCache || []).find(x => x.id_reserva === idReserva);
  if (!reserva) return;
  if (estadoCodigo(reserva.estadoReserva) === 'CAN') {
    CT.alertWarn('Reserva cancelada', 'No puedes editar una reserva cancelada. El administrador debe cambiar el estado para habilitarla nuevamente.');
    return;
  }

  let paquetes = [];
  try { paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET'); } catch (e) { paquetes = []; }
  const opts = (paquetes || []).map(p => `<option value="${p.id_paquete}" ${reserva.paquete && reserva.paquete.id_paquete === p.id_paquete ? 'selected' : ''}>${p.nombre} - ${CT.money(p.precio)}</option>`).join('');
  const fechaActual = fechaInput(reserva.fecha_programada);
  const hoy = new Date().toISOString().slice(0, 10);

  const r = await Swal.fire({
    title: 'Editar reserva',
    html: `<div class="row g-2 text-start">
      <div class="col-12"><label class="form-label small">Paquete</label><select id="swPaquete" class="form-select form-select-sm">${opts}</select></div>
      <div class="col-12"><label class="form-label small">Fecha del tour</label><input id="swFecha" type="date" min="${hoy}" value="${fechaActual}" class="form-control form-control-sm"></div>
      <div class="col-12"><label class="form-label small">Cantidad de personas</label><input id="swCantidad" type="number" min="1" value="${reserva.cantidad || 1}" class="form-control form-control-sm"></div>
      <div class="col-12"><div class="alert alert-info py-2 mb-0 small">El pago se actualizará automáticamente según el precio del paquete y la cantidad de personas.</div></div>
    </div>`,
    showCancelButton: true,
    confirmButtonText: 'Guardar cambios',
    confirmButtonColor: '#A0522D',
    preConfirm: () => {
      const fecha = document.getElementById('swFecha').value;
      const cantidad = document.getElementById('swCantidad').value;
      if (!fecha || !cantidad || Number(cantidad) <= 0) {
        Swal.showValidationMessage('Completa fecha y cantidad correctamente');
        return false;
      }
      return { id_paquete: document.getElementById('swPaquete').value, fecha, cantidad };
    }
  });
  if (!r.isConfirmed) return;

  try {
    const ok = await CT.call('ReservaController', {
      action: 'editarCliente',
      id_reserva: idReserva,
      id_paquete: r.value.id_paquete,
      fecha_programada: r.value.fecha,
      cantidad_personas: r.value.cantidad
    });
    if (ok) { CT.alertOk('Reserva actualizada', 'También se actualizó el monto del pago.'); cargar(); }
    else CT.alertErr('No se pudo actualizar', 'Verifica los datos de la reserva.');
  } catch (e) { CT.alertErr('Error de conexión', ''); }
}

