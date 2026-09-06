package com.novaathletics.common.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

public class RenderDatabaseUrlProcessor implements EnvironmentPostProcessor {
  private static String toJdbc(String postgresUrl) {
    String jdbc = postgresUrl.replaceFirst("^postgres(ql)?://[^@]*@", "jdbc:postgresql://");
    if (jdbc.equals(postgresUrl)) {
      jdbc = postgresUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
    }
    // Fix Render internal host: dpg-xxx-a -> dpg-xxx-a.singapore-postgres.render.com (or oregon)
    // If host is short (no dot) like dpg-daeecrn40ujc73ete7gg-a, expand to external
    // Pattern: jdbc:postgresql://dpg-....-a[/or:port/db]
    if (jdbc.matches(".*://dpg-[a-z0-9]+-a([:/].*|/.*|$)") && !jdbc.contains(".render.com")) {
      // extract host part
      jdbc = jdbc.replaceFirst("(://dpg-[a-z0-9]+-a)([:/])", "$1.singapore-postgres.render.com$2");
      // also handle without port/slash: jdbc:postgresql://dpg-xxx-a
      if (jdbc.matches(".*://dpg-[a-z0-9]+-a$")) {
        jdbc = jdbc + ".singapore-postgres.render.com";
      }
      // fallback try oregon if singapore fails? we try singapore first
    }
    // Ensure sslmode=require for Render (PG requires SSL)
    if (!jdbc.contains("sslmode=")) {
      jdbc += (jdbc.contains("?") ? "&" : "?") + "sslmode=require";
    }
    return jdbc;
  }
  private static String stripUserinfo(String jdbcUrl) {
    if (jdbcUrl.contains("@") && jdbcUrl.startsWith("jdbc:postgresql://")) {
      String stripped = jdbcUrl.replaceFirst("^jdbc:postgresql://[^@]*@", "jdbc:postgresql://");
      // also expand short host if needed
      if (stripped.matches(".*://dpg-[a-z0-9]+-a([:/].*|/.*|$)") && !stripped.contains(".render.com")) {
        stripped = stripped.replaceFirst("(://dpg-[a-z0-9]+-a)([:/])", "$1.singapore-postgres.render.com$2");
      }
      if (!stripped.contains("sslmode=")) stripped += (stripped.contains("?") ? "&" : "?") + "sslmode=require";
      return stripped;
    }
    return jdbcUrl;
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
        System.out.println("[RenderFix] Converted " + key + " -> " + jdbc.substring(0, Math.min(110, jdbc.length())) + "...");
        converted = true;
      } else if (url != null && url.startsWith("jdbc:postgresql://") && url.contains("@")) {
        String jdbc = stripUserinfo(url);
        Map<String, Object> map = new HashMap<>();
        map.put("spring.datasource.url", jdbc);
        map.put("SPRING_DATASOURCE_URL", jdbc);
        System.setProperty("spring.datasource.url", jdbc);
        env.getPropertySources().addFirst(new MapPropertySource("renderFix-strip-" + key, map));
        System.out.println("[RenderFix] Stripped userinfo " + key + " -> " + jdbc.substring(0, Math.min(110, jdbc.length())) + "...");
        converted = true;
      } else if (url != null && url.startsWith("jdbc:postgresql://")) {
        // also fix short host and sslmode even if no userinfo
        String fixed = url;
        if (fixed.matches(".*://dpg-[a-z0-9]+-a([:/].*|/.*|$)") && !fixed.contains(".render.com")) {
          fixed = fixed.replaceFirst("(://dpg-[a-z0-9]+-a)([:/])", "$1.singapore-postgres.render.com$2");
        }
        if (!fixed.contains("sslmode=")) fixed += (fixed.contains("?") ? "&" : "?") + "sslmode=require";
        if (!fixed.equals(url)) {
          Map<String, Object> map = new HashMap<>();
          map.put("spring.datasource.url", fixed);
          System.setProperty("spring.datasource.url", fixed);
          env.getPropertySources().addFirst(new MapPropertySource("renderFix-hostfix-" + key, map));
          System.out.println("[RenderFix] Fixed host/ssl for " + key + " -> " + fixed.substring(0, Math.min(110, fixed.length())) + "...");
        } else {
          System.out.println("[RenderFix] Found existing jdbc URL for " + key);
        }
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
          System.out.println("[RenderFix] Converted System.getenv " + e.getKey() + " -> " + jdbc.substring(0, Math.min(110, jdbc.length())) + "...");
          converted = true;
        }
      }
    }
    if (!converted) {
      System.out.println("[RenderFix] No postgres:// URL found. SPRING_DATASOURCE_URL=" + env.getProperty("SPRING_DATASOURCE_URL") + " DATABASE_URL=" + env.getProperty("DATABASE_URL"));
    }
  }
}
