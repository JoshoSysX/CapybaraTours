CT.requireRole('ADMIN');

const btnSalir = document.getElementById('btnSalir');
if (btnSalir) {
  btnSalir.addEventListener('click', async (e) => {
    e.preventDefault();
    await CT.logout();
    window.location.href = '../login.html';
  });
}

function barra(nombre, total, totalReservas) {
  const porcentajeReal = totalReservas > 0 ? (total / totalReservas) * 100 : 0;
  const ancho = Math.max(8, porcentajeReal);
  return `<div class="mb-3">
    <div class="d-flex justify-content-between small mb-1"><b>${nombre}</b><span>${porcentajeReal.toFixed(1)}% (${total})</span></div>
    <div class="progress" style="height:14px;"><div class="progress-bar barra-reporte" style="width:${ancho}%"></div></div>
  </div>`;
}

function nombreMes(numero) {
  const meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre', 'Octubre', 'Noviembre', 'Diciembre'];
  return meses[(Number(numero) || 1) - 1] || 'Mes';
}

function inicializarFiltroPagos() {
  const anioActual = new Date().getFullYear();
  const mesActual = new Date().getMonth() + 1;

  const tipo = document.getElementById('tipoConsultaPago');
  const anio = document.getElementById('anioPago');
  const mes = document.getElementById('mesPago');
  const grupoAnio = document.getElementById('grupoAnioPago');
  const grupoMes = document.getElementById('grupoMesPago');
  const form = document.getElementById('formConsultaPagos');

  if (!tipo || !anio || !mes || !grupoAnio || !grupoMes || !form) return;

  anio.value = anioActual;
  mes.value = mesActual;

  function actualizarVistaFiltro() {
    const valor = tipo.value;
    grupoAnio.classList.toggle('d-none', valor === 'total');
    grupoMes.classList.toggle('d-none', valor !== 'mes');
  }

  tipo.addEventListener('change', actualizarVistaFiltro);
  form.addEventListener('submit', async (e) => {
    e.preventDefault();
    await cargarConsultaPagos();
  });

  actualizarVistaFiltro();
}

async function cargarConsultaPagos() {
  const tbody = document.getElementById('tablaConsultaPagos');
  const tipoEl = document.getElementById('tipoConsultaPago');
  const anioEl = document.getElementById('anioPago');
  const mesEl = document.getElementById('mesPago');

  if (!tbody || !tipoEl) return 0;

  const tipo = tipoEl.value || 'total';
  const params = { action: 'reporteVentas', tipo };

  if (tipo === 'anio' || tipo === 'mes') {
    params.anio = anioEl.value;
  }
  if (tipo === 'mes') {
    params.mes = mesEl.value;
  }

  tbody.innerHTML = '<tr><td colspan="3" class="text-muted">Consultando pagos...</td></tr>';

  try {
    const data = await CT.call('PagoController', params, 'GET');
    if (!data || data.success === false) {
      throw new Error(data && data.message ? data.message : 'No se pudo obtener el reporte.');
    }

    let periodo = 'Todos los pagos registrados';
    if (tipo === 'anio') periodo = data.anio || params.anio;
    if (tipo === 'mes') periodo = `${nombreMes(data.mes || params.mes)} ${data.anio || params.anio}`;

    tbody.innerHTML = `
      <tr>
        <td>${data.descripcion || 'Consulta de pagos'}</td>
        <td>${periodo}</td>
        <td class="text-end fw-bold">${CT.money(data.total)}</td>
      </tr>`;

    return Number(data.total) || 0;
  } catch (e) {
    console.error('Error en consulta de pagos:', e);
    tbody.innerHTML = `<tr><td colspan="3" class="text-danger">No se pudo cargar la consulta de pagos.</td></tr>`;
    return 0;
  }
}

async function cargarConsultas() {
  let reservas = [];
  try { reservas = await CT.call('ReservaController', { action: 'listar' }, 'GET'); } catch (e) { reservas = []; }

  const totalVendido = await cargarConsultaPagos();

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
    <div class="col-md-4"><div class="card card-ct reporte-box p-3 h-100"><div class="small text-muted">Total vendido</div><h3>${CT.money(totalVendido)}</h3></div></div>
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

inicializarFiltroPagos();
cargarConsultas();
