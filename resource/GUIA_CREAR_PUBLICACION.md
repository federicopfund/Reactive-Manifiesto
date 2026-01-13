# 📝 Guía: Cómo Crear una Publicación

## 🎯 Flujo Completo de Publicaciones

### 1️⃣ Acceder al Dashboard de Usuario

**Opción A: Iniciar sesión como usuario**
1. Ve a: http://localhost:9000/login
2. Ingresa tus credenciales de usuario
3. Serás redirigido automáticamente a: http://localhost:9000/user/dashboard

**Opción B: Iniciar sesión como admin**
1. Ve a: http://localhost:9000/admin/login
2. Usuario: `federico`
3. Contraseña: `admin123`

---

### 2️⃣ Crear Nueva Publicación

#### Desde el Dashboard:
Verás un banner de bienvenida con el botón:
```
✍️ Crear Nueva Publicación
```

Al hacer clic, serás redirigido a:
```
http://localhost:9000/user/publications/new
```

#### Formulario de Publicación:

**Campos obligatorios:**
- **Título** (5-200 caracteres)
- **Contenido** (mínimo 50 caracteres, soporta Markdown)
- **Categoría** (Scala, Akka, Play Framework, etc.)

**Campos opcionales:**
- **Extracto** (resumen de hasta 500 caracteres)
- **Imagen de portada** (URL de la imagen)
- **Tags** (separados por comas: reactive,scala,functional)

#### Ejemplo de contenido Markdown:
```markdown
# Mi Primera Publicación

Este es un párrafo introductorio sobre programación reactiva.

## Conceptos Clave

- Asincronía
- Resiliencia
- Elasticidad

```scala
val future = Future {
  // Código asíncrono
}
```

---

### 3️⃣ Estados de una Publicación

| Estado | Descripción | Acciones Disponibles |
|--------|-------------|---------------------|
| **draft** | Borrador inicial | ✏️ Editar, 📤 Enviar a Revisión, 🗑️ Eliminar |
| **pending** | En revisión por admin | 👁️ Ver solamente |
| **approved** | Publicada (visible públicamente) | 👁️ Ver solamente |
| **rejected** | Rechazada con comentario | 👁️ Ver, ✏️ Editar, 📤 Re-enviar |

---

### 4️⃣ Workflow Completo

```
1. Usuario crea publicación (estado: draft)
   ↓
2. Usuario edita y revisa el contenido
   ↓
3. Usuario envía para revisión (estado: pending)
   ↓
4. Admin revisa la publicación
   ↓
5a. Admin aprueba (estado: approved) ✅
    - Publicación visible en /publicaciones
    
5b. Admin rechaza (estado: rejected) ❌
    - Usuario puede ver el motivo
    - Usuario puede editar y re-enviar
```

---

### 5️⃣ Rutas Disponibles

#### Usuario:
- `GET  /user/dashboard` - Dashboard con todas las publicaciones
- `GET  /user/publications/new` - Formulario para crear publicación
- `POST /user/publications/new` - Guardar nueva publicación
- `GET  /user/publications/:id` - Ver detalle de publicación
- `GET  /user/publications/:id/edit` - Editar publicación
- `POST /user/publications/:id/edit` - Guardar cambios
- `POST /user/publications/:id/submit` - Enviar para revisión
- `POST /user/publications/:id/delete` - Eliminar publicación

#### Admin:
- `GET  /admin/publications/pending` - Ver publicaciones pendientes
- `GET  /admin/publications/:id/review` - Revisar publicación
- `POST /admin/publications/:id/approve` - Aprobar publicación
- `POST /admin/publications/:id/reject` - Rechazar con motivo

---

### 6️⃣ Tips y Mejores Prácticas

#### Para el contenido:
- ✅ Usa Markdown para formatear tu contenido
- ✅ Incluye ejemplos de código con bloques ```scala
- ✅ Divide en secciones con encabezados (##, ###)
- ✅ Agrega un extracto atractivo (se muestra en la lista)

#### Para las categorías:
- Scala
- Akka
- Play Framework
- Reactive Programming
- Functional Programming
- Microservices
- Testing

#### Para los tags:
Separa con comas, ejemplo:
```
scala, reactive, actors, concurrency
```

---

### 7️⃣ Verificar que todo funciona

#### Test rápido:
```bash
# Crear un usuario de prueba
curl -X POST http://localhost:9000/register \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "username=testuser&email=test@example.com&password=Test123&confirmPassword=Test123"

# Ver admins disponibles
curl http://localhost:9000/setup/list-admins | python3 -m json.tool
```

#### Credenciales Admin:
- Usuario: `federico`
- Contraseña: `admin123`
- Login: http://localhost:9000/admin/login

---

### 8️⃣ Características del Dashboard

#### Banner de Bienvenida:
- Saludo personalizado con nombre de usuario
- Botón principal: **"✍️ Crear Nueva Publicación"**
- Botón secundario: **"👤 Mi Perfil"**

#### Estadísticas:
- 📝 Borradores
- ⏳ Pendientes de revisión
- ✅ Aprobadas
- ❌ Rechazadas

#### Tabla de Publicaciones:
- Título, Categoría, Estado, Fecha
- Acciones contextuales según estado
- Iconos visuales para cada acción

#### Estado Vacío:
Si no tienes publicaciones, verás:
- Icono grande 📝
- Mensaje motivacional
- Botón CTA: **"✍️ Crear Mi Primera Publicación"**

---

## 🎨 Capturas de Pantalla del Flujo

### Dashboard Vacío:
```
┌────────────────────────────────────────┐
│  👋 Hola, usuario                       │
│  Gestiona tus publicaciones            │
│                                        │
│  [✍️ Crear Nueva Publicación]         │
│  [👤 Mi Perfil]                        │
└────────────────────────────────────────┘

┌─────┬─────┬─────┬─────┐
│📝 0 │⏳ 0 │✅ 0 │❌ 0 │
└─────┴─────┴─────┴─────┘

        📝
   No tienes publicaciones aún
   Comienza creando tu primera publicación
   
   [✍️ Crear Mi Primera Publicación]
```

### Dashboard con Publicaciones:
```
┌────────────────────────────────────────┐
│ Título              │ Estado  │ Acciones│
├────────────────────────────────────────┤
│ Mi Primer Post      │ draft   │👁️ ✏️ 📤 🗑️│
│ Tutorial Akka       │ pending │👁️       │
│ Intro a Scala       │approved │👁️       │
└────────────────────────────────────────┘
```

---

## 🚀 ¡Listo para Empezar!

1. Inicia sesión en: http://localhost:9000/login
2. Haz clic en **"✍️ Crear Nueva Publicación"**
3. Completa el formulario
4. Guarda como borrador
5. Edita y mejora tu contenido
6. Envía para revisión cuando esté listo
7. El admin aprobará o rechazará tu publicación

---

## 📧 Soporte

¿Problemas? Revisa:
- El servidor está corriendo en puerto 9000
- Las rutas están correctamente configuradas
- Los admins están creados (usa `/setup/list-admins`)
