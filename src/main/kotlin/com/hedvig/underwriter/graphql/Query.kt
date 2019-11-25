package com.hedvig.underwriter.graphql

import com.coxautodev.graphql.tools.GraphQLQueryResolver
import org.springframework.stereotype.Component

// This is unfortunately needed
@Component
class Query : GraphQLQueryResolver
