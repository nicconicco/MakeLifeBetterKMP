package com.carlosnicolaugalves.makelifebetter.repository

actual fun createAdminRepository(): AdminRepository {
    return FirebaseAdminRepository()
}
