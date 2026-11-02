# laundry-management-system
# 🧺 Laundry Management System

A Java-based **Laundry Management System** developed using **Java Swing** to simplify and manage daily laundry operations. The application provides separate dashboards for Admin, Staff, and Customers to manage orders, payments, machine bookings, inventory, and maintenance activities.

## 📌 Project Overview

The Laundry Management System is a desktop application designed to automate laundry service operations. It provides an easy-to-use graphical interface where customers can place and track orders, staff can manage daily laundry activities, and administrators can control the overall system.

## ✨ Features

### 👨‍💼 Admin Module
- View and manage all laundry orders.
- Add and manage staff accounts.
- Manage inventory and stock.
- View and manage washing machines.
- Create and manage maintenance requests.
- Assign maintenance tasks to staff.
- Create, view, and cancel machine booking slots.
- View customer feedback.

### 👨‍🔧 Staff Module
- Register new customers.
- Create new laundry orders.
- Add items to existing orders.
- Search orders by customer name.
- Update order status and cancel orders.
- Accept customer payments.
- View and manage machine booking slots.
- Reserve, reschedule, and release slots.
- Report machine issues.
- View and update assigned maintenance tasks.

### 👤 Customer Module
- Register and log in.
- Place new laundry orders.
- Select services and item quantities.
- View available machine booking slots.
- Book machine slots.
- Track orders using order ID.
- View personal order history.
- Make payments using Cash, Card, and UPI.
- Submit feedback and ratings.

### ⚙️ Additional Features
- Role-based login system.
- Automatic order total calculation.
- Machine slot availability checking.
- Prevention of overlapping bookings for the same machine.
- Input validation and exception handling.
- Custom exception classes.
- Interactive GUI using Java Swing.

## 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java SE 21 | Core programming language |
| Java Swing | Graphical User Interface |
| Java AWT | GUI components and event handling |
| OOP Concepts | Encapsulation, Inheritance, Abstraction and Polymorphism |
| Java Collections Framework | Managing application data |
| Exception Handling | Handling errors and invalid operations |
| Java Date and Time API | Managing booking dates and time slots |
| Eclipse IDE | Development environment |

## 📂 Project Structure

```text
Laundry Management System/
│
├── src/
│   └── in/edu/tint/lms/
│       │
│       ├── controller/
│       │   ├── Main.java
│       │   ├── LaundryManager.java
│       │   ├── SlotManager.java
│       │   └── MaintenanceManager.java
│       │
│       ├── exception/
│       │   └── LMSException.java
│       │
│       ├── interfaces/
│       │   ├── Action.java
│       │   ├── AdminManage.java
│       │   ├── CustomerManage.java
│       │   ├── PaymentManage.java
│       │   ├── ReportManage.java
│       │   ├── StaffManage.java
│       │   └── View.java
│       │
│       ├── model/
│       │   ├── Admin.java
│       │   ├── Customer.java
│       │   ├── Feedback.java
│       │   ├── Inventory.java
│       │   ├── Machine.java
│       │   ├── MaintenanceRequest.java
│       │   ├── Order.java
│       │   ├── OrderStatus.java
│       │   ├── Payment.java
│       │   ├── Service.java
│       │   ├── Slot.java
│       │   ├── Staff.java
│       │   └── User.java
│       │
│       └── view/
│           ├── BaseView.java
│           ├── LoginView.java
│           ├── AdminView.java
│           ├── StaffView.java
│           └── CustomerView.java
│
├── .classpath
└── .project
```

## 🚀 Getting Started

### Prerequisites

Make sure you have installed:

- Java Development Kit (JDK 21)
- Eclipse IDE (recommended)
- Git (optional, for cloning the repository)

Check your Java installation:

```bash
java -version
javac -version
```

### Installation and Setup

**Step 1: Clone the repository**

```bash
git clone YOUR_REPOSITORY_URL
```

**Step 2: Open the project in Eclipse**

1. Open Eclipse IDE.
2. Select File → Import.
3. Choose Existing Projects into Workspace.
4. Select the extracted project folder.
5. Ensure JDK 21 is configured.

**Step 3: Run the application**

Navigate to:

```text
src/in/edu/tint/lms/controller/Main.java
```

Right-click `Main.java` and select:

```text
Run As → Java Application
```

### Run Using Terminal

Compile:

```bash
javac -d bin -sourcepath src src/in/edu/tint/lms/controller/Main.java
```

Execute:

```bash
java -cp bin in.edu.tint.lms.controller.Main
```

## 🔑 Demo Login Credentials

The application provides three initial demo accounts.

| Role | User ID | Password |
|---|---|---|
| Admin | 1001 | admin123 |
| Staff | 1002 | staff123 |
| Customer | 1003 | cust123 |

**Note:** These are demonstration credentials. Do not use them in a production environment.

## 🔄 Application Workflow

1. Launch the application.
2. Open the login screen.
3. Select the required user role.
4. Enter the User ID and Password.
5. Access the respective dashboard.
6. Perform operations according to user permissions.
7. Log out to return to the login screen.

New customers can also register through the Customer Sign Up option.

## 💾 Data Storage

The current implementation uses Java Collections to store application data in memory.

The application initializes sample users, inventory, machines, and booking slots when it starts.

**Note:** A database integration and persistent storage mechanism are not included in the current implementation. Data created during a session is not retained after the application closes.

## 🔮 Future Enhancements

- Integration with MySQL or another relational database.
- Secure password hashing and authentication.
- Real payment gateway integration.
- Downloadable invoices and receipts.
- Advanced reports and analytics.
- Improved dashboard design.
- Automated testing.
- Persistent customer and order history.

## 🎯 Learning Outcomes

This project demonstrates practical implementation of:

- Object-Oriented Programming in Java.
- Java Swing and AWT.
- GUI event handling.
- Interfaces and inheritance.
- Exception handling.
- Java Collections Framework.
- Modular software design.
- Role-based application workflows.

## 👨‍💻 Author

**Your Name**

Java | Object-Oriented Programming | Swing | Software Development

---

⭐ If you find this project useful, consider giving the repository a star!
