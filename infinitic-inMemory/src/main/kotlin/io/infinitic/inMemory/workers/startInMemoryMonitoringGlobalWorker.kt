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

package io.infinitic.inMemory.workers

import io.infinitic.common.monitoring.global.state.MonitoringGlobalState
import io.infinitic.common.storage.keyValue.KeyValueCache
import io.infinitic.common.storage.keyValue.KeyValueStorage
import io.infinitic.monitoring.global.engine.storage.MonitoringGlobalStateKeyValueStorage
import io.infinitic.monitoring.global.engine.transport.MonitoringGlobalInputChannels
import io.infinitic.monitoring.global.engine.transport.MonitoringGlobalMessageToProcess
import io.infinitic.monitoring.global.engine.worker.startMonitoringGlobalEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch

fun CoroutineScope.startInMemoryMonitoringGlobalWorker(
    keyValueStorage: KeyValueStorage,
    keyValueCache: KeyValueCache<MonitoringGlobalState>,
    monitoringGlobalChannel: Channel<MonitoringGlobalMessageToProcess>,
    logChannel: SendChannel<MonitoringGlobalMessageToProcess>
) = launch {

    startMonitoringGlobalEngine(
        "monitoring-global-engine",
        MonitoringGlobalStateKeyValueStorage(keyValueStorage, keyValueCache),
        MonitoringGlobalInputChannels(
            monitoringGlobalChannel,
            logChannel
        )
    )
}
