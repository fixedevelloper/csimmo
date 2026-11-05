# Image de l'API ImmoGest. Contexte de build : la racine du dépôt.
#   docker build -f deploy/Dockerfile.app -t immogest/app:dev .
# OBFUSCATION=false pour construire un jar lisible (diagnostic uniquement).
FROM eclipse-temurin:21-jdk AS build
ARG OBFUSCATION=true
WORKDIR /src
COPY backend/ backend/
COPY deploy/proguard/ deploy/proguard/
WORKDIR /src/backend
RUN --mount=type=cache,target=/root/.m2 \
    if [ "$OBFUSCATION" = "true" ]; then PROFIL="-Pobfuscation"; else PROFIL=""; fi; \
    ./mvnw -B -q -pl app -am package -DskipTests $PROFIL \
 && mkdir /out \
 && cp "$(ls app/target/app-*.jar | grep -v -E 'original|proguard' | head -n1)" /out/app.jar

FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y --no-install-recommends curl && rm -rf /var/lib/apt/lists/* \
 && useradd --system --uid 10001 --no-create-home immogest
COPY --from=build /out/app.jar /app/app.jar
USER immogest
ENV SPRING_PROFILES_ACTIVE=prod \
    JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=60 -Duser.timezone=UTC"
EXPOSE 8080
HEALTHCHECK --interval=15s --timeout=5s --start-period=90s --retries=8 \
  CMD curl -fsS http://localhost:8080/actuator/health || exit 1
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
