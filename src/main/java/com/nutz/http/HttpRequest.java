package com.nutz.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.nutz.lang.Strings;

/**
 * 
 * @author weirhp
 * 
 */
public class HttpRequest {
    private static HttpRequest _http  = new HttpRequest();
    protected String           cookie = "";
    protected String           refer;
    
    public static enum METHOD {
        GET, POST
    }
    
    private Map<String, String> requestHeaders;
    
    public void clearCookie() {
        cookie = "";
    }
    
    public static ResponseBody requestNoCookie(String url, String method, String contents) {
        return _http.request(url, method, contents);
    }
    
    // HTTP 消息发送
    public ResponseBody request(String url, String method, String contents) {
        ResponseBody body = new ResponseBody();
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            fillRequestHeader(conn, method);
            if (method.equalsIgnoreCase(METHOD.GET.name())) {
                conn.connect();
            } else if (method.equalsIgnoreCase(METHOD.POST.name())) {
                conn.setDoOutput(true);
                conn.connect();
                conn.getOutputStream().write(contents.getBytes());
            } else
                throw new RuntimeException("your method is not implement");
            
            if (conn.getHeaderFields().get("Set-Cookie") != null) {
                for (String s : conn.getHeaderFields().get("Set-Cookie")) {
                    cookie += s + ";";
                }
            }
            
            InputStream ins = conn.getInputStream();
            
            // 处理GZIP压缩的
            if (null != conn.getHeaderField("Content-Encoding")
                    && conn.getHeaderField("Content-Encoding").equals("gzip")) {
                ins = new GZIPInputStream(ins);
            }
            
            String charset = "UTF-8";
            InputStreamReader inr = new InputStreamReader(ins, charset);
            BufferedReader br = new BufferedReader(inr);
            
            String line = "";
            StringBuffer sb = new StringBuffer();
            do {
                sb.append(line);
                line = br.readLine();
            } while (line != null);
            body.result = sb.toString();
            body.connection = conn;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return body;
    }
    
    public InputStream getImage(String url) {
        InputStream ins = null;
        try {
            URL serverUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
            fillRequestHeader(conn, "GET");
            conn.connect();
            if (conn.getHeaderFields().get("Set-Cookie") != null) {
                for (String s : conn.getHeaderFields().get("Set-Cookie")) {
                    cookie += s + ";";
                }
            }
            ins = conn.getInputStream();
            // 处理GZIP压缩的
            if (null != conn.getHeaderField("Content-Encoding")
                    && conn.getHeaderField("Content-Encoding").equals("gzip")) {
                ins = new GZIPInputStream(ins);
            }
            
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ins;
    }
    
    protected void fillRequestHeader(HttpURLConnection conn, String method) {
        try {
            conn.setConnectTimeout(20000);
            conn.setRequestMethod(method);// "POST" ,"GET"
            if (null != refer)
                conn.addRequestProperty("Referer", refer);
            
            if (!Strings.isEmpty(cookie)) {
                conn.addRequestProperty("Cookie", cookie);
            }
            Map<String, String> map = getRequestHeaders();
            if (map == null) {
                map = getDefaultRequestHeaders();
            }
            for (Entry<String, String> entry : map.entrySet()) {
                conn.addRequestProperty(entry.getKey(), entry.getValue());
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        }
        
    }
    
    /**
     * 默认的请求头信息
     * 
     * @return
     */
    protected Map<String, String> getDefaultRequestHeaders() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("Connection", "Keep-Alive");
        map.put("Accept-Language", "zh-cn");
        map.put("Accept-Encoding", "gzip, deflate");
        map.put("Cache-Control", "no-cache");
        map.put("Accept-Charset", "UTF-8;");
        map.put("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
        return map;
    }
    
    public String getCookie() {
        return cookie;
    }
    
    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
    
    public String getRefer() {
        return refer;
    }
    
    public void setRefer(String refer) {
        this.refer = refer;
    }
    
    public Map<String, String> getRequestHeaders() {
        return requestHeaders;
    }
    
    public void setRequestHeaders(Map<String, String> requestHeaders) {
        this.requestHeaders = requestHeaders;
    }
}
