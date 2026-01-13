# 🔐 Configurar Gmail para Envío de Emails

## ⚠️ Importante: Contraseña de Aplicación

Gmail **NO acepta tu contraseña normal** para aplicaciones externas por seguridad.
Necesitas generar una **Contraseña de Aplicación** (App Password).

---

## 📝 Pasos para Configurar Gmail

### 1️⃣ Habilitar Verificación en 2 Pasos

1. Ve a: https://myaccount.google.com/security
2. En la sección **"Cómo iniciar sesión en Google"**
3. Haz clic en **"Verificación en 2 pasos"**
4. Sigue los pasos para habilitarla (si aún no lo has hecho)

### 2️⃣ Generar Contraseña de Aplicación

1. Ve a: https://myaccount.google.com/apppasswords
2. En **"Seleccionar app"**: elige **"Correo"**
3. En **"Seleccionar dispositivo"**: elige **"Otro (nombre personalizado)"**
4. Escribe: **"Reactive Manifesto"**
5. Haz clic en **"Generar"**
6. Gmail mostrará una contraseña de 16 caracteres: `xxxx xxxx xxxx xxxx`
7. **¡Cópiala! No podrás verla de nuevo**

### 3️⃣ Configurar Variables de Entorno

```bash
# Exportar las variables (reemplaza con tu contraseña de aplicación)
export EMAIL_USER="federicopfund@gmail.com"
export EMAIL_PASSWORD="xxxx xxxx xxxx xxxx"  # Los 16 caracteres generados por Gmail

# Verificar que se configuraron correctamente
echo "Usuario: $EMAIL_USER"
echo "Contraseña configurada: ${EMAIL_PASSWORD:0:4}****"  # Solo muestra primeros 4 caracteres
```

### 4️⃣ Reiniciar la Aplicación

```bash
cd /workspaces/Reactive-Manifiesto
sbt run
```

---

## ✅ Verificar Funcionamiento

1. **Inicia la aplicación**: `sbt run`
2. **Abre el navegador**: http://localhost:9000
3. **Registra un usuario** con tu email
4. **Revisa tu bandeja de entrada** (o spam)
5. **Copia el código de 3 dígitos** del email
6. **Ingrésalo** en la página de verificación

---

## 🐛 Solución de Problemas

### Error: "Username and Password not accepted"

✅ **Causa**: Estás usando tu contraseña normal de Gmail
📝 **Solución**: Usa la contraseña de aplicación generada en el Paso 2

### Error: "Connection timeout"

✅ **Causa**: Firewall o puerto bloqueado
📝 **Solución**: 
```bash
# Verificar conectividad
telnet smtp.gmail.com 587

# Si falla, intenta con puerto 465 (SSL):
# Edita application.conf y cambia:
# email.smtp.port = 465
```

### Los emails no llegan

✅ **Revisa SPAM**: Gmail puede marcarlos como spam la primera vez
✅ **Verifica límites**: Gmail gratuito permite ~500 emails/día
✅ **Chequea logs**: Busca errores en la consola de sbt

---

## 📊 Límites de Gmail

| Tipo de Cuenta | Límite Diario |
|----------------|---------------|
| Gmail Gratuito | ~500 emails   |
| Google Workspace | ~2000 emails |

Para volúmenes mayores, considera:
- SendGrid (12,000 gratis/mes)
- Mailgun (5,000 gratis/mes)
- AWS SES (62,000 gratis/mes)

---

## 🔒 Seguridad

✅ **Nunca compartas** tu contraseña de aplicación
✅ **Nunca hagas commit** de credenciales en Git
✅ **Usa variables de entorno** siempre
✅ **Revoca contraseñas** no utilizadas en: https://myaccount.google.com/apppasswords

---

## 📧 Formato del Email que Recibirán los Usuarios

```
De: Reactive Manifesto <federicopfund@gmail.com>
Para: usuario@example.com
Asunto: Código de Verificación - Reactive Manifesto

╔═══════════════════════════════════╗
║   🔐 Código de Verificación       ║
╚═══════════════════════════════════╝

Hola,

Usa el siguiente código para verificar tu cuenta:

        ╔═══════════╗
        ║    4 5 6  ║
        ╚═══════════╝

⏱️ Este código expira en 5 minutos
👥 Tienes máximo 3 intentos

Si no solicitaste este código, ignora este email.
```

---

**¿Listo?** Sigue los pasos arriba y tu aplicación enviará emails automáticamente! 🚀
