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

package io.infinitic.monitoring.perName.engine

import io.infinitic.common.monitoring.global.messages.TaskCreated
import io.infinitic.common.monitoring.perName.messages.MonitoringPerNameEngineMessage
import io.infinitic.common.monitoring.perName.messages.TaskStatusUpdated
import io.infinitic.common.monitoring.perName.state.MonitoringPerNameState
import io.infinitic.common.tasks.data.TaskStatus
import io.infinitic.monitoring.perName.engine.storage.MonitoringPerNameStateStorage
import io.infinitic.monitoring.perName.engine.transport.MonitoringPerNameOutput
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class MonitoringPerNameEngine(
    private val storage: MonitoringPerNameStateStorage,
    private val monitoringPerNameOutput: MonitoringPerNameOutput
) {
    private val logger: Logger
        get() = LoggerFactory.getLogger(javaClass)

    suspend fun handle(message: MonitoringPerNameEngineMessage) {
        logger.debug("name {} - receiving {} (messageId {})", message.taskName, message, message.messageId)

        // get state
        val oldState = storage.getState(message.taskName)

        // checks if this message has already just been handled
        if (oldState != null && oldState.lastMessageId == message.messageId) {
            return logDiscardingMessage(message, "as state already contains this messageId")
        }

        val newState = oldState
            ?.copy(lastMessageId = message.messageId)
            ?: MonitoringPerNameState(message.messageId, message.taskName)

        when (message) {
            is TaskStatusUpdated -> handleTaskStatusUpdated(message, newState)
        }

        // It's a new task type
        if (oldState == null) {
            val tsc = TaskCreated(taskName = message.taskName)
            monitoringPerNameOutput.sendToMonitoringGlobal(newState, tsc)
        }

        // Update stored state if needed and existing
        if (newState != oldState) {
            storage.updateState(message.taskName, newState, oldState)
        }
    }

    private fun logDiscardingMessage(message: MonitoringPerNameEngineMessage, reason: String) {
        logger.info("name {} - discarding {}: {} (messageId {})", message.taskName, reason, message, message.messageId)
    }

    private fun handleTaskStatusUpdated(message: TaskStatusUpdated, state: MonitoringPerNameState) {
        when (message.oldStatus) {
            TaskStatus.RUNNING_OK -> state.runningOkCount--
            TaskStatus.RUNNING_WARNING -> state.runningWarningCount--
            TaskStatus.RUNNING_ERROR -> state.runningErrorCount--
            TaskStatus.TERMINATED_COMPLETED -> state.terminatedCompletedCount--
            TaskStatus.TERMINATED_CANCELED -> state.terminatedCanceledCount--
            else -> Unit
        }

        when (message.newStatus) {
            TaskStatus.RUNNING_OK -> state.runningOkCount++
            TaskStatus.RUNNING_WARNING -> state.runningWarningCount++
            TaskStatus.RUNNING_ERROR -> state.runningErrorCount++
            TaskStatus.TERMINATED_COMPLETED -> state.terminatedCompletedCount++
            TaskStatus.TERMINATED_CANCELED -> state.terminatedCanceledCount++
        }
    }
}
