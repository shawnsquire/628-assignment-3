Assignment 3
=============
**Due: April 20th**

This assignment involves building a social application that allow you to find friends 
around you in a 1 km radius.Here are some of the features of the application that I 
want you build. [Note: There would be four assignments and I will take the best 3]

1. When the application starts it should register with a username and a password, and 
a person’s name. Every time the application restarts, the user has to authenticate with 
the server making sure that the username and password are correct. For the present 
application, you can send the password in plaintext.

2. After authenticating, the application should open up a google map interface which 
shows the user’s present location and the location of other people who have 
registered with the service and are within one kilometer of the user.  Clicking on the 
pushpin you should display the name of the person. The way you can implement it is 
the following. When the application boots up, it queries a php script on the backend 
and finds the location of the people registered for the service within a radius. You can 
use the php script that we discussed in class, as a reference.

3. **[Extra Credit: 4 points]** This is for extra credit and you should do this only if you 
have the time. I want you to build a messaging service on top of the friend finding 
application. On clicking the pushpin (representing the person around you) in addition 
to  displaying the name of the person and you should have the option of writing a 
message and clicking send. The message should be sent to the server using the 
relaying php script and stored in a database. When the receiver polls the php script it 
should get all the message addressed to him. Note that I do not want you to 
implement any notification though that might be a better way of implementing this 
(polling is sufficient for this application). The receiver should be able to see all the 
messages addressed to him with the name of the person who sent the message. The 
choice of the UI element is upto your group.

4. What you need: you need an account on my server. Groups that have already send 
me a username request, the account would be created for you this week (I will send a 
personal email to the groups). The server is called mpss.csce.uark.edu. Your php 
scripts will reside in a public\_html which I will create with the proper permissions. 
You will have access to the sql server on mpss. I have created a database called mobsys 
and you should create your tables in that database. Since you will be sharing the 
database, please do not delete anyone else’s table. 

5. What you need to submit: Like last time, you will upload the code on the google site
and create a video and upload a link to the video. This time there **WON’T** be any best 
assignment extra credit awarded, since we are awarding 4 extra points for part 3 of
the assignment.
