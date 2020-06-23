package fi.solita.transactiondemo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Remove isolation part when you want this to work as expected :)
@Transactional(isolation = Isolation.READ_UNCOMMITTED)
//@Transactional
@RestController
public class TransactionDemoEndpoint {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public TransactionDemoEndpoint(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    /**
     * Note that since id is hardcoded to 1 this can be run only once successfully,
     * succeeding runs will fail on second operation
     *
     * Note that since we're using in-memory h2 sql database, it resets when app is restarted
     *
     * @return
     */
    // TODO: Enable transactions when you want this to work as expected ;)
//    @Transactional
    @RequestMapping(value="/testrollback", method= RequestMethod.GET)
    @ResponseBody
    public Map<String,String> testTransactionRollback() {
        System.out.println("Updating the product quantity in db");
        jdbcTemplate.update("UPDATE product SET quantity=quantity-1 WHERE id=1");
        System.out.println("Attempting to create a new order line");
        jdbcTemplate.update("INSERT INTO order_line VALUES (1,1,1)");
        return Collections.singletonMap("response","ok");
    }

    @RequestMapping(value="/listcontents", method= RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, List<Map<String,Object>>> listContents() {
        var result = new HashMap<String,List<Map<String,Object>>>();
        result.put("products",jdbcTemplate.queryForList("SELECT * FROM product"));
        result.put("orders",jdbcTemplate.queryForList("SELECT * FROM order_line"));
        return result;
    }

    @Transactional
    @RequestMapping(value="/slowtransaction", method= RequestMethod.GET)
    @ResponseBody
    public Map<String,String> testSlowTransaction() throws InterruptedException {
        jdbcTemplate.update("UPDATE product SET quantity=quantity-1 WHERE id=1");
        System.out.println("Updated products quantity, waiting to insert order to db");
        Thread.sleep(30000);
        jdbcTemplate.update("INSERT INTO order_line VALUES (1,1,1)");
        System.out.println("Okay, created new orderline too");
        return Collections.singletonMap("response","ok");
    }

    // TODO: Test this by first triggering this operation, then running the transactional operation, then observing the results
    @RequestMapping(value="/phantomread", method= RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Map<String, List<Map<String,Object>>> phantomRead() throws InterruptedException {
        var result = new HashMap<String,List<Map<String,Object>>>();
        result.put("products_before",jdbcTemplate.queryForList("SELECT * FROM product"));
        result.put("orders_before",jdbcTemplate.queryForList("SELECT * FROM order_line"));
        Thread.sleep(30000);
        result.put("products_after",jdbcTemplate.queryForList("SELECT * FROM product"));
        result.put("orders_after",jdbcTemplate.queryForList("SELECT * FROM order_line"));
        return result;
    }
}
