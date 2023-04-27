package ibf2022.paf.assessment.server.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import ibf2022.paf.assessment.server.models.Task;
import static ibf2022.paf.assessment.server.repositories.DBQueries.*;

import java.sql.Timestamp;

// TODO: Task 6
@Repository
public class TaskRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int insertTask(Task task) {
        Timestamp date = new Timestamp(task.getDate().toDateTime().getMillis());
        return jdbcTemplate.update(INSERT_TASK, task.getDescription(), 
                task.getPriority(), date, task.getUserId());
    }

}
