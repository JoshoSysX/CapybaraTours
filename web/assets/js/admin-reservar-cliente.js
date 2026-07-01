CT.requireRole('ADMIN');

document.getElementById('btnSalir').addEventListener('click', async (e) => {
  e.preventDefault();
  await CT.logout();
  window.location.href = '../login.html';
});

function hoyISO() { return new Date().toISOString().slice(0, 10); }
function valor(id) { return document.getElementById(id).value.trim(); }

let paquetesDisponibles = [];

function obtenerPaqueteSeleccionado() {
  const id = Number(valor('paquete'));
  return paquetesDisponibles.find(p => Number(p.id_paquete) === id);
}

function calcularMontoAutomatico() {
  const paquete = obtenerPaqueteSeleccionado();
  const cantidad = Math.max(1, Number(valor('cantidad')) || 1);
  const monto = paquete ? Number(paquete.precio || 0) * cantidad : 0;
  document.getElementById('monto').value = monto.toFixed(2);
  const detalle = document.getElementById('detalleMonto');
  if (detalle) {
    detalle.textContent = paquete
      ? `Precio por persona: ${CT.money(paquete.precio)} × ${cantidad} persona(s)`
      : 'Seleccione un paquete para calcular el monto.';
  }
}

async function cargarPaquetes() {
  const select = document.getElementById('paquete');
  document.getElementById('fecha').min = hoyISO();
  try {
    paquetesDisponibles = await CT.call('PaqueteController', { action: 'listar' }, 'GET');
    select.innerHTML = '<option value="">Seleccione</option>' + (paquetesDisponibles || []).map(p =>
      `<option value="${p.id_paquete}" data-precio="${p.precio}">${p.nombre} - ${CT.money(p.precio)}</option>`
    ).join('');
    calcularMontoAutomatico();
  } catch (e) {
    select.innerHTML = '<option value="">No se pudieron cargar paquetes</option>';
  }
}

async function registrarReservaCliente() {
  if (!valor('nombres') || !valor('apellidos') || !valor('dni') || !valor('telefono') || !valor('correo') || !valor('paquete')) {
    CT.alertWarn('Datos incompletos', 'Completa los datos del cliente y selecciona un paquete.');
    return;
  }

  const fecha = valor('fecha');
  if (!fecha || fecha < hoyISO()) {
    CT.alertWarn('Fecha inválida', 'Selecciona hoy o una fecha posterior.');
    return;
  }

  try {
    const resp = await CT.call('ReservaController', {
      action: 'guardarAdminCliente',
      nombres: valor('nombres'),
      apellidos: valor('apellidos'),
      numero_doc: valor('dni'),
      telefono: valor('telefono'),
      email: valor('correo'),
      id_paquete: valor('paquete'),
      fecha_programada: fecha,
      cantidad_personas: valor('cantidad') || 1,
      monto: valor('monto') || 0,
      metodo_pago: valor('metodo') || 'EFE'
    });

    if (resp && resp.success) {
      await CT.alertOk('Reserva registrada', 'El cliente fue guardado solo en PERSONA y se creó su reserva.');
      window.location.href = 'reservas.html';
    } else {
      CT.alertErr('No se pudo registrar', (resp && resp.message) || 'Revisa los datos ingresados.');
    }
  } catch (e) {
    CT.alertErr('Error de conexión', 'No se pudo conectar con ReservaController.');
  }
}

document.getElementById('paquete').addEventListener('change', calcularMontoAutomatico);
document.getElementById('cantidad').addEventListener('input', calcularMontoAutomatico);
document.getElementById('btnGuardar').addEventListener('click', registrarReservaCliente);
cargarPaquetes();
