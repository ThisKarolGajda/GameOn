# GameOn 

## Overview
GameOn Server Middleware is a comprehensive solution designed to connect Minecraft servers with a mobile application, providing a seamless experience for players and server owners. This part focuses on communication between game servers and the GameOn mobile app, allowing for features such as player statistics, economy management, and server promotion.

## Table of Contents
- [Architecture](#architecture)
- [API Documentation](#api-documentation)
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
- **AuthenticationAPI**: Manages abstractUser authentication and sessions.
- **EconomyAPI**: Handles in-game economy operations (balance, transactions).
- ***PlayerStatsAPI**: Provides player statistics and game data.
- ***ServerMapAPI**: Supplies information about available servers and their statuses.
- ***ChatAPI**: Facilitates in-game chat functionality.
## API Documentation

<details>
<summary>IEconomy Interface</summary>

```java
public interface IEconomy extends IExtension {
    /**
     * Retrieves the balance of the specified abstractUser.
     *
     * @param userId The UserId of the abstractUser whose balance is to be retrieved.
     * @return The balance of the abstractUser as a double.
     */
    double getBalance(UserId userId);

    /**
     * Checks if the specified abstractUser has a sufficient balance for a given amount.
     *
     * @param userId The UserId of the abstractUser whose balance is to be checked.
     * @param amount The amount to check against the abstractUser's balance.
     * @return True if the abstractUser has sufficient balance, false otherwise.
     */
    boolean hasSufficientBalance(UserId userId, double amount);

    /**
     * Withdraws a specified amount from the abstractUser's balance.
     *
     * @param userId The UserId of the abstractUser from whose account the amount will be withdrawn.
     * @param amount The amount to withdraw.
     */
    void withdraw(UserId userId, double amount);

    /**
     * Deposits a specified amount into the abstractUser's balance.
     *
     * @param userId The UserId of the abstractUser to whom the amount will be deposited.
     * @param amount The amount to deposit.
     */
    void deposit(UserId userId, double amount);

    /**
     * Transfers a specified amount from one abstractUser to another.
     *
     * @param fromUserId The UserId of the abstractUser from whom the amount will be transferred.
     * @param toUserId The UserId of the abstractUser to whom the amount will be transferred.
     * @param amount The amount to transfer.
     */
    void transfer(UserId fromUserId, UserId toUserId, double amount);

    /**
     * Sets the balance of the specified abstractUser to a given amount.
     *
     * @param userId The UserId of the abstractUser whose balance will be set.
     * @param amount The new balance to set for the abstractUser.
     */
    void setBalance(UserId userId, double amount);

    /**
     * Retrieves a map of all abstractUser balances.
     *
     * @return A map where the key is the UserId and the value is the abstractUser's balance.
     */
    Map<UserId, Double> getAllUserBalances();
}
```
</details> <details> <summary>IAuthentication Interface</summary>

```java
public interface IAuthentication extends IExtension {

    /**
     * Authenticates a abstractUser based on their UserId.
     *
     * @param userId The UserId of the abstractUser to authenticate.
     * @return A token representing the authenticated abstractUser session.
     */
    String authenticate(UserId userId);

    /**
     * Validates a given authentication token.
     *
     * @param token The token to validate.
     * @return True if the token is valid, false otherwise.
     */
    boolean validateToken(String token);

    /**
     * Retrieves the UserId associated with a given authentication token.
     *
     * @param token The token from which to extract the UserId.
     * @return The UserId associated with the token, or null if the token is invalid.
     */
    UserId getUserFromToken(String token);
}
```
</details> 

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