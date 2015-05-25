package twstudio.infrastructure.repoImpl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.*;
import java.sql.*;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import twstudio.domain.Topic;
import twstudio.domain.TopicRepo;;

public class TopicRepoImpl implements TopicRepo {

    private NamedParameterJdbcOperations jdbcTemplate;
    private DataSource dataSource;
    private TopicRowMapper topicRowMapper = new TopicRowMapper();

    public void setJdbcTemplate(NamedParameterJdbcOperations jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public void setDatasource(DataSource datasource){
        this.dataSource = datasource;
    }

    @Override
    public List<Topic> getTopics() {
        return jdbcTemplate.query("SELECT id, name, portal_id, parent_topic_id, display_order FROM topics ORDER BY display_order;", topicRowMapper);
    }

    @Override
    public void updateTitle(Topic topic) {
        String sql = "UPDATE topics SET name=:name WHERE id=:id";

        Map<String, Object> paramMap = new HashMap<String, Object>();
        paramMap.put("name", topic.getName());
        paramMap.put("id", Integer.valueOf(topic.getId()));

        jdbcTemplate.update(sql, paramMap);
    }

    @Override
    public int add(Topic topic) throws SQLException {

        String sql = "{? = call new_topic(?, ?, ?, ?)}";
        Connection conn = dataSource.getConnection();
        CallableStatement statement = conn.prepareCall(sql);
        statement.registerOutParameter(1, Types.INTEGER);
        statement.setString(2, topic.getName());
        statement.setInt(3, topic.getParentTopicId());
        statement.setInt(4, topic.getPortalId());
        statement.setInt(5, topic.getDisplayOrder());

        statement.execute();
        int newId = statement.getInt(1);
        statement.close();
        conn.close();
        return newId;
    }

    @Override
    public void changeParent(Topic topic) {
        String sql = "UPDATE topics SET parent_topic_id=:parentId, portal_id=:portalId, display_order=:displayOrder WHERE id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("parentId", topic.getParentTopicId())
                .addValue("portalId", topic.getPortalId())
                .addValue("displayOrder", topic.getDisplayOrder())
                .addValue("id", topic.getId());

        jdbcTemplate.update(sql,  param);

    }

}
