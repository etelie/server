package com.etelie.network

import com.etelie.application.EtelieException

class WebContentNotFoundException : EtelieException {

    constructor() : super()
    constructor(message: String) : super(message)

}
