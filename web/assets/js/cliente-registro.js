document.getElementById('btnRegistrar').addEventListener('click', registrar);

    async function registrar() {
      const campos = ['nombres','apellidos','numDoc','telefono','correo','password','confirmar'];
      for (const c of campos) {
        if (!document.getElementById(c).value.trim()) {
          CT.alertWarn('Campo requerido', 'Por favor completa todos los campos obligatorios.');
          return;
        }
      }
      if (document.getElementById('password').value !== document.getElementById('confirmar').value) {
        CT.alertErr('Contraseñas no coinciden', 'Verifica que las contraseñas sean iguales.');
        return;
      }
      if (document.getElementById('password').value.length < 8) {
        CT.alertWarn('Contraseña muy corta', 'Usa al menos 8 caracteres.');
        return;
      }
      if (!document.getElementById('terminos').checked) {
        CT.alertWarn('Acepta los términos', 'Debes aceptar los términos y condiciones para continuar.');
        return;
      }

      try {
        const res = await CT.call('AuthController', {
          action: 'register',
          nombres: document.getElementById('nombres').value.trim(),
          apellidos: document.getElementById('apellidos').value.trim(),
          documento: document.getElementById('tipoDoc').value,
          numero_doc: document.getElementById('numDoc').value.trim(),
          telefono: document.getElementById('telefono').value.trim(),
          email: document.getElementById('correo').value.trim(),
          password: document.getElementById('password').value
        });

        if (res.success) {
          Swal.fire({ icon:'success', title:'¡Registro exitoso!', text:'Tu cuenta fue creada con rol Cliente. Bienvenido a Capibara Tours.', confirmButtonColor:'#A0522D', timer:2000, showConfirmButton:false })
            .then(() => { window.location.href = '../login.html'; });
        } else {
          CT.alertErr('No se pudo registrar', res.message || 'Verifica que el correo no esté ya registrado.');
        }
      } catch (e) {
        CT.alertErr('Error de conexión', 'No se pudo contactar al servidor. Intenta nuevamente.');
      }
    }
