package vttp.batch5.csf.assessment.server.controllers;

import java.io.StringReader;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp.batch5.csf.assessment.server.services.RestaurantService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;



@RestController
@RequestMapping("/api")
public class RestaurantController {

  @Autowired
  private RestaurantService restaurantService;

  // TODO: Task 2.2
  // You may change the method's signature
  @GetMapping("/menu")
  public ResponseEntity<String> getMenus()
  {
    try {
      JsonArray jMenusArray = restaurantService.getMenu();
      System.out.println("jMenusArray: " + jMenusArray.toString());

      return ResponseEntity.ok(jMenusArray.toString());
      
    } catch (Exception e) {
      System.out.println(">>> error: " + e.getMessage());
      
      if(e.getMessage().contains("Invalid username or password"))
      {
        return ResponseEntity.badRequest().body(Json.createObjectBuilder().add("message", e.getMessage()).build().toString());
      }
      
      return ResponseEntity.internalServerError().body(Json.createObjectBuilder().add("message", e.getMessage()).build().toString());
    }
    
  }

  // TODO: Task 4
  // Do not change the method's signature
  @PostMapping("/food_order")
  public ResponseEntity<String> postFoodOrder(@RequestBody String jOrderPayload) 
  {
    try {
      JsonReader jReader = Json.createReader(new StringReader(jOrderPayload));
      JsonObject jOrder = jReader.readObject();

      System.out.println("JOrder: " + jOrder.toString());

      JsonObject response = restaurantService.processOrder(jOrder);

      return ResponseEntity.ok(response.toString());
      
    } catch (Exception e) {
      System.out.println(">>> error: " + e.getMessage());
      return ResponseEntity.status(401).body(Json.createObjectBuilder().add("message", e.getMessage()).build().toString());
    }
  }
}