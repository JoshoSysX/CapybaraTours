CT.requireRole('ADMIN');

document.getElementById('btnSalir').addEventListener('click', async (e) => {
  e.preventDefault();
  await CT.logout();
  window.location.href = '../login.html';
});

function tarjeta(valor, color, label, icono) {
  return `<div class="col-6 col-md-3"><div class="card card-ct text-center py-3">
    <i class="bi ${icono} fs-3 mb-1" style="color:${color};"></i>
    <div style="font-size:1.8rem;font-weight:800;color:${color};">${valor}</div>
    <div class="small text-muted">${label}</div></div></div>`;
}

function estadoCodigo(e) {
  return ({ PENDIENTE:'PEN', CONFIRMADO:'CNF', PAGADA:'PAG', EN_CURSO:'CUR', FINALIZADA:'FIN', CANCELADA:'CAN' }[e] || e || 'PEN');
}

async function cargarDashboard() {
  let paquetes = [], reservas = [], guias = [], transportes = [], detalles = [];
  try { paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET'); } catch (e) {}
  try { reservas = await CT.call('ReservaController', { action: 'listar' }, 'GET'); } catch (e) {}
  try { guias = await CT.call('GuiaController', { action: 'listar' }, 'GET'); } catch (e) {}
  try { transportes = await CT.call('TransporteController', { action: 'listar' }, 'GET'); } catch (e) {}
  try { detalles = await CT.call('DetalleReservaController', { action: 'listar' }, 'GET'); } catch (e) {}

  const pendientesEstado = (reservas || []).filter(r => estadoCodigo(r.estadoReserva) === 'PEN').length;
  const reservasConDetalle = new Set((detalles || []).map(d => d.reserva ? d.reserva.id_reserva : 0));
  const sinAsignar = (reservas || []).filter(r => !reservasConDetalle.has(r.id_reserva));

  document.getElementById('tarjetas').innerHTML =
    tarjeta(paquetes.length || 0, 'var(--ct-accent-dark)', 'Paquetes activos', 'bi-map-fill') +
    tarjeta(reservas.length || 0, 'var(--ct-accent)', 'Reservas totales', 'bi-calendar-check-fill') +
    tarjeta(pendientesEstado, '#dc3545', 'Reservas pendientes', 'bi-hourglass-split') +
    tarjeta(sinAsignar.length, '#ffc107', 'Sin asignar', 'bi-exclamation-triangle-fill');

  const avisos = document.getElementById('avisos');
  if (avisos) {
    if (!reservas.length) {
      avisos.innerHTML = '<p class="small text-muted mb-0">No hay reservas registradas.</p>';
    } else if (!sinAsignar.length && pendientesEstado === 0) {
      avisos.innerHTML = '<p class="small text-success mb-0"><i class="bi bi-check-circle me-1"></i>No hay pendientes importantes por gestionar.</p>';
    } else {
      avisos.innerHTML = `
        <div class="alert alert-warning mb-2 py-2"><b>${pendientesEstado}</b> reserva(s) con estado pendiente.</div>
        <div class="alert alert-info mb-0 py-2"><b>${sinAsignar.length}</b> reserva(s) sin guía/transporte asignado. <a href="reservas.html">Ir a gestión de reservas</a>.</div>`;
    }
  }

  if (!paquetes.length) {
    document.getElementById('ranking').innerHTML = '<tr><td colspan="4" class="text-muted small">Aún no hay paquetes registrados.</td></tr>';
    return;
  }
  const conteo = {};
  reservas.forEach(r => {
    if (!r.paquete) return;
    const id = r.paquete.id_paquete;
    conteo[id] = (conteo[id] || 0) + (Number(r.cantidad) || 0);
  });
  const ranking = paquetes
    .map(p => ({ p, total: conteo[p.id_paquete] || 0 }))
    .sort((a, b) => b.total - a.total)
    .slice(0, 5);

  document.getElementById('ranking').innerHTML = ranking.map((item, i) => `
    <tr><td>${i + 1}</td><td>${item.p.nombre}</td><td>${item.total}</td><td>${CT.money(item.p.precio)}</td></tr>
  `).join('');
}

cargarDashboard();
