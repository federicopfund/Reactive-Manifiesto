# Problema: No llega el código de verificación

## 📋 Diagnóstico

El sistema de verificación por email **está funcionando correctamente**, pero el envío de emails está **deshabilitado** por configuración.

### Estado Actual

```properties
# conf/application.conf línea 94
email.enabled = false  # ❌ DESHABILITADO
```

## 🔍 ¿Qué está pasando?

Cuando `email.enabled = false`:
1. ✅ El código de verificación SÍ se genera (3 dígitos aleatorios)
2. ✅ El código SÍ se guarda en la base de datos
3. ✅ El código expira en 5 minutos
4. ❌ El email NO se envía
5. ✅ El código se imprime en los **logs del servidor** para desarrollo

### Ver el código en los logs

El código aparece en la consola del servidor con este formato:

```
========================================
📧 CÓDIGO DE VERIFICACIÓN (DEV MODE)
========================================
Email: usuario@ejemplo.com
Código: 456
Expira en: 5 minutos
========================================
```

## ✅ Soluciones

### Opción 1: Modo Desarrollo (Usar logs)

**Para desarrollo local sin configurar Gmail:**

1. Mantén `email.enabled = false`
2. Inicia el servidor: `sbt run`
3. Registra un usuario o solicita código
4. **Busca el código en los logs del servidor**
5. Ingresa el código en la aplicación

### Opción 2: Habilitar Gmail (Producción)

**Para enviar emails reales:**

#### Paso 1: Configurar cuenta Gmail

1. Ve a tu cuenta de Google: https://myaccount.google.com/
2. Habilita **"Verificación en 2 pasos"**
3. Genera una **"Contraseña de aplicación"**:
   - Ve a: https://myaccount.google.com/apppasswords
   - Selecciona "Mail" y "Other (Custom name)"
   - Nombra: "Reactive Manifesto"
   - Copia la contraseña de 16 caracteres

#### Paso 2: Configurar variables de entorno

```bash
# Linux/Mac - Agrega a ~/.bashrc o ~/.zshrc
export EMAIL_USER="tu-email@gmail.com"
export EMAIL_PASSWORD="xxxx xxxx xxxx xxxx"  # Contraseña de app de 16 dígitos

# Windows - CMD
set EMAIL_USER=tu-email@gmail.com
set EMAIL_PASSWORD=xxxx xxxx xxxx xxxx

# Windows - PowerShell
$env:EMAIL_USER="tu-email@gmail.com"
$env:EMAIL_PASSWORD="xxxx xxxx xxxx xxxx"
```

#### Paso 3: Habilitar el envío de emails

```properties
# conf/application.conf
email.enabled = true  # ✅ HABILITADO
```

#### Paso 4: Reiniciar el servidor

```bash
sbt run
```

### Opción 3: Desactivar verificación por email (Solo desarrollo)

**⚠️ NO RECOMENDADO para producción**

Si quieres saltarte la verificación temporalmente:

1. Modifica `AuthController.scala`
2. Comenta la validación de email en el registro
3. Marca usuarios como verificados automáticamente

## 🧪 Probar el sistema

### Test 1: Verificar logs (Modo desarrollo)

```bash
# Terminal 1: Inicia el servidor
sbt run

# Terminal 2: Registra un usuario
curl -X POST http://localhost:9000/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "email": "test@example.com",
    "password": "123456",
    "fullName": "Test User"
  }'

# Busca el código en los logs del Terminal 1
```

### Test 2: Verificar Gmail (Modo producción)

```bash
# Configura las variables de entorno
export EMAIL_USER="tu-email@gmail.com"
export EMAIL_PASSWORD="tu-contraseña-de-app"

# Edita application.conf
# email.enabled = true

# Inicia el servidor
sbt run

# Registra un usuario
# Revisa tu bandeja de entrada
```

## 📁 Archivos Relacionados

- **Configuración**: [conf/application.conf](conf/application.conf) línea 94
- **Servicio de Email**: [app/services/EmailService.scala](app/services/EmailService.scala)
- **Servicio de Verificación**: [app/services/EmailVerificationService.scala](app/services/EmailVerificationService.scala)
- **Controlador**: [app/controllers/AuthController.scala](app/controllers/AuthController.scala)
- **Guía detallada**: [CONFIGURAR_GMAIL.md](CONFIGURAR_GMAIL.md)

## 📚 Recursos Adicionales

- [Google App Passwords](https://myaccount.google.com/apppasswords)
- [Gmail SMTP Settings](https://support.google.com/mail/answer/7126229)
- [Play Framework Email Configuration](https://www.playframework.com/documentation/latest/ScalaMail)

## 🐛 Troubleshooting

### El código no aparece en los logs

**Problema**: No veo el código impreso en la consola

**Solución**:
1. Verifica que `email.enabled = false`
2. Busca líneas que contengan "CÓDIGO DE VERIFICACIÓN"
3. Verifica el nivel de log en `conf/logback.xml`

### Gmail rechaza el login

**Problema**: `Authentication failed: 535 Username and Password not accepted`

**Soluciones**:
1. ✅ Usa una **contraseña de aplicación**, NO tu contraseña normal
2. ✅ Habilita "Verificación en 2 pasos"
3. ✅ Verifica que `EMAIL_USER` sea tu email completo
4. ✅ Verifica que `EMAIL_PASSWORD` tenga los 16 caracteres (sin espacios en el código)

### El código expira muy rápido

**Problema**: El código expira antes de poder usarlo

**Solución**: Modifica `CODE_EXPIRATION_MINUTES` en `EmailVerificationService.scala`:

```scala
private val CODE_EXPIRATION_MINUTES = 10  // Cambia de 5 a 10 minutos
```

## 🎯 Recomendación

**Para desarrollo local**: Usa **Opción 1** (logs)
**Para producción**: Usa **Opción 2** (Gmail configurado)
