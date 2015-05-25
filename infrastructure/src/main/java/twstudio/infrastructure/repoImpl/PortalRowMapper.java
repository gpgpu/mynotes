package twstudio.infrastructure.repoImpl;

/**
 * Created by Tao on 5/22/2015.
 */
import java.sql.SQLException;
import java.sql.ResultSet;

import org.springframework.jdbc.core.RowMapper;
import twstudio.domain.Portal;

public class PortalRowMapper implements RowMapper<Portal> {

    @Override
    public Portal mapRow(ResultSet rs, int rowNum) throws SQLException {
        Portal p = new Portal();
        p.setId(rs.getInt(1));
        p.setName(rs.getString(2));
        p.setDisplayOrder(rs.getInt(3));
        return p;
    }
}
