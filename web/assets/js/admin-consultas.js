CT.requireRole('ADMIN');

document.getElementById('btnSalir').addEventListener('click', async (e) => {
  e.preventDefault();
  await CT.logout();
  window.location.href = '../login.html';
});

function barra(nombre, total, totalReservas) {
  const porcentajeReal = totalReservas > 0 ? (total / totalReservas) * 100 : 0;
  const ancho = Math.max(8, porcentajeReal);
  return `<div class="mb-3">
    <div class="d-flex justify-content-between small mb-1"><b>${nombre}</b><span>${porcentajeReal.toFixed(1)}% (${total})</span></div>
    <div class="progress" style="height:14px;"><div class="progress-bar barra-reporte" style="width:${ancho}%"></div></div>
  </div>`;
}

async function cargarConsultas() {
  let reservas = [], pagos = [];
  try { reservas = await CT.call('ReservaController', { action: 'listar' }, 'GET'); } catch (e) { reservas = []; }
  try { pagos = await CT.call('PagoController', { action: 'listar' }, 'GET'); } catch (e) { pagos = []; }

  const ym = new Date().toISOString().slice(0, 7);
  const ventasMes = (pagos || [])
    .filter(p => String(p.fecha_pago || '').slice(0, 7) === ym)
    .reduce((s, p) => s + (Number(p.monto) || 0), 0);

  const porPaq = {};
  const clientes = {};
  (reservas || []).forEach(r => {
    const nombrePaquete = r.paquete ? r.paquete.nombre : 'Sin paquete';
    porPaq[nombrePaquete] = (porPaq[nombrePaquete] || 0) + 1;
    const nombreCliente = r.persona ? `${r.persona.nombre || ''} ${r.persona.apellido || ''}`.trim() : 'Cliente';
    clientes[nombreCliente || 'Cliente'] = (clientes[nombreCliente || 'Cliente'] || 0) + 1;
  });

  const rankingPaquetes = Object.entries(porPaq).sort((a, b) => b[1] - a[1]);
  const masVendido = rankingPaquetes[0] ? rankingPaquetes[0][0] : '—';
  const menosVendido = rankingPaquetes.length ? rankingPaquetes[rankingPaquetes.length - 1][0] : '—';

  document.getElementById('kpis').innerHTML = `
    <div class="col-md-4"><div class="card card-ct reporte-box p-3 h-100"><div class="small text-muted">Total vendido este mes</div><h3>${CT.money(ventasMes)}</h3></div></div>
    <div class="col-md-4"><div class="card card-ct reporte-box p-3 h-100"><div class="small text-muted">Paquete más vendido</div><h5>${masVendido}</h5></div></div>
    <div class="col-md-4"><div class="card card-ct reporte-box p-3 h-100"><div class="small text-muted">Paquete menos vendido</div><h5>${menosVendido}</h5></div></div>`;

  const totalReservas = rankingPaquetes.reduce((s, x) => s + x[1], 0);
  document.getElementById('graficoPaquetes').innerHTML = rankingPaquetes.length
    ? rankingPaquetes.map(([nombre, total]) => barra(nombre, total, totalReservas)).join('')
    : '<p class="small text-muted">Sin reservas para mostrar ranking.</p>';

  document.getElementById('clientes').innerHTML = Object.entries(clientes)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 10)
    .map(([nombre, total], i) => `<tr><td>${i + 1}</td><td>${nombre}</td><td><b>${total}</b> reserva(s)</td></tr>`)
    .join('') || '<tr><td class="text-muted">Sin datos.</td></tr>';
}

cargarConsultas();
