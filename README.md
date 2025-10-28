# Counter++

Counter++ is a reactive Jetpack Compose counter app that increments and decrements a number using UI buttons and auto incrementing abilities

## Features

- Increment (+1), decrement (-1), and reset the counter.
- Toggle **Auto Mode** to automatically increment the counter every few seconds.
- View current count and auto mode status.
- Configure the auto-increment interval in a dedicated **Settings** screen.
---

## How It Works

- Tap **+1** → increases the counter
- Tap **–1** → decreases the counter
- Tap **Reset** → sets counter back to 0
- Toggle **Auto Mode** → starts a coroutine incrementing the counter every few seconds
- Open **Settings** → choose how many seconds between auto increments

---

## Screens

### Home Screen
- Counter display
- Auto Mode toggle
- +1 / –1 / Reset buttons
- Status text 

### Settings Screen
- Input to configure auto-increment interval
- Saves changes to the ViewModel

---

