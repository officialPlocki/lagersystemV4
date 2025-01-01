package de.kabuecher.storage.v4.server;

import de.kabuecher.storage.v4.server.listener.APIListener;
import io.undertow.Undertow;
import io.undertow.server.handlers.PathHandler;

public class UndertowServer {

    private Undertow server;

    public void start() {

        PathHandler pathHandler = new PathHandler();
        pathHandler.addPrefixPath("/", new APIListener());

        server = Undertow.builder()
                .addHttpListener(3200, "127.0.0.1")
                .setHandler(pathHandler)
                .build();



        server.start();

        System.out.println("Server started on port 3200");
    }

    public void stop() {
        server.stop();

        System.out.println("Server stopped");
    }

}
