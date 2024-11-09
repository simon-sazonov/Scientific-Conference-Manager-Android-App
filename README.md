# Scientific Conference Manager App

## Overview

This mobile application, developed as a final project for the Mobile Application Development course, allows users to manage, browse, and view details of scientific conferences and associated articles. Designed for efficiency and ease of use, it provides real-time information on upcoming conferences, schedules, and locations, with enhanced features for admins to manage content and moderate user interactions.

## Key Features

- **User Management**: Secure user authentication and authorization using Firebase.
- **Conference and Article Viewing**: Users can view comprehensive information on conferences, including schedules, location maps, and relevant articles.
- **Admin Panel**: Admin users can add, edit, and delete conference details, articles, and comments directly from the app.
- **Commenting System**: Users can leave comments and ask questions on articles, enhancing engagement.

## Technology Stack

- **Programming Language**: Kotlin, leveraging its concise syntax and interoperability for Android development.
- **Development Environment**: Android Studio, providing a powerful integrated development environment tailored for Android.
- **Backend & Database**: Firebase, utilizing both Firebase Authentication for secure user management and Firestore for scalable, real-time database solutions.
- **Mapping**: Integration with location-based services to display conference locations.

## Application Flow and Functionalities

1. **Authentication & Authorization**: 
   - Secure login using Firebase Authentication.
   - Role-based access where users and admins have distinct interfaces and permissions.

2. **Main User Interface**:
   - After logging in, users are directed to the Main Page, which lists current conferences and articles.
   - **Search Functionality**: Users can quickly find conferences or articles using a search bar.

3. **Article Details**:
   - Users can view article information, including authors, publication date, and full text.
   - A comment section allows users to engage with the content by posting comments or questions.

4. **Conference Details**:
   - The Conference Page provides details such as the event's schedule, location on a map, and specific sessions or presentations.

5. **Admin Features**:
   - Admins have access to a unique main page where they can manage existing articles, conferences, and comments.
   - Admins can add, edit, or delete conference information, articles, and user comments, providing robust content moderation tools.

## Database Structure

The app leverages Firebase Firestore to store information across various tables, enabling real-time data updates and scalability.

- **Authentication Database**: Manages user credentials securely (User ID, Email, Password).
- **Firestore Database**:
  - **Articles Table**: Stores information about conference-related articles (Article ID, Date, Author, Title, Main Text).
  - **Conferences Table**: Manages conference details (Conference ID, Title, Location, Start Date, End Date).
  - **Comments Table**: Logs user comments on articles (Comment ID, Article ID, Author ID, Main Text).
  - **Days Table**: Stores day-by-day conference details (Day ID, Activity Name, Conference ID, Info, Room, Time).

## Installation

To run this project locally:
1. Clone the repository.
2. Open the project in **Android Studio**.
3. Set up **Firebase Authentication** and **Firestore** as outlined in the documentation.
4. Build and run the application on an Android device or emulator.

## User Credentials for Testing

To log in as a regular user:
- **Email**: rodrigo@gmail.com
- **Password**: 123123

To log in as an admin:
- **Email**: admin@gmail.com
- **Password**: admin123
