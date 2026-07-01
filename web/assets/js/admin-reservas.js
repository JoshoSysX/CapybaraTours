CT.requireRole('ADMIN');

document.getElementById('btnSalir').addEventListener('click', async (e) => {
  e.preventDefault();
  await CT.logout();
  window.location.href = '../login.html';
});

const ESTADOS = [
  { c: 'PEN', n: 'Pendiente' },
  { c: 'CNF', n: 'Confirmado' },
  { c: 'PAG', n: 'Pagada' },
  { c: 'CUR', n: 'En curso' },
  { c: 'FIN', n: 'Finalizada' },
  { c: 'CAN', n: 'Cancelada' }
];

let reservasCache = [];
let guiasDisp = [];
let transportesDisp = [];
let detallesPorReserva = {};

function estadoCodigo(e) {
  return ({ PENDIENTE:'PEN', CONFIRMADO:'CNF', PAGADA:'PAG', EN_CURSO:'CUR', FINALIZADA:'FIN', CANCELADA:'CAN' }[e] || e || 'PEN');
}

function fechaInput(valor) {
  if (!valor) return '';
  const texto = String(valor);
  if (/^\d{4}-\d{2}-\d{2}/.test(texto)) return texto.slice(0, 10);
  const d = new Date(texto);
  if (isNaN(d.getTime())) return '';
  return d.toISOString().slice(0, 10);
}

