package com.etelie.schedule

import org.quartz.Job
import org.quartz.JobBuilder
import org.quartz.Trigger
import org.quartz.TriggerBuilder
import kotlin.reflect.KClass

class JobMissingSimpleNameException : IllegalStateException("Simple name not found on Job KClass")

fun JobBuilder.withEtelieIdentity(klass: KClass<out Job>): JobBuilder {
    return withIdentity(klass.simpleName?.let { "$it-job " } ?: throw JobMissingSimpleNameException())
}

fun TriggerBuilder<out Trigger>.withEtelieIdentity(klass: KClass<out Job>): TriggerBuilder<out Trigger> {
    return withIdentity(klass.simpleName?.let { "$it-trigger" } ?: throw JobMissingSimpleNameException())
}
