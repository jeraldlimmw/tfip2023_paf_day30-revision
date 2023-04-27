package ibf2022.paf.assessment.server.repositories;

public class DBQueries {
    public static final String SELECT_USER_BY_ID = """
            select * from user where username like ?
            """;

    public static final String INSERT_USER= """
            insert into user (user_id, username, name)
            values (?, ?, ?)
            """;

    public static final String INSERT_TASK = """
            insert into task (description, priority, due_date, user_id)
            values (?, ?, ?, ?)
            """;
}
