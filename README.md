# 🌤️ MoodJurnal

**MoodJurnal** is a modern Android mood journaling application designed to help users record their daily moods, reflect on their experiences, and visualize their emotional patterns over time.

The application is built with **Kotlin** and **Jetpack Compose**, focusing on a clean user experience, reactive UI, and maintainable Android architecture.

## ✨ Features

* 😊 Daily mood tracking
* 📝 Write personal journal entries
* 📅 View mood history
* 📊 Mood statistics and trends
* 🔍 Browse previous journal entries
* ✏️ Edit journal entries
* 🗑️ Delete journal entries
* 📈 Track emotional patterns
* 🎨 Modern Material 3 interface
* 📱 Responsive Jetpack Compose UI
* ⚡ Reactive state management

## 🎭 Mood Tracking

Users can record their daily emotional state and attach a journal entry to provide additional context.

Mood tracking allows users to look back at their previous entries and identify patterns in their daily experiences.

Example mood categories:

```text
😊 Happy
😌 Calm
😐 Neutral
😔 Sad
😡 Angry
😰 Anxious
🤩 Excited
```

## 📊 Mood Insights

The application can present mood information in an easy-to-understand format, allowing users to review their emotional history.

Potential insights include:

* Current mood
* Mood history
* Mood frequency
* Daily mood records
* Weekly trends
* Monthly trends

## 📝 Journal Entries

Each mood record can be accompanied by a personal journal entry.

Users can:

* Write their thoughts
* Edit previous entries
* Delete entries
* Review their journal history
* Connect journal entries with daily moods

## 🏗️ Architecture

MoodJurnal follows a modern Android architecture designed to separate the UI, application logic, and data layer.

```text
┌──────────────────────┐
│   Jetpack Compose    │
│        UI            │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│      ViewModel       │
│                      │
│ UI State / Logic     │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│     Repository       │
└──────────┬───────────┘
           │
           ▼
┌──────────────────────┐
│      Data Layer      │
│                      │
│ Database / Storage   │
└──────────────────────┘
```

This structure makes the application easier to maintain, test, and extend.

## 🛠️ Tech Stack

* **Kotlin**
* **Jetpack Compose**
* **Material 3**
* **Android SDK**
* **MVVM Architecture**
* **Kotlin Coroutines**
* **Kotlin Flow**
* **Repository Pattern**
* **Room Database** *(if enabled in the current implementation)*

## 📂 Project Structure

```text
app/
├── data/
│   ├── dao/
│   ├── database/
│   ├── entity/
│   └── repository/
│
├── ui/
│   ├── components/
│   ├── screens/
│   └── theme/
│
├── viewmodel/
│
└── MainActivity.kt
```

> The exact package structure may vary depending on the current implementation.

## 📸 Screenshots

Add screenshots of the application here.

Recommended screenshots:

* 🏠 Home / Dashboard
* 😊 Mood Selection
* 📝 Journal Entry
* 📅 Mood History
* 📊 Mood Statistics
* 👤 Profile

Example:

| Home       | Mood Selection |
| ---------- | -------------- |
| Screenshot | Screenshot     |

| Journal    | Statistics |
| ---------- | ---------- |
| Screenshot | Screenshot |

## 🎯 Project Goals

MoodJurnal was developed as an Android portfolio project to demonstrate practical implementation of modern Android development concepts.

The project focuses on:

* Kotlin programming
* Jetpack Compose UI development
* MVVM architecture
* State management
* Reactive UI
* Local data management
* Kotlin Coroutines
* Kotlin Flow
* Material 3 design
* User-focused mobile UX

## 🚀 Getting Started

### Requirements

* Android Studio
* JDK 17+
* Android SDK
* Kotlin
* Gradle

### Installation

Clone the repository:

```bash
git clone https://github.com/stornado345-prog/MoodJurnal.git
```

Open the project in **Android Studio**, allow Gradle to synchronize, and run the application on an Android emulator or physical Android device.

## 🔮 Future Improvements

Possible future improvements include:

* 📊 More advanced mood analytics
* 📅 Calendar-based mood history
* 🔔 Daily mood reminders
* 🎨 Custom mood themes
* 🔐 Biometric app lock
* 📤 Export journal entries
* 📥 Import journal data
* ☁️ Optional cloud backup
* 🌓 Improved dark mode
* 📈 More detailed mood charts

## 👨‍💻 Developer

**Satria**

Android Developer focused on building modern Android applications with:

* Kotlin
* Jetpack Compose
* MVVM
* Material 3
* Modern Android Architecture

---

⭐ If you find this project useful, feel free to explore the source code and give the repository a star.
