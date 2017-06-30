package org.fredy.example.swagger;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.eclipse.jetty.util.resource.Resource;
import org.glassfish.jersey.server.ServerProperties;
import org.glassfish.jersey.servlet.ServletContainer;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import java.net.URI;
import java.net.URL;
import java.util.StringJoiner;

public class HelloServer {
    public class Bootstrap extends HttpServlet {
        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);

            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setVersion("1.0.2");
            beanConfig.setSchemes(new String[]{"http"});
            beanConfig.setHost("localhost:8080");
            beanConfig.setBasePath("/api");
            beanConfig.setResourcePackage("io.swagger.resources");
            beanConfig.setScan(true);
        }
    }

    public static void main(String[] args) throws Exception {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(context);

        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
            ServerProperties.PROVIDER_CLASSNAMES,
            new StringJoiner(",")
                .add(HelloResource.class.getCanonicalName())
                .add(JacksonJsonProvider.class.getCanonicalName())
                .add(ApiListingResource.class.getCanonicalName())
                .add(SwaggerSerializers.class.getCanonicalName())
            .toString());

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setBaseResource(Resource.newClassPathResource("/webapp"));

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resourceHandler, context});
        jettyServer.setHandler(handlers);

//        jerseyServlet = context.addServlet(Bootstrap.class, "/doc/*");

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
