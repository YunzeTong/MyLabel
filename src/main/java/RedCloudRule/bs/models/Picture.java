package RedCloudRule.bs.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Picture {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String path;
    private boolean labelornot;
    public String owneraccount;
    // private boolean missionornot;
    private int width;
    private int height;

    protected Picture(){}

    public Picture(String path, String owneraccount){
        this.path = path;
        this.owneraccount = owneraccount;
        this.labelornot = false;
        // this.missionornot = false;
    }

    public Picture(String path, String owneraccount, int width, int height){
        this.path = path;
        this.owneraccount = owneraccount;
        this.labelornot = false;
        // this.missionornot = false;
        this.width = width;
        this.height = height;
    }

    public Long getId(){
        return id;
    }

    public String getPath(){return path;}

    public String getOwneraccount(){return owneraccount;}

    public boolean getLabelornot(){
        return this.labelornot;
    }

    // public boolean getMissionornot(){
    //     return this.missionornot;
    // }


    public int getHeight(){return this.height;}

    public int getWidth(){return this.width;}

    public void setLabelornot(){
        this.labelornot = !this.labelornot;
    }

    // public void setMissionornot(){
    //     this.missionornot = !this.missionornot;
    // }
}
