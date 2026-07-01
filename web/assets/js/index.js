async function cargarPaquetesRecomendados() {
  const contenedor = document.getElementById('paquetesRecomendados');
  if (!contenedor) return;

  try {
    const paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET');
    const recomendados = (paquetes || []).slice(0, 2);

    if (recomendados.length === 0) {
      contenedor.innerHTML = '<div class="col-12"><p class="text-muted small">Aún no hay paquetes registrados en la base de datos.</p></div>';
      return;
    }

    contenedor.innerHTML = recomendados.map(p => {
      const img = p.imagen ? p.imagen : 'assets/png/logo.png';
      return `
        <div class="col-12 col-md-6">
          <div class="card card-ct tour-card h-100">
            <img src="${img}" class="card-img-top" alt="${p.nombre || 'Paquete turístico'}" onerror="this.src='assets/png/logo.png'">
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between align-items-start mb-2">
                <h5 class="card-title mb-0">${p.nombre || 'Paquete turístico'}</h5>
                <span class="tour-badge">${p.duracion || 'Tour'}</span>
              </div>
              <p class="card-text text-muted small flex-fill">${p.descripcion || 'Experiencia turística disponible en Capibara Tours.'}</p>
              <div class="d-flex justify-content-between align-items-center">
                <b style="color:var(--ct-accent-dark);">${CT.money(p.precio)}</b>
                <a href="cliente/catalogo.html" class="btn btn-ct-primary btn-sm px-3">Ver paquete</a>
              </div>
            </div>
          </div>
        </div>`;
    }).join('');
  } catch (e) {
    contenedor.innerHTML = '<div class="col-12"><p class="text-danger small">No se pudieron cargar los paquetes desde la base de datos.</p></div>';
  }
}

cargarPaquetesRecomendados();
