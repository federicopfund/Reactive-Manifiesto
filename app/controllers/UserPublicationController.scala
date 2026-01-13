package controllers

import javax.inject._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json._
import scala.concurrent.{ExecutionContext, Future}
import repositories.{PublicationRepository, UserRepository}
import models.{Publication, PublicationStatus}
import actions.{UserAction, AuthRequest}
import java.time.Instant

case class PublicationFormData(
  title: String,
  content: String,
  excerpt: Option[String],
  coverImage: Option[String],
  category: String,
  tags: Option[String]
)

@Singleton
class UserPublicationController @Inject()(
  cc: ControllerComponents,
  publicationRepo: PublicationRepository,
  userRepo: UserRepository,
  userAction: UserAction
)(implicit ec: ExecutionContext) extends AbstractController(cc) {

  // Definición del formulario
  val publicationForm = Form(
    mapping(
      "title" -> nonEmptyText(minLength = 5, maxLength = 200),
      "content" -> nonEmptyText(minLength = 50),
      "excerpt" -> optional(text(maxLength = 500)),
      "coverImage" -> optional(text),
      "category" -> nonEmptyText,
      "tags" -> optional(text)
    )(PublicationFormData.apply)(PublicationFormData.unapply)
  )

  /**
   * Dashboard del usuario - Ver todas sus publicaciones
   */
  def dashboard = userAction.async { implicit request: AuthRequest[AnyContent] =>
    for {
      publications <- publicationRepo.findByUserId(request.userId)
      stats <- publicationRepo.getUserStats(request.userId)
    } yield {
      Ok(views.html.user.dashboard(
        username = request.username,
        publications = publications,
        stats = stats
      ))
    }
  }

  /**
   * Formulario para crear nueva publicación
   */
  def newPublicationForm = userAction { implicit request: AuthRequest[AnyContent] =>
    Ok(views.html.user.publicationForm(
      publicationForm,
      None,
      request.username
    ))
  }

  /**
   * Crear nueva publicación
   */
  def createPublication = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationForm.bindFromRequest().fold(
      formWithErrors => {
        Future.successful(
          BadRequest(views.html.user.publicationForm(
            formWithErrors,
            None,
            request.username
          ))
        )
      },
      formData => {
        val slug = generateSlug(formData.title)
        val publication = Publication(
          userId = request.userId,
          title = formData.title,
          slug = slug,
          content = formData.content,
          excerpt = formData.excerpt,
          coverImage = formData.coverImage,
          category = formData.category,
          tags = formData.tags,
          status = PublicationStatus.Draft.toString
        )
        
        publicationRepo.create(publication).map { id =>
          Redirect(routes.UserPublicationController.editPublicationForm(id))
            .flashing("success" -> "Publicación creada exitosamente como borrador")
        }
      }
    )
  }

  /**
   * Formulario para editar publicación existente
   */
  def editPublicationForm(id: Long) = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationRepo.findById(id).map {
      case Some(publication) if publication.userId == request.userId =>
        val filledForm = publicationForm.fill(PublicationFormData(
          title = publication.title,
          content = publication.content,
          excerpt = publication.excerpt,
          coverImage = publication.coverImage,
          category = publication.category,
          tags = publication.tags
        ))
        Ok(views.html.user.publicationForm(
          filledForm,
          Some(publication),
          request.username
        ))
      case Some(_) =>
        Forbidden("No tienes permiso para editar esta publicación")
      case None =>
        NotFound("Publicación no encontrada")
    }
  }

  /**
   * Actualizar publicación
   */
  def updatePublication(id: Long) = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationRepo.findById(id).flatMap {
      case Some(existingPub) if existingPub.userId == request.userId =>
        publicationForm.bindFromRequest().fold(
          formWithErrors => {
            Future.successful(
              BadRequest(views.html.user.publicationForm(
                formWithErrors,
                Some(existingPub),
                request.username
              ))
            )
          },
          formData => {
            val slug = generateSlug(formData.title)
            val updatedPub = existingPub.copy(
              title = formData.title,
              slug = slug,
              content = formData.content,
              excerpt = formData.excerpt,
              coverImage = formData.coverImage,
              category = formData.category,
              tags = formData.tags,
              updatedAt = Instant.now()
            )
            
            publicationRepo.update(updatedPub).map { success =>
              if (success) {
                Redirect(routes.UserPublicationController.dashboard())
                  .flashing("success" -> "Publicación actualizada exitosamente")
              } else {
                InternalServerError("Error al actualizar la publicación")
              }
            }
          }
        )
      case Some(_) =>
        Future.successful(Forbidden("No tienes permiso para editar esta publicación"))
      case None =>
        Future.successful(NotFound("Publicación no encontrada"))
    }
  }

  /**
   * Enviar publicación para revisión
   */
  def submitForReview(id: Long) = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationRepo.findById(id).flatMap {
      case Some(publication) if publication.userId == request.userId =>
        val updated = publication.copy(
          status = PublicationStatus.Pending.toString,
          updatedAt = Instant.now()
        )
        publicationRepo.update(updated).map { success =>
          if (success) {
            Redirect(routes.UserPublicationController.dashboard())
              .flashing("success" -> "Publicación enviada para revisión")
          } else {
            InternalServerError("Error al enviar la publicación")
          }
        }
      case Some(_) =>
        Future.successful(Forbidden("No tienes permiso"))
      case None =>
        Future.successful(NotFound("Publicación no encontrada"))
    }
  }

  /**
   * Eliminar publicación
   */
  def deletePublication(id: Long) = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationRepo.delete(id, request.userId).map { success =>
      if (success) {
        Redirect(routes.UserPublicationController.dashboard())
          .flashing("success" -> "Publicación eliminada")
      } else {
        BadRequest("No se pudo eliminar la publicación")
      }
    }
  }

  /**
   * Ver publicación (preview)
   */
  def viewPublication(id: Long) = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationRepo.findById(id).map {
      case Some(publication) if publication.userId == request.userId || publication.status == PublicationStatus.Approved.toString =>
        Ok(views.html.user.publicationPreview(publication, request.username))
      case Some(_) =>
        Forbidden("No tienes permiso para ver esta publicación")
      case None =>
        NotFound("Publicación no encontrada")
    }
  }

  /**
   * API: Listar publicaciones del usuario (JSON)
   */
  def listPublicationsJson = userAction.async { implicit request: AuthRequest[AnyContent] =>
    publicationRepo.findByUserId(request.userId).map { publications =>
      Ok(Json.toJson(publications.map { pub =>
        Json.obj(
          "id" -> pub.id,
          "title" -> pub.title,
          "status" -> pub.status,
          "category" -> pub.category,
          "viewCount" -> pub.viewCount,
          "createdAt" -> pub.createdAt.toString,
          "updatedAt" -> pub.updatedAt.toString
        )
      }))
    }
  }

  /**
   * Generar slug a partir del título
   */
  private def generateSlug(title: String): String = {
    val slug = title.toLowerCase
      .replaceAll("[áàäâ]", "a")
      .replaceAll("[éèëê]", "e")
      .replaceAll("[íìïî]", "i")
      .replaceAll("[óòöô]", "o")
      .replaceAll("[úùüû]", "u")
      .replaceAll("[ñ]", "n")
      .replaceAll("[^a-z0-9]+", "-")
      .replaceAll("^-|-$", "")
    
    s"$slug-${System.currentTimeMillis()}"
  }
}
