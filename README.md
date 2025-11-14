NotiGoal App ⚽
Integrantes: 
-Donnovan Urrutia
-David Muñoz
NotiGoal es una aplicación de resultados de fútbol para Android, creada con tecnologías 100% modernas de Kotlin y Jetpack Compose. La app permite a los usuarios ver resultados en vivo, próximos partidos y seguir a sus equipos favoritos.

<img width="396" height="882" alt="image" src="https://github.com/user-attachments/assets/1c53b8b8-a864-4307-925e-f5841c415ec3" />


🚀 Características Principales

Feed de Partidos Multi-Liga: Muestra partidos de 8+ competiciones europeas (Premier League, La Liga, Champions, etc.) consumiendo la API de football-data.org.

Interfaz con Pestañas: La pantalla principal se divide en "Próximos", "En Vivo" y "Finalizados" para una fácil navegación.

Secciones Colapsables: Las ligas en la pestaña "Partidos" se pueden expandir y colapsar para una interfaz más limpia.

Pantalla de Perfil: Una sección de usuario que muestra:

Equipos favoritos (guardados en una base de datos local).

Próximos partidos solo de equipos seguidos.

Gestor de permisos de la app (Notificaciones y Cámara).

Selección de Equipos: Pantalla dedicada para que los usuarios seleccionen y guarden sus equipos favoritos.

Manejo de API de Plan Gratuito: La app está diseñada inteligentemente para superar las limitaciones del plan gratuito de la API, realizando llamadas en paralelo a las ligas permitidas.

Notificaciones Simuladas: Incluye una función de prueba para simular notificaciones de goles desde la pantalla de detalles del partido.

🛠️ Stack Tecnológico

Este proyecto utiliza un stack 100% moderno basado en Kotlin y la arquitectura recomendada por Google.

UI: Jetpack Compose

Arquitectura: MVVM (Model-View-ViewModel)

Asincronía: Kotlin Coroutines (para viewModelScope, async/awaitAll)

Networking:

Retrofit 2 (para consumir la API REST)

OkHttp 3 (para Interceptors de autenticación y logging)

Coil (para carga de imágenes, con soporte para SVG)

Persistencia de Datos:

Room (para la base de datos de equipos favoritos)

DataStore (para las preferencias del perfil de usuario)

Gestión de Permisos: Accompanist Permissions

Navegación: Jetpack Navigation for Compose

🐛 Manejo de Errores y Casos de Uso

Una parte clave del desarrollo fue asegurar la robustez de la app:

Manejo de Nulos (Null Safety): Todo el código que consume la API (ViewModels y Composables) utiliza "safe calls" (?.) y elvis operators (?:) para manejar de forma segura los datos nulos (ej. marcadores en partidos futuros), previniendo crashes.

Estados de UI: La app maneja Loading, Success y Error para cada pantalla que depende de una red, mostrando al usuario un CircularProgressIndicator o un mensaje de error claro.

Lógica de Plan Gratuito: El MatchesViewModel lanza 8 llamadas de red en paralelo (async) y las combina (awaitAll) para construir la lista principal, en lugar de usar el endpoint v4/matches (que está bloqueado en el plan gratuito).

📸 Capturas de Pantalla
<img width="393" height="881" alt="image" src="https://github.com/user-attachments/assets/d7e72c3f-ac2b-4c8b-a312-6467a6d9397e" />

<img width="403" height="889" alt="image" src="https://github.com/user-attachments/assets/cdfb5f7c-7a1e-49d7-b7f5-e4dabf7bb602" />
