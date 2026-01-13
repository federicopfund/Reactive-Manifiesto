# 📝 Sistema de Publicaciones de Usuarios

## Descripción General

Sistema completo de gestión de publicaciones que permite a los usuarios crear contenido y a los administradores aprobar/rechazar publicaciones antes de su publicación.

## 🎯 Funcionalidades Implementadas

### Para Usuarios Regulares

1. **Dashboard Personal** (`/user/dashboard`)
   - Ver todas sus publicaciones
   - Estadísticas por estado (borradores, pendientes, aprobadas, rechazadas)
   - Acciones rápidas (editar, enviar, eliminar)

2. **Crear Publicaciones** (`/user/publications/new`)
   - Título (5-200 caracteres)
   - Categoría (Scala, Akka, Play Framework, etc.)
   - Contenido (mínimo 50 caracteres, soporta Markdown)
   - Extracto opcional (500 caracteres)
   - Etiquetas (separadas por comas)
   - Imagen de portada (URL)

3. **Editar Publicaciones** (`/user/publications/:id/edit`)
   - Modificar cualquier campo
   - Solo publicaciones propias
   - Solo si están en estado borrador o rechazadas

4. **Flujo de Aprobación**
   - **Borrador (draft)**: Estado inicial, solo visible para el autor
   - **Pendiente (pending)**: Enviada para revisión de administradores
   - **Aprobada (approved)**: Visible públicamente
   - **Rechazada (rejected)**: Con motivo del rechazo

5. **Vista Previa** (`/user/publications/:id`)
   - Ver cómo se verá la publicación
   - Disponible en cualquier estado

### Para Administradores

1. **Panel de Revisión** (`/admin/publications/pending`)
   - Lista de todas las publicaciones pendientes
   - Vista tipo card con información resumida
   - Acciones rápidas de aprobación/rechazo

2. **Detalle de Publicación** (`/admin/publications/:id`)
   - Vista completa del contenido
   - Información del autor
   - Barra de acciones fija en la parte inferior
   - Aprobar o rechazar con motivo

3. **Aprobar Publicaciones** (`POST /admin/publications/:id/approve`)
   - Cambia estado a "approved"
   - Establece fecha de publicación
   - Registra el revisor

4. **Rechazar Publicaciones** (`POST /admin/publications/:id/reject`)
   - Cambia estado a "rejected"
   - Requiere motivo del rechazo
   - El usuario puede ver el motivo y corregir

## 📁 Estructura de Archivos

### Modelos
- `app/models/Publication.scala` - Modelo de publicación con estados

### Repositorios
- `app/repositories/PublicationRepository.scala` - Operaciones de base de datos

### Controladores
- `app/controllers/UserPublicationController.scala` - CRUD para usuarios
- `app/controllers/AdminController.scala` - Extensión con aprobación de publicaciones

### Actions (Autenticación)
- `app/controllers/actions/AuthAction.scala`
  - `AuthAction` - Usuario autenticado (cualquier rol)
  - `UserAction` - Usuario con rol "user" o "admin"
  - `AdminOnlyAction` - Solo administradores
  - `OptionalAuthAction` - Autenticación opcional

### Vistas
- `app/views/user/dashboard.scala.html` - Dashboard del usuario
- `app/views/user/publicationForm.scala.html` - Formulario crear/editar
- `app/views/user/publicationPreview.scala.html` - Vista previa
- `app/views/admin/publicationReview.scala.html` - Lista de pendientes
- `app/views/admin/publicationDetail.scala.html` - Detalle para revisar

### Base de Datos
- `sql/publications_management.sql` - Script SQL completo
- `conf/evolutions/default/6.sql` - Migración para Play Framework

## 🗃️ Esquema de Base de Datos

```sql
CREATE TABLE publications (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  title VARCHAR(200) NOT NULL,
  slug VARCHAR(250) NOT NULL UNIQUE,
  content TEXT NOT NULL,
  excerpt VARCHAR(500),
  cover_image VARCHAR(500),
  category VARCHAR(100) NOT NULL,
  tags VARCHAR(500),
  status VARCHAR(20) NOT NULL DEFAULT 'draft',
  view_count INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  published_at TIMESTAMP,
  reviewed_by BIGINT,
  reviewed_at TIMESTAMP,
  rejection_reason TEXT,
  
  CONSTRAINT fk_publication_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_publication_reviewer FOREIGN KEY (reviewed_by) REFERENCES admins(id) ON DELETE SET NULL,
  CONSTRAINT chk_status CHECK (status IN ('draft', 'pending', 'approved', 'rejected'))
);
```

## 🚀 Instalación y Configuración

### 1. Ejecutar Migración de Base de Datos

**Opción A: Usando Play Evolutions (Recomendado)**
- La migración se ejecutará automáticamente al iniciar la aplicación
- Archivo: `conf/evolutions/default/6.sql`

**Opción B: Manualmente**
```bash
psql -U usuario -d nombre_db -f sql/publications_management.sql
```

### 2. Configurar Rutas

Las rutas ya están configuradas en `conf/routes`:

**Rutas de Usuario:**
- `GET  /user/dashboard` - Dashboard
- `GET  /user/publications/new` - Formulario nueva publicación
- `POST /user/publications/new` - Crear publicación
- `GET  /user/publications/:id/edit` - Editar publicación
- `POST /user/publications/:id/edit` - Actualizar publicación
- `GET  /user/publications/:id` - Ver publicación
- `POST /user/publications/:id/submit` - Enviar para revisión
- `POST /user/publications/:id/delete` - Eliminar publicación

