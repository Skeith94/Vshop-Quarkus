package it.skeith.payload.request;

import org.jboss.resteasy.reactive.PartType;
import org.jboss.resteasy.reactive.RestForm;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.InputStream;

public class DownloadFormData {

    @RestForm
    String name;

    @RestForm
    @PartType(MediaType.APPLICATION_OCTET_STREAM)
    File file;
}