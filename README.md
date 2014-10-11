### OneFeed

An experiment in unifying social media feeds. Made to practice:
 - threading
 - login management and security
 - APIs
 - extensibility/modularity
 - maybe later, graphical frontends

Still nearly completely non-functional and in-development.

### Components:
 - `OneFeed`: the main logical center of the application.
 - `Feed`: an abstract class defining the (very basic) behavior of a feed. Every feed must implement
 Runnable, must be initialized with a OneFeed in the constructor, and must interact with its OneFeed
 through FeedEvents.
 - `FeedEvent`: the feed event a Feed sends to the OneFeed. Currently very basic... it'll expand as the
 implementation gets more sophisticated.

Right now the only type of Feed (besides the abstract superclass) is a DummyFeed; my first goal will be to make some Feeds I can test (Facebook and Tumblr will probably be first to come).

###Bugs:
 - Doesn't really do anything yet.


###To-do:
 1. create account/login controls & storage
 2. add some Feeds
 3. make Feeds \& FeedEvents more robust (links to originating posts, icons for different sources)
 4. ...
 5. graphical support! I want things to be majority frontend-independent, so people can have lots
 of different frontend types, from CLI to web interface to GTK+ application.
