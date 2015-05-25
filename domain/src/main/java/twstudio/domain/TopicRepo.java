package twstudio.domain;

/**
 * Created by Tao on 5/23/2015.
 */
import java.sql.SQLException;
import java.util.List;

public interface TopicRepo {
    List<Topic> getTopics();
    int add(Topic topic) throws SQLException;
    void updateTitle(Topic topic);
    void changeParent(Topic topic);
}