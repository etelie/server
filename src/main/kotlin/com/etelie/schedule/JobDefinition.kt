package com.etelie.schedule

import org.quartz.JobDetail
import org.quartz.Trigger

data class JobDefinition(
    val jobDetail: JobDetail,
    val trigger: Trigger,
)
