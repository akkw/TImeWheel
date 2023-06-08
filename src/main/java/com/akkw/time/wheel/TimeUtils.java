package com.akkw.time.wheel;

import com.cronutils.model.Cron;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Optional;

import static com.cronutils.model.CronType.QUARTZ;

public class TimeUtils {

    public static long getNextExecutorTimeSecond(String corn) {
        CronParser quartzCronParser = new CronParser(CronDefinitionBuilder.instanceDefinitionFor(QUARTZ));

        Cron parsedQuartzCronExpression = quartzCronParser.parse(corn);

        ZonedDateTime now = ZonedDateTime.now();
        ExecutionTime executionTime = ExecutionTime.forCron(parsedQuartzCronExpression);
        Optional<Duration> optional = executionTime.timeToNextExecution(now);
        if (!optional.isPresent()) {
            throw new RuntimeException("the next execution time is incorrectly calculated, " + corn);
        }

        Duration duration = optional.get();
        return duration.getSeconds();
    }

}
