import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { Observable } from 'rxjs';
import { MenuItem } from '../models';


@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit
{
  private restaurantSvc = inject(RestaurantService);

  menuList$!: Observable<MenuItem[]>;
  totalObs$: Observable<number> = this.restaurantSvc.totalObs$;
  // currentTotal!: number;


  ngOnInit(): void {
    // this.restaurantSvc.getMenuItems().subscribe((data) => console.log("data: ", data));

    this.menuList$ = this.restaurantSvc.getMenuItems();
    // this.currentTotal = this.getTotalInCart();
  }

  protected addToCart(menuItem: MenuItem): void
  {
    this.restaurantSvc.addToCart(menuItem)
  }

  protected removeFromCart(menuItemId: string): void
  {
    this.restaurantSvc.removeFromCart(menuItemId);
  }

  protected getItemQuantity(menuItemId: string): number
  {
    return this.restaurantSvc.getLineItemQuantity(menuItemId);
  }

  protected getTotalInCart(): number
  {
    return this.restaurantSvc.getTotalItemsInCart();
  }

}



