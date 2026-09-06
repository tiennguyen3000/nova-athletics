package com.novaathletics.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseUrlProcessor implements EnvironmentPostProcessor {
  private static String toJdbc(String postgresUrl) {
    // postgres://user:pass@host:port/db?params -> jdbc:postgresql://host:port/db?params
    // Strip userinfo (user:pass@) because Hikari uses separate SPRING_DATASOURCE_USERNAME/PASSWORD
    // and PG driver rejects userinfo with special chars
    String jdbc = postgresUrl.replaceFirst("^postgres(ql)?://[^@]*@", "jdbc:postgresql://");
    // If no @ found (no userinfo), fallback to simple prefix replace
    if (jdbc.equals(postgresUrl)) {
      jdbc = postgresUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
    }
    return jdbc;
  }
  @Override
  public void postProcessEnvironment(ConfigurableEnvironment env, SpringApplication app) {
    String[] keys = {
        "SPRING_DATASOURCE_URL", "spring.datasource.url",
        "DATABASE_URL", "DATABASE_CONNECTION_STRING",
        "POSTGRES_URL", "POSTGRESQL_URL"
    };
    boolean converted = false;
    for (String key : keys) {
      String url = env.getProperty(key);
      if (url == null) url = System.getenv(key);
      if (url == null && key.contains(".")) {
        String envKey = key.toUpperCase().replace('.', '_');
        url = System.getenv(envKey);
        if (url == null) url = env.getProperty(envKey);
      }
      if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
        String jdbc = toJdbc(url);
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", jdbc);
        map.put("SPRING_DATASOURCE_URL", jdbc);
        map.put("DATABASE_URL", jdbc);
        System.setProperty("spring.datasource.url", jdbc);
        env.getPropertySources().addFirst(new MapPropertySource("renderFix-" + key, map));
        System.out.println("[RenderFix] Converted " + key + " postgres:// -> jdbc:postgresql:// stripped userinfo, result=" + jdbc.substring(0, Math.min(90, jdbc.length())) + "...");
        converted = true;
      } else if (url != null && url.startsWith("jdbc:postgresql://") && url.contains("@")) {
        // Already jdbc but still contains userinfo -> strip it too
        String jdbc = url.replaceFirst("^jdbc:postgresql://[^@]*@", "jdbc:postgresql://");
        if (!jdbc.equals(url)) {
          Map<String, Object> map = new HashMap<>();
          map.put("spring.datasource.url", jdbc);
          map.put("SPRING_DATASOURCE_URL", jdbc);
          System.setProperty("spring.datasource.url", jdbc);
          env.getPropertySources().addFirst(new MapPropertySource("renderFix-strip-" + key, map));
          System.out.println("[RenderFix] Stripped userinfo from existing jdbc URL " + key + " -> " + jdbc.substring(0, Math.min(90, jdbc.length())) + "...");
          converted = true;
        } else {
          System.out.println("[RenderFix] Found existing jdbc URL for " + key);
          converted = true;
        }
      } else if (url != null && url.startsWith("jdbc:postgresql://")) {
        System.out.println("[RenderFix] Found existing jdbc URL for " + key);
        converted = true;
      }
    }
    if (!converted) {
      for (Map.Entry<String, String> e : System.getenv().entrySet()) {
        String v = e.getValue();
        if (v != null && (v.startsWith("postgres://") || v.startsWith("postgresql://")) && e.getKey().toUpperCase().contains("DATA") ) {
          String jdbc = toJdbc(v);
          Map<String, Object> map = new HashMap<>();
          map.put("spring.datasource.url", jdbc);
          System.setProperty("spring.datasource.url", jdbc);
          env.getPropertySources().addFirst(new MapPropertySource("renderFix-env-" + e.getKey(), map));
          System.out.println("[RenderFix] Converted System.getenv " + e.getKey() + " to JDBC stripped");
          converted = true;
        }
      }
    }
    if (!converted) {
      System.out.println("[RenderFix] No postgres:// URL found. SPRING_DATASOURCE_URL=" + env.getProperty("SPRING_DATASOURCE_URL") + " DATABASE_URL=" + env.getProperty("DATABASE_URL"));
    }
  }
}
