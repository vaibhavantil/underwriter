package com.hedvig.underwriter.serviceIntegration.memberService.dtos

data class UpdateContactInformationRequest(
       var memberId: String,
       val firstName: String,
       val lastName: String,
       val email: String,
       val phoneNumber: String,
       val addressMemberService: AddressMemberService,
       val ssn: String

)