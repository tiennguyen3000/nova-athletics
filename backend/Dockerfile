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
ENTRYPOINT ["sh","-c","if echo \"$SPRING_DATASOURCE_URL\" | grep -q '^postgres'; then export SPRING_DATASOURCE_URL=$(echo \"$SPRING_DATASOURCE_URL\" | sed -E 's|^postgres(ql)?://[^@]*@|jdbc:postgresql://|' | sed -E 's|^postgres(ql)?://|jdbc:postgresql://|'); echo \"[DockerEntry] postgres->jdbc\"; fi; if echo \"$SPRING_DATASOURCE_URL\" | grep -q 'jdbc:postgresql://[^@]*@'; then export SPRING_DATASOURCE_URL=$(echo \"$SPRING_DATASOURCE_URL\" | sed -E 's|^jdbc:postgresql://[^@]*@|jdbc:postgresql://|'); echo \"[DockerEntry] stripped userinfo\"; fi; if echo \"$SPRING_DATASOURCE_URL\" | grep -q 'dpg-.*-a[:/]' && ! echo \"$SPRING_DATASOURCE_URL\" | grep -q 'render.com'; then export SPRING_DATASOURCE_URL=$(echo \"$SPRING_DATASOURCE_URL\" | sed -E 's|://(dpg-[a-z0-9-]+)([:/])|://\\1.singapore-postgres.render.com\\2|'); echo \"[DockerEntry] fixed host\"; fi; case \"$SPRING_DATASOURCE_URL\" in *sslmode=*) ;; *) if [ -n \"$SPRING_DATASOURCE_URL\" ]; then export SPRING_DATASOURCE_URL=\"${SPRING_DATASOURCE_URL}?sslmode=require\"; export SPRING_DATASOURCE_URL=$(echo \"$SPRING_DATASOURCE_URL\" | sed 's|?sslmode=require?|?|'); export SPRING_DATASOURCE_URL=$(echo \"$SPRING_DATASOURCE_URL\" | sed 's|??|?|'); echo \"[DockerEntry] added sslmode\"; fi;; esac; exec java -jar app.jar"]
