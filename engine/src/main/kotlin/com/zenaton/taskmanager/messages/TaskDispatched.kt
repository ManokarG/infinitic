package com.zenaton.taskmanager.messages

import com.zenaton.engine.data.DateTime
import com.zenaton.engine.data.WorkflowId
import com.zenaton.taskmanager.data.TaskData
import com.zenaton.taskmanager.data.TaskId
import com.zenaton.taskmanager.data.TaskName
import com.zenaton.taskmanager.messages.interfaces.TaskMessageInterface

data class TaskDispatched(
    override var taskId: TaskId,
    override var sentAt: DateTime? = DateTime(),
    val taskName: TaskName,
    val taskData: TaskData?,
    val workflowId: WorkflowId? = null
) : TaskMessageInterface