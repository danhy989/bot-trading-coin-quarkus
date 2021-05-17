package com.danhy989.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danhy989.service.CryptocurrencyDataHandler;

@ApplicationScoped
public class BscDataHandlerImpl implements CryptocurrencyDataHandler {
  private final Logger log = LoggerFactory.getLogger(BscDataHandlerImpl.class);

  @ConfigProperty(name = "rsi.period")
  private Double rsiPeriod;

  @ConfigProperty(name = "rsi.overbought")
  private Double rsiOverBought;

  @ConfigProperty(name = "rsi.oversold")
  private Double rsiOverSold;

  @ConfigProperty(name = "trade.quantity")
  private Double tradeQuantity;

  private List<Float> closes = new ArrayList<Float>();

  @Override
  public void handleMessage(String message) {
    log.info("Handling message {}", message);
    try {
      final JSONObject entryDataObject = new JSONObject(message);
      final JSONObject candle = entryDataObject.getJSONObject("k");
      final boolean is_candle_closed = candle.getBoolean("x");
      final String closePrice = candle.getString("c");
      if (is_candle_closed) {
        log.info("candle closed at {}", Float.valueOf(closePrice));
        closes.add(Float.valueOf(closePrice));
        log.info("Closes = [{}]", closes);

        if (closes.size() > rsiPeriod) {
          // Include TaLIB Technical Anylist Lib...
        }
      }
    } catch (JSONException err) {
      log.error("Error", err.getMessage());
    }
  }
}
