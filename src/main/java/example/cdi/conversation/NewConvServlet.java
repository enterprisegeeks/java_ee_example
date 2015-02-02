package example.cdi.conversation;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.atomic.AtomicInteger;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CDIサンプル
 */
@WebServlet(urlPatterns = {"/newConversaion"})
public class NewConvServlet extends HttpServlet {

    // CDI Beanのインジェクション
    @Inject
    private ConvBean cbean;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getSession();
        cbean.begin();
        
        request.getRequestDispatcher("conv.jsp").forward(request, response);
    }
}
