---
name: workflow-scripts
description: |
  Documentation file for agent operations and skill usage.
  Use when: you need procedural guidance for agent execution and context management.
---

# Workflow Scripts

## 1. The "Spec-First" Template (`spec.md`)

```markdown
# Feature: [Name]

## Context
[Why are we building this?]

## Requirements
- [ ] Req 1: ...
- [ ] Req 2: ...

## Technical Plan
- [ ] Create `models/user.ts`
- [ ] Update `api/auth.ts`
- [ ] Add tests in `tests/auth.test.ts`
```

## 2. Git Hook for AI Commits
Add to `.git/hooks/prepare-commit-msg`:

```bash
#!/bin/sh
# If message is empty, generate one from diff
if [ -z "$2" ]; then
    git diff --cached | llm "Generate a conventional commit message for this diff" > "$1"
fi
```

## 3. TDD Loop Script (`tdd.sh`)

```bash
#!/bin/bash
# Usage: ./tdd.sh "Description of feature"
llm "Generate a test file for: $1" > tests/feature.test.ts
npm test
# (Fail)
llm "Write code to pass tests/feature.test.ts" > src/feature.ts
npm test
# (Pass)
```

