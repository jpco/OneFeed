### main.*
 - OneFeed
 - OneFeedView (interface)
 - OneFeedController (interface)

### util.*
 - FeedEvent
 - FeedListener
 - JsonTree
 - FeedVC (????)
 - EncryptedPreferences (!!!)

### feed.*
**should probably make a real inheritance tree, there will be a lot of repeated code, but I still don't know how it's gonna work!**
 - Feed (abstract class)
 - DummyFeed
 - TumblrFeed
 - TwitterFeed

### prefs file: (remember dependency injection bit from class?!)
 - what feeds to use
 - what view/controller to use
