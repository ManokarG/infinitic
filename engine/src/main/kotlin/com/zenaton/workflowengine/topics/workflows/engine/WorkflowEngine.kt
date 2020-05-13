package com.zenaton.workflowengine.topics.workflows.engine

import com.zenaton.decisionmanager.data.DecisionData
import com.zenaton.decisionmanager.data.DecisionId
import com.zenaton.decisionmanager.data.DecisionName
import com.zenaton.decisionmanager.messages.DecisionDispatched
import com.zenaton.workflowengine.data.DecisionInput
import com.zenaton.workflowengine.interfaces.LoggerInterface
import com.zenaton.workflowengine.interfaces.StaterInterface
import com.zenaton.workflowengine.topics.workflows.interfaces.WorkflowEngineDispatcherInterface
import com.zenaton.workflowengine.topics.workflows.interfaces.WorkflowMessageInterface
import com.zenaton.workflowengine.topics.workflows.messages.ChildWorkflowCompleted
import com.zenaton.workflowengine.topics.workflows.messages.DecisionCompleted
import com.zenaton.workflowengine.topics.workflows.messages.DelayCompleted
import com.zenaton.workflowengine.topics.workflows.messages.EventReceived
import com.zenaton.workflowengine.topics.workflows.messages.TaskCompleted
import com.zenaton.workflowengine.topics.workflows.messages.WorkflowCompleted
import com.zenaton.workflowengine.topics.workflows.messages.WorkflowDispatched
import com.zenaton.workflowengine.topics.workflows.state.Branch
import com.zenaton.workflowengine.topics.workflows.state.Store
import com.zenaton.workflowengine.topics.workflows.state.WorkflowState

class WorkflowEngine(
    val stater: StaterInterface<WorkflowState>,
    val dispatcher: WorkflowEngineDispatcherInterface,
    val logger: LoggerInterface
) {
    fun handle(msg: WorkflowMessageInterface) {
        // get associated state
        var state = stater.getState(msg.getStateId())
        if (state == null) {
            // a null state should mean that this workflow is already terminated => all messages others than WorkflowDispatched are ignored
            if (msg !is WorkflowDispatched) {
                logger.warn("No state found for message:%s(It's normal if this workflow is already terminated)", msg)
                return
            }
            // init a state
            state = WorkflowState(workflowId = msg.workflowId)
        } else {
            // this should never happen
            if (state.workflowId != msg.workflowId) {
                logger.error("Inconsistent workflowId in message:%s and State:%s)", msg, state)
                return
            }
            // a non-null state with WorkflowDispatched should mean that this message has been replicated
            if (msg is WorkflowDispatched) {
                logger.error("Already existing state for message:%s", msg)
                return
            }
        }

        if (msg is DecisionCompleted) {
            // check ongoing decision
            if (state.ongoingDecisionId != msg.decisionId) {
                logger.error("Inconsistent decisionId in message:%s and State:%s", msg, state)
                return
            }
            // remove ongoing decision from state
            state.ongoingDecisionId = null
        } else {
            if (state.ongoingDecisionId != null) {
                // buffer this message to handle it after decision returns
                state.bufferedMessages.add(msg)
                // save state
                stater.updateState(msg.getStateId(), state)
                return
            }
        }

        when (msg) {
            is WorkflowDispatched -> dispatchWorkflow(state, msg)
            is DecisionCompleted -> completeDecision(state, msg)
            is TaskCompleted -> completeTask(state, msg)
            is ChildWorkflowCompleted -> completeChildWorkflow(state, msg)
            is DelayCompleted -> completeDelay(state, msg)
            is EventReceived -> eventReceived(state, msg)
            is WorkflowCompleted -> workflowCompleted(state, msg)
        }
    }

    private fun dispatchWorkflow(state: WorkflowState, msg: WorkflowDispatched) {
        val decisionId = DecisionId()
        // define branch
        val branch = Branch.Handle(workflowData = msg.workflowData)
        // initialize state
        state.ongoingDecisionId = decisionId
        state.runningBranches.add(branch)
        // create DecisionDispatched message
        val decisionInput = DecisionInput(listOf(branch), filterStore(state.store, listOf(branch)))
        val m = DecisionDispatched(
            decisionId = decisionId,
            workflowId = msg.workflowId,
            decisionName = DecisionName(msg.workflowName.name),
            decisionData = DecisionData("".toByteArray()) // AvroSerDe.serialize(decisionInput))
        )
        // dispatch decision
        dispatcher.dispatch(m)
        // save state
        stater.createState(msg.getStateId(), state)
    }

    private fun completeDecision(state: WorkflowState, msg: DecisionCompleted) {
        TODO()
    }

    private fun completeTask(state: WorkflowState, msg: TaskCompleted) {
        TODO()
    }

    private fun completeChildWorkflow(state: WorkflowState, msg: ChildWorkflowCompleted) {
        TODO()
    }

    private fun completeDelay(state: WorkflowState, msg: DelayCompleted) {
        TODO()
    }

    private fun eventReceived(state: WorkflowState, msg: EventReceived) {
        TODO()
    }

    private fun workflowCompleted(state: WorkflowState, msg: WorkflowCompleted) {
        TODO()
    }

    private fun filterStore(store: Store, branches: List<Branch>): Store {
        // Retrieve properties at step at completion in branches
        val listProperties1 = branches.flatMap {
            b -> b.steps.filter { it.propertiesAfterCompletion != null }.map { it.propertiesAfterCompletion!! }
        }
        // Retrieve properties when starting in branches
        val listProperties2 = branches.map {
                b -> b.propertiesAtStart
        }
        // Retrieve List<PropertyHash?> relevant for branches
        val listHashes = listProperties1.union(listProperties2).flatMap { it.properties.values }
        // Keep only relevant keys
        val properties = store.properties.filterKeys { listHashes.contains(it) }

        return Store(properties)
    }
}