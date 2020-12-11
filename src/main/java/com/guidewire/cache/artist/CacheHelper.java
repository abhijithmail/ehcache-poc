package com.guidewire.cache.artist;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;


/**
 * Uses configuration in artist-cache.xml
 * POC will assume that the cache is for musical artists.
 * In practice, there would be a cache for each entity or some better generalized solution.
 * @param <K>
 * @param <V>
 */
public class CacheHelper<K,V> {
    Logger log = LoggerFactory.getLogger(CacheHelper.class);
    public static final String CACHE_CONFIG_LOCATION_PARAM = "com.guidewire.ehcache.config.location";
    String cacheConfigLocation;

    private CacheManager cacheManager;
    private String cacheName;
    private Class<K> keyClass;
    private Class<V> valueClass;
    private long hitCounter;
    private long missCounter;

    private boolean isTest;

    private static CacheHelper _INSTANCE;

    private CacheHelper() {}

    private CacheHelper(String pCacheName, Class pKeyType, Class pValueType) {
        keyClass = pKeyType;
        valueClass = pValueType;
        cacheName = pCacheName;
        cacheConfigLocation = System.getProperty(CACHE_CONFIG_LOCATION_PARAM);
    }

    public static synchronized CacheHelper getInstance(String pCacheName, Class pKeyType, Class pValueType) {
        if(_INSTANCE == null) {
            _INSTANCE = new CacheHelper(pCacheName, pKeyType, pValueType);
        }

        return _INSTANCE;
    }

    public void initXMLConfig() throws Exception {
        log.info("initXMLConfig() creating cache with configuration, {}.",cacheConfigLocation);
        try {
            final URL myUrl = getClass().getResource(cacheConfigLocation);
            log.info("initXMLConfig() URL[{}]",myUrl);
            XmlConfiguration xmlConfig = new XmlConfiguration(myUrl);
            //log.debug("initXMLConfig() - xml config - \n{}",xmlConfig.toString());
            this.cacheManager = CacheManagerBuilder.newCacheManager(xmlConfig);
            this.cacheManager.init();
            log.debug("initXMLConfig cacheManager - {}", this.cacheManager);
       } catch(Exception e) {
            log.error("cache,{}, configuration is wonky. Fail over to default. You should fix this.",this.cacheName,e);
            throw e;
        }
    }

    void initCustom() throws Exception {
        isTest = true;
        cacheConfigLocation = System.getProperty(CACHE_CONFIG_LOCATION_PARAM);
        initXMLConfig();
    }


    Cache<K, V> getCache() {
        if(log.isDebugEnabled()) {
            log.debug("getCache - {}[{}, {}]",this.cacheName,this.keyClass.getName(), this.valueClass.getName());
        }
        return this.cacheManager.getCache(cacheName, keyClass, valueClass);
    }

    public V get(K id) {
        if(log.isDebugEnabled()) {
            log.debug("get - {}[{}]",this.cacheName,id);
        }

        V rVal = getCache().get(id);
        if(null == rVal) {
            missCounter++;
            log.warn("get - {}[{}] - cache miss. Total misses, {}.",this.cacheName,id, missCounter);
        } else {
            hitCounter++;
        }
        return rVal;
    }

    public void put(K aKey, V aValue) {
        if(log.isDebugEnabled()) {
            log.debug("put - {}[{}, {}]",this.cacheName,aKey, aValue);
        }
        getCache().put(aKey,aValue);
    }

    public void remove(K aKey) {
        getCache().remove(aKey);
    }

    public void flush() {
        if(log.isDebugEnabled()) {
            log.debug("getCache - {}[{},{}]",this.cacheName,this.keyClass.getName(), this.valueClass.getName());
        }

        getCache().clear();

        hitCounter = 0;
        missCounter = 0;
    }

    Cache getCache(String pCacheName) {
        log.debug("getCache({})",pCacheName);
        return cacheManager.getCache(pCacheName, keyClass, valueClass);
    }

    public long getHitCounter() {
        return hitCounter;
    }

    public long getMissCounter() {
        return missCounter;
    }
}