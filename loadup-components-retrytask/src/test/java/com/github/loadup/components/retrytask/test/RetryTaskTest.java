package com.github.loadup.components.retrytask.test;

/*-
 * #%L
 * loadup-components-ip
 * %%
 * Copyright (C) 2022 - 2023 loadup_cloud
 * %%
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * #L%
 */

import com.github.loadup.components.retrytask.RetryComponentService;
import com.github.loadup.components.retrytask.model.RetryTask;
import com.github.loadup.components.retrytask.model.RetryTaskRequest;
import com.github.loadup.components.retrytask.repository.RetryTaskRepository;
import java.util.Date;
import java.util.UUID;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
//@ActiveProfiles("remote")
public class RetryTaskTest {
    @Resource
    RetryComponentService retryComponentService;
    @Resource
    RetryTaskRepository   retryTaskRepository;

    @Test
    void testExponentialTask() throws InterruptedException {
        // 0,20,40,80,160,320
        String bizId = UUID.randomUUID().toString();
        String bizType = "EXPONENTIAL";
        try {
            RetryTaskRequest req = new RetryTaskRequest();
            req.setBizType(bizType);
            req.setBizId(bizId);
            req.setBizContext("xxx");
            retryComponentService.deleteRetryTask(bizId, bizType);
            Date now = new Date();
            RetryTask register = retryComponentService.register(req);
            log.info("register={}", register);
            //15s 注册立即执行一次
            Thread.sleep(15 * 1000);
            Assertions.assertTrue(register.getExecutedTimes() == 1);
            assertDateEquals(DateUtils.addSeconds(now, 20), register.getNextExecuteTime());
            //
            ////75s 自动执行一次
            //Thread.sleep(40 * 1000);
            //RetryTask db = retryTaskRepository.loadByBizId(bizId, bizType);
            //log.info("75s={}", register);
            //Assertions.assertTrue(db.getExecutedTimes() == 2);
            //assertDateEquals(DateUtils.addSeconds(now, 160), db.getNextExecuteTime());
            //
            ////150s 自动执行2次
            //Thread.sleep(80 * 1000);
            //db = retryTaskRepository.loadByBizId(bizId, bizType);
            //log.info("150s={}", register);
            //Assertions.assertTrue(db.getExecutedTimes() == 3);
            //assertDateEquals(DateUtils.addSeconds(now, 320), db.getNextExecuteTime());

        } finally {
            // retryTaskRepository.delete(bizId, bizType);
        }

    }

    String formatDate(Date date) {
        return DateFormatUtils.format(date, "yyyyMMddHHmmss");
    }

    boolean assertDateEquals(Date expected, Date actual) {
        long i = Long.parseLong(formatDate(expected)) - Long.parseLong(formatDate(actual));
        return Math.abs(i) <= 1000;
    }

    boolean assertDateEquals(Date expected, Date actual, long limit) {
        long i = Long.parseLong(formatDate(expected)) - Long.parseLong(formatDate(actual));
        return Math.abs(i) <= limit;
    }

}
