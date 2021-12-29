package RedCloudRule.bs.controllers;

import RedCloudRule.bs.exceptions.ResourceNotFoundException;
import RedCloudRule.bs.repositories.UserRepository;
import RedCloudRule.bs.models.User;

import RedCloudRule.bs.models.LogRequest;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private UserRepository userRepository;

    @Autowired
    public UserController(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @PostMapping("/user/save")
    public User saveUser(@RequestBody User user){
        return this.userRepository.save(user);
    }

    @GetMapping("/user/all")
    public ResponseEntity<List<User>> getUsers(){
        return ResponseEntity.ok(
          this.userRepository.findAll()
        );
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUser(@PathVariable(value = "id" ) Long id){
        User user = this.userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User not found")
        );

        return  ResponseEntity.ok().body(user);
    }

    @PutMapping("user/{id}")
    public User updateUser(@RequestBody User newUser, @PathVariable(value = "id") Long id){
        return this.userRepository.findById(id)
                .map(user -> {
                    user.setAccount(newUser.getAccount());
                    user.setEmail(newUser.getEmail());
                    user.setUsername(newUser.getUsername());
                    user.setPassword(newUser.getPassword());
                    return this.userRepository.save(user);
                })
                .orElseGet(()->{
                   newUser.setId(id);
                   return this.userRepository.save(newUser);
                });
    }

    @DeleteMapping("user/{id}")
    public ResponseEntity<Void> removeUser(@PathVariable(value = "id") Long id){
        User user =this.userRepository.findById(id).orElseThrow(
                ()-> new ResourceNotFoundException("User not found"+id)
        );

        this.userRepository.delete(user);
        return ResponseEntity.ok().build();
    }


    @PostMapping("/handle_register")
    public User handle_register(@RequestBody User olduser){
        User user = this.userRepository.findByaccount(olduser.getAccount());
        if (user == null){
            return this.userRepository.save(olduser);
        } else {
            return null;
        }
        
    }

    @PostMapping("/handle_log")
    public boolean getSingleUser(@RequestBody LogRequest logrequest){
        User user = this.userRepository.findByaccount(logrequest.account);
        if (user == null){
            System.out.println("无此account");
            return false;
        }
        String true_password = user.getPassword();
        System.out.println(true_password);
        if (true_password.equals(logrequest.password))
            return true;
        else
            return false;
    }

    @GetMapping("/getname/{acc}")
    public String getName(@PathVariable(value = "acc") String acc){
        String name =this.userRepository.getNamewithAcc(acc);
        if (!name.equals("")){
            return name;
        } else {
            return "";
        }
    }

}