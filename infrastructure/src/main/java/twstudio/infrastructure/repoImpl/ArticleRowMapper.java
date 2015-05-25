package twstudio.infrastructure.repoImpl;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;
import twstudio.domain.Article;

public class ArticleRowMapper implements RowMapper<Article> {

    @Override
    public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
        Article article = new Article();
        article.setId(rs.getInt(1));
        article.setTopicId(rs.getInt(2));
        article.setName(rs.getString(3));
        article.setDisplayOrder(rs.getInt(4));
        return article;
    }

}