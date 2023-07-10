package com.github.loadup.components.retrytask;

import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.junit.jupiter.api.Assertions;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
//@ActiveProfiles("remote")
public class BaseTest {
    protected String formatDate(Date date) {
        return DateFormatUtils.format(date, "yyyyMMddHHmmss");
    }

    protected void assertDateEquals(Date expected, Date actual) {
        long i = Long.parseLong(formatDate(expected)) - Long.parseLong(formatDate(actual));
        Assertions.assertTrue(Math.abs(i) <= 1);
    }

    protected void assertDateEquals(Date expected, Date actual, long limit) {
        long i = Long.parseLong(formatDate(expected)) - Long.parseLong(formatDate(actual));
        Assertions.assertTrue(Math.abs(i) <= 1);
    }
}
