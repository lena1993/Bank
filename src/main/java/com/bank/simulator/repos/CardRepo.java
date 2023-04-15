package com.bank.simulator.repos;

import com.bank.simulator.domain.Cards;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface CardRepo extends CrudRepository<Cards, Integer> {
    Cards findByPan(String pan);
}