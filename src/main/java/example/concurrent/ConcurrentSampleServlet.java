/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.concurrent;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@WebServlet(name = "concurrent", urlPatterns = {"/concurrent"}, asyncSupported = true)
public class ConcurrentSampleServlet extends HttpServlet {

    @Resource(lookup = "concurrent/__defaultManagedExecutorService")
    private ExecutorService executor;

    @Inject
    Logger log;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        // 入力値の取得
        Enumeration<String> params = request.getParameterNames();
        Map<Integer, String> input = new TreeMap<>();
        while (params.hasMoreElements()) {
            String param = params.nextElement();
            System.out.println(param);
            if (param.matches("^num[0-9]+$")) {
                input.put(Integer.parseInt(param.substring(3)),
                        request.getParameter(param));
            }
        }

        CountDownLatch latch = new CountDownLatch(input.size());
        PrintWriter w = response.getWriter();

        log.info(() -> "ConcurrentServlet start:" + input);
        AsyncContext ac = request.startAsync();
        // 並列処理開始
        for (Map.Entry<Integer, String> entry : input.entrySet()) {
            executor.execute(() -> {
                long start = System.currentTimeMillis();
                int index = entry.getKey();
                String strNum = entry.getValue();

                sendDataAndFlush(w, index, "処理開始 ");
              
                if (!strNum.matches("^[0-9]+$")) {
                   sendDataAndFlush(w, index, "形式不正");
                } else {
                    long num = Long.parseLong(strNum);
                    if (num > 45) {
                        sendDataAndFlush(w, index, "45以下で入力");
                    } else {
                        long answer = fib(num);
                        long time = System.currentTimeMillis() - start;
                        sendDataAndFlush(w, index, "(" + time + "msで計算) fib(" + num + ")=" + answer);
                    }
                }
                latch.countDown();
                log.info(() -> index + " end.");
            });
        }
        // 終了待ち合わせ
        executor.execute(() -> {
            try {
                latch.await();
            } catch (InterruptedException ex) {
            }
            w.write("event: close\ndata: 計算終了\n\n");
            w.close();
            ac.complete();
            log.info(()-> "ConcurrentServlet concurrent process end.");
        });
        log.info(() -> "ConcurrentServlet end:");
    }
    /** 効率の悪い実装 */
    private long fib(long n) {
        if (n <= 2){
            return 1;
        }
        return fib(n-1) + fib(n-2);
    }

    private void sendDataAndFlush(PrintWriter w, int index, String message) {
        String data = String.format("data: {\"index\":%d, \"message\":\"%s\"}\n\n", index, message);
        w.write(data);
        w.flush();
    }
}
