Examproject for Android App, KEA

My take on the bank app assignment for the exam.
The app contains almost the must-have requirements (being able to pay bills manually is missing). 
I've used Firebase as my data storage and I've used their Authentication system aswell.

Must-have requirements:

User-friendly design (not just the basics) - check

At least two activities - check

Restore Activity state change - check

Being able to transfer money between own and other accounts - Did this using firebase

Pay bills manually and automatically - Last part is implemented

Reset password - Used the firebaseAuth.sendPasswordRestEmail method for this

Register and login - Used the firebase authentication system

Use NemId in the above described cases - Used an alertDialog as part of my solution

Validate input from user - I validate a lot of user input throughout my app (login, correct recipient, correct amount etc.)

Exception & error handling - I've spent a lot of time on error handling my app.

Clean architecture - I think it's pretty clean

Implement Parceables models transferred between Activities - My account class implement parceable. An account object is sent 
from the MenuActivity class to the AccountActivity class everytime a user clicks on a specific account. This object is crucial for showing the correct data inside that account.

Implement a new feature in the app (not been taught by me in during class) - Spent a lot of time learning the features of firebase. I've implemented some I'll show at the exam.


Made by Mikkel Olsen	
