package RedCloudRule.bs.models;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Mylabel {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Long picid;
    private String owner;
    private String pictureurl;
    private double vx;
    private double vy;
    private int picwidth;
    private int picheight;
    private int width;
    private int height;
    private String name;

    protected Mylabel(){}

    public Mylabel(String owner_, String pic_url, int picwidth, int picheight, 
    double v_x, double v_y, int width, int height, Long picid, String name){
        owner = owner_;
        pictureurl = pic_url;
        vx = v_x;
        vy = v_y;
        this.width = width;
        this.height = height;
        this.picwidth = picwidth;
        this.picheight = picheight;
        this.picid = picid;
        this.name = name;
    }


    public Long getId(){return this.id;}
    public Long getPicid(){return this.picid;}
    public String getOwner(){return this.owner;}
    public String getPictureurl(){return this.pictureurl;}
    public double getVx(){return vx;}
    public double getVy(){return vy;}
    public int getWidth(){return width;}
    public int getHeight(){return height;}
    public int getPicwidth(){return picwidth;}
    public int getPicheight(){return picheight;}
    public String getName(){return name;}
}
