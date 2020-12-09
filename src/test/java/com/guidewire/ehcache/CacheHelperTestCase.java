package com.guidewire.ehcache;

import org.ehcache.Cache;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

public class CacheHelperTestCase {
    static final Logger log = LoggerFactory.getLogger(CacheHelperTestCase.class);
    private static final String cacheName = "myCache";
    private static CacheHelper<String, Integer> cacheHelper;
    private static final String aKey = "testKey123";
    private static final Integer aValue = 42;

    @BeforeClass
    public static void initCache() throws Exception {
        log.info("TEST - initCache");
        cacheHelper = new CacheHelper<String, Integer>(cacheName,String.class,Integer.class);
        System.setProperty(CacheHelper.CACHE_CONFIG_LOCATION_PARAM, "../../../ehcache-config.xml");
        cacheHelper = new CacheHelper<String,Integer>(cacheName,String.class,Integer.class);
        cacheHelper.initXMLConfig();
    }

    @Test
    public void testCache()  throws Exception {
        log.info("TEST - testCache");
        cacheHelper.put(aKey,aValue);
        Integer theValue = cacheHelper.get(aKey);
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
        Integer rVal = cacheHelper.get("myMissingCode");
        assertNotNull(rVal);
        cacheHelper.put("myMissingCode",rVal);
        rVal = cacheHelper.get("myMissingCode");
        assertNotNull(rVal);

        // there should be exactly one miss.
        long missCount = cacheHelper.getMissCounter();
        assertEquals(1, missCount);
    }

    @Test
    public void testUpdateEvent() throws Exception {
        log.info("TEST - testUpdateEvent");
        cacheHelper.put(aKey,aValue);
        cacheHelper.put(aKey, new Integer("99"));
    }
}
