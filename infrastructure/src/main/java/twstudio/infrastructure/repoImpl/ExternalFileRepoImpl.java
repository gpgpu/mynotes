package twstudio.infrastructure.repoImpl;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import javax.sql.*;
import java.sql.*;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import twstudio.domain.ExternalFileRepo;
import twstudio.domain.ExternalFile;

public class ExternalFileRepoImpl implements ExternalFileRepo {
    private NamedParameterJdbcOperations jdbcTemplate;
    private DataSource dataSource;
    private ExternalFileRowMapper externalFileRowMapper = new ExternalFileRowMapper();

    public void setJdbcTemplate(NamedParameterJdbcOperations jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }
    public void setDatasource(DataSource datasource){
        this.dataSource = datasource;
    }

    @Override
    public ExternalFile getFile(int id) {
        String sql = "SELECT id, mimetype,  file_content FROM external_files WHERE id=:id";

        SqlParameterSource param = new MapSqlParameterSource()
                .addValue("id", id);

        return jdbcTemplate.queryForObject(sql, param, externalFileRowMapper);
    }

    @Override
    public int saveFile(ExternalFile theFile) throws SQLException {
        Connection conn = dataSource.getConnection();

        String sql = "{? = call new_file(?, ?, ?, ?)}";
        CallableStatement statement = conn.prepareCall(sql);
        statement.registerOutParameter(1, Types.INTEGER);
        statement.setString(2, theFile.getFileName());
        statement.setString(3, theFile.getMimeType());
        statement.setInt(4, theFile.getArticleId());
        statement.setBytes(5,  theFile.getContent());

        statement.execute();
        int newId = statement.getInt(1);
        statement.close();
        conn.close();
        return newId;
    }
}