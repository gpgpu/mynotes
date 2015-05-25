package twstudio.domain;

/**
 * Created by Tao on 5/23/2015.
 */
import java.sql.SQLException;
import java.util.List;

public interface ArticleRepo {
    List<Article> getArticles();
    Article getArticle (int id);
    int add(Article article) throws SQLException;
    void updateTitle(Article article);
    void saveContent(Article article);
    void changeParent(Article article);
}
