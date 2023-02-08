<video src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/cruise-video.mp4" controls="controls" style="max-width: 730px;">
</video>

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

Our REST API is developed in Java's Spring Boot framework. This is the first time that we were introduced to the concept of Dependency Injection, and how it is implemented in Spring with the use of Aspect-oriented programming coupled with Annotations. In our app there are 3 user roles: Admin 👨‍💼, Driver 👮‍♂️ and Passenger 🙋‍♀️, and all of them are authenticated and authorized by JWT. For communication with the database, we took advantage of Spring's JPQL and wrote our custom queries and methods. Finally, for the experience of seamless communication between passenger and driver, while riding together, we implemented web sockets.

### Frontend layer

Our client application is developed in the Angular framework, where we learned a lot about JavaScript and TypeScript, client-side rendering, component tree, HTTP communication, and observables. In the pictures below we tried to show you that our Driver and Passenger have real-time communication when riding together, and for achieving such functionality we used STOMP protocol over WebSoket. In the end, we concluded that Angular is good if you want to separate UI logic and template generating, but for some more dynamic UI, frameworks like React may do a better job. 🤔 

<img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img1.PNG" width="45%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img3.PNG" width="45%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img4.PNG" width="45%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img5.PNG" width="45%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img6.PNG" width="45%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img7.PNG" width="45%"></img><img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img8.PNG" width="45%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/img9.PNG" width="45%"></img>

### Mobile App

Our mobile application is developed as an Android native app 📱 and published to Google Play Store, so if you want [check it out](https://play.google.com/store/apps/details?id=com.cruisemobile.cruise). The main concept that we got to experience in practise is how Android Activity and Fragment Lifecycles ♻️ work and what kind of job is suited for each one of them. Also, we implemented chat communication where users receive and send messages in real-time, by making use of basic Java WebSockets and their sessions.

<img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_002950.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_003040.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_004159.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_003957.png" width="18%"></img><img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_003907.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_003939.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_004019.png" width="18%"></img><img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_004034.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_004053.png" width="18%"></img> <img src="https://github.com/tamarailic/cruise/blob/main/docs/assets/images/Screenshot_20230203_004336.png" width="18%"></img>

### Testing

We wanted to ensure that our core functionality works properly, so we provided unit(JUnit 5), integration (JUnit 5, Jasmin🌸 and Karma), and end-to-end (Selenium) tests. We started by testing our Spring Boot repositories then proceeded to mock and stub for our service unit tests and concluded with an integrated mvc test of our controllers. Testing angular components with Jasmin was peace of cake 🧁 and for more production-like circumstances we tested passenger ride requests and different ride states with end-to-end tests.

### Scrum

The whole process of our app development was tracked on the project management tool ClickUp 👆. Each week we had a meeting for sprint planning with our "Scrum master" where we agreed on goals for the next sprint. Each sprint was evaluated by definition of done and internally reviewed and retrospected by our development team.
 #
