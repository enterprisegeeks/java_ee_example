package exmaple.cdi.servlet2;

import java.io.IOException;
import java.io.PrintWriter;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * CDIサンプル
 */
@WebServlet(name = "scopeExample", urlPatterns = {"/scopeExample"})
public class ScopeExampleServlet extends HttpServlet {

    // CDI Beanのインジェクション
    @Inject
    private RequestBean rbean;
    @Inject
    private SessionBean sbean;
    @Inject
    private DependentBean depBean;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try (PrintWriter out = response.getWriter()) {
            out.println(rbean.format());
            out.println(sbean.format());
            out.println("DependentBean on Servlet:" + depBean.getHashCode());
        }
    }
}
