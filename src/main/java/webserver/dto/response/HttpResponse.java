package webserver.dto.response;

import common.dto.ControllerResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import webserver.common.util.HttpUtils;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

@Slf4j
@Getter
@Builder
public class HttpResponse {
    private final HttpResponseStartLine startLine;
    private final List<String> header;
    private final byte[] body;

    public static HttpResponse valueOf(ControllerResponse controllerResponse, String httpVersion) throws IOException {
        String redirectTo = controllerResponse.getRedirectTo();
        log.debug("[Redirect]: " + redirectTo);

        Map<String, String> mappedHeader = controllerResponse.getHeader();
        HttpStatus httpStatus = controllerResponse.getHttpStatus();
        byte[] body = Files.readAllBytes(new File(redirectTo).toPath());
        mappedHeader.put("Content-Length", String.valueOf(body.length));

        return HttpResponse.builder()
                .startLine(new HttpResponseStartLine(httpVersion, httpStatus))
                .header(HttpUtils.mappedHeaderToList(mappedHeader))
                .body(body)
                .build();
    }

    public void respond(OutputStream out) {
        log.debug("[HTTP Response]");
        DataOutputStream dos = new DataOutputStream(out);
        outputHeader(dos);
        outputBody(dos);
    }

    private void outputHeader(DataOutputStream dos) {
        try {
            dos.writeBytes(this.startLine.toString());
            for (String s : header) {
                dos.writeBytes(s);
            }
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private void outputBody(DataOutputStream dos) {
        try {
            dos.write(this.body, 0, this.body.length);
            dos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
