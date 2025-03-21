package vttp.batch5.csf.assessment.server.repositories;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

// Use the following class for MySQL database
@Repository
public class RestaurantRepository 
{
    @Autowired
    private JdbcTemplate tempalte;

    public static final String SQL_AUTH_USER = "SELECT COUNT(*) FROM customers WHERE username = ? AND password = sha2(?, 224)";

    public boolean isAuthenticated(String username, String password)
    {
        Integer count = tempalte.queryForObject(SQL_AUTH_USER, Integer.class, username, password);

        if(count != null && count > 0)
        {
            return true;
        }

        return false;
    }

    private static final String SQL_SAVE_ORDER = """
            INSERT INTO place_orders 
                (order_id, payment_id, order_date, total, username)
            VALUES
                (?, ?, ?, ?, ?)
            """;
    public void saveOrder(String orderId, String paymentId, long timestamp, Double total, String username)
    {
        tempalte.update(SQL_SAVE_ORDER, orderId, paymentId, new Date(timestamp), total, username);
    }
}
  