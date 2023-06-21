package com.etelie.schedule

import org.quartz.CronScheduleBuilder
import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.JobDetail
import org.quartz.ScheduleBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
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
