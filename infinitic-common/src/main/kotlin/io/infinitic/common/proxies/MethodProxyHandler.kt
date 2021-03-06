/**
 * "Commons Clause" License Condition v1.0
 *
 * The Software is provided to you by the Licensor under the License, as defined
 * below, subject to the following condition.
 *
 * Without limiting other conditions in the License, the grant of rights under the
 * License will not include, and the License does not grant to you, the right to
 * Sell the Software.
 *
 * For purposes of the foregoing, “Sell” means practicing any or all of the rights
 * granted to you under the License to provide to third parties, for a fee or
 * other consideration (including without limitation fees for hosting or
 * consulting/ support services related to the Software), a product or service
 * whose value derives, entirely or substantially, from the functionality of the
 * Software. Any license notice or attribution required by the License must also
 * include this Commons Clause License Condition notice.
 *
 * Software: Infinitic
 *
 * License: MIT License (https://opensource.org/licenses/MIT)
 *
 * Licensor: infinitic.io
 */

package io.infinitic.common.proxies

import io.infinitic.common.tasks.exceptions.MultipleMethodCalls
import java.lang.Thread.currentThread
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.util.concurrent.ConcurrentHashMap

open class MethodProxyHandler<T>(private val klass: Class<T>) : InvocationHandler {
    // multiple threads can use a same MethodProxyHandler instance without mixing values
    private val _dataMethodHash = ConcurrentHashMap<Thread, DataMethod>()
    private val dataMethod: DataMethod
        get() = _dataMethodHash.getOrPut(currentThread(), { DataMethod() })

    var isSync: Boolean
        get() = dataMethod.isSync
        set(value) { dataMethod.isSync = value }

    var method: Method?
        get() = dataMethod.method
        set(value) { dataMethod.method = value }

    var args: Array<out Any>
        get() = dataMethod.args
        set(value) { dataMethod.args = value }

    /*
     * invoke method is called when a method is applied to the proxy instance
     */
    override fun invoke(proxy: Any, method: Method, args: Array<out Any>?): Any? {
        if (method.name == "toString") return klass.name

        // invoke should called only once per ProxyHandler instance
        if (this.method != null) throw MultipleMethodCalls(method.declaringClass.name, this.method?.name, method.name)

        // method
        this.method = method
        this.args = args ?: arrayOf()

        return getAsyncReturnValue(method)
    }

    /*
     * provides a stub of type T
     */
    @Suppress("UNCHECKED_CAST")
    fun stub() = Proxy.newProxyInstance(
        klass.classLoader,
        arrayOf(klass),
        this
    ) as T

    /*
     * Prepare for reuse
     */
    fun reset() {
        _dataMethodHash.remove(currentThread())
    }

    private fun getAsyncReturnValue(method: Method) = when (method.returnType.name) {
        "long" -> 0L
        "int" -> 0
        "short" -> 0.toShort()
        "byte" -> 0.toByte()
        "double" -> 0.toDouble()
        "float" -> 0.toFloat()
        "char" -> 0.toChar()
        "boolean" -> false
        else -> null
    }
}
