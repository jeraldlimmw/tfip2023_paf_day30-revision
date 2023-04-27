package ibf2022.paf.assessment.server.services;

import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ibf2022.paf.assessment.server.models.Task;
import ibf2022.paf.assessment.server.models.User;
import ibf2022.paf.assessment.server.repositories.TaskRepository;
import ibf2022.paf.assessment.server.repositories.UserRepository;

// TODO: Task 7
@Service
public class TodoService {

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private TaskRepository tRepo;

    public Optional<User> findUserByUsername(String username) {
        return uRepo.findUserByUsername(username);
    }

    @Transactional (rollbackFor = Exception.class)
    public int upsertTask(Task task, String username) throws Exception{
        Integer rowsAdded = null;
        String userId = null;
        Optional<User> existingUser = uRepo.findUserByUsername(username);
        
        if (Objects.isNull(existingUser)) {
            User newUser = new User();
            newUser.setUsername(username);
            userId = uRepo.insertUser(newUser);
            System.out.println(">>>> User " + username + " created - user_id = " + newUser.getUserId());
        } else {
            userId = existingUser.get().getUserId();
            System.out.println(">>>> User " + username + " exists - user_id = " + userId);
        }
        System.out.println(">>>> INSERTING TASK");
        task.setUserId(userId);
        rowsAdded = tRepo.insertTask(task);
        System.out.println(">>>> TASK INSERTED");

        return rowsAdded;
    }

}
