let paquetes = [];

    async function cargarPaquetes() {
      try {
        paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET');
      } catch (e) {
        paquetes = [];
      }
      document.getElementById('cargando').classList.add('d-none');
      if (!paquetes || paquetes.length === 0) {
        document.getElementById('sinPaquetes').classList.remove('d-none');
        return;
      }
      document.getElementById('listaPaquetes').classList.remove('d-none');
      render(paquetes);
    }

    function imgSrc(p) {
      if (p.imagen) return '../' + p.imagen;
      return 'https://placehold.co/640x200/A0522D/FFF?text=' + encodeURIComponent(p.nombre || 'Tour');
    }

    function render(lista) {
      const cont = document.getElementById('listaPaquetes');
      cont.innerHTML = '';
      if (lista.length === 0) {
        cont.innerHTML = '<div class="col-12 text-center text-muted py-4">No hay paquetes que coincidan con tu búsqueda.</div>';
        return;
      }
      lista.forEach(p => {
        const col = document.createElement('div');
        col.className = 'col-12 col-md-6 col-lg-4';
        col.innerHTML = `
          <div class="card card-ct tour-card h-100">
            <img src="${imgSrc(p)}" class="card-img-top" alt="${p.nombre}"
                 onerror="this.src='https://placehold.co/640x200/A0522D/FFF?text=Capibara+Tours'">
            <div class="card-body d-flex flex-column">
              <div class="d-flex justify-content-between align-items-start mb-1">
                <h5 class="card-title mb-0">${p.nombre}</h5>
                <span class="tour-badge">${p.duracion || ''}</span>
              </div>
              <p class="card-text small flex-fill">${p.descripcion || ''}</p>
              <div class="d-flex justify-content-between align-items-center mt-2">
                <span class="price-tag">${CT.money(p.precio)} <small class="text-muted fw-normal" style="font-size:.75rem;">/ persona</small></span>
                <a href="reservar.html?paquete=${p.id_paquete}" class="btn btn-ct-primary btn-sm px-3">Reservar</a>
              </div>
            </div>
          </div>`;
        cont.appendChild(col);
      });
    }

    function filtrar() {
      const nombre = document.getElementById('filtroNombre').value.trim().toLowerCase();
      const precioMax = document.getElementById('filtroPrecio').value;
      let filtrados = paquetes.filter(p => {
        const okNombre = !nombre || (p.nombre || '').toLowerCase().includes(nombre);
        const okPrecio = precioMax === 'todos' || p.precio <= parseInt(precioMax);
        return okNombre && okPrecio;
      });
      render(filtrados);
    }
    function resetFiltros() {
      document.getElementById('filtroNombre').value = '';
      document.getElementById('filtroPrecio').value = 'todos';
      render(paquetes);
    }

    cargarPaquetes();
