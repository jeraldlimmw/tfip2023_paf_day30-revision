package ibf2022.paf.assessment.server.repositories;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import ibf2022.paf.assessment.server.models.User;

import static ibf2022.paf.assessment.server.repositories.DBQueries.*;

// TODO: Task 3
@Repository
public class UserRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public Optional<User> findUserByUsername(String username) {
        SqlRowSet rs = jdbcTemplate.queryForRowSet(SELECT_USER_BY_ID, username);
        
        if(rs.first()) {
            return Optional.of(User.create(rs));
        } 
        return null;
    }

    public String insertUser(User user) {
        // KeyHolder keyHolder = new GeneratedKeyHolder();

        String userId = null;
        Optional<User> existingUser = findUserByUsername(user.getUsername());

        if(Objects.isNull(existingUser)) {
            // generate random user id
            userId = UUID.randomUUID().toString().substring(0, 8);
            System.out.println(">>>> UserId Generated: " + userId);
            user.setUserId(userId);

            jdbcTemplate.update(INSERT_USER, user.getUserId(), user.getUsername(), user.getName());

            // jdbcTemplate.update(conn -> {
            //     PreparedStatement statement = conn.prepareStatement
            //     (INSERT_USER, Statement.RETURN_GENERATED_KEYS);
            //     statement.setString(1, user.getUserId());
            //     statement.setString(2, user.getUsername());
            //     statement.setString(3, user.getName());
            //     return statement;
            // }, keyHolder);
        }

        return userId;
    }

}
