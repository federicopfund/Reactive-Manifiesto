# 🛡️ Panel de Administración - Sistema CRUD de Contactos

## 📋 Descripción

Sistema completo de administración para gestionar los contactos registrados en la base de datos. Incluye autenticación segura, operaciones CRUD completas, búsqueda, filtros y paginación.

## 🔐 Credenciales por Defecto

**⚠️ IMPORTANTE: Cambiar en producción**

- **Usuario**: `admin`
- **Contraseña**: `admin123`
- **Email**: `admin@reactivemanifesto.com`

## 🚀 Características Implementadas

### ✅ Autenticación y Seguridad
- Login con usuario y contraseña
- Hash de contraseñas con BCrypt
- Sesiones seguras
- Protección de rutas administrativas
- CSRF protection en todos los formularios

### ✅ Operaciones CRUD Completas
- **Create**: Crear nuevos contactos desde el panel
- **Read**: Visualizar listado y detalles de contactos
- **Update**: Editar información y cambiar estados
- **Delete**: Eliminar contactos con confirmación

### ✅ Funcionalidades Avanzadas
- 🔍 **Búsqueda**: Por nombre, email o mensaje
- 📄 **Paginación**: 10 registros por página
- 🏷️ **Estados**: Pendiente, Procesado, Archivado
- 📊 **Estadísticas**: Endpoint API para métricas
- 🎨 **UI Moderna**: Diseño responsivo y profesional

## 📍 Rutas Principales

### Acceso Público
- `GET /admin/login` - Página de login

### Panel Administrativo (Requiere autenticación)
- `GET /admin` o `GET /admin/dashboard` - Dashboard principal
- `GET /admin/logout` - Cerrar sesión

### CRUD de Contactos
- `GET /admin/contacts/new` - Formulario nuevo contacto
- `POST /admin/contacts/new` - Crear contacto
- `GET /admin/contacts/:id` - Ver detalle
- `GET /admin/contacts/:id/edit` - Formulario edición
- `POST /admin/contacts/:id/edit` - Actualizar contacto
- `POST /admin/contacts/:id/delete` - Eliminar contacto

### API JSON
- `GET /admin/stats` - Estadísticas generales
- `POST /admin/contacts/:id/status/:status` - Actualizar estado rápido

## 🗄️ Base de Datos

### Tabla `admins`
```sql
- id (BIGSERIAL PRIMARY KEY)
- username (VARCHAR UNIQUE)
- email (VARCHAR UNIQUE)
- password_hash (VARCHAR)
- role (VARCHAR)
- created_at (TIMESTAMP)
- last_login (TIMESTAMP)
```

### Tabla `contacts` (Existente)
```sql
- id (BIGSERIAL PRIMARY KEY)
- name (VARCHAR)
- email (VARCHAR)
- message (TEXT)
- created_at (TIMESTAMP)
- status (VARCHAR) - pending, processed, archived
```

## 🔧 Tecnologías Utilizadas

- **Backend**: Play Framework 2.8+ (Scala)
- **Database**: Slick ORM + H2/PostgreSQL
- **Security**: BCrypt password hashing
- **Frontend**: Twirl Templates + CSS inline
- **Migrations**: Play Evolutions

## 📦 Archivos Creados

### Modelos
- `app/models/Admin.scala` - Modelo de administrador

### Repositorios
- `app/repositories/AdminRepository.scala` - Operaciones BD admins
- `app/repositories/ContactRepository.scala` - Actualizado con método `update()`

### Controladores
- `app/controllers/AdminController.scala` - Toda la lógica administrativa

### Vistas
- `app/views/admin/adminLayout.scala.html` - Layout base del admin panel
- `app/views/admin/login.scala.html` - Página de login
- `app/views/admin/dashboard.scala.html` - Dashboard principal con tabla
- `app/views/admin/contactDetail.scala.html` - Vista detalle de contacto
- `app/views/admin/contactForm.scala.html` - Formulario crear contacto
- `app/views/admin/contactEdit.scala.html` - Formulario editar contacto

