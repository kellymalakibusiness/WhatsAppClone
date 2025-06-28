package com.malakiapps.whatsappclone.domain.use_cases

import com.malakiapps.whatsappclone.domain.common.Error
import com.malakiapps.whatsappclone.domain.common.InvalidUpdate
import com.malakiapps.whatsappclone.domain.common.Response
import com.malakiapps.whatsappclone.domain.user.AuthenticatedUserAccountRepository
import com.malakiapps.whatsappclone.domain.user.UserDetails
import com.malakiapps.whatsappclone.domain.user.UserDetailsUpdate
import com.malakiapps.whatsappclone.domain.user.isContactUpdateValid

class UpdateUserDetailsUseCase(
    val authenticatedUserAccountRepository: AuthenticatedUserAccountRepository,
) {
    suspend operator fun invoke(userDetailsUpdate: UserDetailsUpdate): Response<UserDetails, Error>{
        //Check if the update is valid
        if(!userDetailsUpdate.removeContact.isContactUpdateValid()){
            return Response.Failure(InvalidUpdate("Invalid email provided"))
        }
        if(!userDetailsUpdate.addContact.isContactUpdateValid()){
            return Response.Failure(InvalidUpdate("Invalid email provided"))
        }
        return authenticatedUserAccountRepository.updateUserDetails(
                userDetailsUpdate = userDetailsUpdate
            )
    }
}