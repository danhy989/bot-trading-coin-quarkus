package com.danhy989.service.impl;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBarSeriesBuilder;
import org.ta4j.core.Indicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import com.arjuna.common.logging.commonI18NLogger;
import com.danhy989.service.CryptocurrencyDataHandler;
import com.danhy989.service.CryptocurrencyOrderService;

@ApplicationScoped
public class BscDataHandlerImpl implements CryptocurrencyDataHandler {
  private final Logger log = LoggerFactory.getLogger(BscDataHandlerImpl.class);

  @ConfigProperty(name = "rsi.period")
  private Integer rsiPeriod;

  @ConfigProperty(name = "rsi.overbought")
  private Float rsiOverBought;

  @ConfigProperty(name = "rsi.oversold")
  private Float rsiOverSold;

  @ConfigProperty(name = "trade.quantity")
  private Float tradeQuantity;
  
  private static BarSeries series = new BaseBarSeriesBuilder().withName("bsc").build();
  
  private static boolean in_position = false;
  
  @Inject
  public CryptocurrencyOrderService cryptocurrencyOrderService;

  @Override
  public void handleMessage(String message) {
    try {
      
      final JSONObject entryDataObject = new JSONObject(message);
      
      final String namePairSymbol = entryDataObject.getString("s");
      cryptocurrencyOrderService.order(namePairSymbol, tradeQuantity);
      final JSONObject candle = entryDataObject.getJSONObject("k");
      final boolean is_candle_closed = candle.getBoolean("x");
      final BigDecimal highPrice = BigDecimal.valueOf(candle.getFloat("h"));
      final BigDecimal lowPrice = BigDecimal.valueOf(candle.getFloat("l"));
      final BigDecimal closePrice = BigDecimal.valueOf(candle.getFloat("c"));
      final BigDecimal openPrice = BigDecimal.valueOf(candle.getFloat("o"));
      final BigDecimal volume = BigDecimal.valueOf(candle.getFloat("v"));

      
      log.info("{} ClosePrice ={} volume={} Time={}", namePairSymbol, closePrice,
          volume, ZonedDateTime.now());
      if (is_candle_closed) {
        series.addBar(ZonedDateTime.now(),openPrice,highPrice,lowPrice,closePrice,volume);
        final ClosePriceIndicator closePriceIndi = new ClosePriceIndicator(series);
        final int barSize = series.getBarCount();
        log.info("candle closed at {}", BigDecimal.valueOf(closePriceIndi.getValue(barSize-1).floatValue()));
        if (barSize > rsiPeriod) {
          final RSIIndicator rsiIndicator = new RSIIndicator(closePriceIndi, rsiPeriod);
          final float lastRsi = rsiIndicator.getValue(barSize-1).floatValue();
          log.info("The current rsi is {}",lastRsi);
          
          if(lastRsi > rsiOverBought) {
            if(in_position) {
              
              log.info("Exc bsc logic: Sell sell");
            }else {
              log.info("It is overbought, but we don't own any. Nothing to do.");
            }
            
          }
          
          if(lastRsi < rsiOverSold) {
            if(in_position) {
              log.info("It is oversold, but you already own it, nothing to do.");
            }else {
              log.info("Exc bsc logic: Buy buy");
            }
            
          }
        }
      }
    } catch (JSONException err) {
      log.error("Error", err.getMessage());
    }
  }
}
