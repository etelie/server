package com.etelie.network

import org.http4k.core.Request

fun Request.addAllQueries(queries: Map<String, String?>): Request {
    return queries.entries.fold(this) { request, (key, value) ->
        return request.query(key, value)
    }
}