async function cargar() {
  let detalles = [];
  try { reservasCache = await CT.call('ReservaController', { action: 'listar' }, 'GET'); } catch (e) { reservasCache = []; }
  try { guiasDisp = await CT.call('GuiaController', { action: 'listar' }, 'GET'); } catch (e) { guiasDisp = []; }
  try { transportesDisp = await CT.call('TransporteController', { action: 'listar' }, 'GET'); } catch (e) { transportesDisp = []; }
  try { detalles = await CT.call('DetalleReservaController', { action: 'listar' }, 'GET'); } catch (e) { detalles = []; }

  detallesPorReserva = {};
  (detalles || []).forEach(d => { if (d.reserva) detallesPorReserva[d.reserva.id_reserva] = d; });

  const tbody = document.getElementById('tbody');
  tbody.innerHTML = '';

  if (!reservasCache || reservasCache.length === 0) {
    tbody.innerHTML = '<tr><td colspan="8" class="text-muted small">No hay reservas registradas.</td></tr>';
    return;
  }

  reservasCache.forEach(r => {
    const det = detallesPorReserva[r.id_reserva];
    const cod = estadoCodigo(r.estadoReserva);
    const opciones = ESTADOS.map(e => `<option value="${e.c}" ${e.c === cod ? 'selected' : ''}>${e.n}</option>`).join('');
    const asignacion = det ? `${det.guia ? det.guia.nombre : 'Guía'} / ${det.transporte ? det.transporte.placa : 'Transporte'}` : 'Sin asignar';
    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td>#${r.id_reserva}</td>
      <td>${r.persona ? `${r.persona.nombre || ''} ${r.persona.apellido || ''}`.trim() : 'Cliente'}</td>
      <td>${r.paquete ? r.paquete.nombre : '—'}</td>
      <td>${CT.fechaCorta(r.fecha_programada)}</td>
      <td>${r.cantidad || '—'}</td>
      <td><select class="form-select form-select-sm" onchange="cambiarEstado(${r.id_reserva}, this.value)">${opciones}</select></td>
      <td class="small">${asignacion}</td>
      <td>
        <button class="btn btn-sm btn-warning me-1" onclick="editarReservaAdmin(${r.id_reserva})"><i class="bi bi-pencil"></i></button>
        <button class="btn btn-sm btn-ct-outline" onclick="asignar(${r.id_reserva})"><i class="bi bi-person-check me-1"></i>Asignar</button>
      </td>`;
    tbody.appendChild(tr);
  });
}

async function cambiarEstado(idReserva, nuevoEstado) {
  try {
    const ok = await CT.call('ReservaController', { action: 'actualizarEstado', id_reserva: idReserva, estado: nuevoEstado });
    if (ok) {
      CT.alertOk('Estado actualizado', 'La reserva fue actualizada correctamente.');
      cargar();
    } else {
      CT.alertErr('No se pudo actualizar', 'Verifica que el estado sea válido para Oracle.');
      cargar();
    }
  } catch (e) {
    CT.alertErr('Error de conexión', 'No se pudo actualizar el estado.');
    cargar();
  }
}

async function asignar(idReserva) {
  const reserva = reservasCache.find(r => r.id_reserva === idReserva);
  const det = detallesPorReserva[idReserva];
  const optsGuia = guiasDisp.map(g => `<option value="${g.idGuia}" ${det && det.guia && det.guia.idGuia === g.idGuia ? 'selected' : ''}>${g.nombre}</option>`).join('');
  const optsTransp = transportesDisp.map(t => `<option value="${t.idTransporte}" ${det && det.transporte && det.transporte.idTransporte === t.idTransporte ? 'selected' : ''}>${t.vehiculo} - ${t.placa}</option>`).join('');

  const r = await Swal.fire({
    title: 'Asignar guía y transporte', showCancelButton: true, confirmButtonText: 'Guardar', confirmButtonColor: '#A0522D',
    html: `<div class="row g-2 text-start">
      <div class="col-12"><label class="form-label small">Guía</label><select id="swGuia" class="form-select form-select-sm">${optsGuia}</select></div>
      <div class="col-12"><label class="form-label small">Transporte</label><select id="swTransp" class="form-select form-select-sm">${optsTransp}</select></div>
      <div class="col-12"><div class="alert alert-info py-2 mb-0 small"><i class="bi bi-info-circle me-1"></i>La fecha de asignación se guardará automáticamente con la fecha y hora actual.</div></div>
    </div>`,
    preConfirm: () => {
      return { id_guia: document.getElementById('swGuia').value, id_transporte: document.getElementById('swTransp').value };
    }
  });
  if (!r.isConfirmed) return;
  try {
    const accion = det ? 'editar' : 'guardar';
    const params = { action: accion, id_reserva: idReserva, id_guia: r.value.id_guia, id_transporte: r.value.id_transporte };
    if (det) params.id_detalle = det.id_detalleReserva;
    const ok = await CT.call('DetalleReservaController', params);
    if (ok) { CT.alertOk('Asignación guardada'); cargar(); }
    else CT.alertErr('No se pudo guardar la asignación', '');
  } catch (e) { CT.alertErr('Error de conexión', ''); }
}


async function editarReservaAdmin(idReserva) {
  const reserva = reservasCache.find(x => x.id_reserva === idReserva);
  if (!reserva) return;
  let paquetes = [];
  try { paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET'); } catch (e) { paquetes = []; }
  const optsPaq = (paquetes || []).map(p => `<option value="${p.id_paquete}" ${reserva.paquete && reserva.paquete.id_paquete === p.id_paquete ? 'selected' : ''}>${p.nombre} - ${CT.money(p.precio)}</option>`).join('');
  const optsEst = ESTADOS.map(e => `<option value="${e.c}" ${e.c === estadoCodigo(reserva.estadoReserva) ? 'selected' : ''}>${e.n}</option>`).join('');
  const fechaActual = fechaInput(reserva.fecha_programada);
  const hoy = new Date().toISOString().slice(0, 10);
  const r = await Swal.fire({
    title: 'Editar reserva #' + idReserva,
    html: `<div class="row g-2 text-start">
      <div class="col-12"><label class="form-label small">Paquete</label><select id="swPaquete" class="form-select form-select-sm">${optsPaq}</select></div>
      <div class="col-6"><label class="form-label small">Fecha tour</label><input id="swFecha" type="date" min="${hoy}" value="${fechaActual}" class="form-control form-control-sm"></div>
      <div class="col-6"><label class="form-label small">Personas</label><input id="swCantidad" type="number" min="1" value="${reserva.cantidad || 1}" class="form-control form-control-sm"></div>
      <div class="col-12"><label class="form-label small">Estado</label><select id="swEstado" class="form-select form-select-sm">${optsEst}</select></div>
      <div class="col-12"><div class="alert alert-info py-2 mb-0 small">Al cambiar paquete o cantidad, se actualiza automáticamente el monto del pago.</div></div>
    </div>`,
    showCancelButton: true,
    confirmButtonText: 'Guardar',
    confirmButtonColor: '#A0522D',
    preConfirm: () => {
      const fecha = document.getElementById('swFecha').value;
      const cantidad = document.getElementById('swCantidad').value;
      if (!fecha || !cantidad || Number(cantidad) <= 0) { Swal.showValidationMessage('Completa fecha y cantidad'); return false; }
      return { id_paquete: document.getElementById('swPaquete').value, fecha, cantidad, estado: document.getElementById('swEstado').value };
    }
  });
  if (!r.isConfirmed) return;
  try {
    const ok = await CT.call('ReservaController', {
      action: 'editar',
      id_reserva: idReserva,
      id_persona: reserva.persona ? reserva.persona.id_persona : 0,
      id_paquete: r.value.id_paquete,
      fecha: fechaInput(reserva.fecha) || hoy,
      fecha_programada: r.value.fecha,
      cantidad_personas: r.value.cantidad,
      estado: r.value.estado
    });
    if (ok) { CT.alertOk('Reserva actualizada', 'También se actualizó el pago.'); cargar(); }
    else CT.alertErr('No se pudo actualizar', 'Verifica los datos.');
  } catch (e) { CT.alertErr('Error de conexión', ''); }
}

cargar();
