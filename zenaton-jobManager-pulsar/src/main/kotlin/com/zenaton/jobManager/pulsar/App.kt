/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package com.zenaton.jobManager.pulsar

import com.zenaton.common.data.AvroSerializedData
import com.zenaton.common.data.AvroSerializationType
import com.zenaton.common.json.Json
import com.zenaton.jobManager.data.JobOutput

fun main(args: Array<String>) {
    val p1 = JobOutput("fdggsdf".toByteArray(), AvroSerializationType.JSON)
    println(p1)

    val p2 = Json.parse<AvroSerializedData>(Json.stringify(p1))
    println(p2)

    val p3 = Json.parse<JobOutput>(Json.stringify(p2))
    println(p3)
    println(p3 == p1)

    val p4 = Json.parse<AvroSerializedData>(Json.stringify(p3))
    println(p4)
    println(p4 == p2)
//    Assert.assertTrue(SchemaCompatibility.schemaNameEquals(newSchema, oldSchema))
//    Assert.assertNotNull(compatResult)
//    Assert.assertEquals(
//        SchemaCompatibility.SchemaCompatibilityType.COMPATIBLE,
//        compatResult.getType()
//    )
//    val client = PulsarClient.builder().serviceUrl("pulsar://localhost:6650").build()
//    val producer = client.newProducer(AvroSchema.of(AvroTaskEngineMessage::class.java)).topic("persistent://public/default/tasks").create()
//
//    var msg = DispatchTask(
//        taskId = TaskId(),
//        taskName = TaskName("MyTask"),
//        taskData = TaskData("abc".toByteArray()),
//        workflowId = WorkflowId()
//    )
//
//    producer.send(TaskAvroConverter.toAvro(msg))
//
//    producer.close()
//    client.close()
}