### Configuración
- `conf/routes` - Rutas administrativas añadidas
- `conf/evolutions/default/2.sql` - Migration para tabla admins
- `build.sbt` - Dependencia BCrypt añadida

## 🎯 Guía de Uso

### 1. Iniciar la aplicación
```bash
sbt run
```

### 2. Aplicar migraciones
Las evolutions se aplican automáticamente al iniciar. Si hay problemas:
- Acceder a `http://localhost:9000`
- Click en "Apply this evolution"

### 3. Acceder al panel
1. Ir a `http://localhost:9000/admin/login`
2. Usar credenciales por defecto
3. Dashboard aparece con todos los contactos

### 4. Operaciones disponibles
- **Buscar**: Usar barra de búsqueda en dashboard
- **Ver**: Click en botón "👁️ Ver"
- **Editar**: Click en botón "✏️ Editar"
- **Eliminar**: Click en "🗑️ Eliminar" (con confirmación)
- **Crear**: Click en "➕ Nuevo Contacto"
- **Cambiar estado**: Desde la vista detalle o edición

## 🔒 Recomendaciones de Seguridad para Producción

### Prioridad Alta
1. **Cambiar contraseña admin por defecto**
   ```sql
   UPDATE admins 
   SET password_hash = '$2a$10$TU_NUEVO_HASH_AQUI'
   WHERE username = 'admin';
   ```

2. **Usar HTTPS en producción**
   - Configurar SSL/TLS
   - HttpOnly cookies
   - Secure cookies

3. **Variables de entorno**
   ```bash
   export ADMIN_USERNAME="tu-usuario"
   export ADMIN_PASSWORD="tu-password-seguro"
   ```

### Mejoras Recomendadas
- Implementar autenticación JWT
- Rate limiting en login
- Logging de accesos administrativos
- Two-factor authentication (2FA)
- Password reset por email
- Roles y permisos granulares
- Session timeout configurable

### Librerías Recomendadas
- **Silhouette**: Framework de autenticación robusto
- **Pac4j**: Autenticación y autorización
- **Play-authenticated**: Autenticación simple

## 📊 Estados de Contactos

| Estado | Emoji | Significado |
|--------|-------|-------------|
| `pending` | 🕐 | Nuevo, sin revisar |
| `processed` | ✓ | Revisado y gestionado |
| `archived` | 📁 | Archivado/Finalizado |

## 🧪 Testing

Para crear tests del sistema administrativo:

```scala
class AdminControllerSpec extends PlaySpec with GuiceOneAppPerTest {
  "AdminController" should {
    "redirect to login when not authenticated" in {
      val controller = app.injector.instanceOf[AdminController]
      val result = controller.dashboard(0, None).apply(FakeRequest())
      status(result) mustBe SEE_OTHER
    }
    
    "allow access after valid login" in {
      // Test implementation
    }
  }
}
```

## 🤝 Contribución

Para añadir nuevas funcionalidades al panel:
1. Agregar métodos al `AdminController.scala`
2. Crear vistas correspondientes en `app/views/admin/`
3. Actualizar rutas en `conf/routes`
4. Documentar cambios

## 📝 Notas Adicionales

- El sistema usa sesiones Play Framework por defecto
- Los estilos CSS están inline por simplicidad (considera extraer a archivo separado)
- Compatible con H2 (desarrollo) y PostgreSQL (producción)
- Todas las operaciones son reactivas (Futures)
- CSRF protection habilitado por defecto

## 🐛 Troubleshooting

**Error de compilación con BCrypt**
```bash
sbt clean
sbt compile
```

**Evolution no se aplica**
- Verificar `conf/application.conf`
- Asegurar `play.evolutions.enabled = true`

**Session no persiste**
- Verificar configuración de cookies
- Revisar secret key en `application.conf`

---

**Desarrollado con ❤️ usando Play Framework y Reactive Principles**
