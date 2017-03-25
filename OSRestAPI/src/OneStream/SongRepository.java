package OneStream;

/**
 * Created by ruspe_000 on 2017-02-25.
 */

import OneStream.Controller.Util.Song;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;

public interface SongRepository extends MongoRepository<Song, String> {

    @Query  ("{ 'uri': ?0 }")
    public ArrayList<Song> findByUri(String uri);
}