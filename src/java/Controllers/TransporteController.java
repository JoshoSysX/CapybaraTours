package Controllers;

import Dao.TransporteDaoImpl;
import Interface.ITransporte;
import Model.Transporte;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author JoshoSysX
 */
@WebServlet(name = "TransporteController", urlPatterns = {"/TransporteController"})
public class TransporteController extends HttpServlet {

    private final ITransporte tDao = new TransporteDaoImpl();
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
                guardarTransporte(request, response);
                break;
            case "editar":
                editarTransporte(request, response);
                break;
            case "eliminar":
                eliminarTransporte(request, response);
                break;
            case "buscar":
                buscarTransporte(request, response);
                break;
            default:
                listarTransportes(request, response);
                break;
        }
    }

    private void listarTransportes(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Transporte> transportes = tDao.lista();
        response.getWriter().print(gson.toJson(transportes));
    }

    private void guardarTransporte(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Transporte t = new Transporte();
            t.setVehiculo(request.getParameter("vehiculo"));
            t.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
            t.setPlaca(request.getParameter("placa"));

            boolean res = tDao.insert(t);
            response.getWriter().print(gson.toJson(res));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void editarTransporte(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Transporte t = new Transporte();
            t.setIdTransporte(Integer.parseInt(request.getParameter("id_transporte")));
            t.setVehiculo(request.getParameter("vehiculo"));
            t.setCapacidad(Integer.parseInt(request.getParameter("capacidad")));
            t.setPlaca(request.getParameter("placa"));

            boolean res = tDao.update(t);
            response.getWriter().print(gson.toJson(res));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void buscarTransporte(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Transporte t = tDao.SearchById(id);
        response.getWriter().print(gson.toJson(t));
    }

    private void eliminarTransporte(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean res = tDao.delete(id);
        response.getWriter().print(gson.toJson(res));
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