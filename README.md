Messenger
=========

Android Development

This application called Messenger contains the following components: Activity (main activity, displays messages), 
server thread (listens to the default port for incoming connection), and client thread (connects to a server).

How to test:
set up the redirect network environment by these commands:
  Set up device with 5554 number:
    telnet localhost 5554
    redir add tcp:11108:10000
  Set up device with 5556 number:
    telnet localhost 5556
    redir add tcp:11112:10000
