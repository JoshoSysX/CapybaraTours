const usuario = CT.getSesion();
    document.addEventListener('DOMContentLoaded', () => { const f = document.getElementById('fechaTour'); if (f) f.min = new Date().toISOString().slice(0,10); });
    if (!usuario) {
      document.getElementById('alertaLogin').classList.remove('d-none');
    } else {
      document.getElementById('navSesion').innerHTML = '<a class="btn btn-sm btn-ct-outline px-3" href="mis-reservas.html"><i class="bi bi-person-check-fill me-1"></i>' + (usuario.persona ? usuario.persona.nombre : usuario.usuario) + '</a>';
    }

    let paquetes = [];

    async function cargarPaquetes() {
      try { paquetes = await CT.call('PaqueteController', { action: 'listar' }, 'GET'); } catch (e) { paquetes = []; }
      const sel = document.getElementById('paquete');
      if (!paquetes || paquetes.length === 0) {
        document.getElementById('sinPaquetes').classList.remove('d-none');
        document.getElementById('formPaquete').classList.add('d-none');
        document.getElementById('btnConfirmar').disabled = true;
        return;
      }
      paquetes.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.id_paquete;
        opt.dataset.precio = p.precio;
        opt.dataset.duracion = p.duracion || '';
        opt.textContent = p.nombre + ' — ' + CT.money(p.precio) + (p.duracion ? ' (' + p.duracion + ')' : '');
        sel.appendChild(opt);
      });

      const params = new URLSearchParams(window.location.search);
      const paqParam = params.get('paquete');
      if (paqParam) {
        for (let i = 0; i < sel.options.length; i++) {
          if (sel.options[i].value === paqParam) { sel.selectedIndex = i; break; }
        }
        actualizarResumen();
      }
    }

    function actualizarResumen() {
      const sel = document.getElementById('paquete');
      const opt = sel.options[sel.selectedIndex];
      const personas = parseInt(document.getElementById('personas').value) || 1;
      const fecha = document.getElementById('fechaTour').value;

      if (!opt || !opt.value) {
        document.getElementById('resumen-vacio').classList.remove('d-none');
        document.getElementById('resumen-contenido').classList.add('d-none');
        return;
      }
      const precio = parseFloat(opt.dataset.precio);
      const total = precio * personas;
      const anticipo = Math.ceil(total * 0.30 * 100) / 100;

      document.getElementById('resumen-vacio').classList.add('d-none');
      document.getElementById('resumen-contenido').classList.remove('d-none');
      document.getElementById('res-paquete').textContent = opt.text.split('—')[0].trim();
      document.getElementById('res-duracion').textContent = opt.dataset.duracion;
      document.getElementById('res-fecha').textContent = fecha || '—';
      document.getElementById('res-personas').textContent = personas;
      document.getElementById('res-precio-unit').textContent = CT.money(precio);
      document.getElementById('res-total').textContent = CT.money(total);
      document.getElementById('res-anticipo').textContent = CT.money(anticipo);
    }

    function selMetodo(el) {
      document.querySelectorAll('.metodo-pago').forEach(m => m.style.borderColor = '#E8D5B7');
      el.style.borderColor = 'var(--ct-accent)';
      document.getElementById('metodoPago').value = el.dataset.metodo;
    }

    async function confirmarReserva() {
      if (!usuario) {
        CT.alertWarn('Inicia sesión', 'Debes iniciar sesión o registrarte para reservar.');
        return;
      }
      const sel = document.getElementById('paquete');
      const fecha = document.getElementById('fechaTour').value;
      const personas = document.getElementById('personas').value;
      if (!sel.value) { CT.alertWarn('Falta el paquete', 'Selecciona un paquete turístico.'); return; }
      if (!fecha) { CT.alertWarn('Falta la fecha', 'Selecciona la fecha del tour.'); return; }
      if (fecha < new Date().toISOString().slice(0,10)) { CT.alertWarn('Fecha inválida', 'Solo puedes reservar para hoy o fechas posteriores.'); return; }
      if (!document.getElementById('metodoPago').value) { CT.alertWarn('Método de pago', 'Selecciona un método de pago para el anticipo.'); return; }

      const totalTxt = document.getElementById('res-total').textContent;
      const anticipoTxt = document.getElementById('res-anticipo').textContent;
      const anticipoNum = parseFloat(anticipoTxt.replace('S/', '').trim());

      const conf = await Swal.fire({
        icon: 'question', title: 'Confirmar reserva',
        html: `<p>Anticipo a pagar: <strong>${anticipoTxt}</strong><br>Total del tour: <strong>${totalTxt}</strong></p><p class="small text-muted">¿Confirmas tu reserva con Capibara Tours?</p>`,
        showCancelButton: true, confirmButtonText: 'Sí, confirmar', cancelButtonText: 'Cancelar', confirmButtonColor: '#A0522D'
      });
      if (!conf.isConfirmed) return;

      try {
        const hoy = new Date().toISOString().slice(0, 10);
        const resReserva = await CT.call('ReservaController', {
          action: 'guardar',
          id_paquete: sel.value,
          fecha: hoy,
          fecha_programada: fecha,
          cantidad_personas: personas
        });

        if (!resReserva) { CT.alertErr('No se pudo reservar', 'Intenta nuevamente.'); return; }
        if (resReserva.success === false) {
          CT.alertWarn('No se pudo reservar', resReserva.message || 'Debes iniciar sesión.');
          return;
        }

        // buscamos el id de la reserva recien creada para registrar el anticipo
        const misReservas = await CT.call('ReservaController', { action: 'misReservas' }, 'GET');
        const ultima = Array.isArray(misReservas) ? misReservas[0] : null;

        if (ultima) {
          await CT.call('PagoController', {
            action: 'guardar',
            id_reserva: ultima.id_reserva,
            monto: anticipoNum,
            fecha_pago: hoy,
            metodo_pago: document.getElementById('metodoPago').value
          });
        }

        Swal.fire({ icon: 'success', title: '¡Reserva confirmada!', html: '<p>Tu reserva fue registrada' + (ultima ? ' con el número <strong>#' + ultima.id_reserva + '</strong>' : '') + '.</p><p class="small">Recibirás la confirmación de tu guía y transporte próximamente.</p>', confirmButtonColor: '#A0522D' })
          .then(() => { window.location.href = 'mis-reservas.html'; });
      } catch (e) {
        CT.alertErr('Error de conexión', 'No se pudo registrar la reserva. Intenta nuevamente.');
      }
    }

    cargarPaquetes();
