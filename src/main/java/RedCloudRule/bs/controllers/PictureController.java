package RedCloudRule.bs.controllers;

// import RedCloudRule.bs.exceptions.ResourceNotFoundException;
import RedCloudRule.bs.repositories.PictureRepository;
import RedCloudRule.bs.models.Picture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PictureController {
    private PictureRepository pictureRepository;

    @Autowired
    public PictureController(PictureRepository pictureRepository){
        this.pictureRepository = pictureRepository;
    }

    @GetMapping("/allpic_get/{user_account}")
    public ResponseEntity<List<Picture>> allpic_get(@PathVariable(value = "user_account") String acc){
        List<Picture> pic_list = this.pictureRepository.findByowneraccount(acc);
        for (int i =0 ;i<pic_list.size();i++){
            System.out.printf("owner:%s\n", pic_list.get(i).getOwneraccount());
            System.out.printf("path:%s\n", pic_list.get(i).getPath());
        }
        return ResponseEntity.ok(this.pictureRepository.findByowneraccount(acc));
    }

    //这个没被该，直接换会报错，改成通过picture_url进行判断,测试的时候要重新建图片
    @GetMapping("/change_pic_label/{pic_id}")
    public boolean change_pic_label(@PathVariable(value = "pic_id") Long pic_id){
        System.out.println(pic_id);
        this.pictureRepository.findById(pic_id)
            .map(picture -> {
                picture.setLabelornot();
                return this.pictureRepository.save(picture);
            }
        );
        //此处应该打印一下看看
        Picture changed_picture = this.pictureRepository.choosePicture(pic_id);
        System.out.println(changed_picture.getLabelornot());
        return true;
    }

    // @GetMapping("/mis_change_pic/{pic_id}")
    // public boolean mis_change_pic(@PathVariable(value = "pic_id") Long pic_id){
    //     System.out.println(pic_id);
    //     this.pictureRepository.findById(pic_id)
    //         .map(picture -> {
    //             picture.setMissionornot();
    //             return this.pictureRepository.save(picture);
    //         }
    //     );
    //     //此处应该打印一下看看
    //     Picture changed_picture = this.pictureRepository.choosePicture(pic_id);
    //     System.out.println(changed_picture.getMissionornot());
    //     return true;
    // }

    @PostMapping("/newpic_up")
    public boolean newpic_up(@RequestBody Picture new_info){
        System.out.println(new_info.getPath());
        System.out.println(new_info.getOwneraccount());
        Picture new_picture = new Picture(new_info.getPath(), new_info.getOwneraccount(),
                                    new_info.getWidth(), new_info.getHeight());
        this.pictureRepository.save(new_picture);

        return true;
    }

    @GetMapping("/setpic_label/{id}")
    public boolean setpic_up(@PathVariable(value = "id") Long pic_id){
        this.pictureRepository.findById(pic_id)
            .map(picture -> {
                picture.setLabelornot();
                return this.pictureRepository.save(picture);
            }
        );
        Picture changed_picture = this.pictureRepository.choosePicture(pic_id);
        System.out.println(changed_picture.getLabelornot());
        return true;
    }

    public int widthfind(String path){
        return this.pictureRepository.chooseWidth(path);
    }

    public int heightfind(String path){
        return this.pictureRepository.chooseHeight(path);
    }
}
