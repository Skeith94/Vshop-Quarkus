package it.skeith.entity;

import at.favre.lib.bytes.Bytes;
import com.mongodb.client.gridfs.model.GridFSFile;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoEntity;
import io.smallrye.mutiny.Uni;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.BsonBinary;
import org.bson.types.ObjectId;
import org.jboss.resteasy.reactive.multipart.FileUpload;

import java.io.File;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity
public class Photos extends ReactivePanacheMongoEntity {

  private ObjectId id;
  private Long productId;
  private byte[]  photo;



  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Photos photos = (Photos) o;

    return id.equals(photos.id);
  }

  @Override
  public int hashCode() {
    return id.hashCode();
  }


  public Photos(Long productId, byte[] photo) {
    this.productId = productId;
    this.photo = photo;
  }

 public static Uni<Void>save(Photos photos){
    return persist(photos);
 }
}
