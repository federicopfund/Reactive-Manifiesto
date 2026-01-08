# 🚀 Inicio Rápido - Panel de Administración

## Pasos para levantar el sistema

### 1. Compilar y ejecutar
```bash
cd /workspaces/Reactive-Manifiesto
sbt run
```

### 2. Esperar a que inicie (primera vez puede tardar)
Verás algo como:
```
[info] p.c.s.AkkaHttpServer - Listening for HTTP on /0.0.0.0:9000
```

### 3. Aplicar evolutions automáticamente
- Ir a: http://localhost:9000
- Si aparece página de evolutions, click en "Apply this script!"
- Se crearán las tablas `contacts` y `admins`

### 4. Acceder al panel de admin
```
URL: http://localhost:9000/admin/login
Usuario: admin
Contraseña: admin123
```

### 5. ¡Listo! Ya puedes:
- ✅ Ver todos los contactos en el dashboard
- ✅ Buscar contactos por nombre, email o mensaje
- ✅ Crear nuevos contactos manualmente
- ✅ Editar contactos existentes
- ✅ Cambiar estados (pendiente/procesado/archivado)
- ✅ Eliminar contactos
- ✅ Ver estadísticas

## 🔐 Crear nuevo administrador

### Opción 1: Desde terminal
```bash
# Generar hash de contraseña
sbt "runMain utils.PasswordHasher mi_contraseña_segura"

# Copiar el hash generado y ejecutar SQL
# Conectarse a la base de datos y ejecutar:
INSERT INTO admins (username, email, password_hash, role) 
VALUES ('nuevo_admin', 'nuevo@example.com', 'HASH_COPIADO_AQUI', 'admin');
```

### Opción 2: Usar H2 Console (desarrollo)
1. Agregar a `conf/application.conf`:
   ```
   db.default.url = "jdbc:h2:mem:play;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_UPPER=false"
   ```

2. Acceder a: http://localhost:9000/h2-console (si está habilitado)

## 📍 URLs Principales

| URL | Descripción |
|-----|-------------|
| http://localhost:9000 | Sitio público |
| http://localhost:9000/admin/login | Login admin |
| http://localhost:9000/admin | Dashboard |
| http://localhost:9000/admin/contacts/new | Crear contacto |
| http://localhost:9000/admin/stats | Estadísticas JSON |

## 🛠️ Comandos útiles SBT

```bash
# Iniciar aplicación
sbt run

# Compilar sin ejecutar
sbt compile

# Limpiar compilación
sbt clean

# Modo desarrollo con auto-reload
sbt ~run

# Ejecutar tests
sbt test

# Consola interactiva
sbt console

# Generar hash de password
sbt "runMain utils.PasswordHasher mi_password"
```

## 🐛 Solución de problemas

### Puerto 9000 ocupado
```bash
# Linux/Mac
lsof -ti:9000 | xargs kill -9

# O cambiar puerto en application.conf
http.port = 9001
```

### Evolution no se aplica
```bash
# Verificar archivo conf/application.conf
play.evolutions.enabled = true
play.evolutions.autoApply = true  # Para auto-aplicar
```

### Error de compilación
```bash
sbt clean
sbt compile
```

### No carga BCrypt
```bash
# Verificar que build.sbt tenga:
"org.mindrot" % "jbcrypt" % "0.4"

# Luego:
sbt update
sbt clean compile
```

## 📊 Datos de prueba

### Insertar contactos de prueba
```sql
INSERT INTO contacts (name, email, message, status) VALUES 
('Juan Pérez', 'juan@example.com', 'Consulta sobre servicios reactivos', 'pending'),
('María García', 'maria@example.com', '¿Tienen curso de Akka?', 'pending'),
('Carlos López', 'carlos@example.com', 'Excelente artículo sobre streams', 'processed');
```

## 🔒 Seguridad

**⚠️ ANTES DE PRODUCCIÓN:**
- [ ] Cambiar contraseña admin
- [ ] Configurar HTTPS
- [ ] Usar base de datos persistente (PostgreSQL)
- [ ] Configurar secret key segura
- [ ] Habilitar rate limiting
- [ ] Revisar logs de acceso

## 📚 Documentación completa
Ver archivo: [ADMIN_PANEL.md](ADMIN_PANEL.md)

---

**¿Problemas?** Revisar logs en la consola donde ejecutaste `sbt run`
