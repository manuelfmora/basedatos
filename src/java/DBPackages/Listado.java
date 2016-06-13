/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/salud?zeroDateTimeBehavior=convertToNull", "root", "");
package DBPackages;

import java.io.IOException;
import java.io.PrintWriter;
import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Manuel Mora
 */

public class Listado extends HttpServlet {
    
    private Statement statement = null;
    private Connection conexion = null;

    /**
     * Inicia La base de datos
     */
    @Override
    public void init(ServletConfig config) {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/salud","root", "");
            statement = conexion.createStatement();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Listado.class.getName()).log(Level.SEVERE,
                    "No se pudo cargar el driver de la base de datos", ex);
        } catch (SQLException ex) {
            Logger.getLogger(Listado.class.getName()).log(Level.SEVERE,
                    "No se pudo obtener la conexión a la base de datos", ex);
        }
    }

    /**
     * Cierra La base de datos
     */
    @Override
    public void destroy() {
        try {
            statement.close();
        } catch (SQLException ex) {
            Logger.getLogger(Listado.class.getName()).log(Level.SEVERE,
                    "No se pudo cerrar el objeto Statement", ex);

            out.println("Error, no se pudo cerrar el objeto Statement");
        } finally {
            try {
                conexion.close();
            } catch (SQLException ex) {
                Logger.getLogger(Listado.class.getName()).log(Level.SEVERE,
                        "No se pudo cerrar el objeto Conexion", ex);
            }

            out.println("Error, no se pudo cerrar el objeto Conexion");
        }
    }

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //Variables
        String tabla = "";
        int numcentros = -1;
        int inicio = 0;

        if (request.getParameter("inicio") == null) {//La primera vez que entra es 0
            inicio = 0;
        } else {
            inicio = Integer.parseInt(request.getParameter("inicio"));
        }

        tabla = GetTabla(inicio);
        numcentros = GetNumCentros();

        int numPaginas = getNumPaginas(numcentros);

        //Pasamos los datos a Listado.jsp
        RequestDispatcher dispatcher = request.getRequestDispatcher("/Listado.jsp");

        request.setAttribute("tabla", tabla);
        request.setAttribute("numcentros", numcentros);
        request.setAttribute("inicio", inicio);
        request.setAttribute("numPaginas", numPaginas);

        dispatcher.forward(request, response);//Redirigimos a Listado
    }

    /**
     * Devuelve el número de páginas que hay que mostrar según el nº de
     * elementos, teniendo en cuenta que mostrará 20 elementos por página.
     *
     * @param numeroElementos
     * @return Nº páginas
     */
    public int getNumPaginas(double numeroElementos) {

        if (numeroElementos % 20 != 0) {
            return (int) numeroElementos / 20 + 1;
        } else {
            return (int) numeroElementos / 20;
        }

    }

    /**
     * Devuelva una tabla html con los datos de los centros
     *
     * @param inicio Desde donde tiene que mostrar
     * @return Tabla con los datos
     */
    protected String GetTabla(int inicio) {
        String tabla = "";

        //HACEMOS LA CONSULTA
        ResultSet listado = null;
        String sql="";
        try {
            synchronized (statement) {
                 sql="select s.nombre,s.Direccion,s.localidad,p.NOMBRE as Provincia " 
                                                    +"from salud_centros s,municipios m, provincias p "
                                                    +"where s.codmu = m.CODMU "
                                                    +"and m.CODPROV=p.IDPROV " 
                                                    +"order by s.nombre "
                                                    +"limit " + Integer.toString(inicio) + ", 20;";
                
                listado = statement.executeQuery(sql);
//                "SELECT u.id 'id', u.nombre 'nombre', u.apellido1 'apellido1', u.apellido2 'apellido2', p.nombre 'provincia', prov_cod 'id' "
//                        + "FROM usuarios.t_usuarios u  INNER JOIN t_provincias p "
//                        + "ON u.prov_cod = p.cod "
//                        + "ORDER BY u.nombre "
//                        + "LIMIT " + Integer.toString(inicio) + ", 20;");
            }
        } catch (SQLException ex) {
           out.println("Se produjo un error haciendo una consulta");
           return "<p>HAY ERROR: </p><pre>"+sql+"</pre><p>"+ex.getMessage()+"</p>";
        }

        //RECORREMOS EL RESULTADO Y CREAMOS LA TABLA
        tabla += "<table>";
        tabla += "\n\t<tr>\t<th>#</th>\t<th>NOMBRE</th>\t<th>DIRECCIÓN </th>\t<th>LOCALIDAD</th>\t<th>PROVINCIA</th></tr>";

        int cont = inicio + 1;//Columna #

        try {
            while (listado.next()) {
                tabla += "\n\t<tr>";
                tabla += "\n\t\t<td>" + cont + "</td>"
                        + "\n\t\t<td>" + listado.getString("nombre") + "</td>"
                        + "\n\t\t<td>" + listado.getString("direccion") + "</td>"
                        + "\n\t\t<td>" + listado.getString("localidad") + "</td>"
                        + "\n\t\t<td>" + listado.getString("provincia") + "</td>";
                tabla += "\n\t</tr>";

                cont++;
            }
        } catch (SQLException ex) {
            out.println("Se ha producido un error leyendo el listado");
        }
        tabla += "</table>";

        return tabla;
    }

    /**
     * Devuelve el número total centros
     *
     * @return Número de centros
     */
    protected int GetNumCentros() {
      out.println("Entra en GETnumcentro");
        //HACEMOS LA CONSULTA
        ResultSet listado = null;
        try {
            synchronized (statement) {
                listado = statement.executeQuery("SELECT count(IDCENTRO) 'num' "
                        + "FROM salud.salud_centros;");
            }
        } catch (SQLException ex) {
            out.println("Se produjo un error haciendo una consulta");
        }

        //RECORREMOS EL RESULTADO Y CREAMOS LA TABLA
        String num = "";
        try {
            while (listado.next()) {
                num = listado.getString("num");
            }
        } catch (SQLException ex) {
            out.println("Se ha producido un error leyendo el listado");
        }

        return Integer.parseInt(num);
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
        processRequest(request, response);
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
