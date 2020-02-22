/*
 * Copyright (c) 2018-2020 Karlatemp. All rights reserved.
 * @author Karlatemp <karlatemp@vip.qq.com> <https://github.com/Karlatemp>
 * @create 2020/02/22 17:10:17
 *
 * Nide8.com Auth v2 HMCL Proxy/Nide8.com Auth v2 HMCL Proxy/Metadata.java
 */

package cn.mcres.karlatemp.n8ahmclproxy;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import java.nio.charset.StandardCharsets;

public class Metadata {
    public static final byte[] MetaInf;
    public static final String VERSION;
    public static final String JAVA_VERSION = System.getProperty("java.version");
    public static final String USER_AGENT = System.getProperty("agent.override", "Java/" + JAVA_VERSION);

    static {
        String ver = System.getProperty("version.override");
        if (ver == null) ver = Metadata.class.getPackage().getImplementationVersion();
        if (ver == null) ver = "Direct Run - v{DEBUG}";
        // System.out.println("Ver: " + ver);
        // System.out.println("UserAgent: " + USER_AGENT);
        VERSION = ver;
        JsonObject object = new JsonObject();
        object.add("Application-Author", new JsonPrimitive("Karlatemp"));
        object.add("Application-Description", new JsonPrimitive("A Proxy Server to provide HTML nide8.com auth v2."));
        object.add("Application-Owner", new JsonPrimitive("Karlatemp"));
        object.add("Implementation-Version", new JsonPrimitive(VERSION));
        MetaInf = object.toString().getBytes(StandardCharsets.UTF_8);
    }
}
