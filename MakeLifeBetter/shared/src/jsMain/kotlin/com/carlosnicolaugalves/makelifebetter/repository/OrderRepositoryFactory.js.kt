package com.carlosnicolaugalves.makelifebetter.repository

actual fun createOrderRepository(): OrderRepository = LocalOrderRepository()
