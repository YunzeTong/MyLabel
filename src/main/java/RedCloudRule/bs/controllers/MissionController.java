package RedCloudRule.bs.controllers;

// import RedCloudRule.bs.exceptions.ResourceNotFoundException;
import RedCloudRule.bs.repositories.MissionRepository;
import RedCloudRule.bs.models.Mission;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.jpa.repository.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class MissionController {
    private MissionRepository missionRepository;

    @Autowired
    public MissionController(MissionRepository missionRepository){
        this.missionRepository = missionRepository;
    }

    @GetMapping("/allmission_get/{acc}")
    public ResponseEntity<List<Mission>> getAllMission(@PathVariable(value = "acc" ) String account){
        System.out.println(account);
        //这块有问题，应该是找不为当前id发布的mission
        // List<Mission> mission_list = this.missionRepository.findBypublisheraccount(account);
        //找不为当前账号发布的任务
        List<Mission> mission_list = this.missionRepository.findBypublisheraccountNot(account);
        for (int i=0;i<mission_list.size();i++){
            System.out.println(mission_list.get(i).getPublisheraccount());
            // System.out.println(mission_list.get(i).getPictureid());
            System.out.println(mission_list.get(i).getId());
            System.out.println(mission_list.get(i).getPictureurl());
        }
        return ResponseEntity.ok(this.missionRepository.findBypublisheraccountNot(account));
    }

    @PostMapping("/create_mission")
    public boolean create_mission(@RequestBody Mission new_info){
        //单张图片对应任务时候的写法
        // Mission new_mission = new Mission(new_info.getPictureid(), new_info.getPublisheraccount(), new_info.getPictureurl());
        Mission new_mission = new Mission(new_info.getPublisheraccount(), new_info.getPictureurl());
        this.missionRepository.save(new_mission);
        // System.out.println(new_info.getPictureid());
        System.out.println(new_info.getPictureurl());
        System.out.println(new_info.getPublisheraccount());
        //找到对应图片，把状态改掉
        //先不写
        return true;
    }

}


// @GetMapping("/create_mission/{pic_id}/{user_acc}/{pic_url}")
    // public boolean create_mission(@PathVariable(value = "pic_id") Long pic_id, @PathVariable(value = "user_acc") String owner_acc,  @PathVariable(value = "pic_url") String pic_url){
    //     Mission new_mission = new Mission(pic_id, owner_acc, pic_url);
    //     this.missionRepository.save(new_mission);
    //     System.out.println(owner_acc);
    //     System.out.println(pic_id);
    //     System.out.println(pic_url);
    //     //找到对应图片，把状态改掉
    //     //先不写
    //     return true;
    // }
