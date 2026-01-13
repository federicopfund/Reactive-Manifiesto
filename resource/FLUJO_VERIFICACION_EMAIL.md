# Flujo de Verificación de Email

## 📋 Descripción General

El sistema de registro requiere verificación de email antes de permitir el acceso completo al usuario.

## 🔄 Flujo Completo

### 1. Registro de Usuario
- Usuario completa formulario en `/register`
- Sistema crea usuario con `emailVerified = false`
- Redirige a `/login` con mensaje: "Registro exitoso. Por favor inicia sesión para verificar tu email."

### 2. Primer Login (Sin Verificar)
- Usuario ingresa credenciales en `/login`
- Sistema valida username y password
- Detecta que `emailVerified = false`
- Genera código de 3 dígitos aleatorio (100-999)
- **En modo desarrollo**: Imprime código en logs
- **En modo producción**: Envía código por email
- Redirige a `/verify-email/:userId`

### 3. Verificación de Código
- Usuario ve página con formulario de verificación
- Ingresa código de 3 dígitos
- Sistema valida:
  - ✅ Código existe
  - ✅ No ha expirado (5 minutos)
  - ✅ No ha excedido intentos (máximo 3)
  - ✅ Código coincide
- Si válido:
  - Marca `emailVerified = true` en la base de datos
  - Crea sesión de usuario
  - Actualiza `lastLogin`
  - Redirige a `/dashboard` con mensaje de bienvenida

### 4. Logins Posteriores
- Una vez verificado, el login es directo al dashboard
- No se requiere re-verificación

## 🛠️ Modo Desarrollo

La aplicación está configurada en modo desarrollo con `email.enabled = false`.

### Ver Códigos de Verificación

Cuando un usuario intenta hacer login sin verificar, el código se imprime en los logs del servidor:

```bash
# Ejecutar la aplicación
sbt run

# El código aparecerá así:
========================================
 📧 CÓDIGO DE VERIFICACIÓN (DEV MODE)
========================================
 Email: usuario@example.com
 Código: 456
 Expira en: 5 minutos
========================================
```

### Prueba Completa

1. **Iniciar servidor:**
   ```bash
   sbt run
   ```

2. **Registrar usuario:**
   - Ir a http://localhost:9000/register
   - Completar formulario
   - Click en "Registrarse"

3. **Hacer login:**
   - Ir a http://localhost:9000/login
   - Ingresar credenciales
   - Click en "Iniciar Sesión"

4. **Ver código en logs:**
   - Revisar la terminal donde está corriendo `sbt run`
   - Buscar el bloque con "CÓDIGO DE VERIFICACIÓN"
   - Copiar el código de 3 dígitos

5. **Verificar email:**
   - Serás redirigido automáticamente a `/verify-email/:userId`
   - Ingresar el código de 3 dígitos
   - Click en "Verificar Código"

6. **Acceso completo:**
   - Ahora tienes acceso al dashboard
   - Login futuro será directo

## 🚀 Modo Producción

Para habilitar envío real de emails:

1. **Configurar variables de entorno:**
   ```bash
   export EMAIL_USER="tu-email@gmail.com"
   export EMAIL_PASSWORD="tu-app-password"
   ```

2. **Habilitar emails en application.conf:**
   ```properties
   email.enabled = true
   ```

3. **Configurar Gmail:**
   - Habilitar verificación en 2 pasos
   - Generar "Contraseña de aplicación" en https://myaccount.google.com/apppasswords
   - Usar esa contraseña en `EMAIL_PASSWORD`

## 🔧 Endpoints

| Ruta | Método | Descripción |
|------|--------|-------------|
| `/register` | GET | Muestra formulario de registro |
| `/register` | POST | Procesa registro de usuario |
| `/login` | GET | Muestra formulario de login |
| `/login` | POST | Procesa login y envía código si no verificado |
| `/verify-email/:userId` | GET | Muestra formulario de verificación |
| `/verify-email` | POST | Procesa código de verificación |
| `/resend-code/:userId` | GET | Reenvía código de verificación |
| `/dashboard` | GET | Dashboard de usuario (requiere verificación) |

## 📊 Base de Datos

### Tabla: users
```sql
CREATE TABLE users (
  id BIGSERIAL PRIMARY KEY,
  username VARCHAR(255) UNIQUE NOT NULL,
  email VARCHAR(255) UNIQUE NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  full_name VARCHAR(255) NOT NULL,
  role VARCHAR(50) DEFAULT 'user',
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP NOT NULL,
  last_login TIMESTAMP,
  email_verified BOOLEAN DEFAULT false  -- ← Campo clave
);
```

### Tabla: email_verification_codes
```sql
CREATE TABLE email_verification_codes (
  id BIGSERIAL PRIMARY KEY,
  user_id BIGINT NOT NULL,
  email VARCHAR(255) NOT NULL,
  code VARCHAR(3) NOT NULL,
  attempts INT DEFAULT 0,
  verified BOOLEAN DEFAULT false,
  expires_at TIMESTAMP NOT NULL,
  created_at TIMESTAMP NOT NULL
);
```

## 🐛 Troubleshooting

### Usuario no puede hacer login después del registro

**Síntoma:** Usuario se registra pero el login falla o redirige constantemente.

**Causa:** Usuario no ha verificado su email.

**Solución:**
1. Hacer login → Te redirige a página de verificación
2. Ver código en logs (modo desarrollo)
3. Ingresar código en formulario
4. Verificar y acceder al dashboard

### No veo el código en los logs

**Verificar:**
- Terminal donde corre `sbt run` está visible
- `email.enabled = false` en application.conf
- Reiniciar servidor después de cambios

### Código expiró

**Solución:**
- Click en "Reenviar Código" en la página de verificación
- Se generará un nuevo código válido por 5 minutos

### Demasiados intentos fallidos

**Solución:**
- Click en "Reenviar Código" para obtener un código nuevo
- El contador de intentos se reinicia

## 📝 Notas de Seguridad

- Códigos de 3 dígitos: 1000 combinaciones posibles
- Expiración: 5 minutos
- Máximo 3 intentos por código
- BCrypt para passwords (salt rounds: 10)
- Sesiones basadas en cookies HTTP
