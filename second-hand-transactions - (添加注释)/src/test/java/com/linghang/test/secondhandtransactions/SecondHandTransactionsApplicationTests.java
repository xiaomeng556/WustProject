package com.linghang.test.secondhandtransactions;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SecondHandTransactionsApplicationTests {

    @Test
    void contextLoads() {
        String test ="123123";
        boolean result = SensitiveWordHelper.contains(test);
        System.out.println(result);
    }

}
