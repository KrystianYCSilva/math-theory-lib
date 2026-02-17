---
name: spec-kit-fundamentals
description: |
  Guide for working with specifications and the spec-kit system including feature specs, plans, research, and structured development workflows.
  Use when: creating feature specifications, developing structured plans, conducting research synthesis, or following spec-driven development workflows.
---

# Spec-Kit Fundamentals

The spec-kit system provides a structured approach to feature development through specifications, plans, research, and task management. This skill teaches how to work with specs and the spec-kit infrastructure for creating well-defined, research-backed features.

## How to create feature specifications

Develop comprehensive feature specifications using the spec-kit template system.

- **Start with user stories**: Define clear user journeys with priorities (P1, P2, P3)
  - Format: "As a [user type], I want [capability] so that [benefit]"
  - Include acceptance scenarios with Given/When/Then format
- **Define functional requirements**: Use FR-001, FR-002 format with MUST/SHOULD statements
- **Include success criteria**: Measurable outcomes (SC-001, SC-002) that verify feature completion
- **Document key entities**: Define data models and relationships without implementation details
- **Address edge cases**: Consider boundary conditions, error scenarios, and system limits

## How to develop implementation plans

Create structured implementation plans that guide feature development.

- **Technical context**: Document language/version, dependencies, storage, testing, platform requirements
- **Project structure**: Define directory layouts for documentation and source code
- **Constitution check**: Verify plan aligns with project governance rules (T0 rules)
- **Structure decision**: Document chosen architecture and reference real directories
- **Complexity tracking**: Justify any constitution violations with alternatives considered

## How to conduct research synthesis

Synthesize research from academic papers and technical documentation into actionable insights.

- **Academic references**: Integrate findings from 87+ research papers (IEEE, ACM, arXiv)
- **Practical application**: Connect theoretical concepts to implementation patterns
- **AI-instruments principles**: Apply reification of user intent, reflection, and anchoring
- **Trustworthy AI**: Incorporate security and verifiability from the start
- **JIT resource management**: Plan for just-in-time loading of supplementary content

## How to manage structured development workflows

Follow the spec-kit workflow for consistent, research-backed feature development.

- **Phase 1: Understanding**: Extract concrete examples from user requirements
- **Phase 2: Planning**: Identify reusable resources (scripts, references, assets)
- **Phase 3: Implementation**: Follow structured templates for consistency
- **Phase 4: Validation**: Test each user story independently for MVP delivery
- **Phase 5: Iteration**: Update specs and plans based on real usage feedback

## How to work with spec-kit templates

Utilize the spec-kit template system for consistent artifact generation.

- **spec-template.md**: Create feature specifications with user stories and requirements
- **plan-template.md**: Develop implementation plans with technical context
- **tasks-template.md**: Generate task lists organized by user story for parallel work
- **checklist-template.md**: Create validation checklists for quality assurance
- **agent-file-template.md**: Define agent behaviors and skill compositions

## Common Warnings & Pitfalls

### Specification Issues
- **Vague requirements**: Ensure all functional requirements are testable and specific
- **Missing edge cases**: Consider error scenarios, boundary conditions, and system limits
- **Unclear entities**: Define key data models without implementation details

### Planning Issues  
- **Overlooking constitution**: Verify all plans comply with T0 governance rules
- **Ignoring technical debt**: Account for complexity trade-offs in structure decisions
- **Insufficient context**: Include all necessary technical details for implementation

### Research Integration
- **Theory vs practice**: Bridge academic concepts with practical implementation
- **Citation accuracy**: Verify all research references and paper attributions
- **Knowledge obsolescence**: Update research synthesis as new papers emerge

## Best Practices

| Level | Focus | Key Practices |
|-------|-------|---------------|
| **Beginner** | Template Usage | Follow spec-kit templates exactly, understand user story format, practice acceptance scenarios. |
| **Intermediate** | Research Integration | Synthesize academic papers into implementation patterns, connect theory to practice, apply AI-instruments principles. |
| **Expert** | Workflow Optimization | Parallelize user story development, optimize JIT resource loading, advance governance compliance. |

## Application Areas

- **Feature Development**: Structured approach to new capability implementation
- **Research Projects**: Academic paper synthesis and practical application
- **Quality Assurance**: Validation checklists and success criteria definition
- **Team Coordination**: Parallel development through user story organization
- **Governance Compliance**: Constitution adherence and rule enforcement

## References

- [Agent Skills Specification](https://agentskills.io)
- [Model Context Protocol](https://modelcontextprotocol.io)
- [AI-Instruments: Embodying Prompts as Concretes](https://dl.acm.org/doi/full/10.1145/3706598.3714259)
- [Trustworthy and Explainable Artificial Intelligence Review](https://ieeexplore.ieee.org/iel7/6287639/10005208/10188681.pdf)