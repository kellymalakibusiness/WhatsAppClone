package com.malakiapps.whatsappclone.domain.user

import kotlinx.coroutines.flow.Flow

actual interface AuthenticationRepository {
    actual fun getAuthContext(): AuthenticationContext?
    actual suspend fun updateProfile(name: String?): Boolean
    actual fun getAuthContextState(): Flow<AuthenticationContext?>
}