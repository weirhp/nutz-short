package com.nutz;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.nutz.json.Json;

import com.nutz.http.HttpRequest;
import com.nutz.http.HttpRequest.METHOD;
import com.nutz.http.ResponseBody;

/**
 * 发送http请求 获取短点链接
 * 
 * @author weirhp@gmail.com
 * 
 */
public class NutzShortServlet extends HttpServlet {
    private static final long   serialVersionUID = 1L;
    private static final String nutzShortUrl     = "http://nutz.cn/api/create/url";
    
    public NutzShortServlet() {
        super();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {
        String url = request.getParameter("url");
        
        HttpRequest http = new HttpRequest();
        ResponseBody body = http.request(nutzShortUrl, METHOD.POST.name(), "data=" + URLEncoder.encode(url, "utf-8"));
        @SuppressWarnings("unchecked")
        Map<String, Object> map = Json.fromJson(Map.class, body.result);
        if ((Boolean) map.get("ok")) {
            Object code = map.get("code");
//            PrintWriter out = response.getWriter();
//            out.write(String.format(iframeStr, code, "http://nutz.cn/" + code,
//                    URLEncoder.encode("http://nutz.cn/" + code, "utf-8")));
//            out.flush();
            request.setAttribute("url", "http://nutz.cn/" + code);
            request.setAttribute("imageUrl", URLEncoder.encode("http://nutz.cn/" + code, "utf-8"));
        }
        
        RequestDispatcher dispatcher = request.getRequestDispatcher("iframe.jsp");
        dispatcher .forward(request, response); 
    }
    
    private static final String iframeStr = "(function(){"
                                                  + "var i = document.getElementById('nutzIframe');"
                                                  + "if (!i) {"
                                                  + "i = document.createElement('iframe');"
                                                  + "i.setAttribute('name', 'nutzIframe');"
                                                  + "i.setAttribute('id', 'nutzIframe');"
                                                  + "i.setAttribute('style', 'z-index: 99999999; position: fixed;left:10px;top:10px;width:168px;'"
                                                  + "+ 'width:168px;height: 140px; border: 3px solid #FFA54F;');"
                                                  + "document.body.appendChild(i);"
                                                  + "}else{i.contentWindow.document.getElementsByTagName('body')[0].innerHTML = '';}"
                                                  + "i.contentWindow.document"
                                                  + ".write('<html><body style=\"color: #A00000; background-color: #FFFFC0; text-align: center; margin: 0px; font-family: Georgia, Times, serif; font-size: 26px;\">'"
                                                  + "+ '<div style=\"text-align: left; padding: 2px; margin: 0 auto 15px auto; font-size: 13px; border-bottom: 1px solid #ccc; color: #333;\">"
                                                  + "<a style=\"color:red;float:right;margin-right:5px;\" href=\"javascript:var i = top.document.getElementById(\\'nutzIframe\\');i.parentNode.removeChild(i);\">[X]</a>"
                                                  + "<a target=\"_blank\" href=\"http://nutz.cn\">nutz.cn</a></div>'"
                                                  + "+ '<p style=\"font-size:13px\">http://nutz.cn/%s<a href=\"javascript:copy(\\'%s\\')\">Copy</a></p>"
                                                  + "<div>"
                                                  + "<img src=\"https://chart.googleapis.com/chart?chs=72x72&amp;cht=qr&amp;choe=UTF-8&amp;chl=%s\"/>"
                                                  + "</div>'" + "+ '</body></html>');" + "})();";
}
