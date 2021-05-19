
package com.danhy989.service;


public interface CryptocurrencyOrderService {
  public boolean order(final String pair,final float quantity);
  public boolean cancelOrder();
}
