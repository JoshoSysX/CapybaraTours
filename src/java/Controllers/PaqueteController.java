package Controllers;

import Dao.PaqueteDaoImpl;
import Interface.IPaquete;
import Model.Paquete;
import com.google.gson.Gson;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

/**
 *
 * @author JoshoSysX
 */
@MultipartConfig
@WebServlet(name = "PaqueteController", urlPatterns = {"/PaqueteController"})
public class PaqueteController extends HttpServlet {

    private final IPaquete pDao = new PaqueteDaoImpl();
    private final Gson gson = new Gson();
    private static final String UPLOAD_DIR = "assets/img/paquetes";

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
                guardarPaquete(request, response);
                break;
            case "editar":
                editarPaquete(request, response);
                break;
            case "eliminar":
                eliminarPaquete(request, response);
                break;
            case "buscar":
                buscarPaquete(request, response);
                break;
            default:
                listarPaquetes(request, response);
                break;
        }
    }

    private void listarPaquetes(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<Paquete> paquetes = pDao.lista();
        response.getWriter().print(gson.toJson(paquetes));
    }

    private void guardarPaquete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Paquete p = new Paquete();
            p.setNombre(request.getParameter("nombre"));
            p.setDescripcion(request.getParameter("descripcion"));
            p.setDuracion(request.getParameter("duracion"));
            p.setPrecio(Double.parseDouble(request.getParameter("precio")));

            Part part = request.getPart("imagen");
            if (part != null && part.getSize() > 0) {
                String fileName = part.getSubmittedFileName();
                // obtener la ruta donde guardar la imagen
                String pathBuild = getServletContext().getRealPath("/")
                        + "assets/img/paquetes" + File.separator;
                System.out.println("Ruta Build: " + pathBuild);
                String pathSource = pathBuild.replace("build" + File.separator + "web", "web");

                if (pathSource.equals(pathBuild)) {
                    System.out.println("colocar ruta fija");
                }
                System.out.println("Ruta Source: " + pathSource);
                try {
                    new File(pathSource).mkdirs();
                    new File(pathBuild).mkdirs();

                    File fileSource = new File(pathSource + fileName);
                    try (InputStream input = part.getInputStream()) {
                        java.nio.file.Files.copy(input, fileSource.toPath(),
                                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    }
                    System.out.println("Guardado en Source OK");

                    part.write(pathBuild + fileName);
                    System.out.println("Guardado en build OK");

                } catch (Exception e) {
                    System.err.println("Error critico" + e.getMessage());
                    e.printStackTrace();
                }
                p.setImagen("assets/img/paquetes/" + fileName);
            }

            boolean res = pDao.insert(p);
            response.getWriter().print(gson.toJson(res));

        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void editarPaquete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        try {
            Paquete p = new Paquete();
            p.setId_paquete(Integer.parseInt(request.getParameter("id_paquete")));
            p.setNombre(request.getParameter("nombre"));
            p.setDescripcion(request.getParameter("descripcion"));
            p.setDuracion(request.getParameter("duracion"));
            p.setPrecio(Double.parseDouble(request.getParameter("precio")));

            Part part = request.getPart("imagen");
            if (part != null && part.getSize() > 0) {
                String fileName = part.getSubmittedFileName();
                String uploadPath = getServletContext().getRealPath("")
                        + File.separator + UPLOAD_DIR;
                new File(uploadPath).mkdirs();
                part.write(uploadPath + File.separator + fileName);
                p.setImagen(UPLOAD_DIR + "/" + fileName);
            } else {
                p.setImagen(request.getParameter("imagen_actual"));
            }

            boolean res = pDao.update(p);
            response.getWriter().print(gson.toJson(res));

        } catch (Exception e) {
            response.getWriter().print(gson.toJson(false));
        }
    }

    private void buscarPaquete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Paquete p = pDao.SearchById(id);
        response.getWriter().print(gson.toJson(p));
    }

    private void eliminarPaquete(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        boolean res = pDao.delete(id);
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