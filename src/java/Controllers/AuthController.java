/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package Controllers;

import Dao.PersonaDaoImpl;
import Dao.UsuarioDaoImpl;
import Interface.IPersona;
import Interface.IUsuario;
import Model.Persona;
import Model.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "AuthController", urlPatterns = {"/AuthController"})
public class AuthController extends HttpServlet {

    private final IUsuario uDao = new UsuarioDaoImpl();
    private final IPersona pDao = new PersonaDaoImpl();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet AuthController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AuthController at " + request.getContextPath() + "</h1>");
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

        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {

            // ====================== LOGIN ======================
            if (action.equals("login")) {
                String user = request.getParameter("usuario");
                String pass = request.getParameter("password");
                

                Usuario us = uDao.validate(user, pass);

                if (us != null && us.getUsuario() != null) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", us);

                    jsonResponse.addProperty("sucess", true);
                    jsonResponse.addProperty("message", "Inicio de Sesion");
                    jsonResponse.add("userData", gson.toJsonTree(us));
                } else {
                    jsonResponse.addProperty("sucess", false);
                    jsonResponse.addProperty("message", "Usuario o contraseña invalida");
                }
                out.print(jsonResponse.toString());

            } // ====================== LOGOUT ======================
            else if (action.equals("salir")) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                jsonResponse.addProperty("sucess", true);
                jsonResponse.addProperty("message", "Sesion cerrada");
                out.print(jsonResponse.toString());
            } // ====================== REGISTRO ======================
            else if (action.equals("register")) {
                Persona p = new Persona();
                Usuario u = new Usuario();

                p.setNombre(request.getParameter("nombres"));
                p.setApellido(request.getParameter("apellidos"));
                p.setDocumento(request.getParameter("documento"));
                p.setNumeroDoc(request.getParameter("numero_doc"));
                p.setTelefono(request.getParameter("telefono"));
                p.setEmail(request.getParameter("email"));

                u.setContraseña(request.getParameter("password"));
                // u.setUsuario se asigna dentro del DAO con el email

                int resultado = pDao.insert(p, u);

                jsonResponse.addProperty("sucess", resultado > 0);
                jsonResponse.addProperty("message", resultado > 0 ? "Registro exitoso" : "Error en el registro");
                out.print(jsonResponse.toString());
            } // ====================== ACCIÓN NO VÁLIDA ======================
            else {
                jsonResponse.addProperty("sucess", false);
                jsonResponse.addProperty("message", "Acción no válida");
                out.print(jsonResponse.toString());
            }

        } catch (Exception e) {
            response.setStatus(500);
            jsonResponse.addProperty("sucess", false);
            jsonResponse.addProperty("message", "Error: " + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
            e.printStackTrace();
        }
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
