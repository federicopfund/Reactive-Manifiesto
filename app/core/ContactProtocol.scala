package core

import akka.actor.typed.ActorRef
import java.time.Instant

// Command messages following Reactive Manifesto message-driven principle
sealed trait ContactCommand

final case class SubmitContact(
  name: String,
  email: String,
  message: String,
  replyTo: ActorRef[ContactResponse]
) extends ContactCommand

final case class GetContactStats(
  replyTo: ActorRef[ContactStatsResponse]
) extends ContactCommand

// Response messages - immutable and explicit
sealed trait ContactResponse
final case object ContactAccepted extends ContactResponse
final case class ContactRejected(reason: String) extends ContactResponse

// Statistics response for monitoring (Elastic principle)
final case class ContactStatsResponse(
  totalReceived: Long,
  totalAccepted: Long,
  totalRejected: Long
) extends ContactResponse

// Internal events for event sourcing pattern
sealed trait ContactEvent
final case class ContactSubmitted(
  name: String,
  email: String,
  message: String,
  timestamp: Instant
) extends ContactEvent

final case class ContactValidated(
  timestamp: Instant
) extends ContactEvent

final case class ContactRejectedEvent(
  reason: String,
  timestamp: Instant
) extends ContactEvent
