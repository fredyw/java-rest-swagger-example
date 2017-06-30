package org.fredy.example.rest;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Api(value = "/hello", description = "API for Hello")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path("/hello")
public class HelloResource {
    @ApiModel("request")
    public static class HelloRequest {
        @ApiModelProperty(value = "The name", required = true)
        private final String name;

        @JsonCreator
        public HelloRequest(@JsonProperty("name") String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    @ApiModel("response")
    public static class HelloResponse {
        @ApiModelProperty(value = "The message", required = true)
        private final String message;

        @JsonCreator
        public HelloResponse(@JsonProperty("message") String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @ApiOperation(value = "Gets the message", response = HelloResponse.class)
    @ApiResponse(code = 200, message = "Successful operation")
    @Path("/message")
    @POST
    public Response getMessage(HelloRequest request) {
        return Response.ok(new HelloResponse("Hello, " + request.getName())).build();
    }
}
