package it.skeith.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import javax.json.bind.JsonbException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;


@Provider
public class JsonbExceptionMapper implements ExceptionMapper<JsonbException> {

    @Override
    public Response toResponse(JsonbException exception) {
        if( exception.getCause() instanceof JsonParseException ||
                exception.getCause() instanceof JsonMappingException ) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("error parsing json body - " + exception.getMessage())
                    .type(MediaType.TEXT_PLAIN_TYPE)
                    .build();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(exception.getMessage())
                .type(MediaType.TEXT_PLAIN_TYPE)
                .build();
    }
}
