package RedCloudRule.bs.models;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long missionid;
    private String claimeraccount;

    private boolean labelornot;
    private String publisheraccount;
    private String pictureurl;
    private int state;   //0未标注或者待审核，1被拒绝，2通过

    protected Task(){};

    // public Task(Long missionid, String claimeraccount){
    //     this.missionid = missionid;
    //     this.claimeraccount = claimeraccount;
    //     this.labelornot = false;
    // }

    public Task(Long missionid, String claimeraccount, String publisher_acc, String pic_url){
        this.missionid = missionid;
        this.claimeraccount = claimeraccount;
        this.labelornot = false;
        this.publisheraccount = publisher_acc;
        this.pictureurl = pic_url;
        this.state = 0;
    }

    public Long getMissionid(){
        return this.missionid;
    }

    public String getClaimeraccount(){
        return this.claimeraccount;
    }

    public String getPublisheraccount(){
        return this.publisheraccount;
    }

    public String getPictureurl(){
        return this.pictureurl;
    }

    public boolean getLabelornot(){
        return this.labelornot;
    }

    public Long getId(){
        return this.id;
    }
    
    public void setLabel(boolean state){
        this.labelornot = state;
    }

    public int getState(){
        return this.state;
    }

    public void setState(int new_state){
        this.state = new_state;
    }
}
