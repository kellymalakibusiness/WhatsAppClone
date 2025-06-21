package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.None
import com.malakiapps.whatsappclone.domain.user.Update
import com.malakiapps.whatsappclone.domain.user.User
import com.malakiapps.whatsappclone.domain.user.UserStorageRepository
import com.malakiapps.whatsappclone.domain.user.UserUpdate

class OnLoginUpdateAccountUseCase(
    val userStorageRepository: UserStorageRepository,
    val anonymousUserStorageRepository: UserStorageRepository,
) {
    suspend operator fun invoke(currentUser: User?, email: Email?, name: Name, image: Image?): Response<User, Error> {
        //Check if the user updated anything before making the update call
        return if(currentUser?.name == name && currentUser.image == image){
            //Same user just logged in
            Response.Success(data = currentUser)
        } else {
            //Something was updated, we make the update call
            val nameUpdate = if(name != currentUser?.name){
                Update(name)
            } else {
                None
            }

            val imageUpdate = if(image != currentUser?.image){
                Update(image)
            } else {
                None
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
                        email = ANONYMOUS_EMAIL,
                        name = nameUpdate,
                        image = imageUpdate
                    )
                )
            }
        }
    }
}