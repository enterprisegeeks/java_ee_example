package example.concurrent;

import java.io.IOException;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 非同期サーブレットサンプル
 */
@WebServlet(name = "asyncSample", urlPatterns = {"/asyncSample"}, asyncSupported = true)
public class AsyncSampleServlet extends HttpServlet {
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
            response.setContentType("text/plain");
            response.getWriter().println("output by " + Thread.currentThread().getName());
            response.getWriter().flush();
                    
            AsyncContext ac = request.startAsync();
            ac.start(() ->{
                try {
                    response.getWriter().println(
                            "process in async thread");
                    Thread.sleep(3000);
                    response.getWriter().println(
                            "output by " + Thread.currentThread().getName());
                    response.getWriter().close();
                    ac.complete(); // 実行しないと、リクエストが終了しない。
                }catch(Exception e){
                    e.printStackTrace();
                }
                
            });
    }


}
