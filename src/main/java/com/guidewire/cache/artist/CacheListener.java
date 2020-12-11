package com.guidewire.cache.artist;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.EventFiring;
import org.ehcache.event.EventOrdering;
import org.ehcache.event.EventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CacheListener implements org.ehcache.event.CacheEventListener {
    Logger log = LoggerFactory.getLogger(CacheListener.class);

    /**
     * Invoked on {@link CacheEvent CacheEvent} firing.
     * <p>
     * This method is invoked according to the {@link EventOrdering}, {@link EventFiring} and
     * {@link EventType} requirements provided at listener registration time.
     * <p>
     * Any exception thrown from this listener will be swallowed and logged but will not prevent other listeners to run.
     *
     * @param event the actual {@code CacheEvent}
     */
    @Override
    public void onEvent(CacheEvent event) {
        EventType et = event.getType();
        Object key = event.getKey();
        Object oldValue = event.getOldValue();
        Object newValue = event.getNewValue();
        log.debug("EVENT[ {} ] KEY[ {} ] NEWVALUE[ {} ] OLDVALUE[ {} ]",et.name(),key,newValue,oldValue);
        switch(et) {
            case CREATED:
                log.debug("handle created event.");
                break;
            case UPDATED:
                log.debug("handle updated event.");
                break;
            case REMOVED:
                log.debug("handle removed event.");
                break;
            case EXPIRED:
                log.debug("handle expired event.");
                break;
            case EVICTED:
                log.debug("handle evicted event.");
                break;
            default:
                log.debug("handle unknown event.");
                break;
        }
    }
}
