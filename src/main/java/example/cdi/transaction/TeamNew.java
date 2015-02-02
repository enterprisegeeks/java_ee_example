package example.cdi.transaction;

import example.cdi.bean.FlashMessage;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author kentaro.maeda
 */
@WebServlet(name = "TeamNew", urlPatterns = {"/TeamNew"})
public class TeamNew extends HttpServlet {

    @Inject
    private FlashMessage message;
    
    @Inject
    private SampleService service;
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String name = request.getParameter("teamName");

        String result = "";
        try {
            service.newTeam(name);
            result ="success.registerd=" + name;
        } catch(RuntimeException e) {
            result ="roalback by " + e.getClass().getName() + "\n"
                    +"stack trace:\n" +getStackTrace(e);
            System.out.println(e.getMessage());
        }catch (SampleException e) {
            result = "registerd=" + name + "\n"
                + "but, checkedException occuered:"+e.getClass().getName() + "\n" 
                +"stack trace:\n" +getStackTrace(e);
            System.out.println(e.getMessage());
        }
        message.setMessage(result);
        response.sendRedirect("TeamInitial");
        
        //request.getRequestDispatcher("TeamInitial").forward(request, response);
    }
    
    private String getStackTrace(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
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
