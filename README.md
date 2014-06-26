# sockplay

My attempt on creating a multi-room webchat using Clojure & AngularJS.
It's still an ugly one (not yet well organised and no test), especially on the Angular part
since I'm totally new to Angular thus no idea of how angular conventions are.
However the codes are simple and straightforward especially on the clojure side.

The key here is the Channel atom that is a vector of map of each individual user,
containing {:username [string] :chatroom [string] :channel [native http-kit websocket channel]}

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein run

## License

Copyright © 2014 EPL same as Clojure