**Rutas de Admin:**
- `GET  /admin/publications/pending` - Ver pendientes
- `GET  /admin/publications/:id` - Detalle para revisar
- `POST /admin/publications/:id/approve` - Aprobar
- `POST /admin/publications/:id/reject` - Rechazar

### 3. Iniciar la Aplicación

```bash
sbt run
```

## 📊 Flujo de Trabajo

### Usuario Crea Publicación
```
1. Usuario → /user/dashboard
2. Click "Nueva Publicación"
3. Completar formulario
4. Guardar como "Borrador"
5. Editar si es necesario
6. Click "Enviar para Revisión" (estado → pending)
```

### Admin Revisa Publicación
```
1. Admin → /admin/publications/pending
2. Ver lista de publicaciones pendientes
3. Click "Ver Completa" en una publicación
4. Revisar contenido
5. Aprobar o Rechazar:
   - Aprobar → Estado: approved, visible públicamente
   - Rechazar → Estado: rejected, con motivo
```

### Usuario Recibe Feedback
```
- Si aprobada: Ver en dashboard con badge verde "Aprobada"
- Si rechazada: Ver motivo, puede editar y reenviar
```

## 🎨 Diseño y Estilos

### Dashboard de Usuario
- **Colores**: Grises profesionales con acentos azules
- **Cards de estadísticas**: Una por cada estado
- **Tabla responsive**: Con acciones contextuales
- **Badges de estado**: Colores semánticos
  - Draft: Gris
  - Pending: Amarillo
  - Approved: Verde
  - Rejected: Rojo

### Panel de Admin
- **Header azul corporativo**: Con degradado
- **Cards de publicaciones**: Hover con elevación
- **Barra de acciones fija**: En detalle de publicación
- **Modal de rechazo**: Para especificar motivo

## 🔐 Seguridad

### Actions de Autorización
```scala
// Solo usuarios autenticados (user o admin)
def dashboard = userAction.async { implicit request: AuthRequest[AnyContent] =>
  // ...
}

// Solo administradores
def pendingPublications = Action.async { implicit request =>
  if (!isAdmin(request)) {
    Future.successful(Redirect(routes.AdminController.loginPage()))
  } else {
    // ...
  }
}
```

### Validaciones
- Usuario solo puede editar/eliminar sus propias publicaciones
- Admin puede ver todas las publicaciones
- Slug único generado automáticamente
- CSRF tokens en todos los formularios

## 📈 Características Adicionales

### 1. Estadísticas
```scala
publicationRepo.getUserStats(userId)
// Retorna: Map("draft" -> 3, "pending" -> 2, "approved" -> 10)
```

### 2. Búsqueda por Categoría
```scala
publicationRepo.findByCategory("Scala", limit = 20)
```

### 3. Contador de Vistas
```scala
publicationRepo.incrementViewCount(publicationId)
```

### 4. API JSON
- `GET /api/user/publications` - Publicaciones del usuario
- `GET /api/admin/publications` - Todas las publicaciones (admin)

## 🧪 Testing

### Probar el Sistema

1. **Crear un usuario regular**:
```bash
# Registrarse en /register
```

2. **Crear publicaciones de prueba**:
```bash
# Navegar a /user/dashboard
# Click "Nueva Publicación"
# Completar formulario y guardar
```

3. **Enviar para revisión**:
```bash
# En el dashboard, click "Enviar" en una publicación
```

4. **Login como admin**:
```bash
# Ir a /admin/login
```

5. **Revisar publicaciones**:
```bash
# Ir a /admin/publications/pending
# Aprobar o rechazar publicaciones
```

## 🔄 Próximas Mejoras Sugeridas

1. **Editor Markdown Rico**
   - Integrar editor WYSIWYG (SimpleMDE, TUI Editor)
   - Preview en tiempo real

2. **Sistema de Comentarios**
   - Comentarios de admin en las revisiones
   - Historial de cambios

3. **Notificaciones**
   - Email cuando se aprueba/rechaza una publicación
   - Notificaciones en tiempo real

4. **Búsqueda y Filtros**
   - Búsqueda full-text en publicaciones
   - Filtrar por categoría, tags, estado

5. **Versioning**
   - Guardar versiones anteriores de publicaciones
   - Comparar cambios

6. **Analytics**
   - Métricas de visualizaciones
   - Tiempo de lectura estimado
   - Publicaciones más populares

## 📝 Notas Técnicas

### Generación de Slugs
Los slugs se generan automáticamente a partir del título:
- Convierte a minúsculas
- Reemplaza caracteres especiales
- Agrega timestamp para garantizar unicidad

### Estados de Publicación
```scala
object PublicationStatus extends Enumeration {
  val Draft = Value("draft")        // Borrador
  val Pending = Value("pending")    // En revisión
  val Approved = Value("approved")  // Aprobada
  val Rejected = Value("rejected")  // Rechazada
}
```

### Triggers de Base de Datos
- `updated_at` se actualiza automáticamente en cada UPDATE
- Función PostgreSQL para mantener timestamps

## 🆘 Troubleshooting

### Error: No se puede crear publicación
- Verificar que la tabla `publications` existe
- Verificar que el usuario está autenticado
- Revisar logs de Play Framework

### Error: Admin no puede ver publicaciones pendientes
- Verificar que el admin está logueado
- Verificar método `isAdmin()` en AdminController
- Revisar sesión del usuario

### Error: Publicaciones no se actualizan
- Verificar que el `user_id` coincide
- Revisar permisos en el repositorio
- Verificar trigger de `updated_at`

## 📞 Soporte

Para más información o reportar issues:
- Revisar logs en consola de Play
- Verificar configuración de base de datos
- Revisar rutas en `conf/routes`

---

**¡Sistema de Publicaciones implementado exitosamente! 🎉**
