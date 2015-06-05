package twstudio.infrastructure.repoImpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import javax.sql.DataSource;

import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import twstudio.domain.ArticleRepo;
import twstudio.domain.Article;

public class ArticleRepoImpl implements ArticleRepo {

    private NamedParameterJdbcOperations jdbcTemplate;
    private DataSource dataSource;
    private ArticleRowMapper articleRowMapper = new ArticleRowMapper();
    private ArticleContentRowMapper articleContentRowMapper = new ArticleContentRowMapper();

    public void setJdbcTemplate(NamedParameterJdbcOperations jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public void setDatasource(DataSource datasource){
        this.dataSource = datasource;
    }

    @Override
    public List<Article> getArticles() {
        return jdbcTemplate.query("SELECT id, topic_id, name, display_order FROM articles ORDER BY display_order;", articleRowMapper);
    }

    @Override
    public Article getArticle(int id) {
        String sql = "SELECT articles.id, articles.name, article_contents.article_content, modified_on FROM articles join article_contents ON articles.id=article_contents.id WHERE articles.id=:id";
        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", id);
        return jdbcTemplate.queryForObject(sql, param, articleContentRowMapper);

    }

    @Override
    public int add(Article article) throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "{? = call new_article(?, ?, ?)}";
        CallableStatement statement = conn.prepareCall(sql);
        statement.registerOutParameter(1, Types.INTEGER);
        statement.setString(2, article.getName());
        statement.setInt(3, article.getTopicId());
        statement.setInt(4, article.getDisplayOrder());


        statement.execute();
        int newId = statement.getInt(1);
        statement.close();
        conn.close();
        return newId;
    }

    @Override
    public void updateTitle(Article article) {
        String sql = "UPDATE articles SET name=:name WHERE id=:id";
        SqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("name", article.getName())
                .addValue("id", article.getId());

        jdbcTemplate.update(sql, paramSource);
    }

    @Override
    public void saveContent(Article article) {
        String sql = "UPDATE article_contents SET article_content=:content, modified_on=:modified_on WHERE id=:id";
        SqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("content", article.getContent())
                .addValue("modified_on", article.getModifiedOn())
                .addValue("id", article.getId());

        jdbcTemplate.update(sql, paramSource);
    }

    @Override
    public void changeParent(Article article) {
        String sql = "UPDATE articles SET topic_id=:topicId, display_order=:displayOrder WHERE id=:id;";

        SqlParameterSource paramSource = new MapSqlParameterSource()
                .addValue("topicId", article.getTopicId())
                .addValue("displayOrder", article.getDisplayOrder())
                .addValue("id", article.getId());
        jdbcTemplate.update(sql, paramSource);
    }
}
