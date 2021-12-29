package RedCloudRule.bs.models;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Mission {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    // private Long pictureid;
    private String publisheraccount;
    private String pictureurl;

    protected Mission(){}

    //2031
    // public Mission(Long pictureid, String publisheraccount, String picture_url){
    //     this.pictureid = pictureid;
    //     this.publisheraccount = publisheraccount;
    //     this.pictureurl = picture_url;
    // }

    public Mission(String publisheraccount, String picture_url){
        this.publisheraccount = publisheraccount;
        this.pictureurl = picture_url;
    }

    // public Long getPictureid(){
    //     return this.pictureid;
    // }

    public String getPublisheraccount(){
        return this.publisheraccount;   //暂且这么写着，要改
    }

    public Long getId(){
        return this.id;
    }

    public String getPictureurl(){
        return this.pictureurl;
    }


}
