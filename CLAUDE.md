# CLAUDE Harness Orchestrator (StoreOps)

This file defines the autonomous development harness used to extend the StoreOps codebase.
It coordinates four agents:

1. Planner
2. Generator
3. Evaluator
4. Monitor

The harness runs in autonomous loops once the developer approves the Planner’s spec.

---

# 1. ENTRY PROMPT FORMAT (Developer → Planner)

To start a harness run, the developer provides:

