FROM oracle/graalvm-ce:19.3.0-java8 as graalvm
#FROM oracle/graalvm-ce:19.3.0-java11 as graalvm # For JDK 11
COPY . /home/app/jbert
WORKDIR /home/app/jbert
RUN gu install native-image
RUN native-image --no-server --static -cp build/libs/jbert-*-all.jar

FROM frolvlad/alpine-glibc
EXPOSE 8080
COPY --from=graalvm /home/app/jbert/jbert /app/jbert
ENTRYPOINT ["/app/jbert", "-Djava.library.path=/app"]
