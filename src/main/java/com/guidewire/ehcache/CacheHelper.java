package com.guidewire.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.xml.XmlConfiguration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;


/**
 * Uses configuration in ehcache-config.xml
 * @param <K>
 * @param <V>
 */
public class CacheHelper<K,V> {
    Logger log = LoggerFactory.getLogger(CacheHelper.class);
    public static final String CACHE_CONFIG_LOCATION_PARAM = "com.guidewire.ehcache.config.location";
    String cacheConfigLocation = "src/config/ehcache.xml";

    private CacheManager cacheManager;
    private final String cacheName;
    private final Class<K> keyClass;
    private final Class<V> valueClass;
    private long hitCounter;
    private long missCounter;

    private boolean isTest;


    public CacheHelper(String pCacheName, Class pKeyType, Class pValueType) {
        keyClass = pKeyType;
        valueClass = pValueType;
        cacheName = pCacheName;
        cacheConfigLocation = System.getProperty(CACHE_CONFIG_LOCATION_PARAM);

    }

    public void initXMLConfig() throws Exception {
        log.debug("initXMLConfig() creating cache.");
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
            try {
                rVal = this.valueClass.getDeclaredConstructor(String.class).newInstance("42");
                getCache().put(id,rVal);
            } catch (ReflectiveOperationException e) {
                log.error("error instantiating new cache entry.", e);
            }
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