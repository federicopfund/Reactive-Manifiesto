# 🚀 Comandos Rápidos - Sistema de Verificación de Email

## 📝 Desarrollo Local

### Iniciar Aplicación (Modo Desarrollo)
```bash
# Modo normal
sbt run

# Con script de verificación
./test-email.sh
```

### Compilar
```bash
# Compilación normal
sbt compile

# Compilación limpia (desde cero)
sbt clean compile

# Compilar assets (CSS/SCSS)
sbt assets
```

### Ver Logs en Tiempo Real
```bash
# Los códigos de verificación aparecen como:
========================================
 📧 CÓDIGO DE VERIFICACIÓN (DEV MODE)
========================================
 Email: usuario@example.com
 Código: 456
 Expira en: 5 minutos
========================================
```

---

## 🌐 Producción

### Activar Envío Real de Emails

#### 1. Editar Configuración
```bash
nano conf/application.conf

# Cambiar esta línea:
email.enabled = false
# Por esta:
email.enabled = true
```

#### 2. Descomentar Configuración SMTP
```conf
email.smtp.host = "smtp.gmail.com"
email.smtp.port = 587
email.smtp.user = ${?EMAIL_USER}
email.smtp.password = ${?EMAIL_PASSWORD}
email.from = "tu-email@gmail.com"
email.fromName = "Reactive Manifesto"
```

#### 3. Configurar Variables de Entorno
```bash
# Temporal (sesión actual)
export EMAIL_USER="tu-email@gmail.com"
export EMAIL_PASSWORD="xxxx-xxxx-xxxx-xxxx"

# Permanente (agregar a ~/.bashrc o ~/.zshrc)
echo 'export EMAIL_USER="tu-email@gmail.com"' >> ~/.bashrc
echo 'export EMAIL_PASSWORD="xxxx-xxxx-xxxx-xxxx"' >> ~/.bashrc
source ~/.bashrc
```

#### 4. Reiniciar Aplicación
```bash
sbt run
```

---

## 🧪 Testing

### Probar Flujo de Verificación

1. **Iniciar aplicación**
   ```bash
   sbt run
   ```

2. **Abrir navegador**
   ```
   http://localhost:9000
   ```

3. **Registrar nuevo usuario**
   - Click en "Registrarse"
   - Completar formulario
   - Submit

4. **Ver código en consola** (modo desarrollo)
   - Buscar en logs el código de 3 dígitos
   - Ejemplo: `Código: 456`

5. **Ingresar código**
   - Serás redirigido automáticamente a `/verify-email/:userId`
   - Ingresar el código de 3 dígitos
   - Submit

6. **Verificación exitosa**
   - Redirige al dashboard
   - Email marcado como verificado

### Probar Escenarios de Error

#### Código Incorrecto
```
1. Ingresar código erróneo (ej: 999)
2. Ver mensaje de error
3. Contador de intentos incrementa
4. Después de 3 intentos, código se bloquea
```

#### Código Expirado
```
1. Esperar más de 5 minutos
2. Intentar usar el código
3. Ver mensaje "Código expirado"
4. Click en "Reenviar código"
5. Recibir nuevo código
```

#### Reenviar Código
```
1. En página de verificación
2. Click en "Reenviar código"
3. Código nuevo generado
4. Código anterior invalidado
```

---

## 🗄️ Base de Datos

### Verificar Migraciones
```bash
# Las migraciones se aplican automáticamente al iniciar
# Ver estado en:
http://localhost:9000/@evolutions
```

### Consultas Útiles (H2 Console)

```sql
-- Ver usuarios no verificados
SELECT id, full_name, email, email_verified 
FROM users 
WHERE email_verified = false;

-- Ver códigos de verificación activos
SELECT * FROM email_verification_codes 
WHERE verified = false 
  AND expires_at > CURRENT_TIMESTAMP;

-- Ver intentos por código
SELECT user_id, code, attempts, verified, expires_at
FROM email_verification_codes
ORDER BY created_at DESC;

-- Marcar usuario como verificado manualmente (solo para testing)
UPDATE users 
SET email_verified = true 
WHERE email = 'usuario@example.com';

-- Limpiar códigos expirados
DELETE FROM email_verification_codes 
WHERE expires_at < CURRENT_TIMESTAMP;
```

### Acceder a H2 Console
```
URL: http://localhost:9000/@db
JDBC URL: jdbc:h2:mem:play
User: sa
Password: (vacío)
```

---

## 📧 Gmail - Configuración Rápida

### Generar Contraseña de Aplicación

1. **Habilitar 2FA**
   ```
   https://myaccount.google.com/security
   → Verificación en 2 pasos → Activar
   ```

2. **Generar Contraseña**
   ```
   https://myaccount.google.com/apppasswords
   → Seleccionar "Correo"
   → Seleccionar "Otro"
   → Escribir "Reactive Manifesto"
   → Generar
   ```

