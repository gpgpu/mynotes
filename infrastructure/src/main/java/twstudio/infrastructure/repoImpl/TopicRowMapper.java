package twstudio.infrastructure.repoImpl;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import twstudio.domain.Topic;

@Component
public class TopicRowMapper implements RowMapper<Topic> {
    public Topic mapRow(ResultSet resultSet, int rowNum) throws SQLException{
        Topic t = new Topic();
        t.setId(resultSet.getInt(1));
        t.setName(resultSet.getString(2));
        t.setPortalId(resultSet.getInt(3));
        t.setParentTopicId(resultSet.getInt(4));
        t.setDisplayOrder(resultSet.getInt(5));
        return t;
    }
}