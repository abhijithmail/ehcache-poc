package com.guidewire.cache.artist;

import com.guidewire.domain.artist.Artist;
import org.ehcache.Cache;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class CacheHelperTestCase {
    static final Logger log = LoggerFactory.getLogger(CacheHelperTestCase.class);
    private static final String cacheName = "artistCache";
    private static CacheHelper<String, Artist> cacheHelper;
    private static final String aKey = "testKey123";
    private static final Artist aValue = new Artist("testKey123","Foo","1972");

    @BeforeClass
    public static void initCache() throws Exception {
        log.info("TEST - initCache");
        //cacheHelper = new CacheHelper<String, Integer>(cacheName,String.class,Integer.class);
        System.setProperty(CacheHelper.CACHE_CONFIG_LOCATION_PARAM, "../../../../artist-cache.xml");
        cacheHelper = CacheHelper.getInstance(cacheName,String.class,Artist.class);
        cacheHelper.initXMLConfig();
    }

    @Test
    public void testCache()  throws Exception {
        log.info("TEST - testCache");
        cacheHelper.put(aKey,aValue);
        Artist theValue = cacheHelper.get(aKey);
        assertNotNull(theValue);
        assertEquals(aValue,theValue);
    }

    @Test
    public void testGetBadCache()  throws Exception {
        log.info("TEST - testGetBadCache");
        Cache c = cacheHelper.getCache("myBadCache");
        assertNull(c);
    }

    @Test
    public void testCacheMiss()  throws Exception {
        log.info("TEST - testCacheMiss");
        Artist rVal = cacheHelper.get("myMissingCode");
        assertNull(rVal);
    }

    @Test
    public void testUpdateEvent() throws Exception {
        log.info("TEST - testUpdateEvent");
        cacheHelper.put(aKey,aValue);
        aValue.setName("FOO");
        cacheHelper.put(aKey, aValue);
    }
}
