package vttp.batch5.csf.assessment.server.services;


import java.io.StringReader;
import java.util.List;
import java.util.UUID;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonObjectBuilder;
import jakarta.json.JsonReader;
import vttp.batch5.csf.assessment.server.repositories.OrdersRepository;
import vttp.batch5.csf.assessment.server.repositories.RestaurantRepository;

@Service
public class RestaurantService 
{
  @Autowired
  private OrdersRepository ordersRepository;

  @Autowired 
  private RestaurantRepository restaurantRepository;

  // TODO: Task 2.2
  // You may change the method's signature
  public JsonArray getMenu() 
  {
    List<Document> menuListDoc = ordersRepository.getMenu();
    JsonArrayBuilder jab = Json.createArrayBuilder();

    for (Document d: menuListDoc)
    {
      JsonObjectBuilder job = Json.createObjectBuilder();
      job.add("id", d.getString("_id"))
          .add("name", d.getString("name"))
          .add("price", d.getDouble("price"))
          .add("description", d.getString("description"));
      
      jab.add(job);
    }

    JsonArray jMenusArray = jab.build();

    return jMenusArray;
  } 
  
  // TODO: Task 4
  public JsonObject processOrder(JsonObject jOrder) throws Exception
  {
    String username = jOrder.getString("username");
    String password = jOrder.getString("password");
    JsonArray jItemsArray = jOrder.getJsonArray("items");

    // check if username password correct
    if(!restaurantRepository.isAuthenticated(username, password))
    {
      throw new Exception("Invalid username or password");
    }

    // gen random 8 char orderId
    String orderId = UUID.randomUUID().toString().replace("-", "").substring(0, 8);

    // calculate total
    Double total = 0.0;
    for(int i = 0; i < jItemsArray.size(); i++)
    {
      JsonObject jItem = jItemsArray.getJsonObject(i);
      Double price = jItem.getJsonNumber("price").doubleValue();
      int quantity = jItem.getInt("quantity");

      total += (price * quantity);
    }

    JsonObject paymentResp = makePayment(orderId, total, username);
    System.out.println("paymentResp: " + paymentResp.toString());

    String paymentId = paymentResp.getString("payment_id");
    long timestamp = paymentResp.getJsonNumber("timestamp").longValue();

    // Save to sql order_id, payment_id, order_date, total, username 
    restaurantRepository.saveOrder(orderId, paymentId, timestamp, total, username);

    // Save to mongo 
    ordersRepository.saveOrder(orderId, username, total, timestamp, jItemsArray);

    return paymentResp;
  }


  
  public JsonObject makePayment(String orderId, Double total, String username)
  {
    String url = "https://payment-service-production-a75a.up.railway.app/api/payment";

    JsonObject jRequestBody = Json.createObjectBuilder()
                              .add("order_id", orderId)
                              .add("payer", username)
                              .add("payee", "SHAMUS SWEE JUNHUI")
                              .add("payment", total)
                              .build();

    RestTemplate restTemplate = new RestTemplate();

    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Type", "application/json");
    headers.add("Accept", "application/json");
    headers.add("X-Authenticate", username);

    RequestEntity<String> req = RequestEntity.post(url).headers(headers).body(jRequestBody.toString());

    try {
      ResponseEntity<String> resp = restTemplate.exchange(req, String.class);

      JsonReader jReader = Json.createReader(new StringReader(resp.getBody()));
      JsonObject jResp = jReader.readObject();

      return jResp;
    } catch (Exception e) {
      System.out.println("Error: " + e.getMessage());
      return Json.createObjectBuilder().add("error", e.getMessage()).build();
    }
         
  }

}