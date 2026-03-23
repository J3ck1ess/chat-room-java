# Java IM Chat System

## 📌 Overview
This project is an instant messaging system built with Java Socket and multithreading.  
It supports core features such as group chat, private messaging, and real-time online user synchronization.

The system simulates a real-world chat application with a clear client-server architecture.

---

## 🚀 Features

- Group chat (broadcast messaging)
- Private messaging (supports both @mention and user selection)
- Real-time online user list synchronization
- User online/offline notifications
- Multi-client concurrent communication

---

## 🧠 Technical Implementation

- Java Socket for network communication
- Multithreading for handling concurrent clients
- Custom communication protocol (MessageType)
- Online user management using `Map<Socket, User>`
- Swing GUI for client-side interface

---

## 🔥 Highlights (Interview Focus)

- Custom protocol design for message dispatching (group vs private)
- Precise routing for private messages (avoiding unnecessary broadcasting)
- Decoupled client-server architecture
- Thread-safe handling of concurrent connections
- Interactive user experience similar to real chat applications

---

## 📂 Project Structure

chat-server    → Server-side (handles communication logic)  
chat-system    → Client-side (UI + networking)

---

## ▶️ How to Run

1. Start the Server  
2. Launch one or multiple Clients  
3. Enter a nickname to join the chat  

---

## 🖼️ Demo

(Screenshots coming soon)

---

## 📈 Future Improvements

- Private chat windows (like QQ/WeChat)
- Message persistence (database support)
- File transfer functionality
- Emoji / rich text support
- Refactor networking layer using Netty
