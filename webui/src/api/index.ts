type WorkflowName = string;

export * from "./tasks";

interface WorkflowStartedMetric {
  readonly value: number;
  readonly previousValue: number;
}

interface WorkflowCompletedMetric {
  readonly value: number;
  readonly previousValue: number;
}

interface WorkflowErrorRateMetric {
  readonly value: number;
  readonly previousValue: number;
}

interface WorkflowMetrics {
  readonly workflowName: WorkflowName;
  readonly metrics: [
    WorkflowStartedMetric,
    WorkflowCompletedMetric,
    WorkflowErrorRateMetric
  ];
}

export function getAllWorkflowMetrics(): WorkflowMetrics[] {
  return [
    {
      workflowName: "SequentialWorkflow",
      metrics: [
        { value: 71897, previousValue: 70946 },
        { value: 70946, previousValue: 71897 },
        { value: 28.62, previousValue: 26.14 }
      ]
    },
    {
      workflowName: "AsyncWorkflow",
      metrics: [
        { value: 71897, previousValue: 70946 },
        { value: 71897, previousValue: 70946 },
        { value: 28.62, previousValue: 26.14 }
      ]
    },
    {
      workflowName: "ParallelWorkflow",
      metrics: [
        { value: 71897, previousValue: 70946 },
        { value: 70946, previousValue: 71897 },
        { value: 26.14, previousValue: 28.62 }
      ]
    }
  ];
}
