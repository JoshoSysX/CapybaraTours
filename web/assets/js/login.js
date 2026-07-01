document.addEventListener('DOMContentLoaded', () => {
  const actual = CT.getSesion();
  if (actual) {
    window.location.href = actual.rol === 'ADMIN' ? 'admin/dashboard.html' : 'cliente/mis-reservas.html';
  }
});
document.getElementById('btnLogin').addEventListener('click', iniciarSesion);
document.getElementById('password').addEventListener('keydown', e => { if (e.key === 'Enter') iniciarSesion(); });
async function iniciarSesion(){
  const usuario = document.getElementById('usuario').value.trim();
  const pass = document.getElementById('password').value.trim();
  if(!usuario || !pass){ CT.alertWarn('Campos vacíos','Completa usuario y contraseña.'); return; }
  try{
    const res = await CT.call('AuthController', {action:'validar', usuario, password:pass});
    if(res.success){
      CT.setSesion(res.userData);
      const destino = res.userData.rol === 'ADMIN' ? 'admin/dashboard.html' : 'cliente/mis-reservas.html';
      Swal.fire({icon:'success', title:'Bienvenido', text:'Redirigiendo...', confirmButtonColor:'#A0522D', timer:1000, showConfirmButton:false})
        .then(()=>{ window.location.href = destino; });
    } else CT.alertErr('No se pudo ingresar', res.message || 'Usuario o contraseña incorrectos.');
  }catch(e){ CT.alertErr('Error de conexión','No se pudo contactar al servidor.'); }
}
