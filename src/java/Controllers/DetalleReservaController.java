package Controllers;

import Dao.DetalleReservaDaoImpl;
import Interface.IDetalleReserva;
import Model.DetalleReserva;
import Model.Guia;
import Model.Reserva;
import Model.Transporte;
import com.google.gson.Gson;
import java.io.IOException;
import java.text.SimpleDateFormat;
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
@WebServlet(name = "DetalleReservaController", urlPatterns = {"/DetalleReservaController"})
public class DetalleReservaController extends HttpServlet {

    private final IDetalleReserva dDao = new DetalleReservaDaoImpl();
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
                guardarDetalle(request, response);
                break;
            case "editar":
                editarDetalle(request, response);
                break;
            case "eliminar":
                eliminarDetalle(request, response);
                break;
            case "buscar":
                buscarDetalle(request, response);
                break;
            case "porReserva":
                detallesPorReserva(request, response);
                break;
            default:
                listarDetalles(request, response);
                break;
        }
    }

    private void listarDetalles(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<DetalleReserva> detalles = dDao.lista();
        response.getWriter().print(gson.toJson(detalles));
    }

    private void guardarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            DetalleReserva dr = new DetalleReserva();

            Reserva res = new Reserva();
            res.setId_reserva(Integer.parseInt(request.getParameter("id_reserva")));
            dr.setReserva(res);

            Guia g = new Guia();
            g.setIdGuia(Integer.parseInt(request.getParameter("id_guia")));
            dr.setGuia(g);

            Transporte t = new Transporte();
            t.setIdTransporte(Integer.parseInt(request.getParameter("id_transporte")));
            dr.setTransporte(t);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dr.setFecha_salida(sdf.parse(request.getParameter("fecha_salida")));

            int resultado = dDao.insert(dr);
            response.getWriter().print(gson.toJson(resultado > 0));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void editarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            DetalleReserva dr = new DetalleReserva();
            dr.setId_detalleReserva(Integer.parseInt(request.getParameter("id_detalle")));

            Reserva res = new Reserva();
            res.setId_reserva(Integer.parseInt(request.getParameter("id_reserva")));
            dr.setReserva(res);

            Guia g = new Guia();
            g.setIdGuia(Integer.parseInt(request.getParameter("id_guia")));
            dr.setGuia(g);

            Transporte t = new Transporte();
            t.setIdTransporte(Integer.parseInt(request.getParameter("id_transporte")));
            dr.setTransporte(t);

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            dr.setFecha_salida(sdf.parse(request.getParameter("fecha_salida")));

            boolean resultado = dDao.update(dr);
            response.getWriter().print(gson.toJson(resultado));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void buscarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        DetalleReserva dr = dDao.SearchById(id);
        response.getWriter().print(gson.toJson(dr));
    }

    private void eliminarDetalle(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean res = dDao.delete(id);
        response.getWriter().print(gson.toJson(res));
    }

    private void detallesPorReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idReserva = Integer.parseInt(request.getParameter("id_reserva"));
        List<DetalleReserva> detalles = dDao.listaPorReserva(idReserva);
        response.getWriter().print(gson.toJson(detalles));
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