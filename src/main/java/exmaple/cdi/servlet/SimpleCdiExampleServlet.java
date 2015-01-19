package exmaple.cdi.servlet;

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
@WebServlet(name = "simpleExample", urlPatterns = {"/simpleExample"})
public class SimpleCdiExampleServlet extends HttpServlet {

    // CDI Beanのインジェクション
    @Inject
    private RequestBean rbean;
    @Inject
    private SessionBean sbean;
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // カウントアップの実行
        rbean.countUp(); // 毎回初期化される
        sbean.countUp(); // 呼び出しのたびにカウントアップ
        
        try (PrintWriter out = response.getWriter()) {
            
            out.printf("RequestScope:Count=%s, Hash=%s\n", rbean.getCount(), rbean.getHashCode());
            out.printf("SessionScope:Count=%s, Hash=%s\n", sbean.getCount(), sbean.getHashCode());
            
        }
    }
}
