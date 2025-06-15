package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserUpdate

class OnLoginUpdateAccountUseCase(
    val userStorageRepository: UserStorageRepository,
    val anonymousUserStorageRepository: UserStorageRepository,
) {
    suspend operator fun invoke(email: String?, name: String? = null, image: String? = null): Response<User, Error> {
        val nameUpdate = name?.let {
            Pair(it, true)
        } ?: Pair("", false)

        val imageUpdate = image?.let {
            Pair(it, true)
        } ?: Pair("", false)

        return email?.let { existingEmail ->
            //If email exists, then the user authenticated. Use primary storage repository
            userStorageRepository.updateUser(
                userUpdate = UserUpdate(
                    email = existingEmail,
                    name = nameUpdate,
                    image = imageUpdate
                )
            )
        } ?: run {
            //If no email, use anonymous repository
            anonymousUserStorageRepository.updateUser(
                userUpdate = UserUpdate(
                    email = "anonymous",
                    name = nameUpdate,
                    image = imageUpdate
                )
            )
        }
    }
}