package com.akkw.time.wheel;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import org.junit.Test;

import java.time.ZonedDateTime;

import static com.cronutils.model.CronType.QUARTZ;
import static com.cronutils.model.CronType.UNIX;

public class CronTest {

    @Test
    public void cornTest() {
        String quartzCronExpression = "20 * * * * ?";
        CronParser quartzCronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ));

        // parse the QUARTZ cron expression.
        Cron parsedQuartzCronExpression = quartzCronParser.parse(quartzCronExpression);

        // Create ExecutionTime for a given cron expression.
        ZonedDateTime now = ZonedDateTime.now();
        ExecutionTime executionTime = ExecutionTime.forCron(parsedQuartzCronExpression);
        System.out.println(
                String.format("Given the Quartz cron '%s' and reference date '%s', next execution will be in %s seconds",
                        parsedQuartzCronExpression.asString(), now, executionTime.timeToNextExecution(now).get().getSeconds())
        );
    }
}
