/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/02/22 16:55:08
 *
 * Nide8.com Auth v2 HMCL Proxy/Nide8.com Auth v2 HMCL Proxy/Nide8AuthV2HMCLProxy.java
 */

package cn.mcres.karlatemp.n8ahmclproxy;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Nide8AuthV2HMCLProxy implements HttpHandler {
    public static boolean debug;

    public static void main(String[] args) throws IOException {
        debug = Boolean.getBoolean("debug");
        HttpServer server = HttpServer.create();
        server.createContext("/", new Nide8AuthV2HMCLProxy());
        server.bind(new InetSocketAddress(Integer.getInteger("port", 4443)), 100);
        System.out.println("Proxy bind on port " + server.getAddress().getPort() + ", If you want to change port. Insert -Dport=<port> to VM Options.");
        server.start();
        System.out.println("Proxy Server started.");
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        try {
            final URI uri = httpExchange.getRequestURI();
            final Map<String, List<String>> headerOverride = new HashMap<>();
            headerOverride.put("User-agent", Collections.singletonList(Metadata.USER_AGENT));
            String path = uri.getPath();
            if (debug)
                System.out.println("\n" + httpExchange.getRequestMethod() + " " + path);
            else System.out.println(httpExchange.getRequestMethod() + " " + path);
            InputStream requestBodyOverride = null;
            final OutputStream response = httpExchange.getResponseBody();
            long requestLengthOverride = -1;
            if (path.equals("/")) {
                final Headers headers = httpExchange.getResponseHeaders();
                headers.set("Content-Type", "application/json; charset=utf-8");
                httpExchange.sendResponseHeaders(200, Metadata.MetaInf.length);
                response.write(Metadata.MetaInf);
                response.flush();
                response.close();
                httpExchange.close();
            } else if (path.endsWith("/authserver/authenticate")) {
                // Listing Auth
                if (!httpExchange.getRequestMethod().equals("POST")) {
                    httpExchange.sendResponseHeaders(500, 0);
                    httpExchange.getResponseBody().close();
                    return;
                } else {
                    JsonObject object;
                    try (Reader source = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8)) {
                        try {
                            object = JsonParser.parseReader(source).getAsJsonObject();
                        } catch (Throwable catching) {
                            httpExchange.sendResponseHeaders(500, 0);
                            httpExchange.getResponseBody().close();
                            return;
                        }
                    }
                    object.remove("agent");
                    JsonObject agent = new JsonObject();
                    agent.addProperty("name", "KAPU Minecraft Supreme Launcher");
                    agent.addProperty("version", 1.15);
//                    if (object.has("clientToken")) {
//                        object.addProperty("clientToken", UnsignedUUID.parse(object.get("clientToken").getAsString()).getUUID().toString());
//                    }
                    object.add("agent", agent);
                    // object.addProperty("username", "Mikuy_fish20");
                    byte[] array = object.toString().getBytes(StandardCharsets.UTF_8);
                    requestBodyOverride = new ByteArrayInputStream(array);
                    requestLengthOverride = array.length;
                    headerOverride.put("Content-Type", Collections.singletonList("application/json"));
                }
            }
            final Headers headers = httpExchange.getRequestHeaders();
            long postLength = 0;
            if (requestLengthOverride != -1) postLength = requestLengthOverride;
            else if (headers.containsKey("Content-Length")) {
                postLength = Long.parseLong(headers.getFirst("Content-Length"));
                if (postLength < 0) {
                    httpExchange.sendResponseHeaders(400, 0);
                    httpExchange.getResponseBody().close();
                    return;
                }
            }
            // Proxy
            URL proxy = new URL("https://auth2.nide8.com:233" + uri);
            HttpURLConnection connection = (HttpURLConnection) proxy.openConnection();
            connection.setRequestMethod(httpExchange.getRequestMethod());
            for (Map.Entry<String, List<String>> entry : headerOverride.entrySet()) {
                String key = entry.getKey();
                List<String> val = entry.getValue();
                if (!val.isEmpty()) {
                    Iterator<String> i = val.iterator();
                    connection.setRequestProperty(key, i.next());
                    while (i.hasNext()) {
                        connection.addRequestProperty(key, i.next());
                    }
                }
            }
            for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                String key = entry.getKey();
                List<String> val = entry.getValue();
                if (key != null && !val.isEmpty() && headerOverride.keySet().stream().noneMatch(k -> k.equalsIgnoreCase(key))) {
                    Iterator<String> i = val.iterator();
                    String next = i.next();
                    connection.setRequestProperty(key, next);
                    if (debug)
                        System.out.println(key + ":" + next);
                    while (i.hasNext()) {
                        connection.addRequestProperty(key, i.next());
                    }
                }
            }
            if (postLength > 0) connection.setDoOutput(true);
            connection.connect();
            byte[] buf = new byte[1024];
            if (postLength > 0) {
                final OutputStream stream = connection.getOutputStream();
                if (requestBodyOverride == null) requestBodyOverride = httpExchange.getRequestBody();
                do {
                    int size = (int) postLength;
                    if (size < 0) {
                        size = 1024;
                    }
                    if (size > 1024) {
                        size = 1024;
                    }
                    size = requestBodyOverride.read(buf, 0, size);
                    stream.write(buf, 0, size);
                    if (debug)
                        System.out.write(buf, 0, size);
                    postLength -= size;
                } while (postLength > 0);
            }
            long length = connection.getContentLengthLong();
            if (length == -1) length = 0L;
            int code = connection.getResponseCode();
            final Headers rp = httpExchange.getResponseHeaders();
            for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
                String key = entry.getKey();
                if (key == null) continue;
                List<String> val = entry.getValue();
                for (String v : val)
                    rp.add(key, v);
            }
            httpExchange.sendResponseHeaders(code, length);
            InputStream stream = code >= 400 ? connection.getErrorStream() : connection.getInputStream();
            if (length == 0L) {
                do {
                    int read = stream.read(buf);
                    if (read == -1) break;
                    response.write(buf, 0, read);
                } while (true);
            } else {
                do {
                    int size = (int) postLength;
                    if (size < 0) {
                        size = 1024;
                    }
                    if (size > 1024) {
                        size = 1024;
                    }
                    size = stream.read(buf, 0, size);
                    response.write(buf, 0, size);
                    postLength -= size;
                } while (true);
            }
            response.close();
            httpExchange.close();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            httpExchange.close();
            httpExchange.getResponseBody().close();
        }
    }

}
