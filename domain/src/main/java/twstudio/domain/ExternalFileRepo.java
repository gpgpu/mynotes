package twstudio.domain;

/**
 * Created by Tao on 5/23/2015.
 */
import java.sql.SQLException;

public interface ExternalFileRepo {
    ExternalFile getFile(int id);
    int saveFile(ExternalFile theFile) throws SQLException;
}