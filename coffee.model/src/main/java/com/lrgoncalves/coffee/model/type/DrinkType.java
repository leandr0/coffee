package com.lrgoncalves.coffee.model.type;

public enum DrinkType {
    
    ESPRESSO(1.5),
    
    LATTE(2.0),
    
    CAPPUCCINO(2.0),
    
    FLAT_WHITE(2.0);

    final double price;
    DrinkType(double price) { this.price = price; }
    public double getPrice() { return this.price; }
 }
