# category

Category-theory foundations for `mathsets-kt` Wave 1 (Phase E).

## Overview

This module provides the initial categorical core:

- `Category<Obj, Mor>` and `Morphism<Obj>`
- `Functor` and functor composition
- `NaturalTransformation`
- `Isomorphism`
- `OppositeCategory` and `ProductCategory`
- Concrete finite categories:
  - `FinSetCategory`
  - `FinGroupCategory` (finite Int-based groups)
  - `ForgetfulFinGroupToFinSet`

## Notes

- Composition follows the convention `compose(f, g) = f o g`.
- Law checkers are finite-sample verifiers intended for tests and didactic usage.
