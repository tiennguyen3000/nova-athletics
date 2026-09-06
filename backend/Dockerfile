FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B 2>&1 | tail -5 || true
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 mvn package -DskipTests -B

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
# Fallback: convert postgres:// to jdbc:postgresql:// if EnvironmentPostProcessor missed it (Render injects postgres://)
ENTRYPOINT ["sh","-c","if [ -n \"$SPRING_DATASOURCE_URL\" ]; then case \"$SPRING_DATASOURCE_URL\" in postgres://*|postgresql://*) export SPRING_DATASOURCE_URL=$(echo \"$SPRING_DATASOURCE_URL\" | sed -E 's|^postgres(ql)?://|jdbc:postgresql://|'); echo \"[DockerEntry] Converted SPRING_DATASOURCE_URL to $SPRING_DATASOURCE_URL\" | cut -c1-80;; esac; fi; if [ -n \"$DATABASE_URL\" ] && [ -z \"$SPRING_DATASOURCE_URL\" ]; then case \"$DATABASE_URL\" in postgres://*|postgresql://*) export SPRING_DATASOURCE_URL=$(echo \"$DATABASE_URL\" | sed -E 's|^postgres(ql)?://|jdbc:postgresql://|'); echo \"[DockerEntry] Converted DATABASE_URL\";; esac; fi; exec java -jar app.jar"]
