import { Component, inject, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { OrderDetails } from '../models';

@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent implements OnInit
{
  // TODO: Task 5
  private router = inject(Router);
  orderDetails!: OrderDetails

  ngOnInit(): void {
    const jOrderDetails = localStorage.getItem('orderDetails')
    if (jOrderDetails)
    {
      this.orderDetails = JSON.parse(jOrderDetails);
      console.log("Confirmation page orderDetails: ", this.orderDetails)
    }
  }

}