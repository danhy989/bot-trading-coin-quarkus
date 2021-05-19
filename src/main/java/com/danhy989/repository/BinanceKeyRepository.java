
package com.danhy989.repository;

import javax.enterprise.context.ApplicationScoped;

import com.danhy989.entities.BinanceKey;

import io.quarkus.hibernate.orm.panache.PanacheRepository;

@ApplicationScoped
public class BinanceKeyRepository implements PanacheRepository<BinanceKey>{
  public BinanceKey findById(Long id) {
    return find("id", id).firstResult();
  }
}
