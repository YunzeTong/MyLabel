package RedCloudRule.bs;

import RedCloudRule.bs.models.User;
import RedCloudRule.bs.repositories.UserRepository;

import org.junit.jupiter.api.Test;

import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class UserTest {
    
    @Autowired
    private UserRepository userRepository;

    @Test
    public void testSaveUser(){
        User user = new User("12580","john.doe@email.com","tyz","strong-password");
        userRepository.save(user);
        userRepository.findById(Long.valueOf(1))
                .map(newUser ->{
                    Assert.assertEquals("12580",newUser.getAccount());
                    return true;
                });
    }

    @Test
    public void getUser(){
        User user = new User("12580","john.doe@email.com","johhny","strong-password");
        User user2 = new User("155550","daniel@daniel.com","danie","super_strong_password");
        userRepository.save(user);

        userRepository.save(user2);

        userRepository.findById(Long.valueOf(1))
                .map(newUser ->{
                   Assert.assertEquals("155550",newUser.getUsername());
                   return true;
                });

    }

    @Test
    public void getUsers(){
        User user = new User("12580","john.doe@email.com","johhny","strong-password");
        User user2 = new User("1557","daniel@daniel.com","danie","super_strong_password");
        userRepository.save(user);
        userRepository.save(user2);

        Assert.assertNotNull(userRepository.findAll());
    }

    @Test
    public void deleteUser(){
        User user = new User("154836","john.doe@email.com","johhny","strong-password");
        userRepository.save(user);
        userRepository.delete(user);
        Assert.assertTrue(userRepository.findAll().isEmpty());
    }
}
