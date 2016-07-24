# Simple-long-polling

Please see https://nzhong.github.io/blog/2016/07/simple-long-polling

To try it out, you can do the following:

* git clone https://github.com/nzhong/simple-long-polling
* cd simple-long-polling
* mvn clean package
* java -jar target/simple-long-polling-1.0-SNAPSHOT.jar
* Open up your chrome browser, and load "http://127.0.0.1:9000/static/index.html"
* Open your Chrome's Developer Tools -> Console, and see the messages.


I tweaked the client side timeout a bit, just to add some varieties. The server side will wait randomly between 5-15 seconds, and the client side make its AJAX requests timeout = 12 seconds. So in the Chrome console you should see something like

* After 7002ms the server responds with ECHO.
* After 6004ms the server responds with ECHO.
* 2 request timed out.
* After 9003ms the server responds with ECHO.
* ...
