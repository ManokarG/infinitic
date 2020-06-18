package com.zenaton.common.data.interfaces

import com.zenaton.common.data.AvroSerializationType
import java.math.BigInteger
import java.security.MessageDigest

interface SerializedDataInterface {
    val serializedData: ByteArray
    val serializationType: AvroSerializationType

    fun hash(): String {
        // MD5 implementation
        val md = MessageDigest.getInstance("MD5")
        return BigInteger(1, md.digest(serializedData)).toString(16).padStart(32, '0')
    }

    fun equalsData(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializedDataInterface

        return (serializationType == other.serializationType && serializedData.contentEquals(other.serializedData))
    }

    fun hashCodeData(): Int {
        return serializedData.contentHashCode()
    }
}
