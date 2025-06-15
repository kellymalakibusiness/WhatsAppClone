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
    suspend operator fun invoke(currentUser: User?, email: String?, name: String, image: String?): Response<User, Error> {
        //Check if the user updated anything before making the update call
        return if(currentUser?.name == name && currentUser.image == image){
            //Same user just logged in
            Response.Success(data = currentUser)
        } else {
            //Something was updated, we make the update call
            val nameUpdate = if(name != currentUser?.name){
                Pair(name, true)
            } else {
                Pair("", false)
            }

            val imageUpdate = if(image != currentUser?.image){
                Pair(image, true)
            } else {
                Pair("", false)
            }

            email?.let { existingEmail ->
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
}