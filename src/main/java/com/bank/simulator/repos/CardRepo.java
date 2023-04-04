package com.bank.simulator.repos;

import com.bank.simulator.domain.Cards;
import org.springframework.data.repository.CrudRepository;

public interface CardRepo extends CrudRepository<Cards, Integer> {
}