package com.guidewire.cache.artist;

import com.guidewire.domain.artist.Artist;
import com.guidewire.artist.db.ArtistJDBC;
import org.ehcache.spi.loaderwriter.CacheLoaderWriter;
import org.ehcache.spi.loaderwriter.CacheLoadingException;
import org.ehcache.spi.loaderwriter.CacheWritingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ArtistCacheWriterLoader implements CacheLoaderWriter<String, Artist> {
    private static final Logger log = LoggerFactory.getLogger(ArtistJDBC.class);

    /**
     * Loads a single value.
     * <p>
     * When used with a cache any exception thrown by this method will be thrown
     * back to the user as a {@link CacheLoadingException}.
     *
     * @param key the key for which to load the value
     * @return the loaded value
     * @throws Exception if the value cannot be loaded
     */
    @Override
    public Artist load(String key) throws Exception {
        ArtistJDBC jdbc = new ArtistJDBC();
        Artist rVal = jdbc.read(key);
        log.debug("load({}) returning, {}.", key, rVal);
        return rVal;
    }

    /**
     * Writes a single mapping.
     * <p>
     * The write may represent a brand new value or an update to an existing value.
     * <p>
     * When used with a {@code Cache} any exception thrown by this method will
     * be thrown back to the user through a {@link CacheWritingException}.
     *
     * @param key   the key to write
     * @param value the value to write
     * @throws Exception if the write operation failed
     */
    @Override
    public void write(String key, Artist value) throws Exception {
        ArtistJDBC jdbc = new ArtistJDBC();
        if(value.isPersistent()) {
            // update
            jdbc.update(value);
        } else {
            // create
            jdbc.create(value);
        }
        log.debug("load");
    }

    /**
     * Deletes a single mapping.
     *
     * @param key the key to delete
     * @throws Exception if the write operation failed
     */
    @Override
    public void delete(String key) throws Exception {
        ArtistJDBC jdbc = new ArtistJDBC();
        jdbc.delete(key);
    }
}
