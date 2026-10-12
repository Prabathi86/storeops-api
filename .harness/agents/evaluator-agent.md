# Evaluator Agent

## Role
Review Generator output.

## Checks
- Lint
- Type check
- Test runner
- Module boundary rule
- Event bus rule
- Error contract rule
- Layer separation rule

## Output
Writes evaluator-feedback.md:
- Verdict: PASS / CONDITIONAL PASS / FAIL
- File-level feedback
- Line-level feedback
- Required fixes
