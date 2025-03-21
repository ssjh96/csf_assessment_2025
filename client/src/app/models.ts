// You may use this file to create any models
export interface MenuItem {
    id: string;
    name: string;
    price: number;
    description: string;
}

export interface LineItem {
    id: string,
    name: string,
    quantity: number
    price: number
  }
    
  export interface Order {
    username: string
    password: string
    items: LineItem[]
  }

  export interface OrderDetails {
    order_id: string,
    payment_id: string,
    timestamp: number,
    total: number
  }

