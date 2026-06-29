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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        JsonObject jsonResponse = new JsonObject();
        Gson gson = new Gson();

        try (PrintWriter out = response.getWriter()) {

            if (action.equals("validar")) {
                String user = request.getParameter("usuario");
                String pass = request.getParameter("password");

                Usuario us = uDao.validate(user, pass);

                if (us != null && us.getUsuario() != null) {
                    HttpSession session = request.getSession(true);
                    session.setAttribute("usuario", us);
                    jsonResponse.addProperty("success", true);
                    jsonResponse.addProperty("message", "Inicio de Sesion");
                    jsonResponse.add("userData", gson.toJsonTree(us));
                } else {
                    jsonResponse.addProperty("success", false);
                    jsonResponse.addProperty("message", "Usuario o contraseña invalida");
                }
                out.print(jsonResponse.toString());

            } else if (action.equals("Salir")) {
                HttpSession session = request.getSession(false);
                if (session != null) {
                    session.invalidate();
                }
                jsonResponse.addProperty("success", true);
                jsonResponse.addProperty("message", "Sesion cerrada");
                out.print(jsonResponse.toString());

            } else if (action.equals("register")) {
                Persona p = new Persona();
                Usuario u = new Usuario();

                p.setNombre(request.getParameter("nombres"));
                p.setApellido(request.getParameter("apellidos"));
                p.setDocumento(request.getParameter("documento"));
                p.setNumeroDoc(request.getParameter("numero_doc"));
                p.setTelefono(request.getParameter("telefono"));
                p.setEmail(request.getParameter("email"));

                u.setContraseña(request.getParameter("password"));

                int resultado = pDao.insert(p, u);

                jsonResponse.addProperty("success", resultado != 0);
                jsonResponse.addProperty("message", resultado != 0 ? "Registro exitoso" : "Error en el registro");
                out.print(jsonResponse.toString());
            }

        } catch (Exception e) {
            response.setStatus(500);
            jsonResponse.addProperty("success", false);
            jsonResponse.addProperty("message", "Error" + e.getMessage());
            response.getWriter().print(jsonResponse.toString());
        }
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

}