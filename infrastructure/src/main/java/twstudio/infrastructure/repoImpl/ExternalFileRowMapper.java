package twstudio.infrastructure.repoImpl;

import java.sql.SQLException;
import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;
import twstudio.domain.ExternalFile;


public class ExternalFileRowMapper implements RowMapper<ExternalFile> {

    @Override
    public ExternalFile mapRow(ResultSet rs, int rowNum) throws SQLException {
        ExternalFile theFile = new ExternalFile();
        theFile.setId(rs.getInt(1));
        theFile.setMimeType(rs.getString(2));
        theFile.setContent(rs.getBytes(3));
        return theFile;
    }

}