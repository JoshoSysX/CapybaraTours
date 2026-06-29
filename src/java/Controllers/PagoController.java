package Controllers;

import Dao.PagoDaoImpl;
import Interface.IPago;
import Model.Pago;
import Model.Reserva;
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
@WebServlet(name = "PagoController", urlPatterns = {"/PagoController"})
public class PagoController extends HttpServlet {

    private final IPago pDao = new PagoDaoImpl();
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
                guardarPago(request, response);
                break;
            case "editar":
                editarPago(request, response);
                break;
            case "eliminar":
                eliminarPago(request, response);
                break;
            case "buscar":
                buscarPago(request, response);
                break;
            case "porReserva":
                pagosPorReserva(request, response);
                break;
            default:
                listarPagos(request, response);
                break;
        }
    }

    private void listarPagos(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Pago> pagos = pDao.lista();
        response.getWriter().print(gson.toJson(pagos));
    }

    private void guardarPago(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Pago p = new Pago();

            Reserva res = new Reserva();
            res.setId_reserva(Integer.parseInt(request.getParameter("id_reserva")));
            p.setReserva(res);

            p.setMonto(Double.parseDouble(request.getParameter("monto")));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            p.setFecha_pago(sdf.parse(request.getParameter("fecha_pago")));
            p.setMetodo_pago(request.getParameter("metodo_pago"));

            int resultado = pDao.insert(p);
            response.getWriter().print(gson.toJson(resultado > 0));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void editarPago(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Pago p = new Pago();
            p.setId_pago(Integer.parseInt(request.getParameter("id_pago")));

            Reserva res = new Reserva();
            res.setId_reserva(Integer.parseInt(request.getParameter("id_reserva")));
            p.setReserva(res);

            p.setMonto(Double.parseDouble(request.getParameter("monto")));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            p.setFecha_pago(sdf.parse(request.getParameter("fecha_pago")));
            p.setMetodo_pago(request.getParameter("metodo_pago"));

            boolean resultado = pDao.update(p);
            response.getWriter().print(gson.toJson(resultado));
        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void buscarPago(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Pago p = pDao.SearchById(id);
        response.getWriter().print(gson.toJson(p));
    }

    private void eliminarPago(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean res = pDao.delete(id);
        response.getWriter().print(gson.toJson(res));
    }

    private void pagosPorReserva(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int idReserva = Integer.parseInt(request.getParameter("id_reserva"));
        List<Pago> pagos = pDao.listaPorReserva(idReserva);
        response.getWriter().print(gson.toJson(pagos));
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