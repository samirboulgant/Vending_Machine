package com.model;

import com.enums.CoinsEnum;
import com.enums.ProductsEnum;
import com.exceptions.InsuffisantCoinsInBalanceException;
import com.exceptions.InsuffisantPriceException;
import com.exceptions.InsuffisantStockException;

import java.util.HashMap;

public class User {
    private HashMap<CoinsEnum,Integer> inputPrice;
    private ProductsEnum refund;
    private ProductsEnum productToBuy;
    private boolean isProductVended=false;

    public User(HashMap<CoinsEnum,Integer> inputPrice, ProductsEnum productToBuy) {
        this.inputPrice = inputPrice;
        this.productToBuy = productToBuy;
    }

    public User(HashMap<CoinsEnum,Integer> inputPrice, ProductsEnum refund, ProductsEnum productToBuy) {
        this.inputPrice = inputPrice;
        this.refund = refund;
        this.productToBuy = productToBuy;
    }

    public boolean isProductVended() {
        return isProductVended;
    }

    public HashMap<CoinsEnum,Integer> getInputPrice() {
        return inputPrice;
    }

    public ProductsEnum getRefund() {
        return refund;
    }

    public void setInputPrice(HashMap<CoinsEnum,Integer> inputPrice) {
        this.inputPrice = inputPrice;
    }

    public void setRefund(ProductsEnum refund) {
        this.refund = refund;
    }

    public ProductsEnum getProductToBuy() {
        return productToBuy;
    }

    public void setProductToBuy(ProductsEnum productToBuy) {
        this.productToBuy = productToBuy;
    }

    public void getRefund(VendingMachine machine){
        machine.decreaseBalance(this.inputPrice);
        this.isProductVended=false;
    }
    public HashMap<CoinsEnum,Integer> exchangeMoney(VendingMachine machine) throws InsuffisantPriceException, InsuffisantCoinsInBalanceException {
        double total = CalculateSumOfCoins();
        double rest;
        int numberOfCoin;
        rest = total-productToBuy.getPrice() ;
        HashMap<CoinsEnum,Integer> exchangeCoin = new HashMap<>();
            for (CoinsEnum coinsEnum : CoinsEnum.values()) {
                double check=rest;
                if(rest>=coinsEnum.getPrice()){
                    numberOfCoin = (int) Math.floor(rest/coinsEnum.getPrice());
                    rest = (int) (rest%coinsEnum.getPrice());
                    if(machine.getBalance().get(coinsEnum) < numberOfCoin && coinsEnum.getPrice()>1)  rest =check;
                    else if(machine.getBalance().get(coinsEnum) < numberOfCoin ){
                        throw new InsuffisantCoinsInBalanceException("Sorry insufficient coins in our balance");
                    }
                    else {
                        exchangeCoin.put(coinsEnum,numberOfCoin);
                    }
                }
                else continue;
                if(rest==0) break;
            }
        return exchangeCoin;
    }

    public void buyProduct(VendingMachine machine) throws InsuffisantStockException, InsuffisantPriceException, InsuffisantCoinsInBalanceException {
        double total = CalculateSumOfCoins();
        // return exception when a user doesn't enter enough money to selected
        if(this.productToBuy.getPrice()> total)
            throw new InsuffisantPriceException("You don't have enough money");
        else if(machine.getStock().get(productToBuy)==0) throw new InsuffisantStockException("the product is now out of stock");
        else {
            machine.exchangeToUser(this);
            machine.increaseBalance(this.inputPrice);
            machine.decreaseStock(this.productToBuy);
            this.isProductVended=true;

        }

    }

    // it calculates the total price the user entered in vending machine
    public double CalculateSumOfCoins(){
        double total=0;
        for(CoinsEnum coins : CoinsEnum.values()){
            if(inputPrice.get(coins)!=null)
                total+=coins.getPrice()*inputPrice.get(coins);
        }
        return total;
    }

}
