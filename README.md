pivotal-core
============
Core Java Components for all other Pivotal Java Libraries.

Core Components for all other Pivotal Projects.

Current version 1.3-STABLE

git'it, then mvn clean deploy/install
VERSIONS Stable version 1.3 - STABLE

addition of search engine to pivotal-simple-jpa,
fixes to JDBC DML operation execatuion causing exceptions with newer JDBC drivers such as Oracle 11.2.0.3 and UCP)
Stable Version 1.0.9 - STABLE (major fix in json deserializer) -- OBSOLETE version

JSON DESERIALIZER - highly recommend you use 1.0.9 or higher, 1.3-STABLE is the best version right now.
Stable Version 1.0.1 - STABLE (minor fixes) -- OBSOLETE

bytecode enhancement (e.g. proxies) abstraction classes using javaassist.

String, Number, and a bunch of other utilities.

Reflection utilities.

json serializer and deserializers, could be used in spring (https://jira.springsource.org/browse/SPR-5087)

bunch of other stuff, just check out the code, I'm sure you'll figure it out, or ask.

More updates will come, when I get time to actually update the site.

Send me an email or use the issue tracking, if you have questions.

pivotal-simple-jpa
==================
Pivotal Simple Java Persistence API

Pivotal SimpleJPA - ITS VERY SIMPLE AND FAST
Simple object-relational-mapping library works with most JPA-annotations.
Current version 1.3 - STABLE

fix to DML operation execution ResultSet returning integers and expecting a resulset.
IF YOU WANT TO CONTRIBUTE, PLEASE EMAIL THE OWNERS *

No real documentation yet, check wiki please.

Supports Oracle via pivotal-simple-jpa-oracle module

Supports PostgreSQL via pivotal-simple-jpa-postgresql module

MySQL support would be nice, I'm sure someone can help with this, its not that painful.

Check wiki for configuration guide - if you have issues, email me. code[at]p5solutions[dot]com

pivotal-simple-jpa-oracle
=========================
Pivotal Simple Java Persistence API - Oracle Extensions

Pivotal SimpleJPA - Oracle Extension
Simple object-relational-mapping library works with most JPA-annotations.

See Pivotal SimpleJPA project and wiki for details

Current version 1.3 - STABLE

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
