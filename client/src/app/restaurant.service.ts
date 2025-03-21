import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { BehaviorSubject, catchError, Observable } from "rxjs";
import { LineItem, MenuItem, Order } from "./models";

@Injectable({    
  providedIn: 'root'
})

export class RestaurantService {

  private http = inject(HttpClient)

  private cart: LineItem[] = [];

  private cartSubject = new BehaviorSubject<LineItem[]>([]);
  public cartObs$: Observable<LineItem[]> = this.cartSubject.asObservable();

  private totalSubject = new BehaviorSubject<number>(0);
  public totalObs$: Observable<number> = this.totalSubject.asObservable();

  // TODO: Task 2.2
  // You change the method's signature but not the name

  public getMenuItems() : Observable<any> 
  {
    return this.http.get<any>('/api/menu').pipe(
      catchError((error) => {
        if(error.status === 400) // Bad request
        {
          throw new Error(error.error.message)
        }
        throw new Error("Unexpected error occurred. Please try again.");
      })
    )
  }

  public addToCart(menuItem: MenuItem): void
  {
    // find if selected menuItem already in cart 
    const existingItem = this.cart.find(item => item.id === menuItem.id)

    if(existingItem)
    {
      existingItem.quantity += 1;
    }
    else // item no in cart, push 1 more
    {
      this.cart.push({
        id: menuItem.id,
        name: menuItem.name,
        price: menuItem.price,
        quantity: 1
      });
    }

    this.updateCartStatus();
  }

  public removeFromCart(menuItemId: string): void
  {
    const itemIdx = this.cart.findIndex(item => item.id === menuItemId)

    if(this.cart[itemIdx].quantity > 1)
    {
      this.cart[itemIdx].quantity -=1
    }
    else
    {
      this.cart.splice(itemIdx, 1);
    }

    this.updateCartStatus();
  }



  public getLineItemQuantity(menuItemId: string): number
  {
    const item = this.cart.find(item => item.id === menuItemId);

    if(!item)
    {
      return 0;
    }

    return item.quantity;
  }



  public getTotalItemsInCart(): number
  {
    return this.cart.reduce((count, item) => count + item.quantity, 0)
  }


  
  private updateCartStatus(): void 
  {
    this.cartSubject.next([...this.cart])

    // let total = [0, 1, 2, 3].reduce((accumulator, currentValue) => accumulator + currentValue);
    // console.log(total); 

    this.totalSubject.next(
      this.cart.reduce((total, item) => total + (item.price * item.quantity), 0)
    )
  }



  // TODO: Task 3.2
  public submitOrder(username: string, password: string): Observable<any>
  {
    const order: Order = {
      username,
      password,
      items: this.cart
    }

    console.log("order: ", order)

    return this.http.post<any>('/api/food_order', order).pipe(
      catchError((error) => {
        if(error.status === 401) // Invalid username or password
        {
          throw new Error(error.error.message)
        }
        throw new Error("Unexpected error occurred. Please try again.");
      })
    )
  }
  
  // clear cart after order success
  public clearCart(): void {
    this.cart = [];
    this.updateCartStatus();
  }




}