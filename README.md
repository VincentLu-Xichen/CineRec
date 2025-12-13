# Movie Recommendation and Tracker System

## Authors:
- Xichen Lu
- Zhengye Lu
- Wenbo Wu
- Qinghan Yang
- December 2025

## 1. Introduction
The objective of this project is to design and implement a Movie Recommendation and Tracker System using Java. The system consolidates object-oriented programming principles to allow users to manage watchlists, track viewing history, and receive personalized movie recommendations based on multiple strategies.

This report details the system architecture, algorithm logic, advanced features, and testing results.

## 2. System Design and Class Structure

### 2.1 Overall Architecture
The Movie Recommendation & Tracker System is designed using object-oriented programming principles. The overall design focuses on modularity, data separation, and ease of maintenance.

#### System Architecture Overview:
- **Data**: Loading and saving user and movie CSV files.
- **Logic**: Core classes such as `User`, `Movie`, `Watchlist`, `History`, and `RecommendationEngine`.
- **Interface**: Text-based menus for login, browsing movies, managing watchlists, viewing history, and generating recommendations.

#### UML Class Diagram:
(Refer to **Figure 1** in the document for the UML Class Diagram)

### 2.2 Domain Classes

#### User class
- **Attributes**: `userName`, `password`, `Watchlist watchlist`, `History history`
- **Responsibilities**:
  - Load users from the CSV file into an in-memory map.
  - Manage the user’s personal watchlist and viewing history.
  - Modify user data (e.g., create new accounts, change passwords).
- **Exception Handling**: Use try-catch blocks for error handling.

#### Movie class
- Represents a movie with attributes like `ID`, `title`, `genre`, `year`, and `rating`.
- Parses movie data from CSV files.

#### Main class (Controller)
- Manages the application's startup, user interactions, and flow.
- Coordinates user session, manages data loading, and routes user commands.

### 2.3 Start-up and Shutdown Logic
- **Startup**: The program initializes by loading data from CSV files.
- **Shutdown**: Persists user data back into CSV files when modifications occur.

### 2.4 Data Persistence and File I/O Safety
The system ensures data persistence through the `User.saveUsers()` method and employs error handling (try-catch-finally) to ensure data integrity during file operations.

## 3. Recommendation Algorithm

### 3.1 History-Based Strategy
- Recommends movies based on the user’s viewing history and genre preferences.
- Calculates recommended movie quantities based on the user’s watched genres.
- Filters out already watched or added movies.

### 3.2 Year-Based Strategy
- Recommends movies based on a specific year provided by the user.

### 3.3 Rating-Based Strategy
- Recommends movies based on ratings, starting with the highest-rated movies.

## 4. Implementation of Advanced Features

### 4.1 Runtime User Account Creation
- Users can register a new account via the application interface.
- Includes validation to check for duplicate usernames.

### 4.2 Secure Password Change
- Users can securely change their passwords after entering the current password for validation.

### 4.3 Multi-Strategy Recommendation Engine
- The system supports three dynamic recommendation strategies: History-Based, Year-Based, and Rating-Based.

### 4.4 Custom Password Hashing for Enhanced Security
- Implements a custom password hashing algorithm to secure user credentials in the CSV file.

## 5. Exception Handling and Robustness

### 5.1 Input Validation
- The system ensures all menu choices and inputs are validated to prevent invalid operations.

### 5.2 File I/O Safety and Resource Management
- The system uses try-catch blocks for file operations and ensures resources are properly cleaned up.

### 5.3 Edge Case Handling
- Handles empty collections gracefully and prevents errors like `NullPointerException`.

## 6. Testing Methods and Results

### 6.1 Testing Strategy
- Combines **Automated Unit Testing (JUnit)** for core logic and **Manual Functional Testing** for user interactions.

### 6.2 Automated Unit Testing with JUnit
- Verifies user authentication, data integrity, and recommendation logic.

### 6.3 System Integration Testing (Manual)
- Manual tests verify key user interactions like login, account creation, and movie additions.

## 7. Team Management Practices
- **Communication**: Used WeChat for daily updates and offline meetings for project milestones.
- **Version Control**: Utilized GitHub for source code management.
- **Task Allocation**: Each member contributed to specific modules based on strengths.

## 8. Ethical Assessment

### 8.1 Privacy and Data Protection Issues
- **Solution**: Hash passwords, encrypt CSV files, use the principle of least privilege.

### 8.2 Potential Abuse of the Recommendation System
- **Solution**: Allow users to switch recommendation strategies and add diversity to recommendations.

### 8.3 Security and Misuse Risks
- **Solution**: Implement account lock after failed login attempts and regularly check for biased patterns.

## 9. References
1. Google Gemini 3 Pro Preview, available at [https://gemini.google.com](https://gemini.google.com). Used for grammatical corrections and formatting suggestions.
2. ChatGPT 5.1 Pro, available at [https://chatgpt.com/](https://chatgpt.com/). Used for LaTeX formatting guidance.
