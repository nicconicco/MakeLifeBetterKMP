package com.carlosnicolaugalves.makelifebetter

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform