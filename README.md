https://user-images.githubusercontent.com/62771420/217575348-c120d251-8f59-4187-b870-5b80dc8bb63f.mp4

# Cruise

Cruise is a web and mobile app inspired by Uber and Lift, developed as our college project. **So if you're up for a ride, let's cruise!** 🚗 💨

## Structure

Cruise is entirety of 5 classes that we took this semester:

- Software Backend Engenieering
- Software Frontend Engenieering
- Mobile Application Engenieering
- Software Tests Engenieering
- Agile Software Managemant

🎁 BONUS: For purpose of showing rides on map, we have developed a python script that simulates cars movements.

In the following sections we want to present you some core concepts that we have enjoyed  implementing. 😉 

### Backend layer

Our REST API is developed in Java's Spring Boot framework. This is the first time that we were introduced with the concept of Dependecy Injection, and how it is implemented in Spring with useg of Aspect oriented programming coupled with Annotations. In our app there are 3 user roles: Admin 👨‍💼, Driver 👮‍♂️ and Passenger 🙋‍♀️, and all of them are authenticated and authorized by JWT. For communication with database we took advantage of Spring's JPQL and wrote our custom queries and methods. Finally, for experience of seamless communication betwean passenger and driver while riding together, we implemented web sockets.

### Frontend layer

Our client application is developed in Angular framework, where we learned a lot about JavaScript and TypeScript, client-side rendering, component tree, http communication and observables. In the pictures below we tried to show you that our Driver and Passenger have real time communication when riding together, and for achieving such functionlity we used STOMP protocol over WebSoket. By the end, we came to conclusion that Angular is good if you want to separate UI logic and template generating, but for some more dynamic UI, frameworks like React may do a better job. 🤔 

### Mobile App

Our mobile application is developed as Android native app 📱 and published to Google Play Store, so if you want [check it out](). The main concept that we got to expirience in practise is how Android Activity and Fragment Lifecycles ♻️ work and what kind of job is suited for each one of them. Also, we implemented chat communication where users receive and send messages in real time, by making use of basic Java WebSockets and their sessions.

### Testing

We wanted to ensure that our core functionallity works properly, so we provided unit(JUnit 5), integration (JUnit 5, Jasmin🌸 and Karma) and end-to-end (Selenium) tests. We started by testing our Spring Boot repositories then proceeded to mocking nad stubing for our service unit tests and concluded with integrated mvc test of our controllers. Testing angular components with Jasmin was peace of cake 🧁 and for more production-like circumstances we tested passenger ride requesting and different ride states with end-to-end tests.

### Scrum

The whole proccess of our app development was tracked on project management tool ClickUp 👆. Each week we had a meeting for sprint planning with our "Scrum master" where we agreed on goals for the next sprint. Each sprint was evaluated by definition of done and internally reviewed and retrospected in our development team.

