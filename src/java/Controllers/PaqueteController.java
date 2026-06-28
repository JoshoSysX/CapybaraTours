/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import Dao.PaqueteDaoImpl;
import Interface.IPaquete;
import Model.Paquete;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 *
 * @author JoshoSysX
 */
@WebServlet(name = "PaqueteController", urlPatterns = {"/PaqueteController"})
public class PaqueteController extends HttpServlet {

private final IPaquete dao = new PaqueteDaoImpl();
    private final Gson gson = new Gson();
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet PaqueteController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet PaqueteController at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();

        try (PrintWriter out = response.getWriter()) {

            if ("listar".equals(action)) {
                var lista = dao.lista();
                jsonResponse.addProperty("sucess", true);
                jsonResponse.add("data", gson.toJsonTree(lista));

            } else if ("insert".equals(action)) {
                Paquete p = new Paquete();
                p.setNombre(request.getParameter("nombre"));
                p.setDescripcion(request.getParameter("descripcion"));
                p.setDuracion(request.getParameter("duracion"));
                p.setPrecio(Double.parseDouble(request.getParameter("precio")));

                boolean resultado = dao.insert(p);
                jsonResponse.addProperty("sucess", resultado);
                jsonResponse.addProperty("message", resultado ? "Paquete registrado" : "Error al registrar");

            } else if ("update".equals(action)) {
                Paquete p = new Paquete();
                p.setId_paquete(Integer.parseInt(request.getParameter("id_paquete")));
                p.setNombre(request.getParameter("nombre"));
                p.setDescripcion(request.getParameter("descripcion"));
                p.setDuracion(request.getParameter("duracion"));
                p.setPrecio(Double.parseDouble(request.getParameter("precio")));

                boolean resultado = dao.update(p);
                jsonResponse.addProperty("sucess", resultado);
                jsonResponse.addProperty("message", resultado ? "Paquete actualizado" : "Error al actualizar");

            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                boolean resultado = dao.delete(id);
                jsonResponse.addProperty("sucess", resultado);
                jsonResponse.addProperty("message", resultado ? "Paquete eliminado" : "Error al eliminar");

            } else {
                jsonResponse.addProperty("sucess", false);
                jsonResponse.addProperty("message", "Acción no válida");
            }

            out.print(jsonResponse.toString());

        } catch (Exception e) {
            response.setStatus(500);
            jsonResponse.addProperty("sucess", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
            e.printStackTrace();
        };
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
