package br.com.zup.edu;

import io.micronaut.grpc.server.GrpcEmbeddedServer;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.util.Map;

import static io.micronaut.core.util.CollectionUtils.mapOf;

@Controller
public class StopGrpcServerController {

    private static final Logger logger = LoggerFactory.getLogger(StopGrpcServerController.class);

    @Inject
    private GrpcEmbeddedServer server;

    @Post("/grpc-server/stop")
    public HttpResponse<?> stop() {
        logger.info("Stopping the gRPC Server...");
        server.stop();
        return HttpResponse
                .ok(mapOf("host", server.getHost(),
                           "port", server.getServerConfiguration().getServerPort(),
                           "running", server.isRunning())
                );
    }
}
