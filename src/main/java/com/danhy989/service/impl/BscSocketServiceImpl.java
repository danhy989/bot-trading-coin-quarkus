
package com.danhy989.service.impl;

import java.net.URI;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.ClientEndpoint;
import javax.websocket.CloseReason;
import javax.websocket.ContainerProvider;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.danhy989.service.CryptocurrencyDataHandler;
import com.danhy989.service.CryptocurrencySocketService;

@ClientEndpoint
@ApplicationScoped
public class BscSocketServiceImpl implements CryptocurrencySocketService {
  private final Logger log = LoggerFactory.getLogger(BscSocketServiceImpl.class);

  @ConfigProperty(name = "binance.stream.api")
  private String binanceStreamApi;
  
  @ConfigProperty(name = "pairs.usdt.symbol")
  private List<String> tradeUsdtSymbol;

  @Inject
  private CryptocurrencyDataHandler cryptocurrencyDataHandler;

  private static WebSocketContainer container;

  private static Session session;

  private static boolean isOpeningWebSocket = false;

  public BscSocketServiceImpl() {
    if (null == container) {
      container = ContainerProvider.getWebSocketContainer();
    }
  }

  @Override
  public boolean startSocketClient(final String pair) {
    boolean result = false;
    try {
      if (!tradeUsdtSymbol.contains(pair)) {
        log.error("Incorrect pair value");
        return result;
      }
      if (isOpeningWebSocket) {
        log.warn("REJECT! The websocket is openning, please stop it and comback");
        return result;
      }
      container.connectToServer(this, new URI(String.format(binanceStreamApi, pair)));
      log.info("Started bsc socket client with pair {}", pair);
      result = true;
      isOpeningWebSocket = true;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  @Override
  public boolean stopSocketClient() {
    boolean result = false;
    try {
      if (null == session || !session.isOpen()) {
        log.warn("The web socket session is closed already, do nothing");
        return result;
      }
      session.close();
      log.info("Stop bsc socket client.");
      result = true;
      isOpeningWebSocket = false;
    } catch (Exception e) {
      log.error(e.getMessage());
    }
    return result;
  }

  @OnOpen
  public void onOpen(Session userSession) {
    System.out.println("opening websocket");
    session = userSession;
  }

  @OnClose
  public void onClose(Session userSession, CloseReason reason) {
    System.out.println("closing websocket");
    log.warn("Websocket is closed, reason = {}", reason);
    session = userSession;
  }

  @OnMessage
  public void onMessage(String message) {
    this.cryptocurrencyDataHandler.handleMessage(message);
  }
}
