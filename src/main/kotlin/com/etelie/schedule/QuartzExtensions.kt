package com.etelie.schedule

import com.etelie.application.EtelieException
import io.github.oshai.kotlinlogging.KLogger
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
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

internal class JobMissingSimpleNameException : EtelieException("Simple name not found on Job KClass")

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
        .startNow()
        .build()
}

fun KClass<out Job>.createStandardJobDefinition(cronExpression: String): JobDefinition {
    val jobDetail = createStandardJobDetail()
    val scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withStandardSettings()
    val trigger = createStandardTrigger(scheduleBuilder)
    return JobDefinition(jobDetail, trigger)
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

suspend fun <JobType : Job> JobType.logged(logger: KLogger, context: JobExecutionContext?, execute: suspend () -> Unit) {
    logger.info { context.startMessage }
    execute()
    logger.info { context.finishMessage }
}

val <JobType : Job> JobType.jobCoroutineName
    get() = CoroutineName("${this::class.simpleName}")

val <JobType : Job> JobType.jobCoroutineContext
    get() = Dispatchers.IO + this.jobCoroutineName
