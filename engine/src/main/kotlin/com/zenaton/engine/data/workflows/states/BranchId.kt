package com.zenaton.engine.data.workflows.states

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class BranchId @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@get:JsonValue val id: String = UUID.randomUUID().toString())