package vttp.batch5.csf.assessment.server.repositories;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import jakarta.json.JsonArray;
import jakarta.json.JsonObject;


@Repository
public class OrdersRepository 
{

  @Autowired
  private MongoTemplate template;

  private static final String C_MENUS = "menus";
  private static final String C_ORDERS = "orders";
  // TODO: Task 2.2
  // You may change the method's signature
  // Write the native MongoDB query in the comment below
  //
  //  Native MongoDB query here
  // db.menus.find()
  //          .sort({name:1})
  public List<Document> getMenu() 
  {
    Query query = new Query();
    query.with(Sort.by(Sort.Direction.ASC, "name"));
    return template.find(query, Document.class, C_MENUS);
  }

  // TODO: Task 4
  // Write the native MongoDB query for your access methods in the comment below
  //
  //  Native MongoDB query here
  /* db.orders.insertOne(
		{
      _id:"orderId",         /
      order_id: "orderId",   // orderId
      username: "username",       // username
      total: 123.99,          // total
      timestamp: <in Date>,
      items: [
        {
          id: "xxx",      
          price: 1.99,       
          quantity: 2         
        },
        {doc},
        {doc},
        ...
      ]
    })  */

    public void saveOrder(String orderId, String username, Double total, long timestamp, JsonArray items)
    {
      List<Document> itemsListDoc = new ArrayList<>();
      for(int i = 0; i < items.size(); i++)
      {
        JsonObject item = items.getJsonObject(i);
        Document itemDoc = new Document().append("id", item.getString("id"))
                                          .append("price", item.getJsonNumber("price").doubleValue())
                                          .append("quantity", item.getInt("quantity"));
                                          
        itemsListDoc.add(itemDoc);
      }

      Document orderDoc = new Document().append("_id", orderId)
                                        .append("order_id", orderId)
                                        .append("username", username)
                                        .append("total", total)
                                        .append("timestamp", new Date(timestamp))
                                        .append("items", itemsListDoc);
      
      Document d = template.insert(orderDoc, C_ORDERS);
      System.out.println("inserted: " + d.toJson().toString());
    }
  
}
