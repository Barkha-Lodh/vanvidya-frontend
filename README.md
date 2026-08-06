# 🌿 VanVidya – Frontend (Android App)

An AI-powered Android app that identifies 🌿 plants, 🌸 flowers, and 🍄 mushrooms from camera images, 
and helps users care for them safely.

🔗 Backend repo: [VanVidya-Backend](https://github.com/Barkha-Lodh/vanvidya-backend)

## ✨ Features
- 🔍 Identifies plants/flowers/mushrooms using the Plant.id API
- 🦠 Detects common plant diseases and suggests treatments
- ⚠️ Checks edibility/toxicity of scanned plants and mushrooms
- 💧 Personalized care tips (watering, sunlight, soil) based on plant type
- ☀️ Sensor-based light meter to recommend suitable plants for a location
- 📓 Logbook to save scanned plants with photos and notes
- 🌐 Multilingual support (English/Hindi) via Google Translate API
- 🤖 AI-generated plant facts using Google Gemini and Groq APIs

## 🛠️ Tech Stack
- **Platform:** Android (Java/Kotlin)
- **Communicates with:** Django REST backend (see backend repo)
- **APIs used directly:** Plant.id API, Google Translate API

## ⚙️ How It Works (Frontend Perspective)
1. 📸 User scans a plant, flower, or mushroom using the camera
2. 🌱 App sends image to Plant.id API for species identification
3. 🔄 App calls the backend API to fetch disease/toxicity info and care recommendations
4. 💡 Displays AI-generated facts (from Gemini/Groq, fetched via backend)
5. 💾 Saves results locally to the user's personal logbook

## 📱 Screenshots
<img width="194" height="299" alt="image" src="https://github.com/user-attachments/assets/6d1711ee-0e28-4668-8e99-567fb749ed5d" />

<img width="210" height="314" alt="image" src="https://github.com/user-attachments/assets/2e4b9bb6-26d0-453a-aaca-be9c2ff71026" />

