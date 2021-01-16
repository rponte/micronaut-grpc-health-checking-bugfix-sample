package br.com.zup.edu.bugfix;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.async.publisher.AsyncSingleResultPublisher;
import io.micronaut.grpc.server.GrpcEmbeddedServer;
import io.micronaut.grpc.server.health.GrpcServerHealthIndicator;
import io.micronaut.health.HealthStatus;
import io.micronaut.management.health.indicator.HealthResult;
import org.reactivestreams.Publisher;

import javax.inject.Singleton;
import java.util.Map;

import static io.micronaut.core.util.CollectionUtils.mapOf;

@Singleton
@Replaces(GrpcServerHealthIndicator.class)
public class CustomGrpcServerHealthIndicator extends GrpcServerHealthIndicator {

    private static final String ID = "grpc-server";

    private final GrpcEmbeddedServer server;

    public CustomGrpcServerHealthIndicator(GrpcEmbeddedServer server) {
        super(server);
        this.server = server;
    }

    @Override
    public Publisher<HealthResult> getResult() {
        return new AsyncSingleResultPublisher<>(this::getHealthResult);
    }

    private HealthResult getHealthResult() {

        final HealthStatus healthStatus = server.isRunning() ? HealthStatus.UP : HealthStatus.DOWN;
        /**
         * BUGFIX: it avoids to call the server.getPort() method when the gRPC-Server is DOWN because
         * it throws an unexpected exception that breaks the /health endpoint
         */
        final String serverHost = server.getHost();
        final int serverPort = server.getServerConfiguration().getServerPort(); // don't call the server.getPort() here!
        final Map details = mapOf("host", serverHost, "port", serverPort);

        return HealthResult
                .builder(ID, healthStatus)
                .details(details)
                .build();
    }

}
