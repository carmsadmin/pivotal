pivotal-track-state
===================

Pivotal Java Object Change Tracking &amp; Mapping Library

* Pivotal Track State - Tracks the state of the object, works well with the 
Mapping Utility which will allow you to map flat objects to various entities
at a time, keeping track of all the changes that happened.

* Works well with web sessions, however, we've also managed to use them in CRUD based
web applications with minimal sessions (e.g. security context), the trick is to
load your data, map the posted form data (e.g. value object) back to the pulled 
value object, then map it to entities, and then persist, works well if the proxy 
is kept in tact. OR use some sort of cache, like ehcache or coherence.
- this sounds like a lot of work, but, if you structure your application 
properly, its as simple as one method call; the mapping utility does most
of the work for you.


- Current version 1.2 - STABLE


*Code Samples in Source under src/test/java*

Used mainly for tracking changes within an instance of an object, and mapping it to other objects.

Useful for separating objects used for the presentation layer, tracking changes, and mapping it back to entities.

For example: you can have a class that represents your entire form, and have it mapped to numerous entities.

The mapping utility @MapClass, @MapClasses used in conjunction with @TrackState and @Track annotations can automatically create entities that require persistence.
