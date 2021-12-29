package RedCloudRule.bs.models;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Label {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long taskid;
    private String claimeraccount;
    private boolean passornot;
    private String publisheraccount;
    private String pictureurl;
    private double vx;
    private double vy;
    private int width;
    private int height;
    private String name;

    private Long missionid;

    protected Label(){}
    public Label(Long task_id, String claimer_acc, String pub_acc, String pic_url, 
                    double v_x, double v_y, int width, int height, Long mission_id, String name){
        taskid = task_id;
        claimeraccount = claimer_acc;
        passornot = false;
        publisheraccount = pub_acc;
        pictureurl = pic_url;
        vx = v_x;
        vy = v_y;
        this.width = width;
        this.height = height;
        missionid = mission_id;
        this.name = name;
    }


    public Label(double v_x, double v_y, int width, int height){
        vx = v_x;
        vy = v_y;
        this.width = width;
        this.height = height;
    }

    public Long getId(){return this.id;}
    public String getClaimeraccount(){return this.claimeraccount;}
    public Long getTaskid(){return taskid;}
    public boolean getPass(){return passornot;}
    public String getPublisheraccount(){return this.publisheraccount;}
    public String getPictureurl(){return this.pictureurl;}
    public double getVx(){return vx;}
    public double getVy(){return vy;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public Long getMissionid(){return missionid;}
    // public Long getPicid(){return picid;}
    public String getName(){return name;}

    public void setPass(){passornot = true;}
}
