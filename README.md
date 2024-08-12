# PlatformForSimulationTrainingEvaluation
A platform which can be used to simulate OETPN, train the model and evaluate the performances.

## Project structure
There are 2 main packages, `OETPN` and `Component`. 

### `OETPN`
This package contains all the classes related to OETPN and their execution. 
- `RunnableModel` represents an abstract runnable model: the `step` method is all you need in order to get started. This should perform one simulation step for your model, be it synchronous (EventType.tic) or asynchronous(EventType.input). For the asynchronous event, the `step` method should be used together with `addInputToken`.
- `OETPN` represents a runnable OETPN model with places and transitions. 
  - Transitions can be defined using their name, delay and a `TokenProcessor` (a function which describes how the transition output is generated from the input tokens).
  - OutputTransitions must have delay 0 and only consume tokens. The transition action defines how and where the tokens are inserted.
- The Token can be of type
  - `FuzzyToken`, with values NL, NM, ZR, PM, PL and everything in between
  - `NumberToken` with one floating-point value
  - any `RunnableModel` can also be used as a token (active token), which can be started by the mapping function of a transition

In the `Examples` folder you can find some working examples
### `Component`
This package contains the standard unit displayed below. It has as input port a place (named "P0") and as output port a transition. The component contains 3 blocks: OETPN, controller, plant.
Controller and plant can also be a component.

For now,the OETPN has a basic structure: input port is connected to the output port, a default token is being passed to the plant, then the plant output is passed to the controller, etc.

![component.png](docs%2Fcomponent.png)

## Running the project
When you create a model, you can run it step by step or as a thread. To add asynchronous input, you need to:
- call `addInputToken` method
- make sure to call the `step(EventType.input)` method. This will execute everything that can be executed without any delay.

You can run the examples individually to see how everything works.

## Training new models 
This will be supported in future versions.
