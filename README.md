# NotiGoal App ⚽

**Integrantes:**
* Donnovan Urrutia
* David Muñoz

NotiGoal es una aplicación moderna de resultados de fútbol para Android, desarrollada nativamente con **Kotlin** y **Jetpack Compose**. La app ofrece una experiencia completa que va desde la autenticación de usuarios con roles hasta el seguimiento en vivo de resultados de las ligas más importantes de Europa.

<img width="300" src="https://github.com/user-attachments/assets/1c53b8b8-a864-4307-925e-f5841c415ec3" alt="NotiGoal Home" />

---

## 🚀 Características Principales

### 🔐 Autenticación y Usuarios (Nuevo)
* **Sistema de Login y Registro:** Pantalla de bienvenida completa con validación de credenciales.
* **Roles de Usuario:** Soporte para 4 roles distintos (**ADMIN, USER, GUEST, SUPPORT**) seleccionables al registrarse.
* **Recuperación de Contraseña:** Simulación de flujo de recuperación de acceso.

### 🏆 Fútbol en Vivo
* **Feed Multi-Liga:** Consume la API de *football-data.org* para mostrar partidos de más de 8 competiciones (Premier League, La Liga, Champions League, etc.).
* **Navegación por Pestañas:** Organización clara en "Próximos", "En Vivo" y "Finalizados".
* **Interfaz Dinámica:** Secciones colapsables por liga para mantener el orden visual.

### 👤 Perfil y Personalización
* **Equipos Favoritos:** Base de datos local (Room) para guardar y gestionar los equipos del usuario.
* **Filtro Inteligente:** Visualización exclusiva de partidos de los equipos seguidos.
* **Gestión de Permisos:** Control de acceso a Cámara y Notificaciones dentro de la app.

### ⚙️ Ingeniería Robusta
* **Manejo de API Gratuita:** Algoritmo de llamadas paralelas (`async/awaitAll`) para maximizar la cuota del plan gratuito de la API.
* **Notificaciones Simuladas:** Sistema de prueba para alertas de goles en tiempo real.

---

## 🛠️ Stack Tecnológico

Proyecto construido con las últimas recomendaciones de Google y arquitectura limpia.

* **Lenguaje:** Kotlin 100%
* **UI:** Jetpack Compose (Material3)
* **Arquitectura:** MVVM (Model-View-ViewModel) + Clean Architecture
* **Asincronía:** Kotlin Coroutines & Flow
* **Networking:**
    * Retrofit 2 (API REST)
    * OkHttp 3 (Logging & Interceptors)
    * Coil (Carga de imágenes + SVG Decoder)
* **Persistencia Local:**
    * Room Database (Favoritos)
    * DataStore Preferences (Perfil de usuario)
* **Navegación:** Jetpack Navigation Compose
* **Permisos:** Accompanist Permissions

---

## ✅ Testing y Calidad (Nuevo)

Para asegurar la estabilidad del proyecto, se han implementado pruebas en distintos niveles:

* **Unit Testing (Lógica):** Pruebas unitarias con **JUnit4** y **MockK** para validar la lógica de negocio y ViewModels.
* **UI Testing (Instrumentado):** Pruebas de interfaz con **Espresso** y **Compose Test Rule** para verificar que los elementos visuales (como los ítems de los equipos) se renderizan correctamente en pantalla.

---

## 📦 Despliegue y CI/CD (Nuevo)

El proyecto está configurado para la entrega continua y generación de ejecutables:

* **APK Firmado:** Configuración en `build.gradle` para generar automáticamente `app-release.apk` firmado digitalmente.
* **Keystore Incluida:** (Para fines académicos) El archivo `keystore.jks` se encuentra en el repositorio para facilitar la compilación del release por parte del equipo evaluador.

---

## 🐛 Manejo de Errores

* **Null Safety:** Uso extensivo de *safe calls* y *elvis operators* para evitar crashes por datos incompletos de la API.
* **Estados de UI:** Gestión reactiva de estados `Loading`, `Success` y `Error` en todas las pantallas de red.

---

## 📸 Capturas de Pantalla

| Login / Roles | Selección de Equipos | Feed de Partidos |
|:---:|:---:|:---:|
| <img width="394" height="881" alt="image" src="https://github.com/user-attachments/assets/e716c5df-2ed4-4b53-a465-9a56067a85c9" />
 | <img width="250" src="https://github.com/user-attachments/assets/d7e72c3f-ac2b-4c8b-a312-6467a6d9397e" /> | <img width="250" src="https://github.com/user-attachments/assets/cdfb5f7c-7a1e-49d7-b7f5-e4dabf7bb602" /> |
