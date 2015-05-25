package twstudio.infrastructure.repoImpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import twstudio.domain.Portal;
import twstudio.domain.PortalRepo;
import twstudio.viewmodel.DisplayOrderViewModel;

import javax.sql.*;

import org.springframework.transaction.annotation.Transactional;


public class PortalRepoImpl implements PortalRepo {
    private NamedParameterJdbcOperations jdbcTemplate;
    private PortalRowMapper portalRowMapper = new PortalRowMapper();

    public void setJdbcTemplate(NamedParameterJdbcOperations jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Portal> getPortals() {
        return jdbcTemplate.query("SELECT id, name, display_order FROM portals order by display_order", portalRowMapper);
    }

    @Override
    @Transactional
    public void saveDisplayOrders(List<DisplayOrderViewModel> childItems)
            throws SQLException {
        String topicSql = "UPDATE topics SET display_order=:order WHERE id=:id";
        String articleSql = "UPDATE articles SET display_order=:order WHERE id=:id";

        ArrayList<Map<String, Object>> topicPara = new ArrayList<Map<String, Object>>();
        ArrayList<Map<String, Object>> articlePara = new ArrayList<Map<String, Object>>();

        Integer index = 1;
        for (DisplayOrderViewModel item : childItems){
            if (item.isTopic){
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("order", index);
                hm.put("id", Integer.valueOf(item.id));

                topicPara.add(hm);
            }
            else{
                HashMap<String, Object> hm = new HashMap<String, Object>();
                hm.put("order", index);
                hm.put("id", Integer.valueOf(item.id));

                articlePara.add(hm);
            }
            index = index + 1;
        }

        Map<String, Object>[] topicParaArray = (Map<String, Object>[]) topicPara.toArray(new Map[topicPara.size()]);
        Map<String, Object>[] articleParaArray = (Map<String, Object>[]) articlePara.toArray(new Map[articlePara.size()]);
        jdbcTemplate.batchUpdate(topicSql, topicParaArray);
        jdbcTemplate.batchUpdate(articleSql, articleParaArray);

    }

}
