package Controllers;

import Dao.ReservaDaoImpl;
import Interface.IReserva;
import Model.EstadoReserva;
import Model.Paquete;
import Model.Persona;
import Model.Reserva;
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
            Usuario usuario = (Usuario) session.getAttribute("usuario");

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
            Reserva r = new Reserva();
            r.setId_reserva(Integer.parseInt(request.getParameter("id_reserva")));

            Persona per = new Persona();
            per.setId_persona(Integer.parseInt(request.getParameter("id_persona")));
            r.setPersona(per);

            Paquete paq = new Paquete();
            paq.setId_paquete(Integer.parseInt(request.getParameter("id_paquete")));
            r.setPaquete(paq);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            r.setFecha(sdf.parse(request.getParameter("fecha")));
            r.setFecha_programada(sdf.parse(request.getParameter("fecha_programada")));
            r.setCantidad(Integer.parseInt(request.getParameter("cantidad_personas")));
            r.setEstadoReserva(EstadoReserva.valueOf(request.getParameter("estado")));

            boolean res = rDao.update(r);
            response.getWriter().print(gson.toJson(res));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
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
            Usuario usuario = (Usuario) session.getAttribute("usuario");

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