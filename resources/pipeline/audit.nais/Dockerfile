FROM alpine:3.17
LABEL org.opencontainers.image.source=https://github.com/navikt/fpsak-autotest
RUN apk add --no-cache socat
CMD ["/usr/bin/socat", "-u", "TCP4-LISTEN:6514,fork", "PIPE:/dev/stdout"]
