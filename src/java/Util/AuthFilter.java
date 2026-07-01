package Util;

import Model.Rol;
import Model.Usuario;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(urlPatterns = {
    "/admin/*",
    "/cliente/mis-reservas.html",
    "/cliente/mi-cuenta.html",
    "/cliente/reservar.html",
    "/mi-cuenta.html"
})
public class AuthFilter extends HttpFilter implements Filter {

    @Override
    protected void doFilter(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect(request.getContextPath() + "/login.html");
            return;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");
        String uri = request.getRequestURI();

        if (uri.contains("/admin/")) {
            if (usuario.getRol() != Rol.ADMIN) {
                response.sendRedirect(request.getContextPath() + "/index.html");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
