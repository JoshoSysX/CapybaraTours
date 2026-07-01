package Controllers;

import Dao.ReservaDaoImpl;
import Dao.PersonaDaoImpl;
import Dao.PagoDaoImpl;
import Dao.PaqueteDaoImpl;
import Interface.IReserva;
import Model.EstadoReserva;
import Model.Paquete;
import Model.Persona;
import Model.Reserva;
import Model.Pago;
import Model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 *
 * @author JoshoSysX
 */
@WebServlet(name = "ReservaController", urlPatterns = {"/ReservaController"})
public class ReservaController extends HttpServlet {

    private final IReserva rDao = new ReservaDaoImpl();
    private final PersonaDaoImpl personaDao = new PersonaDaoImpl();
    private final PagoDaoImpl pagoDao = new PagoDaoImpl();
    private final PaqueteDaoImpl paqueteDao = new PaqueteDaoImpl();
    private final Gson gson = new Gson();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        if (action == null) {
            action = "listar";
        }

        switch (action) {
            case "guardar":
                guardarReserva(request, response);
                break;
            case "editar":
                editarReserva(request, response);
                break;
            case "eliminar":
                eliminarReserva(request, response);
                break;
            case "buscar":
                buscarReserva(request, response);
                break;
            case "misReservas":
                misReservas(request, response);
                break;
            case "guardarAdminCliente":
                guardarAdminCliente(request, response);
                break;
            case "actualizarEstado":
                actualizarEstado(request, response);
                break;
            case "editarCliente":
                editarReservaCliente(request, response);
                break;
            default:
                listarReservas(request, response);
                break;
        }
    }

    private void listarReservas(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Reserva> reservas = rDao.lista();
        response.getWriter().print(gson.toJson(reservas));
    }

    private void guardarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Usuario usuario = session != null ? (Usuario) session.getAttribute("usuario") : null;

            if (usuario == null) {
                JsonObject json = new JsonObject();
                json.addProperty("success", false);
                json.addProperty("message", "Debe iniciar sesion");
                response.getWriter().print(json.toString());
                return;
            }

            Reserva r = new Reserva();

            Persona per = new Persona();
            per.setId_persona(usuario.getPersona().getId_persona());
            r.setPersona(per);

            Paquete paq = new Paquete();
            paq.setId_paquete(Integer.parseInt(request.getParameter("id_paquete")));
            r.setPaquete(paq);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            r.setFecha(sdf.parse(request.getParameter("fecha")));
            r.setFecha_programada(sdf.parse(request.getParameter("fecha_programada")));
            if (!fechaValida(r.getFecha_programada())) { response.getWriter().print(gson.toJson(false)); return; }
            r.setCantidad(Integer.parseInt(request.getParameter("cantidad_personas")));
            r.setEstadoReserva(EstadoReserva.PENDIENTE);

            int res = rDao.insert(r);
            response.getWriter().print(gson.toJson(res > 0));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void editarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            int idReserva = Integer.parseInt(request.getParameter("id_reserva"));
            Reserva actual = rDao.SearchById(idReserva);
            if (actual == null) {
                response.getWriter().print(gson.toJson(false));
                return;
            }

            Reserva r = new Reserva();
            r.setId_reserva(idReserva);

            Persona per = actual.getPersona();
            String idPersonaParam = request.getParameter("id_persona");
            if (idPersonaParam != null && !idPersonaParam.trim().isEmpty()) {
                per.setId_persona(Integer.parseInt(idPersonaParam));
            }
            r.setPersona(per);

            Paquete paq = new Paquete();
            paq.setId_paquete(Integer.parseInt(request.getParameter("id_paquete")));
            r.setPaquete(paq);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaParam = request.getParameter("fecha");
            if (fechaParam != null && !fechaParam.trim().isEmpty()) {
                r.setFecha(sdf.parse(fechaParam));
            } else {
                r.setFecha(actual.getFecha());
            }
            r.setFecha_programada(sdf.parse(request.getParameter("fecha_programada")));
            if (!fechaValida(r.getFecha_programada())) { response.getWriter().print(gson.toJson(false)); return; }
            r.setCantidad(Integer.parseInt(request.getParameter("cantidad_personas")));
            r.setEstadoReserva(estadoDesdeParametro(request.getParameter("estado")));

            boolean res = rDao.update(r);
            if (res) {
                actualizarPagoAutomatico(r.getId_reserva(), r.getPaquete().getId_paquete(), r.getCantidad());
            }
            response.getWriter().print(gson.toJson(res));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }


    private boolean fechaValida(java.util.Date fecha) {
        try {
            java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
            java.util.Date hoy = fmt.parse(fmt.format(new java.util.Date()));
            return !fecha.before(hoy);
        } catch (Exception e) {
            return false;
        }
    }

    private EstadoReserva estadoDesdeParametro(String estado) {
        if (estado == null) return EstadoReserva.PENDIENTE;
        switch (estado.trim()) {
            case "PEN": return EstadoReserva.PENDIENTE;
            case "CNF": return EstadoReserva.CONFIRMADO;
            case "PAG": return EstadoReserva.PAGADA;
            case "CUR": return EstadoReserva.EN_CURSO;
            case "FIN": return EstadoReserva.FINALIZADA;
            case "CAN": return EstadoReserva.CANCELADA;
            default: return EstadoReserva.valueOf(estado);
        }
    }

    private void guardarAdminCliente(HttpServletRequest request, HttpServletResponse response) throws IOException {
        JsonObject json = new JsonObject();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String fechaParam = request.getParameter("fecha_programada");
            if (fechaParam == null || fechaParam.trim().isEmpty()) {
                fechaParam = request.getParameter("fecha");
            }
            java.util.Date fechaProgramada = sdf.parse(fechaParam);
            if (!fechaValida(fechaProgramada)) {
                json.addProperty("success", false);
                json.addProperty("message", "La fecha de reserva no puede ser anterior a hoy");
                response.getWriter().print(json.toString());
                return;
            }
            Persona per = new Persona();
            per.setNombre(request.getParameter("nombres"));
            per.setApellido(request.getParameter("apellidos"));
            // DOCUMENTO en la BD es CHAR(1). Usamos D para DNI.
            per.setDocumento("D");
            per.setNumeroDoc(request.getParameter("numero_doc"));
            per.setTelefono(request.getParameter("telefono"));
            per.setEmail(request.getParameter("email"));
            int idPersona = personaDao.insertSoloPersona(per);
            if (idPersona <= 0) {
                json.addProperty("success", false);
                json.addProperty("message", "No se pudo registrar la persona");
                response.getWriter().print(json.toString());
                return;
            }
            int idPaquete = Integer.parseInt(request.getParameter("id_paquete"));
            int cantidadPersonas = Integer.parseInt(request.getParameter("cantidad_personas"));
            if (cantidadPersonas <= 0) {
                cantidadPersonas = 1;
            }

            Paquete paq = paqueteDao.SearchById(idPaquete);
            if (paq == null) {
                json.addProperty("success", false);
                json.addProperty("message", "El paquete seleccionado no existe");
                response.getWriter().print(json.toString());
                return;
            }

            Reserva r = new Reserva();
            per.setId_persona(idPersona);
            r.setPersona(per);
            r.setPaquete(paq);
            r.setFecha(new java.util.Date());
            r.setFecha_programada(fechaProgramada);
            r.setCantidad(cantidadPersonas);
            r.setEstadoReserva(EstadoReserva.PENDIENTE);
            int res = rDao.insert(r);
            if (res <= 0) {
                json.addProperty("success", false);
                json.addProperty("message", "Persona registrada, pero no se pudo crear la reserva");
                response.getWriter().print(json.toString());
                return;
            }
            // El monto para Reservar Cliente lo calcula el backend para evitar errores o manipulación desde la web.
            // Regla: monto = precio del paquete * cantidad de personas.
            double monto = paq.getPrecio() * cantidadPersonas;
            if (monto > 0) {
                Pago pago = new Pago();
                pago.setReserva(r);
                pago.setMonto(monto);
                pago.setFecha_pago(new java.util.Date());
                pago.setMetodo_pago(request.getParameter("metodo_pago"));
                pagoDao.insert(pago);
            }
            json.addProperty("success", true);
            json.addProperty("message", "Reserva registrada correctamente");
            json.addProperty("id_reserva", r.getId_reserva());
            response.getWriter().print(json.toString());
        } catch (Exception e) {
            json.addProperty("success", false);
            json.addProperty("message", "Error: " + e.getMessage());
            response.getWriter().print(json.toString());
        }
    }


    private void actualizarEstado(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int idReserva = Integer.parseInt(request.getParameter("id_reserva"));
            EstadoReserva estado = estadoDesdeParametro(request.getParameter("estado"));
            boolean res = ((ReservaDaoImpl) rDao).updateEstado(idReserva, estado);
            response.getWriter().print(gson.toJson(res));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(gson.toJson(false));
        }
    }
    private void editarReservaCliente(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Usuario usuario = session != null ? (Usuario) session.getAttribute("usuario") : null;
            if (usuario == null || usuario.getPersona() == null) {
                response.getWriter().print(gson.toJson(false));
                return;
            }

            int idReserva = Integer.parseInt(request.getParameter("id_reserva"));
            Reserva actual = rDao.SearchById(idReserva);

            if (actual == null || actual.getPersona() == null
                    || actual.getPersona().getId_persona() != usuario.getPersona().getId_persona()) {
                response.getWriter().print(gson.toJson(false));
                return;
            }

            // Si la reserva esta cancelada, el cliente no puede modificarla.
            // El administrador puede cambiarla a otro estado desde Gestion de Reservas.
            if (actual.getEstadoReserva() == EstadoReserva.CANCELADA) {
                response.getWriter().print(gson.toJson(false));
                return;
            }

            Paquete paq = new Paquete();
            paq.setId_paquete(Integer.parseInt(request.getParameter("id_paquete")));

            Reserva r = new Reserva();
            r.setId_reserva(idReserva);
            r.setPersona(actual.getPersona());
            r.setPaquete(paq);
            r.setFecha(actual.getFecha());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            r.setFecha_programada(sdf.parse(request.getParameter("fecha_programada")));
            if (!fechaValida(r.getFecha_programada())) {
                response.getWriter().print(gson.toJson(false));
                return;
            }

            r.setCantidad(Integer.parseInt(request.getParameter("cantidad_personas")));
            r.setEstadoReserva(actual.getEstadoReserva());

            boolean res = rDao.update(r);
            if (res) {
                actualizarPagoAutomatico(idReserva, paq.getId_paquete(), r.getCantidad());
            }
            response.getWriter().print(gson.toJson(res));
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void actualizarPagoAutomatico(int idReserva, int idPaquete, int cantidadPersonas) {
        try {
            Paquete paq = paqueteDao.SearchById(idPaquete);
            if (paq == null) {
                return;
            }
            double monto = paq.getPrecio() * cantidadPersonas;
            boolean actualizado = pagoDao.actualizarMontoPorReserva(idReserva, monto);
            if (!actualizado) {
                Pago pago = new Pago();
                Reserva reserva = new Reserva();
                reserva.setId_reserva(idReserva);
                pago.setReserva(reserva);
                pago.setMonto(monto);
                pago.setFecha_pago(new java.util.Date());
                pago.setMetodo_pago("EFE");
                pagoDao.insert(pago);
            }
        } catch (Exception e) {
            System.out.println("No se pudo actualizar el pago automatico: " + e.getMessage());
        }
    }


    private void buscarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Reserva r = rDao.SearchById(id);
        response.getWriter().print(gson.toJson(r));
    }

    private void eliminarReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean res = rDao.delete(id);
        response.getWriter().print(gson.toJson(res));
    }

    private void misReservas(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            HttpSession session = request.getSession(false);
            Usuario usuario = session != null ? (Usuario) session.getAttribute("usuario") : null;

            if (usuario == null) {
                JsonObject json = new JsonObject();
                json.addProperty("success", false);
                json.addProperty("message", "Debe iniciar sesion");
                response.getWriter().print(json.toString());
                return;
            }

            List<Reserva> reservas = rDao.listaPorPersona(usuario.getPersona().getId_persona());
            response.getWriter().print(gson.toJson(reservas));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}