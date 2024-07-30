# GameOn 

## Overview
GameOn Server Middleware is a comprehensive solution designed to connect Minecraft servers with a mobile application, providing a seamless experience for players and server owners. This part focuses on communication between game servers and the GameOn mobile app, allowing for features such as player statistics, economy management, and server promotion.

## Table of Contents
- [Architecture](#architecture)
- [Contributing](#contributing)
- [License](#license)
- [Contact](#contact)

## Architecture

### Components
- **Mobile Application**: Built with Flutter, connecting players to game servers.
- **Game Server Plugin**: A Java plugin that integrates with Minecraft servers.
- **Backend Server**: Utilizes Firebase for remote configuration, analytics, and crash reporting.
- **REST API**: Facilitates communication between the mobile application and game servers.

### Communication Flow
1. **Server Registration**: Servers register with Firebase Remote Config to be listed and monitored.
2. **Player Interaction**: Players select a server, log in, and access their accounts through the mobile app.
3. **Data Handling**: The middleware handles requests for player stats, economy transactions, and server information.

### APIs
- **AuthenticationAPI**: Manages user authentication and sessions.
- **EconomyAPI**: Handles in-game economy operations (balance, transactions).
- ***PlayerStatsAPI**: Provides player statistics and game data.
- ***ServerMapAPI**: Supplies information about available servers and their statuses.
- ***ChatAPI**: Facilitates in-game chat functionality.

## API Documentation
```java
public interface IEconomy {
    double getBalance(UserId userId);
    boolean hasSufficientBalance(UserId userId, double amount);
    void withdraw(UserId userId, double amount);
    void deposit(UserId userId, double amount);
    void transfer(UserId fromUserId, UserId toUserId, double amount);
    void setBalance(UserId userId, double amount);
    Map<UserId, Double> getAllUserBalances();
}
```

## Contributing
We welcome contributions! Please follow these steps:
Fork the Repository: Click on the "Fork" button in the top right corner.
Create a New Branch: git checkout -b feature/YourFeature
Make Your Changes: Implement your feature or fix.
Commit Your Changes: git commit -m 'Add some feature'
Push to the Branch: git push origin feature/YourFeature
Create a Pull Request: Go to the original repository and create a pull request.

## License
This project is licensed under the MIT License. See the LICENSE file for details.

## Contact
For questions or support, please contact:
Email: jestemdobrywniczym@gmail.com
Discord: opkarol