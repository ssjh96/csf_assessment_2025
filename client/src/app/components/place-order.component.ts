import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Observable } from 'rxjs';
import { LineItem, Order } from '../models';
import { Router } from '@angular/router';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrl: './place-order.component.css'
})
export class PlaceOrderComponent implements OnInit
{
  // TODO: Task 3
  private restaurantSvc = inject(RestaurantService)
  private fb = inject(FormBuilder)
  private router = inject(Router)

  orderForm!: FormGroup;
  order!: Order
  totalObs$: Observable<number> = this.restaurantSvc.totalObs$;
  currentCart$: Observable<LineItem[]> = this.restaurantSvc.cartObs$
  
  ngOnInit(): void {
    this.orderForm = this.createForm();
  }

  private createForm(): FormGroup
  {
    return this.fb.group({
      username: this.fb.control<string>('', [Validators.required]),
      password: this.fb.control<string>('', [Validators.required])
    })
  }

  protected submitOrder()
  {
    const username = this.orderForm.value.username
    const password = this.orderForm.value.password

    console.log("Username: ", username, " Password: ", password);

    this.restaurantSvc.submitOrder(username, password).subscribe({
      next: (resp) => {
        console.log("resp: ", resp)
        localStorage.setItem('orderDetails', JSON.stringify(resp))
        alert('Successfully placed order!')

        this.restaurantSvc.clearCart();
        this.router.navigate(['/confirm'])
      },
      error: (err) => {
        alert(err.message)
      }
    })
  }

  protected startOver()
  {
    this.restaurantSvc.clearCart();
    this.router.navigate(['/'])
  }

  // Validations
  protected touchedAndInvalid(ctrlName: string): boolean
  {
    const ctrl = this.orderForm.get(ctrlName) as FormControl
    return ctrl.touched && ctrl.invalid
  }

  protected isValid(ctrlName: string): boolean
  {
    return !!this.orderForm.get(ctrlName)?.valid
  }


}
