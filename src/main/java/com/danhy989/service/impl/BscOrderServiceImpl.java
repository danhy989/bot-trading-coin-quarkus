
package com.danhy989.service.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danhy989.entities.BinanceKey;
import com.danhy989.repository.BinanceKeyRepository;
import com.danhy989.service.CryptocurrencyOrderService;

import io.smallrye.common.annotation.Blocking;

@ApplicationScoped
public class BscOrderServiceImpl implements CryptocurrencyOrderService{
  
  private final Logger log = LoggerFactory.getLogger(BscOrderServiceImpl.class);
  
  private BinanceKey binanceKey;
  
  private static boolean isQueryData = false;
  
  @Inject
  BinanceKeyRepository binanceKeyRepository;
  
  public BscOrderServiceImpl() {
    
  }

  @Override
  public boolean order(String pair, float quantity) {
    if(isQueryData) {
      return false;
    }
    if(null == binanceKey) {
      isQueryData = true;
      binanceKey = binanceKeyRepository.findById(1L);
      log.info(binanceKey.getApiKey());
    }
    log.info("1" +binanceKey.getApiKey());
    isQueryData = false;
    return false;
  }

  @Override
  public boolean cancelOrder() {
    
    return false;
  }

}
