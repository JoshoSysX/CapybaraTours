CT.requireRole('ADMIN');

document.getElementById('btnSalir').addEventListener('click', async (e) => {
  e.preventDefault();
  await CT.logout();
  window.location.href = '../login.html';
});

async function cargarLogs() {
  const tbody = document.getElementById('tbody');
  try {
    const logs = await CT.call('LogController', { action: 'listar' }, 'GET');
    if (!logs || logs.length === 0) {
      tbody.innerHTML = '<tr><td colspan="6" class="text-muted small">No hay logs registrados en la tabla LOGS.</td></tr>';
      return;
    }
    tbody.innerHTML = logs.map(l => `
      <tr>
        <td>${l.id_log}</td>
        <td>${CT.fechaCorta(l.fecha)} ${new Date(l.fecha).toLocaleTimeString('es-PE', {hour:'2-digit', minute:'2-digit'})}</td>
        <td>${l.usuario || '—'}</td>
        <td><span class="tour-badge">${l.tabla_afectada || '—'}</span></td>
        <td><b>${l.accion || '—'}</b></td>
        <td class="small">${l.detalle || '—'}</td>
      </tr>`).join('');
  } catch (e) {
    tbody.innerHTML = '<tr><td colspan="6" class="text-danger small">No se pudo cargar LOGS. Verifica que exista LogController y la tabla LOGS.</td></tr>';
  }
}

cargarLogs();
