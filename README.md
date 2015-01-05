rde-solver
==========

This is a Java framework to solve 2D reaction-diffusion systems/models. I used it 2008 for my diploma thesis ([link](https://www.mariokrapp.com/docs/diplomarbeit_mario_krapp.pdf)).
Several RDE models are supported. Have a look into `src/models`. To name a few:

* [Hodgkin-Huxley Model](http://en.wikipedia.org/wiki/Hodgkin–Huxley_model)
* [FitzHugh-Nagumo Model](http://en.wikipedia.org/wiki/FitzHugh–Nagumo_model)
* [Beeler-Reuter Model](http://jp.physoc.org/content/268/1/177)
* [Fenton-Karma Model](http://journals.aps.org/prl/abstract/10.1103/PhysRevLett.81.481)
* [Minimal Model](http://dx.doi.org/10.1016/j.jtbi.2008.03.029)

You can add your own model as implementation of the abstract class [`RDEModel`](src/models/RDEModel.java).


Building and Running
--------------------

You need:
* `commons-cli-1.2.jar` (external library)
* `ant` (build tool)

Some files depend on the command line library `commons-cli-1.2.jar` (download [here](http://archive.apache.org/dist/commons/cli/binaries/commons-cli-1.2-bin.tar.gz)).
Download the archive, untar it, and put the file `commons-cli-1.2.jar` into a directory named `libraries` or edit [`build.xml`](build.xml) accordingly if the file is placed elsewhere.

Example
-------

Here's a spiral wave solution for the [FitzHugh-Nagumo Model](http://en.wikipedia.org/wiki/FitzHugh–Nagumo_model):

![spiral](https://cloud.githubusercontent.com/assets/5938262/5614071/46ef5958-94ed-11e4-80ce-964830fb1eb7.gif)
