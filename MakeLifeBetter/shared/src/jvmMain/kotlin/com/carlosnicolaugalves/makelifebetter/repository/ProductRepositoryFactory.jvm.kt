package com.carlosnicolaugalves.makelifebetter.repository

actual fun createProductRepository(): ProductRepository = LocalProductRepository()
