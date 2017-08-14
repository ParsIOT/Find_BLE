package com.parsin.bletool.Model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by hadi on 8/7/17.
 */

@Entity(nameInDb = "location_validation")
public class LocationValidation {

    @Id(autoincrement = true)
    private Long id;
    @Property(nameInDb = "man_location")
    private String manLocation;
    @Property(nameInDb = "est_location")
    private String estLocation;
    @Property(nameInDb = "time")
    private String time;
    @Generated(hash = 2013476073)
    public LocationValidation(Long id, String manLocation, String estLocation,
            String time) {
        this.id = id;
        this.manLocation = manLocation;
        this.estLocation = estLocation;
        this.time = time;
    }
    @Generated(hash = 917980667)
    public LocationValidation() {
    }
    public Long getId() {
        return this.id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getManLocation() {
        return this.manLocation;
    }
    public void setManLocation(String manLocation) {
        this.manLocation = manLocation;
    }
    public String getEstLocation() {
        return this.estLocation;
    }
    public void setEstLocation(String estLocation) {
        this.estLocation = estLocation;
    }
    public String getTime() {
        return this.time;
    }
    public void setTime(String time) {
        this.time = time;
    }
}
