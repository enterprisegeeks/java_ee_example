/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.concurrent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 並列処理の結果を、SSE(ServerSentEvent)で返信する。
 */
@WebServlet(name = "concurrentSSE", urlPatterns = {"/concurrentSSE"}, asyncSupported = true)
public class ConcurrentSSEServlet extends NotConcurrentSampleServlet {

    @Resource(lookup = "concurrent/__defaultManagedExecutorService")
    private ManagedExecutorService executor;

    @Inject
    Logger log;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        long start = System.currentTimeMillis();
        
        // 入力値の取得
        Map<Integer, String> input = getNums(request);
        
        CountDownLatch latch = new CountDownLatch(input.size());
        PrintWriter w = response.getWriter();

        log.info(() -> "ConcurrentSSEServlet start:" + input);
        AsyncContext ac = request.startAsync();
        // 並列処理開始
        for (Map.Entry<Integer, String> entry : input.entrySet()) {
            executor.execute(() -> {
                int index = entry.getKey();
                sendDataAndFlush(w, makeJson(index, "処理開始"));
                String result = calc(index, entry.getValue());
                sendDataAndFlush(w, result);
                
                latch.countDown();
            });
        }
        // 終了待ち合わせ
        executor.execute(() -> {
            try {
                latch.await();
            } catch (InterruptedException ex) {
            }
            long time = System.currentTimeMillis() - start;
            w.write("event: close\ndata: 計算終了(" +time +"ms)\n\n");
            w.close();
            ac.complete();
            log.info(()-> "ConcurrentSSEServlet concurrent process end.");
        });
        log.info(() -> "ConcurrentSSEServlet end:");
    }
    
    /** SSEイベントの即時反映のため、リクエスト送信後フラッシュする。 */
    private void sendDataAndFlush(PrintWriter w, String message) {
        w.write("data: " + message + "\n\n");
        w.flush();
    }
}
