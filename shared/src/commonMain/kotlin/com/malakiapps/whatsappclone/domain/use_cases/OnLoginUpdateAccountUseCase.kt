package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.ANONYMOUS_EMAIL
import com.malakiapps.whatsappclone.domain.user.AnonymousUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.Email
import com.malakiapps.whatsappclone.domain.user.Image
import com.malakiapps.whatsappclone.domain.user.Name
import com.malakiapps.whatsappclone.domain.user.None
import com.malakiapps.whatsappclone.domain.user.Some
import com.malakiapps.whatsappclone.domain.user.Profile
import com.malakiapps.whatsappclone.domain.user.UserContactUpdate

class OnLoginUpdateAccountUseCase(
    val anonymousUserAccountRepository: AnonymousUserAccountRepository,
    val authenticatedUserAccountRepository: AuthenticatedUserAccountRepository,
) {
    suspend operator fun invoke(currentProfile: Profile?, email: Email?, name: Name, image: Image?): Response<Profile, Error> {
        //Check if the user updated anything before making the update call
        return if(currentProfile?.name == name && currentProfile.image == image){
            //Same user just logged in
            Response.Success(data = currentProfile)
        } else {
            //Something was updated, we make the update call
            val nameUpdate = if(name != currentProfile?.name){
                Some(name)
            } else {
                None
            }

            val imageUpdate = if(image != currentProfile?.image){
                Some(image)
            } else {
                None
            }

            email?.let { existingEmail ->
                //If email exists, then the user authenticated. Use primary storage repository
                authenticatedUserAccountRepository.updateContact(
                    userContactUpdate = UserContactUpdate(
                        email = existingEmail,
                        name = nameUpdate,
                        image = imageUpdate
                    )
                )
            } ?: run {
                //If no email, use anonymous repository
                anonymousUserAccountRepository.updateAccount(
                    userContactUpdate = UserContactUpdate(
                        email = ANONYMOUS_EMAIL,
                        name = nameUpdate,
                        image = imageUpdate
                    )
                )
            }
        }
    }
}