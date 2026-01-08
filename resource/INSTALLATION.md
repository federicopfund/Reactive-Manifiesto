# Scripts de Instalación

Este directorio contiene scripts para facilitar la instalación de las dependencias necesarias para el proyecto **Reactive-Manifiesto**.

## 📋 Requisitos del Sistema

- **Sistema Operativo**: Ubuntu 20.04+ / Debian 10+
- **Privilegios**: Acceso sudo
- **Conexión**: Internet activa

## 🚀 Scripts Disponibles

### 1. `install-dependencies.sh` (Completo)

Script interactivo con verificaciones completas y opciones de configuración.

**Características:**
- ✅ Verificación de dependencias existentes
- ✅ Instalación de OpenJDK 17 LTS
- ✅ Instalación de SBT (versión requerida por el proyecto)
- ✅ Configuración de JAVA_HOME
- ✅ Validación de versiones compatibles con Play Framework
- ✅ Confirmaciones interactivas
- ✅ Colores y mensajes informativos
- ✅ Manejo de errores robusto

**Uso:**
```bash
./install-dependencies.sh
```

### 2. `quick-install.sh` (Rápido)

Script no interactivo para instalación automatizada sin confirmaciones.

**Características:**
- ⚡ Instalación rápida sin confirmaciones
- ⚡ Ideal para CI/CD o automatización
- ⚡ Instalación solo si no están presentes

**Uso:**
```bash
./quick-install.sh
```

## 📦 Dependencias Instaladas

| Herramienta | Versión | Descripción |
|-------------|---------|-------------|
| **Java** | OpenJDK 17 LTS | Entorno de ejecución compatible con Play Framework 3.0 |
| **SBT** | 1.9.7+ | Build tool para proyectos Scala |
| **Scala** | 2.13.12 | Instalado automáticamente por SBT |

## 🎯 Versiones Compatibles

El proyecto **Reactive-Manifiesto** requiere:
- Java: **11, 17 o 21** (LTS)
- SBT: **1.9.7**
- Scala: **2.13.12**
- Play Framework: **3.0.1+**

## 📝 Comandos Útiles (Post-Instalación)

```bash
# Verificar versiones instaladas
java -version
sbt --version

# Compilar el proyecto
sbt compile

# Ejecutar la aplicación
sbt run

# Ejecutar tests
sbt test

# Limpiar build
sbt clean

# Modo interactivo de SBT
sbt
```

## 🐳 Alternativa: Docker

Si prefieres usar Docker, el proyecto incluye un `Dockerfile`:

```bash
# Construir imagen
docker build -t reactive-manifiesto .

# Ejecutar contenedor
docker run -p 9000:9000 reactive-manifiesto
```

## 🔧 Solución de Problemas

### Error: "java: command not found"

```bash
# Recargar variables de entorno
source /etc/environment

# Verificar JAVA_HOME
echo $JAVA_HOME
```

### Error: Clave GPG de SBT

Si falla la descarga de la clave GPG:

```bash
sudo apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv 2EE0EA64E40A89B84B2DF73499E82A75642AC823
```

### Warning: Java version no compatible

Si aparece el warning sobre Java 25:

```bash
# Desinstalar Java 25
sudo apt remove openjdk-25-jdk openjdk-25-jre

# Instalar Java 17
sudo apt install openjdk-17-jdk openjdk-17-jre
```

## 📚 Referencias

- [Play Framework Documentation](https://www.playframework.com/documentation/3.0.x/Home)
- [SBT Documentation](https://www.scala-sbt.org/documentation.html)
- [Scala Documentation](https://docs.scala-lang.org/)

## 🤝 Contribución

Para reportar problemas o sugerir mejoras en los scripts de instalación, por favor abre un issue en el repositorio.

## 📄 Licencia

Estos scripts son parte del proyecto Reactive-Manifiesto y siguen la misma licencia del proyecto principal.
