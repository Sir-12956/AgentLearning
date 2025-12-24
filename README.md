## 🇨🇳 中文版：项目说明

### 项目概述

本项目实现了一个 **基于 LLM 的可扩展 Agent 框架**，以 Java 编写，采用 **显式状态机（State Machine）架构**，支持 **Planning、Tool 调用、Observation 结构化、错误恢复与多步推理**，目标是构建一个**接近生产可用的 Agent 原型**。

该 Agent 不再依赖“隐式 Prompt 魔法”，而是通过 **明确的状态流转、可观测的数据结构和可控的执行逻辑**，实现稳定、可调试、可演进的智能体系统。

---

### 核心设计思想

#### 1. 显式状态机（Explicit State Machine）

Agent 的执行流程被拆分为明确的状态，例如：

* `INIT`：初始化与参数校验
* `PLANNING`：由 LLM 生成执行计划
* `TOOL_CALLING`：根据计划选择并调用工具
* `OBSERVATION`：结构化收集工具执行结果
* `DECISION`：判断是否继续执行下一步
* `FINISHED / ERROR`：正常结束或错误回退

每一步都**可追踪、可中断、可回滚**，避免“黑盒式 Agent”。

---

#### 2. Planning 驱动，而非 Prompt 碰运气

Agent **先规划（Planning）再执行（Execution）**：

* LLM 只负责「决策与规划」
* Tool 只负责「确定性执行」
* Agent 负责「流程编排与状态推进」

这使得：

* 多工具组合成为可能
* Tool 不会被“跳过”或“幻想执行”
* 执行顺序具备可控性

---

#### 3. Tool 机制（Function / Tool Calling）

每个 Tool 都具备：

* 明确的 `name / description / parameters`
* 确定性的 `execute()` 实现
* 可被 Planning 阶段显式选择

示例工具包括：

* `TimeTool`：获取当前时间
* `RandomNumberTool`：生成随机数
* `EchoTool`：重复输出指定文本

Agent **不会假设自己“会做”工具的事**，所有外部能力必须通过 Tool。

---

#### 4. Observation 结构化（关键设计）

所有 Tool 执行结果都会被封装为统一的 `Observation` 结构，例如：

* 工具名称
* 输入参数
* 执行状态（SUCCESS / FAILED）
* 结构化结果数据
* 错误信息（如有）

Observation 是：

* Agent 的“事实来源”
* 后续决策的唯一依据
* Debug / Logging / Replay 的基础

---

#### 5. 错误处理与回退策略

Agent 内置多种防护机制：

* **最大循环步数限制**（防止死循环）
* **Tool 调用失败回退**
* **Planning 失败兜底**
* **超时 / 非法输出检测**
* 状态级别的错误隔离

确保 Agent 在异常情况下 **可失败、可恢复、可终止**。

---

### 当前能力总结

✅ 多 Tool 协同
✅ 显式 Planning
✅ Observation 结构化
✅ 显式状态机执行
✅ 可扩展 Action / Tool
✅ 接近生产可维护性

---

### 适用场景

* LLM Agent 架构学习与实验
* 企业级 Agent 原型
* Tool-Driven AI 系统
* 可调试 / 可观测的智能体系统

---

## 🇺🇸 English Version

### Project Overview

This project implements a **tool-driven LLM Agent framework in Java**, built on an **explicit state machine architecture**.
It supports **planning, tool execution, structured observations, error recovery, and multi-step reasoning**, aiming to be a **production-approachable Agent prototype**.

Instead of relying on implicit prompt tricks, this Agent emphasizes **explicit control flow, structured data, and deterministic execution**, making it stable, debuggable, and evolvable.

---

### Key Design Principles

#### 1. Explicit State Machine

The Agent execution is modeled as a clear state machine, including states such as:

* `INIT`
* `PLANNING`
* `TOOL_CALLING`
* `OBSERVATION`
* `DECISION`
* `FINISHED / ERROR`

Each state transition is explicit and traceable, avoiding black-box behavior.

---

#### 2. Planning-First Execution

The Agent follows a **Plan → Execute → Observe → Decide** loop:

* LLM handles **planning and decision-making**
* Tools handle **deterministic execution**
* Agent orchestrates the workflow

This ensures tools are never skipped or hallucinated.

---

#### 3. Tool-Based Capability Model

All external capabilities are implemented as Tools with:

* Clear metadata (name, description, parameters)
* Deterministic `execute()` logic
* Explicit invocation decided during planning

Example tools include:

* `TimeTool`
* `RandomNumberTool`
* `EchoTool`

The Agent never assumes tool results — all actions must be executed.

---

#### 4. Structured Observation Layer

All tool results are captured as structured `Observation` objects, including:

* Tool name
* Input arguments
* Execution status
* Structured output
* Error details (if any)

Observations serve as the **single source of truth** for future decisions, logging, and debugging.

---

#### 5. Error Handling & Safety Guards

The Agent includes multiple safeguards:

* Max step limits
* Tool execution fallback
* Planning failure recovery
* Timeout and invalid output detection
* State-level error isolation

This ensures the Agent fails safely and predictably.

---

### Capabilities Summary

✅ Multi-tool coordination
✅ Explicit planning
✅ Structured observations
✅ State-machine execution
✅ Extensible tools/actions
✅ Production-oriented design
