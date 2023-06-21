package com.etelie.schedule

import io.github.oshai.kotlinlogging.KLogger
import org.quartz.CronScheduleBuilder
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.JobExecutionContext
import org.quartz.ScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import java.time.Instant
import java.time.format.DateTimeFormatter
import kotlin.reflect.KClass

class JobMissingSimpleNameException : IllegalStateException("Simple name not found on Job KClass")

data class QuartzComponents(
    val jobDetail: JobDetail,
    val trigger: Trigger,
)

fun JobBuilder.withStandardSettings(klass: KClass<out Job>): JobBuilder {
    val identity: String = klass.simpleName?.let { "$it-job " } ?: throw JobMissingSimpleNameException()
    return apply {
        ofType(klass.java)
        withIdentity(identity)
        withDescription(identity)
        storeDurably()
    }
}

fun TriggerBuilder<Trigger>.withStandardSettings(klass: KClass<out Job>): TriggerBuilder<Trigger> {
    val identity: String = klass.simpleName?.let { "$it-trigger" } ?: throw JobMissingSimpleNameException()
    return apply {
        withIdentity(identity)
        withDescription(identity)
        withPriority(Trigger.DEFAULT_PRIORITY)
    }
}

fun CronScheduleBuilder.withStandardSettings(): CronScheduleBuilder {
    return apply {
        withMisfireHandlingInstructionFireAndProceed()
    }
}

fun KClass<out Job>.createStandardJobDetail(): JobDetail {
    return JobBuilder.newJob().withStandardSettings(this).build()
}

fun KClass<out Job>.createStandardTrigger(scheduleBuilder: ScheduleBuilder<out Trigger>): Trigger {
    return TriggerBuilder.newTrigger()
        .withStandardSettings(this)
        .withSchedule(scheduleBuilder)
        .build()
}

fun KClass<out Job>.createStandardQuartzComponents(cronExpression: String): QuartzComponents {
    val jobDetail = createStandardJobDetail()
    val scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withStandardSettings()
    val trigger = createStandardTrigger(scheduleBuilder)
    return QuartzComponents(jobDetail, trigger)
}

val JobExecutionContext?.name: String
    get() = this?.jobDetail?.description ?: "Unknown job"

val JobExecutionContext?.startMessage: String
    get() = run {
        val fireTime: String = this?.fireTime?.toInstant()?.let {
            DateTimeFormatter.RFC_1123_DATE_TIME.format(it)
        } ?: "unknown time"
        "$name started at $fireTime"
    }


val JobExecutionContext?.finishMessage: String
    get() = run {
        val endTime: String = DateTimeFormatter.RFC_1123_DATE_TIME.format(Instant.now())
        "$name finished at $endTime"
    }

fun <JobType : Job> JobType.logged(logger: KLogger, context: JobExecutionContext?, execute: () -> Unit) {
    logger.info { context.startMessage }
    execute()
    logger.info { context.finishMessage }
}
