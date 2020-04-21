package com.zenaton.engine.attributes.decisions

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue
import java.util.UUID

data class DecisionId(val id: String = UUID.randomUUID().toString()) {
    companion object {
        @JvmStatic @JsonCreator
        fun fromJson(value: String) = DecisionId(value)
    }
    @JsonValue
    fun toJson() = id
}