package OneStream;

/**
 * Created by ruspe_000 on 2017-02-25.
 */

import OneStream.Controller.Util.Playlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.ArrayList;

public interface PlaylistRepository
        extends MongoRepository<Playlist, String> {

    @Query  ("{ 'name': ?0 }")
    public ArrayList<Playlist> findByName(String name);

    @Query  ("{ 'owner': ?0 }")
    public ArrayList<Playlist> findByOwner(String owner);

    @Query  ("{ $and: [ { 'name': ?0 }, { 'owner': ?1 } ] }")
    public ArrayList<Playlist> findByNameAndOwner(String name, String owner);
}