3. **Copiar Contraseña** (16 caracteres)
   ```
   xxxx xxxx xxxx xxxx
   ```

4. **Configurar Variables**
   ```bash
   export EMAIL_USER="tu-email@gmail.com"
   export EMAIL_PASSWORD="xxxx xxxx xxxx xxxx"
   ```

---

## 🐳 Docker

### Con Docker Compose
```yaml
# docker-compose.yml
services:
  app:
    build: .
    ports:
      - "9000:9000"
    environment:
      - EMAIL_USER=${EMAIL_USER}
      - EMAIL_PASSWORD=${EMAIL_PASSWORD}
```

### Ejecutar
```bash
# Configurar variables
export EMAIL_USER="tu-email@gmail.com"
export EMAIL_PASSWORD="xxxx-xxxx-xxxx-xxxx"

# Iniciar
docker-compose up -d

# Ver logs
docker-compose logs -f
```

---

## 🔍 Debugging

### Ver Logs Detallados
```bash
# Iniciar con logs de debug
sbt -Dlogger.root=DEBUG run
```

### Verificar Configuración Actual
```bash
# Ver si email está habilitado
grep "email.enabled" conf/application.conf

# Ver configuración SMTP
grep "email.smtp" conf/application.conf
```

### Probar Conexión SMTP (sin SBT)
```bash
# Usando curl (si está instalado)
curl -v --url 'smtp://smtp.gmail.com:587' \
  --ssl-reqd \
  --mail-from 'tu-email@gmail.com' \
  --mail-rcpt 'destino@example.com' \
  --upload-file email.txt \
  --user 'tu-email@gmail.com:xxxx-xxxx-xxxx-xxxx'
```

---

## 📊 Monitoreo

### Logs Importantes a Buscar

#### Éxito
```
✅ Email enviado exitosamente a usuario@example.com
📧 Código 456 enviado a usuario@example.com
```

#### Errores
```
❌ Error enviando email a usuario@example.com: Authentication failed
❌ Error enviando email a usuario@example.com: Connection timeout
```

### Estadísticas Rápidas
```sql
-- Tasa de verificación
SELECT 
  COUNT(*) as total_usuarios,
  SUM(CASE WHEN email_verified THEN 1 ELSE 0 END) as verificados,
  ROUND(100.0 * SUM(CASE WHEN email_verified THEN 1 ELSE 0 END) / COUNT(*), 2) as porcentaje
FROM users;

-- Códigos por día
SELECT 
  DATE(created_at) as fecha,
  COUNT(*) as codigos_generados,
  SUM(CASE WHEN verified THEN 1 ELSE 0 END) as verificados
FROM email_verification_codes
GROUP BY DATE(created_at)
ORDER BY fecha DESC;
```

---

## 🛠️ Mantenimiento

### Limpiar Códigos Expirados
```sql
-- Manual
DELETE FROM email_verification_codes 
WHERE expires_at < CURRENT_TIMESTAMP;

-- O dejar que el servicio lo haga automáticamente
-- (se ejecuta cada vez que se crea un nuevo código)
```

### Reset de Usuario (para testing)
```sql
-- Desverificar usuario
UPDATE users 
SET email_verified = false 
WHERE email = 'usuario@example.com';

-- Eliminar códigos anteriores
DELETE FROM email_verification_codes 
WHERE user_id = (SELECT id FROM users WHERE email = 'usuario@example.com');
```

---

## 📁 Archivos de Referencia Rápida

| Archivo | Propósito |
|---------|-----------|
| `conf/application.conf` | Configuración SMTP |
| `app/services/EmailService.scala` | Lógica de envío |
| `app/services/EmailVerificationService.scala` | Lógica de verificación |
| `resource/EMAIL_CONFIGURATION.md` | Guía completa |
| `resource/email-preview.html` | Vista previa de emails |
| `test-email.sh` | Script de inicio con verificación |

---

## 🆘 Solución de Problemas Comunes

### "Authentication failed"
```bash
# Verificar que EMAIL_USER y EMAIL_PASSWORD estén configurados
echo $EMAIL_USER
echo $EMAIL_PASSWORD

# Regenerar contraseña de aplicación en Gmail
# https://myaccount.google.com/apppasswords
```

### "Connection timeout"
```bash
# Verificar firewall
sudo ufw status

# Probar conectividad
telnet smtp.gmail.com 587

# Intentar con puerto SSL (465) en lugar de TLS (587)
```

### Códigos no aparecen en logs
```bash
# Verificar que estés mirando los logs correctos
# Los códigos aparecen en stdout cuando sbt run está activo

# Aumentar nivel de log
# En logback.xml cambiar a DEBUG
```

### Email no llega
```bash
# 1. Verificar SPAM
# 2. Verificar límites diarios de Gmail (500/día)
# 3. Ver logs de error en la consola
# 4. Verificar email.enabled = true
```

---

**Tip**: Mantén este archivo abierto en una terminal mientras desarrollas para acceso rápido a comandos comunes! 🚀
