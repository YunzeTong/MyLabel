package RedCloudRule.bs.controllers;

// import RedCloudRule.bs.exceptions.ResourceNotFoundException;
import RedCloudRule.bs.repositories.TaskRepository;
import RedCloudRule.bs.exceptions.ResourceNotFoundException;
import RedCloudRule.bs.models.Task;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TaskController {
    
    private TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }
    //认领某一任务，本质上是missionsquare界面的
    @PostMapping("/claim_task")
    public boolean claim_task(@RequestBody Task new_info){
        //在已经领任的列表中查找任务
        System.out.printf("claimer: %s\n", new_info.getClaimeraccount());
        System.out.printf("publisher: %s\n", new_info.getPublisheraccount());
        System.out.printf("url: %s\n", new_info.getPictureurl());
        List<Task> task_list = this.taskRepository.getSpecificTask(new_info.getMissionid(), new_info.getClaimeraccount());
        if (task_list.size() == 0){
            //如果没有，创建实例，认领成功
            System.out.println("no past claim");
            //1206增加
            String[] url_list = new_info.getPictureurl().split("\\$");
            for (String single_url: url_list){
                System.out.println("经过分割后"+single_url);
                this.taskRepository.save(new Task(new_info.getMissionid(), 
                                            new_info.getClaimeraccount(), 
                                            new_info.getPublisheraccount(), 
                                            single_url));
            }


            // this.taskRepository.save(new Task(new_info.getMissionid(), 
            //                                 new_info.getClaimeraccount(), 
            //                                 new_info.getPublisheraccount(), 
            //                                 new_info.getPictureurl()));
            return true;
        }
        else{
            System.out.println("have past claim");
            return false;
        }
    }

    //获得我认领的所有任务
    @GetMapping("/get_myclaim/{account}")
    public ResponseEntity<List<Task>> get_myclaim(@PathVariable(value = "account") String account){
        System.out.println(account);
        List<Task> task_list = this.taskRepository.findByclaimeraccount(account);
        return ResponseEntity.ok(task_list);
    }

    //获得我发布的未完成任务
    @GetMapping("/get_mypublish/{account}")
    public ResponseEntity<List<Task>> get_mypublish(@PathVariable(value = "account") String account){
        System.out.println(account);
        List<Task> task_list = this.taskRepository.getwithPubandJudge(account, false);
        return ResponseEntity.ok(task_list);
    }

    

    //更新task完成状态
    @GetMapping("/update_task/{label}/{id}/{state}")
    public boolean update_task(@PathVariable(value = "state") int state, 
                                @PathVariable(value = "id") Long id,
                                @PathVariable(value = "label") boolean label){
        System.out.println(id);
        this.taskRepository.findById(id)
            .map(task -> {
                task.setLabel(label);
                task.setState(state);
                return this.taskRepository.save(task);
            }
        );
        //此处应该打印一下看看
        Task change_task = this.taskRepository.chooseTask(id);
        System.out.println(change_task.getLabelornot());
        System.out.println(change_task.getState());
        return true;
    }

    //删除掉某个task
    @DeleteMapping("/delete_task/{id}")
    public ResponseEntity<Void> delete_task(@PathVariable(value = "id") Long id){
        Task task = this.taskRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("Task not found")
        );
        this.taskRepository.delete(task);
        return ResponseEntity.ok().build();
    }

}

// @GetMapping("/claim_task/{mission_id}/{user_acc}/{pub_acc}/{pic_url}")
//     public boolean claim_task(@PathVariable(value = "mission_id") Long mission_id, @PathVariable(value = "user_acc") String account,
//                             @PathVariable(value = "pub_acc") String publisher_acc, @PathVariable(value = "pic_url") String pic_url){
//         //在已经领任的列表中查找任务
//         Task task = this.taskRepository.getSpecificTask(mission_id, account);

//         //如果没有，创建实例，认领成功
//         this.taskRepository.save(new Task(mission_id, account, publisher_acc, pic_url));
//         return true;
//     }

// @GetMapping("/update_task/{state}/{id}")
//     public boolean update_task(@PathVariable(value = "state") boolean state, @PathVariable(value = "id") Long id){
//         System.out.println(id);
//         this.taskRepository.findById(id)
//             .map(task -> {
//                 task.setLabel(state);
//                 return this.taskRepository.save(task);
//             }
//         );
//         //此处应该打印一下看看
//         Task change_task = this.taskRepository.chooseTask(id);
//         System.out.println(change_task.getLabelornot());
//         return true;
//     }