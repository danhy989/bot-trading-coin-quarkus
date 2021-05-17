package com.danhy989.service;

public interface CryptocurrencySocketService {

  boolean startSocketClient(String pair);

  boolean stopSocketClient();

}
