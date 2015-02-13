/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package example.concurrent;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 */
@WebServlet(name = "notConcurrentSSE", urlPatterns = {"/notConcurrentSSE"})
public class NotConcurrentSSEServlet extends ConcurrentSampleServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/event-stream");
        response.setHeader("Cache-Control", "no-cache");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter w = response.getWriter();
        long start = System.currentTimeMillis();
        
        // 入力値の取得
        Map<Integer, String> input = getNums(request);

        // 処理開始
        for (Map.Entry<Integer, String> entry : input.entrySet()) {
            int index = entry.getKey();
            sendDataAndFlush(w, makeJson(index, "処理開始"));
            String result = calc(index, entry.getValue());
            sendDataAndFlush(w, result);
        }
        // 終了
        long time = System.currentTimeMillis() - start;
        w.write("event: close\ndata: 計算終了(" +time +"ms)\n\n");
        w.checkError();
        w.close();

    }


    /** SSEイベントの即時反映のため、リクエスト送信後フラッシュする。
     */
    private void sendDataAndFlush(PrintWriter w, String message) {
        w.write("data: " + message + "\n\n");
        w.checkError();
    }
}
