package com.novaathletics.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseUrlProcessor implements EnvironmentPostProcessor {
  @Override
  public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
    // Check all possible env var names Render might inject
    String[] keys = {
        "SPRING_DATASOURCE_URL", "spring.datasource.url",
        "DATABASE_URL", "DATABASE_CONNECTION_STRING",
        "POSTGRES_URL", "POSTGRESQL_URL"
    };
    boolean converted = false;
    for (String key : keys) {
      String url = env.getProperty(key);
      if (url == null) url = System.getenv(key);
      // also check upper case variant via getenv mapping
      if (url == null && key.contains(".")) {
        String envKey = key.toUpperCase().replace('.', '_');
        url = System.getenv(envKey);
        if (url == null) url = env.getProperty(envKey);
      }
      if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
        String jdbc = url.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
        // Render sometimes appends ?sslmode=require which is fine for jdbc
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", jdbc);
        map.put("SPRING_DATASOURCE_URL", jdbc);
        map.put("DATABASE_URL", jdbc);
        // also set as system property for early binding
        System.setProperty("spring.datasource.url", jdbc);
        env.getPropertySources().addFirst(new MapPropertySource("renderFix-" + key, map));
        System.out.println("[RenderFix] Converted " + key + " from postgres:// to jdbc:postgresql:// (len=" + jdbc.length() + ")");
        converted = true;
        // continue to convert other keys too
      } else if (url != null && url.startsWith("jdbc:postgresql://")) {
        // already correct, ensure spring.datasource.url is set
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", url);
        System.setProperty("spring.datasource.url", url);
        env.getPropertySources().addFirst(new MapPropertySource("renderFix-keep-" + key, map));
        System.out.println("[RenderFix] Found existing jdbc URL for " + key);
        converted = true;
      }
    }
    // fallback: directly check System.getenv without Spring env
    if (!converted) {
      for (Map.Entry<String, String> e : System.getenv().entrySet()) {
        String v = e.getValue();
        if (v != null && (v.startsWith("postgres://") || v.startsWith("postgresql://")) && e.getKey().toUpperCase().contains("DATA") ) {
          String jdbc = v.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
          Map<String, Object> map = new HashMap<>();
          map.put("spring.datasource.url", jdbc);
          System.setProperty("spring.datasource.url", jdbc);
          env.getPropertySources().addFirst(new MapPropertySource("renderFix-env-" + e.getKey(), map));
          System.out.println("[RenderFix] Converted System.getenv " + e.getKey() + " to JDBC");
          converted = true;
        }
      }
    }
    if (!converted) {
      System.out.println("[RenderFix] No postgres:// URL found to convert. Available SPRING_DATASOURCE_URL=" + env.getProperty("SPRING_DATASOURCE_URL") + " DATABASE_URL=" + env.getProperty("DATABASE_URL") + " spring.datasource.url=" + env.getProperty("spring.datasource.url"));
      // Dump all env keys containing DATA or POSTGRES for debug (masked)
      for (String k : new String[]{"SPRING_DATASOURCE_URL","DATABASE_URL","POSTGRES_URL","spring.datasource.url"}) {
        String v = env.getProperty(k);
        if (v != null) System.out.println("[RenderFix-debug] " + k + " -> " + v.substring(0, Math.min(60, v.length())) + "...");
        else {
          String gv = System.getenv(k);
          if (gv != null) System.out.println("[RenderFix-debug] System.getenv " + k + " -> " + gv.substring(0, Math.min(60, gv.length())) + "...");
        }
      }
    }
  }
}
