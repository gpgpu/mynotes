package twstudio.domain;

/**
 * Created by Tao on 5/22/2015.
 */

import java.sql.SQLException;
import java.util.List;
import twstudio.viewmodel.DisplayOrderViewModel;

public interface PortalRepo {
    List<Portal> getPortals();
    void saveDisplayOrders(List<DisplayOrderViewModel> childItems) throws SQLException;
}
