# Configuración de Email

## 📧 Sistema de Envío de Emails

El sistema de verificación por email está completamente implementado y soporta dos modos de operación:

### Modo Desarrollo (Por Defecto)
- **Estado**: `email.enabled = false`
- **Comportamiento**: Los códigos de verificación se muestran en la consola/logs
- **Uso**: Ideal para desarrollo y testing sin necesidad de configurar SMTP

### Modo Producción
- **Estado**: `email.enabled = true`
- **Comportamiento**: Los emails se envían realmente a las direcciones de los usuarios
- **Requisito**: Configuración SMTP válida

## 🔧 Configuración para Producción

### Opción 1: Gmail (Recomendado para empezar)

#### Paso 1: Habilitar Verificación en 2 Pasos
1. Ve a [Google Account Security](https://myaccount.google.com/security)
2. Habilita "Verificación en 2 pasos"

#### Paso 2: Generar Contraseña de Aplicación
1. Ve a [App Passwords](https://myaccount.google.com/apppasswords)
2. Selecciona "Correo" y "Otro (nombre personalizado)"
3. Escribe "Reactive Manifesto" y genera
4. Copia la contraseña generada (16 caracteres)

#### Paso 3: Configurar Variables de Entorno
```bash
export EMAIL_USER="tu-email@gmail.com"
export EMAIL_PASSWORD="xxxx xxxx xxxx xxxx"  # Contraseña de aplicación generada
```

#### Paso 4: Actualizar application.conf
```conf
email.enabled = true
email.smtp.host = "smtp.gmail.com"
email.smtp.port = 587
email.smtp.user = ${?EMAIL_USER}
email.smtp.password = ${?EMAIL_PASSWORD}
email.from = "tu-email@gmail.com"
email.fromName = "Reactive Manifesto"
```

### Opción 2: Otros Proveedores SMTP

#### SendGrid
```conf
email.smtp.host = "smtp.sendgrid.net"
email.smtp.port = 587
email.smtp.user = "apikey"
email.smtp.password = ${?SENDGRID_API_KEY}
```

#### Mailgun
```conf
email.smtp.host = "smtp.mailgun.org"
email.smtp.port = 587
email.smtp.user = ${?MAILGUN_USER}
email.smtp.password = ${?MAILGUN_PASSWORD}
```

#### Amazon SES
```conf
email.smtp.host = "email-smtp.us-east-1.amazonaws.com"
email.smtp.port = 587
email.smtp.user = ${?AWS_SMTP_USER}
email.smtp.password = ${?AWS_SMTP_PASSWORD}
```

## 📝 Templates de Email

### Código de Verificación
El sistema envía un email HTML con:
- 🔐 Título "Código de Verificación"
- Código numérico de 3 dígitos en formato grande y claro
- ⏱️ Tiempo de expiración (5 minutos)
- Advertencia sobre límite de intentos (máximo 3)
- Diseño profesional con gradiente púrpura

### Email de Bienvenida
Enviado después de verificar la cuenta con:
- 👋 Saludo personalizado con el nombre del usuario
- Lista de características disponibles
- Diseño consistente con el tema de verificación

## 🧪 Testing

### Probar en Modo Desarrollo
1. Mantén `email.enabled = false`
2. Inicia sesión con un usuario no verificado
3. El código aparecerá en los logs:
```
========================================
 📧 CÓDIGO DE VERIFICACIÓN (DEV MODE)
========================================
 Email: usuario@example.com
 Código: 456
 Expira en: 5 minutos
========================================
```

### Probar en Modo Producción
1. Configura SMTP según las instrucciones arriba
2. Cambia `email.enabled = true`
3. Reinicia la aplicación
4. Inicia sesión con un usuario no verificado
5. Verifica que el email llegue a la bandeja de entrada

## 🔒 Seguridad

### Mejores Prácticas
- ✅ **Nunca** commits credenciales SMTP en el código
- ✅ Usa variables de entorno para información sensible
- ✅ Usa contraseñas de aplicación, no tu contraseña personal de Gmail
- ✅ Configura SPF/DKIM/DMARC si usas tu propio dominio
- ✅ Monitorea límites de envío de tu proveedor

### Límites de Gmail
- **Gratuito**: ~500 emails por día
- **Google Workspace**: ~2000 emails por día
- Para volúmenes mayores, considera SendGrid, Mailgun o AWS SES

## 🚀 Deployment

### Variables de Entorno Requeridas (Producción)
```bash
EMAIL_USER=tu-email@gmail.com
EMAIL_PASSWORD=xxxx-xxxx-xxxx-xxxx
```

### Docker
Agrega al `docker-compose.yml`:
```yaml
environment:
  - EMAIL_USER=${EMAIL_USER}
  - EMAIL_PASSWORD=${EMAIL_PASSWORD}
```

### Render/Heroku
Configura las variables en el dashboard:
- `EMAIL_USER`
- `EMAIL_PASSWORD`

## 📊 Monitoreo

### Logs Importantes
```scala
✅ Email enviado exitosamente a usuario@example.com
❌ Error enviando email a usuario@example.com: Authentication failed
```

### Troubleshooting

#### Error: "Authentication failed"
- Verifica que la contraseña de aplicación sea correcta
- Confirma que la verificación en 2 pasos esté habilitada

#### Error: "Connection timeout"
- Verifica tu firewall/red permite conexiones al puerto 587
- Prueba con puerto 465 (SSL) si 587 falla

#### Emails llegan a SPAM
- Configura SPF record en tu dominio
- Usa un email del mismo dominio que tu aplicación
- Evita palabras spam en el asunto

## 🎨 Personalización

Para personalizar los templates de email, edita los métodos en `EmailService.scala`:
- `createVerificationEmailHtml(code, expirationMinutes)`
- `createWelcomeEmailHtml(fullName)`

Los templates usan HTML inline CSS para máxima compatibilidad con clientes de email.

## 📚 Referencias

- [JavaMail API Documentation](https://javaee.github.io/javamail/)
- [Gmail App Passwords](https://support.google.com/accounts/answer/185833)
- [SendGrid Documentation](https://docs.sendgrid.com/)
- [Amazon SES Documentation](https://docs.aws.amazon.com/ses/)
