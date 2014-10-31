### OneFeed

An experiment in unifying social media feeds. Made to practice:
 - threading
 - login management and security
 - APIs
 - extensibility/modularity
 - maybe later, graphical frontends

Still nearly completely non-functional and in-development.

### Components:
Primary components:
 - `OneFeed`: the main logical center of the application.
 - `Feed`: an abstract class defining the basic behavior of a feed. 
 - `FeedEvent`: the feed event a Feed sends to the OneFeed. Currently very basic...will probably
 	end up being/holding a JSON-based map structure.
 - `OneFeedFrontend`: an abstract superclass for dealing with display & user interaction.

### Feeds:
 - `TwitterFeed`: uses Twitter streaming API, actually (very technically) functional
 - `TumblrFeed`: uses normal REST API, will be the first Feed I have to set up polling for

UNFORTUNATELY, Facebook does not allow apps to read the newsfeed *OR* their own notifications
without review, and specifically says "Desktop apps will not be granted [these] permission[s]".

###To-do:
 - encrypted prefs, for better security (obviously!)
 - JSON parsing and a system for unifying different JSON syntaxes into one OneFeedFrontend-readable
	one.
 - graphical support! I want things to be mainly frontend-independent, so
 people can have lots of different frontend types, from CLI to web interface to GTK+ application.
