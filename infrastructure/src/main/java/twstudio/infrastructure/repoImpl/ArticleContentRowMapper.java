package twstudio.infrastructure.repoImpl;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;
import twstudio.domain.Article;

public class ArticleContentRowMapper implements RowMapper<Article> {

    @Override
    public Article mapRow(ResultSet rs, int rowNum) throws SQLException {
        Article article = new Article();
        article.setId(rs.getInt(1));
        article.setName(rs.getString(2));
        article.setContent(rs.getString(3));
        return article;
    }
}