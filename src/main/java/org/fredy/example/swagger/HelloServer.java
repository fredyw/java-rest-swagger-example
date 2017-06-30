package org.fredy.example.swagger;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.swagger.jaxrs.config.BeanConfig;
import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
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
import java.util.StringJoiner;

public class HelloServer {
    public static class Bootstrap extends HttpServlet {
        @Override
        public void init(ServletConfig config) throws ServletException {
            super.init(config);

            BeanConfig beanConfig = new BeanConfig();
            beanConfig.setTitle("Hello API");
            beanConfig.setVersion("1.0.0");
            beanConfig.setSchemes(new String[]{"http"});
            beanConfig.setBasePath("/api");
            beanConfig.setResourcePackage("org.fredy.example.swagger");
            beanConfig.setScan(true);
        }
    }

    public static void main(String[] args) throws Exception {
        ServletContextHandler servletHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
        servletHandler.setContextPath("/");

        ServletHolder jerseyServlet = servletHandler.addServlet(ServletContainer.class, "/api/*");
        jerseyServlet.setInitOrder(0);

        jerseyServlet.setInitParameter(
            ServerProperties.PROVIDER_CLASSNAMES,
            new StringJoiner(",")
                .add(HelloResource.class.getCanonicalName())
                .add(JacksonJsonProvider.class.getCanonicalName())
                .add(ApiListingResource.class.getCanonicalName())
                .add(SwaggerSerializers.class.getCanonicalName())
            .toString());

        ResourceHandler staticHandler = new ResourceHandler();
        staticHandler.setWelcomeFiles(new String[]{"index.html"});
        staticHandler.setBaseResource(Resource.newClassPathResource("/webapp"));

        HandlerList handlers = new HandlerList();
        // Add two handlers, one for static content and the other one for dynamic content.
        handlers.setHandlers(new Handler[]{staticHandler, servletHandler});

        // The mapping doesn't really matter here.
        jerseyServlet = servletHandler.addServlet(Bootstrap.class, "/");
        jerseyServlet.setInitOrder(2);

        Server jettyServer = new Server(8080);
        jettyServer.setHandler(handlers);

        try {
            jettyServer.start();
            jettyServer.join();
        } finally {
            jettyServer.destroy();
        }
    }
}
