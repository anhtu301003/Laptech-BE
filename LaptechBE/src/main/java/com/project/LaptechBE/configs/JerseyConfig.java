package com.project.LaptechBE.configs;

import com.project.LaptechBE.untils.Endpoints;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@ApplicationPath(Endpoints.API_PREFIX)
public class JerseyConfig extends ResourceConfig {
    public JerseyConfig() {
        packages("com.project.LaptechBE.controllers");
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
    }
}
