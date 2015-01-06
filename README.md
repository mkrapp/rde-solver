rde-solver
==========

This is a Java framework to solve 2D reaction-diffusion systems/models. I used it 2008 for my diploma thesis ([link](https://www.mariokrapp.com/docs/diplomarbeit_mario_krapp.pdf)).
Several RDE models are supported. Have a look into `src/models`. To name a few:

=======
* [Hodgkin-Huxley Model](http://en.wikipedia.org/wiki/Hodgkin–Huxley_model)
* [FitzHugh-Nagumo Model](http://en.wikipedia.org/wiki/FitzHugh–Nagumo_model)
* [Beeler-Reuter Model](http://jp.physoc.org/content/268/1/177)
* [Fenton-Karma Model](http://journals.aps.org/prl/abstract/10.1103/PhysRevLett.81.481)
* [Minimal Model](http://dx.doi.org/10.1016/j.jtbi.2008.03.029)

You can add your own model as implementation of the abstract class [`RDEModel`](src/models/RDEModel.java).

Building and Running
--------------------

You need `ant` (build tool) to compile and run the model. Simply type

```
ant [clean,compile,jar,run]
```

Example
-------

Here's a spiral wave solution for the [FitzHugh-Nagumo Model](http://en.wikipedia.org/wiki/FitzHugh–Nagumo_model):

![spiral](https://cloud.githubusercontent.com/assets/5938262/5614071/46ef5958-94ed-11e4-80ce-964830fb1eb7.gif)

Here's the excitation wave of the same model propagating from a point stimulus:

![target](https://cloud.githubusercontent.com/assets/5938262/5627334/f866c74e-9596-11e4-9549-a07176d3b0e0.gif)
