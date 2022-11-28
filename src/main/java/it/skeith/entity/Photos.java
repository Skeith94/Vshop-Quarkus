package it.skeith.entity;

import com.mongodb.client.gridfs.model.GridFSFile;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import io.quarkus.mongodb.panache.common.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@MongoEntity
public class Photos extends PanacheMongoEntity {
  private Long productId;
  private GridFSFile photo;

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
}
