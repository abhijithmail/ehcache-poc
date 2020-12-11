package com.guidewire.domain.artist;

import java.io.Serializable;

public class Artist implements Serializable {
    private String id;
    private String name;
    private String yearEstablished;
    private boolean persistent;

    public Artist() {}
    public Artist(String pID,String pName,String pYearEstablished) {
        id = pID;
        name = pName;
        yearEstablished = pYearEstablished;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYearEstablished() {
        return yearEstablished;
    }

    public void setYearEstablished(String yearEstablished) {
        this.yearEstablished = yearEstablished;
    }

    /**
     *
     * @return
     */
    public boolean isPersistent() {
        return persistent;
    }

    /**
     * Need to hide this method. Do not want it open for just anyone.
     * TODO make private and use reflection to invoke from the JDBC layer.
     * @param persistent
     */
    public void setPersistent(boolean persistent) {
        this.persistent = persistent;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("Artist[").append(this.id);
        buf.append(", ").append(this.name);
        buf.append(", ").append(this.yearEstablished).append("]");
        return buf.toString();
    }
}
