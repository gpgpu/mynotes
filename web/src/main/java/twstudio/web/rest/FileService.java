package twstudio.web.rest;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import twstudio.domain.ExternalFile;
import twstudio.domain.ExternalFileRepo;

@Path("file")
public class FileService {
    @Inject ExternalFileRepo repo;

    @GET
    @Path("unsecured/{id}")
    public Response getFile(@PathParam("id") int id){
        ExternalFile theFile = repo.getFile(id);
        return Response.status(Response.Status.OK).entity(theFile.getContent()).type(theFile.getMimeType()).build();
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public int saveFile(
            @HeaderParam("X-File-ArticleId") int articleId,
            @HeaderParam("X-File-Type") String mimeType,
            @FormDataParam("fileUploader") InputStream fileInputStream,
            @FormDataParam("fileUploader") FormDataContentDisposition fileDetails ) throws SQLException, IOException{

        ExternalFile theFile = new ExternalFile();
        theFile.setFileName(fileDetails.getFileName());
        theFile.setMimeType(mimeType);
        theFile.setArticleId(articleId);

        byte[] bytes = IOUtils.toByteArray(fileInputStream);
        theFile.setContent(bytes);

        return repo.saveFile(theFile);
    }

    @GET
    @Path("unsecured/timezone")
    @Produces(MediaType.APPLICATION_JSON)
    public Date getTime(){
        return new Date();
    }
}