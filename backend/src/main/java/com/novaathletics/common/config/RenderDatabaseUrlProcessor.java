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
    String url = env.getProperty("SPRING_DATASOURCE_URL");
    if (url == null) url = env.getProperty("spring.datasource.url");
    if (url != null && (url.startsWith("postgres://") || url.startsWith("postgresql://"))) {
      String jdbc = url.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
      // Render's postgres:// includes user:pass@host/db?sslmode=require — Hikari needs separation
      // If URL contains user info, keep as is — Hikari can parse jdbc:postgresql://user:pass@host/db
      // but better to leave user/pass in URL; driver will handle
      Map<String,Object> map = new HashMap<>();
      map.put("spring.datasource.url", jdbc);
      map.put("SPRING_DATASOURCE_URL", jdbc);
      env.getPropertySources().addFirst(new MapPropertySource("renderFix", map));
      System.out.println("[RenderFix] Converted DATABASE_URL to JDBC: " + jdbc.substring(0, Math.min(40, jdbc.length())) + "...");
    }
    // Also handle DATABASE_URL env (Render sometimes injects DATABASE_URL directly)
    String dbUrl = env.getProperty("DATABASE_URL");
    if (dbUrl != null && (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://"))) {
      String jdbc = dbUrl.replaceFirst("^postgres(ql)?://", "jdbc:postgresql://");
      if (env.getProperty("spring.datasource.url") == null || env.getProperty("spring.datasource.url").startsWith("postgres://")) {
        Map<String,Object> map = new HashMap<>();
        map.put("spring.datasource.url", jdbc);
        env.getPropertySources().addFirst(new MapPropertySource("renderFixDb", map));
        System.out.println("[RenderFix] Converted DATABASE_URL to spring.datasource.url");
      }
    }
  }
}
