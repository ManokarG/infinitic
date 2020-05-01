package com.zenaton.engine.data.workflows.states

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import com.zenaton.engine.data.DataInterface

data class BranchOutput @JsonCreator(mode = JsonCreator.Mode.DELEGATING) constructor(@get:JsonValue override val data: ByteArray) :
    DataInterface {
    final override fun equals(other: Any?) = equalsData(other)
    final override fun hashCode() = hashCodeData()
}