# Guarded Query Answering

This project's aim is to perform atomic query answering using guarded TGDs. It supports two materialization-based approaches:
- an approach where the depth of the chase is bound
- an approach where the chase is performed using a Datalog saturation of the guarded TGDs.

## Dependencies

The chase is performed using the [Graal V2](https://gitlab.inria.fr/rules/graal-v2/-/tree/develop/graal) (currently in beta) and the Datalog saturation tools [Guarded saturation](https://github.com/KRR-Oxford/Guarded-saturation).
