package com.etelie.network

import org.http4k.core.Request

fun Request.addAllQueries(queries: Map<String, String?>): Request {
    queries.forEach { (key, value) ->
        query(key, value)
    }
    return this
}
