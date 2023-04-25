package com.bank;

import com.bank.simulator.repos.CardRepo;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import com.bank.simulator.dto.CardData;
import com.bank.simulator.model.Card;
import com.bank.simulator.service.BankService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;


import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
//@RunWith(SpringRunner.class)
@SpringBootTest
@SpringBootApplication
//@SpringBootConfiguration
//@DataJpaTest
public class BankServiceTest {

    @Autowired
    private BankService bankService;

    @Test
    public void checkCardExistence(){
        Card card = new Card(null, "1234567891234567", "02/24", "Lena Sargsyan",
                "VISA", "HSBC");

        CardData cardData = new CardData();
        cardData.setPan(card.getPan());

        ResponseEntity expected =
                ResponseEntity.status(HttpStatus.OK).body(cardData);

        ResponseEntity actual = bankService.checkCardExistence(card);

        assertEquals(expected.getStatusCode(),actual.getStatusCode());
        assertEquals(((CardData) expected.getBody()).getPan(), ((CardData) actual.getBody()).getPan());

    }


}
