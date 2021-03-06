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

package io.infinitic.common.tasks.executors.messages

import io.infinitic.common.data.MessageId
import io.infinitic.common.data.methods.MethodInput
import io.infinitic.common.data.methods.MethodName
import io.infinitic.common.data.methods.MethodOutput
import io.infinitic.common.data.methods.MethodParameterTypes
import io.infinitic.common.tasks.data.TaskAttemptError
import io.infinitic.common.tasks.data.TaskAttemptId
import io.infinitic.common.tasks.data.TaskAttemptRetry
import io.infinitic.common.tasks.data.TaskId
import io.infinitic.common.tasks.data.TaskMeta
import io.infinitic.common.tasks.data.TaskName
import io.infinitic.common.tasks.data.TaskOptions
import io.infinitic.common.tasks.data.TaskRetry
import kotlinx.serialization.Serializable

@Serializable
sealed class TaskExecutorMessage() {
    val messageId = MessageId()
    abstract val taskName: TaskName
    abstract val taskMeta: TaskMeta
}

@Serializable
data class ExecuteTaskAttempt(
    override val taskName: TaskName,
    val taskId: TaskId,
    val taskRetry: TaskRetry,
    val taskAttemptId: TaskAttemptId,
    val taskAttemptRetry: TaskAttemptRetry,
    val previousTaskAttemptError: TaskAttemptError?,
    val methodName: MethodName,
    val methodParameterTypes: MethodParameterTypes?,
    val methodInput: MethodInput,
    val taskOptions: TaskOptions,
    override val taskMeta: TaskMeta
) : TaskExecutorMessage()

@Serializable
data class CancelTaskAttempt(
    override val taskName: TaskName,
    val taskId: TaskId,
    val taskOutput: MethodOutput,
    override val taskMeta: TaskMeta
) : TaskExecutorMessage()
