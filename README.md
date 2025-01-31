## This app is a simple spring boot chat application implemented with spring boot websockets library.

### About STOMP(Simple/Streaming Text Oriented Messaging Protocol)

STOMP stands for Simple (or Streaming) Text Oriented Messaging Protocol.  Think of it as a sub-protocol that runs over WebSockets.  WebSockets provide the raw, bidirectional communication channel, but STOMP adds a structured layer on top.  It defines a standard format for messages exchanged over the WebSocket connection.

#### Why use STOMP with WebSockets?

While WebSockets give you the basic plumbing, STOMP provides several advantages:

* Standardized Messaging: STOMP defines a clear structure for messages (headers, body, etc.). This makes it easier to parse and process messages on both the client and server sides.  Without STOMP, you'd have to invent your own message format, which can become complex. 
* Simplified Client-Server Interaction: STOMP introduces concepts like SUBSCRIBE, SEND, ACK, NACK, CONNECT, and DISCONNECT. These commands simplify common messaging patterns.  For example, a client can SUBSCRIBE to a destination (like a chat room) to receive messages, and SEND a message to that destination.
* Higher-Level Abstractions:  STOMP allows you to work with higher-level concepts like destinations (e.g., /topic/chat, /user/specificUser) instead of just raw WebSocket messages. This makes your code more readable and maintainable.
* Language Agnostic: STOMP is not tied to any specific programming language.  You can use STOMP with JavaScript clients, Java servers, Python clients, etc., as long as they have a STOMP library.
* Improved Error Handling: STOMP provides mechanisms for acknowledging messages (ACK) and negatively acknowledging them (NACK). This allows for more robust error handling and message delivery guarantees.

#### How STOMP works with WebSockets?

* WebSocket Handshake:  First, the client and server establish a WebSocket connection. 
* STOMP Connection:  After the WebSocket connection is established, the client sends a CONNECT frame to the server over the WebSocket. This frame includes information about the STOMP version and other details.   
* Messaging:  Once connected, the client and server can exchange messages using STOMP frames.  For example, the client might send a SUBSCRIBE frame to subscribe to a destination, and the server might send MESSAGE frames to deliver messages to that destination.
* Disconnection:  When finished, the client or server can send a DISCONNECT frame to close the STOMP connection.  The underlying WebSocket connection will also be closed.

#### Other implementation details:

* **@EnableWebSocketMessageBroker**: This annotation enables WebSocket message handling, providing support for STOMP messaging.  It's crucial for using STOMP over WebSockets.
* **WebsocketConfig implements WebSocketMessageBrokerConfigurer**: This interface provides methods to customize the WebSocket message broker.  You must implement these methods to configure STOMP endpoints and the message broker.

### Overall application flow:

1. Client Connection:

* The client (likely a web browser using JavaScript) establishes a WebSocket connection to the /ws endpoint. This is handled by the SockJS library (or directly by the browser if WebSockets are supported).
* The client then establishes a STOMP session over the WebSocket connection by sending a CONNECT frame.

2. User Joins the Chat:

* The client sends a STOMP message to the **/app/chat.addUser** destination. This message contains the user's name (sender). This is handled by the addUser method in your ChatController.
* **@MessageMapping("chat.addUser")**: This annotation maps the **/app/chat.addUser** destination to the addUser method.
* **@Payload Message msg**: This extracts the message payload (the Message object) from the STOMP message.
* **SimpMessageHeaderAccessor headerAccessor**: This allows you to access headers and session attributes associated with the WebSocket session. You store the username in the session attributes so you can retrieve it later.
* **@SendTo("/topic/chat")**: This annotation tells the message broker to send the returned Message object to the **/topic/chat** destination. All clients subscribed to this topic will receive the "user joined" message.
* The addUser method returns the Message object (which now includes the user's name). This message is then broadcast to all subscribed clients, informing them that a new user has joined.

3. Sending a Chat Message:

* A client sends a STOMP message to the /app/chat.sendMessage destination. This message contains the chat message content and the sender's name. This is handled by the sendMsg method in your ChatController.
* **@MessageMapping("chat.sendMessage")**: This annotation maps the /app/chat.sendMessage destination to the sendMsg method.
* **@Payload Message msg**: Extracts the message payload (the Message object) from the STOMP message.
* **@SendTo("/topic/chat")**: This annotation tells the message broker to send the returned Message object to the **/topic/chat** destination.
* The sendMsg method simply returns the received Message object. The message broker then broadcasts this message to all clients subscribed to the **/topic/chat** topic.

4. User Disconnects:

* When a client disconnects (closes the browser tab, etc.), a SessionDisconnectEvent is fired on the server.
* **WebsocketEventListener.handleWebsocketDisconnectHandler(SessionDisconnectEvent event)**: This method is triggered by the SessionDisconnectEvent.
* **StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());**: This gets access to the STOMP headers and session information associated with the disconnected session.
* **String username = (String) headerAccessor.getSessionAttributes().get("username");**: This retrieves the username from the session attributes (which you stored when the user joined).
* **messageOperations.convertAndSend("/topic/chat", ...)**: This uses the SimpMessageSendingOperations to send a **"user left"** message to the ***/topic/chat*** destination. This message is broadcast to all remaining clients, informing them that the user has left the chat.

#### Summary

In summary:  The client connects, joins the chat (sending their name), sends messages, and disconnects.  The server uses STOMP to structure the communication, the message broker to route messages to the correct destinations (either to specific controllers or broadcast to topics), and the event listener to handle disconnections and notify other users.  This architecture allows for real-time, bidirectional communication between clients and the server.