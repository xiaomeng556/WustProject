package com.example.campusmate.campusmate;

import com.github.houbb.sensitive.word.core.SensitiveWordHelper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CampusmateApplicationTests {

    @Test
    void contextLoads() {
        String test="";
        boolean result = SensitiveWordHelper.contains(test);
        System.out.println(result);
    }

}
