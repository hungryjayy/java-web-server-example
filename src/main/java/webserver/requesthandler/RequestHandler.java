package webserver.requesthandler;

import common.controller.AbstractController;
import common.dto.ControllerRequest;
import common.dto.ControllerResponse;
import common.util.ControllerMapper;
import lombok.extern.slf4j.Slf4j;
import webserver.dto.request.HttpRequest;
import webserver.dto.request.HttpRequestStartLine;
import webserver.dto.response.HttpResponse;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RequestHandler extends Thread {

    private final Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            HttpRequest httpRequest = HttpRequest.request(br);
            HttpResponse httpResponse = handleRequest(httpRequest);

            httpResponse.respond(out);
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) throws IOException {
        HttpRequestStartLine startLine = httpRequest.getStartLine();
        String contentType = httpRequest.getHeader().get("Accept").split(",")[0];
        Map<String, String> responseHeader = new HashMap<>();
        responseHeader.put("Content-Type", contentType);

        ControllerRequest controllerRequest = ControllerRequest.builder()
                .httpMethod(startLine.getMethod())
                .url(startLine.getUrl())
                .header(responseHeader)
                .body(httpRequest.getBody())
                .build();

        AbstractController controller = ControllerMapper.getController(startLine.getUrl());
        ControllerResponse controllerResponse = controller.doService(controllerRequest);

        return HttpResponse.valueOf(controllerResponse, startLine.getVersion());
    }
}
