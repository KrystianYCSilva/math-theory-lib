---
description: |
  Project overview: goals, scope, and current state.
  Use when: you need to understand what this project is and why it exists.
---

# Project Overview

## What

mathsets-kt (math-theory-lib) é uma biblioteca Kotlin Multiplatform que implementa computacionalmente a Teoria Axiomática dos Conjuntos (ZFC) e a construção completa da torre numérica (ℕ → ℤ → ℚ → ℝ → ℂ), combinando rigor matemático com engenharia de software.

## Why

Resolver o "Bootstrap Circular" entre Teoria dos Conjuntos e Teoria dos Números. Serve como ferramenta computacional eficiente E laboratório didático rigoroso, provando isomorfismo entre implementação otimizada (kernel) e construção axiomática.

## Scope

- **In:** Axiomas ZFC, Lógica de Primeira Ordem, MathSet (extensional + intensional), Relações (equivalência, ordens parciais/totais/bem-ordenadas), Funções (injeção, surjeção, bijeção, escolha), Construção de Von Neumann (ℕ), Construção de ℤ (pares de ℕ/~), ℚ (pares de ℤ/~), ℝ (sequências de Cauchy), Irracionais (algébricos + axiomáticos), ℂ (pares de ℝ), Ordinais (CNF até ω^ω), Cardinais (ℵ₀, argumento diagonal de Cantor, aritmética cardinal), Topologia finita e hierarquia de Borel, Combinatória (Ramsey, Bell, jogos de Gale-Stewart), Forcing (posets, filtros genéricos, independência de CH).
- **Out:** Interfaces gráficas complexas (além de demos), suporte a hardware exótico.

## Status

v1.0 completo (12 módulos implementados e testados). Build JVM e JS verde. Planejamento da expansão v2.0 documentado (algebra, analysis, topology, category, solvers, proof assistants).
